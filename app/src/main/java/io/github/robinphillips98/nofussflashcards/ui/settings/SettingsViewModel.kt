package io.github.robinphillips98.nofussflashcards.ui.settings

import android.content.Context
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.backup.SerializedBackup
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.data.flashcards.FlashcardsRepository
import io.github.robinphillips98.nofussflashcards.data.backup.SerializedDeck
import io.github.robinphillips98.nofussflashcards.data.backup.SerializedFlashcard
import io.github.robinphillips98.nofussflashcards.ui.errors.SettingsError
import io.github.robinphillips98.nofussflashcards.ui.errors.messageFor
import io.github.robinphillips98.nofussflashcards.utils.StringResolver
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.IOException

class SettingsViewModel(
    private val decksRepository: DecksRepository,
    private val flashcardsRepository: FlashcardsRepository,
    private val stringResolver: StringResolver
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = Channel<SettingsUiEvent>(Channel.BUFFERED)

    /**
     * A flow of events that can be observed by the UI to show snackbars or other one-time events.
     */
    val events = _events.receiveAsFlow()

    /**
     * Exports all decks and flashcards (excluding images) to a JSON file at the given [uri].
     *
     * @param context The [Context] used to open the output stream.
     * @param uri The [Uri] of the file to write to.
     */
    fun exportData(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBackupLoading = true,
                    exportWarningMessage = null
                )
            }
            try {
                val decks = decksRepository.getAllDecks()
                val flashcards = flashcardsRepository.getAllFlashcards()

                if (decks.isEmpty() && flashcards.isEmpty()) {
                    throw NoDataToExportException()
                }

                val deckIds = decks.mapTo(mutableSetOf()) { it.deckId }
                val orphanedFlashcardCount = flashcards.count { it.deckId !in deckIds }
                if (orphanedFlashcardCount > 0) {
                    Log.w(TAG, "Excluded $orphanedFlashcardCount flashcards from export because they reference non-existent decks")
                }

                val exportableFlashcards = flashcards.filter {
                    it.deckId in deckIds && !it.definition.isNullOrBlank()
                }
                val excludedFlashcardCount = flashcards.count {
                    it.deckId in deckIds && it.definition.isNullOrBlank()
                }
                val strippedImageCount = exportableFlashcards.count { !it.imagePath.isNullOrBlank() }

                if (excludedFlashcardCount > 0) {
                    Log.w(TAG, "Excluded $excludedFlashcardCount flashcards from export because they do not have a text definition")
                }

                if (strippedImageCount > 0) {
                    Log.w(TAG, "Stripped images from $strippedImageCount exported flashcards")
                }

                val backup = SerializedBackup(
                    decks = decks.map { it.toSerialized() },
                    flashcards = exportableFlashcards.map { it.toSerialized() }
                )
                val json = Json.encodeToString(SerializedBackup.serializer(), backup)

                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(json.toByteArray())
                } ?: throw IOException("Could not open output stream")

                _uiState.update {
                    it.copy(
                        exportWarningMessage = buildExportWarningMessage(
                            excludedFlashcardCount = excludedFlashcardCount,
                            strippedImageCount = strippedImageCount
                        )
                    )
                }

                emitSuccess(stringResolver.get(R.string.export_success))
            } catch (throwable: Throwable) {
                emitError(throwable.toExportError(), throwable)
            } finally {
                _uiState.update { it.copy(isBackupLoading = false) }
            }
        }
    }

    /**
     * Imports decks and flashcards from a JSON file at the given [uri], re-mapping deck IDs to
     * avoid conflicts with existing data.
     *
     * @param context The [Context] used to open the input stream.
     * @param uri The [Uri] of the file to read from.
     */
    fun importData(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBackupLoading = true) }
            try {
                val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader().readText()
                } ?: throw IOException("Could not open input stream")

                val backup = Json.decodeFromString(SerializedBackup.serializer(), json)

                /*
                 Insert decks first, building a map of old serialized ID -> new Room-generated ID.
                 This allows us to map flashcards to the correct new deck IDs when inserting them.
                 */
                val deckIdMap = mutableMapOf<Int, Int>()
                backup.decks.forEach { serializedDeck ->
                    val newDeck = serializedDeck.toDeck()
                    val newId = decksRepository.insertDeck(newDeck)
                    deckIdMap[serializedDeck.deckId] = newId.toInt()
                }

                // Insert flashcards, remapping their deckId to the newly assigned deck IDs.
                backup.flashcards.forEach { serializedFlashcard ->
                    val newDeckId = deckIdMap[serializedFlashcard.deckId]
                        ?: throw IllegalStateException(
                            "Flashcard references unknown deck ID: ${serializedFlashcard.deckId}"
                        )
                    val newFlashcard = serializedFlashcard.toFlashcard(newDeckId)
                    flashcardsRepository.insertFlashcard(newFlashcard)
                }

                emitSuccess(stringResolver.get(R.string.import_success))
            } catch (throwable: Throwable) {
                emitError(throwable.toImportError(), throwable)
            } finally {
                _uiState.update { it.copy(isBackupLoading = false) }
            }
        }
    }

    /**
     * Emits a success event to the UI with the given [message].
     *
     * @param message The message to be displayed in the success snackbar.
     */
    private suspend fun emitSuccess(message: String) {
        _events.send(SettingsUiEvent.ShowSuccessSnackbar(message))
    }

    private fun emitError(error: SettingsError, throwable: Throwable? = null) {
        Log.e(TAG, "Settings error: $error", throwable)
        viewModelScope.launch {
            _events.send(
                SettingsUiEvent.ShowErrorSnackbar(stringResolver.messageFor(error))
            )
        }
    }

    /**
     * Converts a [Throwable] to a corresponding [SettingsError] for export operations.
     *
     * @return A [SettingsError] representing the error that occurred during export.
     */
    private fun Throwable.toExportError(): SettingsError = when (this) {
        is NoDataToExportException -> SettingsError.NoDataToExport
        is IOException, is SQLiteException -> SettingsError.ExportFailed
        else -> SettingsError.Unknown(this)
    }

    /**
     * Converts a [Throwable] to a corresponding [SettingsError] for import operations.
     *
     * @return A [SettingsError] representing the error that occurred during import.
     */
    private fun Throwable.toImportError(): SettingsError = when (this) {
        is IOException, is SQLiteException -> SettingsError.ImportFailed
        else -> SettingsError.Unknown(this)
    }

    /**
     * Converts a [Deck] to its serialized representation for backup purposes.
     *
     * @return A [SerializedDeck] representing the deck in a format suitable for JSON serialization.
     */
    private fun Deck.toSerialized() = SerializedDeck(deckId, name, description)

    /**
     * Converts a [SerializedDeck] back to a [Deck] for restoring from backup.
     *
     * @return A [Deck] representing the serialized deck.
     */
    private fun SerializedDeck.toDeck() = Deck(0, name, description)

    /**
     * Converts a [Flashcard] to its serialized representation for backup purposes.
     *
     * @return A [SerializedFlashcard] representing the flashcard in a format suitable for JSON serialization.
     */
    private fun Flashcard.toSerialized() = SerializedFlashcard(term, definition, deckId)

    private fun buildExportWarningMessage(
        excludedFlashcardCount: Int,
        strippedImageCount: Int
    ): String? {
        val messages = buildList {
            if (excludedFlashcardCount == 1) {
                add(stringResolver.get(R.string.settings_export_warning_excluded_single))
            } else if (excludedFlashcardCount > 1) {
                add(
                    stringResolver.get(
                        R.string.settings_export_warning_excluded_plural,
                        excludedFlashcardCount
                    )
                )
            }

            if (strippedImageCount == 1) {
                add(stringResolver.get(R.string.settings_export_warning_images_single))
            } else if (strippedImageCount > 1) {
                add(
                    stringResolver.get(
                        R.string.settings_export_warning_images_plural,
                        strippedImageCount
                    )
                )
            }
        }

        return messages.takeIf { it.isNotEmpty() }?.joinToString(separator = "\n")
    }

    /**
     * Converts a [SerializedFlashcard] back to a [Flashcard] for restoring from backup.
     *
     * @param deckId The new deck ID to associate with the flashcard, as the original deck ID will
     * likely have changed during import.
     *
     * @return A [Flashcard] representing the serialized flashcard.
     */
    private fun SerializedFlashcard.toFlashcard(deckId: Int) = Flashcard(
        flashcardId = 0,
        term = term,
        definition = definition,
        deckId = deckId
    )

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}

private class NoDataToExportException : IllegalStateException()

/**
 * Represents events that can be emitted to the UI from the Settings screen.
 */
sealed interface SettingsUiEvent {
    /**
     * Represents an event to show an error snackbar with a given message.
     *
     * @property message The message to be displayed in the snackbar.
     */
    data class ShowErrorSnackbar(val message: String) : SettingsUiEvent

    /**
     * Represents an event to show a snackbar indicating that an operation was successful.
     *
     * @property message The message to be displayed in the snackbar.
     */
    data class ShowSuccessSnackbar(val message: String) : SettingsUiEvent
}

/**
 * Indicates whether an import/export operation is currently in progress.
 */
data class SettingsUiState(
    val isBackupLoading: Boolean = false,
    val exportWarningMessage: String? = null
)