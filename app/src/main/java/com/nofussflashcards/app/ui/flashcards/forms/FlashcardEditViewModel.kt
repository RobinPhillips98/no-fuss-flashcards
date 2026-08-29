package com.nofussflashcards.app.ui.flashcards.forms

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.decks.Deck
import com.nofussflashcards.app.data.decks.DecksRepository
import com.nofussflashcards.app.data.flashcards.Flashcard
import com.nofussflashcards.app.data.flashcards.FlashcardsRepository
import com.nofussflashcards.app.ui.errors.FlashcardEditError
import com.nofussflashcards.app.ui.errors.messageFor
import com.nofussflashcards.app.ui.utils.deleteImageFromInternalStorage
import com.nofussflashcards.app.ui.utils.saveImageToInternalStorage
import com.nofussflashcards.app.utils.StringResolver
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

class FlashcardEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val flashcardsRepository: FlashcardsRepository,
    decksRepository: DecksRepository,
    private val stringResolver: StringResolver
): ViewModel() {
    var flashcardUiState by mutableStateOf(FlashcardUiState())
        private set

    private val flashcardId: Int =
        checkNotNull(savedStateHandle[FlashcardEditDestination.FLASHCARD_ID_ARG])

    private val _events = Channel<FlashcardEditUiEvent>(Channel.BUFFERED)

    /**
     * A flow of events that can be observed by the UI to show snackbars or other one-time events.
     */
    val events = _events.receiveAsFlow()

    /**
     * Holds the original image URI loaded from the database. Never changes after init, so
     * it can always be used to restore the image and to get the path for deletion on save.
     */
    private var originalImageUri: Uri? = null

    /**
     * Exposes whether the flashcard originally had an image, so the screen can decide
     * whether to show the Restore button.
     */
    val hasOriginalImage: Boolean get() = originalImageUri != null

    /**
     * A flow of all available decks, which can be used to populate a dropdown menu in the UI.
     */
    val availableDecks: StateFlow<List<Deck>> =
        decksRepository.getAllDecksStream().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    init {
        loadFlashcard()
    }

    /**
     * Loads the flashcard and updates [flashcardUiState]. Emits a retry-capable error event when
     * loading fails.
     */
    fun loadFlashcard() {
        viewModelScope.launch {
            flashcardUiState = flashcardUiState.copy(isLoading = true, hasLoadError = false)
            flashcardUiState = try {
                val loadedFlashcard = flashcardsRepository.getFlashcardStream(flashcardId)
                    .filterNotNull()
                    .first()
                val loadedFlashcardUiState = loadedFlashcard.toFlashcardUiState()
                originalImageUri = loadedFlashcardUiState.existingImageUri
                loadedFlashcardUiState
            } catch (throwable: Throwable) {
                emitError(
                    throwable.toFlashcardLoadError(),
                    throwable,
                    shouldRetryLoad = true
                )
                flashcardUiState.copy(isLoading = false, hasLoadError = true)
            }
        }
    }

    /**
     * Updates the [Flashcard] in the Room database
     *
     * @return `true` if the flashcard was updated successfully, `false` otherwise
     */
    suspend fun updateFlashcard(context: Context): Boolean {
        if (validateInput()) {
            val oldImagePath = originalImageUri?.path
            val selectedImageUri = flashcardUiState.selectedImageUri
            val newImagePath = when {
                // New image selected - save it first.
                selectedImageUri != null -> {
                    // Try to save the image, abort if it fails and emit an error event to the UI.
                    try {
                        saveImageToInternalStorage(context, selectedImageUri)
                    } catch (throwable: Throwable) {
                        emitError(throwable.toImageSaveError(), throwable)
                        return false
                    }
                }
                // Existing image kept
                flashcardUiState.existingImageUri != null ->
                    oldImagePath
                // Cleared - null
                else -> null
            }

            /*
                If the old image path is different from the new image path, attempt to delete the old
                image. If deletion fails, attempt to roll back the newly saved image and emit an error
                event to the UI.
             */
            if (!deleteOldImageIfNeeded(oldImagePath, newImagePath)) {
                return false
            }

            try {
                flashcardsRepository.updateFlashcard(
                    flashcardUiState.flashcardDetails.toFlashcard(newImagePath)
                )
                emitSuccessEvent()
                return true
            } catch (throwable: Throwable) {
                // If the update fails, attempt to delete the newly saved image to avoid orphan files.
                if (newImagePath != null && newImagePath != oldImagePath) {
                    val imageDeletedSuccessfully = deleteImageFromInternalStorage(newImagePath)
                    if (!imageDeletedSuccessfully) {
                        Log.e(TAG, "Failed to delete image file after flashcard update failure: $newImagePath")
                    }
                }
                emitError(throwable.toFlashcardSaveError(), throwable)
                return false
            }
        } else {
            emitError(FlashcardEditError.InvalidSubmission)
            return false
        }
    }

    /**
     * Checks if the old image needs to be deleted and attempts to delete it.
     *
     * If deletion fails, attempts to roll back the newly saved image to avoid orphan files.
     *
     * @param oldImagePath The path of the old image to delete.
     * @param newImagePath The path of the new image that was saved.
     *
     * @return `true` if the old image was deleted successfully or if no deletion was needed,
     *  `false` if deletion was attempted and failed
     */
    private fun deleteOldImageIfNeeded(oldImagePath: String?, newImagePath: String?): Boolean {
        if (oldImagePath == null || oldImagePath == newImagePath) {
            return true
        }

        val oldImageDeletedSuccessfully = deleteImageFromInternalStorage(oldImagePath)
        if (!oldImageDeletedSuccessfully) {
            if (newImagePath != null) {
                val rollbackDeleteSucceeded = deleteImageFromInternalStorage(newImagePath)
                if (!rollbackDeleteSucceeded) {
                    Log.e(TAG, "Failed to rollback new image after old image deletion failed: $newImagePath")
                }
            }
            emitError(FlashcardEditError.OldImageDeleteFailed)
            return false
        }

        return true
    }

    /**
     * Updates the [flashcardUiState] with the value provided in the argument. This method also
     * triggers a validation for input values.
     */
    fun updateUiState(flashcardDetails: FlashcardDetails) {
        flashcardUiState = flashcardUiState.copy(
            flashcardDetails = flashcardDetails,
            isEntryValid = validateInput(flashcardDetails)
        )
    }

    /**
     * Updates the [flashcardUiState] with the selected image URI. This method also triggers a
     * validation for input values.
     *
     * @param uri The URI of the selected image, or null if the image was cleared.
     */
    fun onImageSelected(uri: Uri?) {
        val newExistingUri = if (uri == null) null else flashcardUiState.existingImageUri
        flashcardUiState = flashcardUiState.copy(
            selectedImageUri = uri,
            // Null out existingImageUri so the preview disappears when cleared
            existingImageUri = newExistingUri,
            isEntryValid = validateInput(imageUri = uri, existingUri = newExistingUri)
        )
    }

    /**
     * Restores the original image URI to the [flashcardUiState]. This method is called when the
     * user clicks the "Restore" button.
     */
    fun restoreExistingImage() {
        flashcardUiState = flashcardUiState.copy(
            selectedImageUri = null,
            existingImageUri = originalImageUri,
            isEntryValid = validateInput(existingUri = originalImageUri)
        )
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        private const val TAG = "FlashcardEditViewModel"
    }

    /** Emits a success event with a user-friendly message. */
    private suspend fun emitSuccessEvent() {
        _events.send(
            FlashcardEditUiEvent.ShowFlashcardSavedSnackbar(
                stringResolver.get(R.string.flashcard_saved_success_message)
            )
        )
    }

    /** Emits an error event and logs throwable details where available. */
    private fun emitError(
        error: FlashcardEditError,
        throwable: Throwable? = null,
        shouldRetryLoad: Boolean = false
    ) {
        if (error is FlashcardEditError.InvalidSubmission) {
            Log.w(TAG, "Invalid submission: ${flashcardUiState.flashcardDetails}")
        } else {
            Log.e(TAG, "Flashcard edit error: $error", throwable)
        }
        viewModelScope.launch {
            _events.send(
                FlashcardEditUiEvent.ShowErrorSnackbar(
                    message = stringResolver.messageFor(error),
                    actionLabel =
                        if (shouldRetryLoad) stringResolver.get(R.string.retry_button)
                        else null,
                    shouldRetryLoad = shouldRetryLoad
                )
            )
        }
    }

    /**
     * Converts a [Throwable] thrown while saving to a user-friendly [FlashcardEditError].
     */
    private fun Throwable.toFlashcardSaveError(): FlashcardEditError {
        return when (this) {
            is IOException, is SQLiteException -> FlashcardEditError.FlashcardSaveFailed
            else -> FlashcardEditError.Unknown(this)
        }
    }

    /**
     * Converts a [Throwable] thrown while loading to a user-friendly [FlashcardEditError].
     */
    private fun Throwable.toFlashcardLoadError(): FlashcardEditError {
        return when (this) {
            is IOException, is SQLiteException -> FlashcardEditError.FlashcardLoadFailed
            else -> FlashcardEditError.Unknown(this)
        }
    }

    /**
     * Converts a [Throwable] thrown while saving an image to a user-friendly [FlashcardEditError].
     */
    private fun Throwable.toImageSaveError(): FlashcardEditError {
        return when (this) {
            is IOException -> FlashcardEditError.ImageSaveFailed
            else -> FlashcardEditError.Unknown(this)
        }
    }

    /**
     * Validates the input fields for the flashcard. The term must be non-blank, and at least one of
     * the definition, selected image URI, or existing image URI must be non-null and non-blank.
     */
    private fun validateInput(
        uiState: FlashcardDetails = flashcardUiState.flashcardDetails,
        imageUri: Uri? = flashcardUiState.selectedImageUri,
        existingUri: Uri? = flashcardUiState.existingImageUri
    ): Boolean {
        return uiState.term.isNotBlank() &&
            (!uiState.definition.isNullOrBlank() || imageUri != null || existingUri != null)
    }
}

/**
 * Converts a [Flashcard] to a [FlashcardUiState] for use in the UI.
 */
private fun Flashcard.toFlashcardUiState(): FlashcardUiState =
    FlashcardUiState(
        flashcardDetails = this.toFlashcardDetails(),
        isEntryValid = true,
        existingImageUri = this.imagePath?.toUri(),
        isLoading = false,
        hasLoadError = false
    )

/**
 * Represents events that can be emitted to the UI from the flashcard edit screen.
 */
sealed interface FlashcardEditUiEvent {
    /**
     * Represents an event to show an error snackbar with an optional retry action.
     */
    data class ShowErrorSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val shouldRetryLoad: Boolean = false
    ) : FlashcardEditUiEvent

    /**
     * Represents an event to show a snackbar indicating that a flashcard was saved.
     */
    data class ShowFlashcardSavedSnackbar(val message: String): FlashcardEditUiEvent
}
