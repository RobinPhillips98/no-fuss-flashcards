package io.github.robinphillips98.nofussflashcards.ui.decks.forms

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import io.github.robinphillips98.nofussflashcards.ui.decks.DeckDetails
import io.github.robinphillips98.nofussflashcards.ui.decks.toDeck
import io.github.robinphillips98.nofussflashcards.ui.errors.DeckEntryError
import io.github.robinphillips98.nofussflashcards.ui.errors.messageFor
import io.github.robinphillips98.nofussflashcards.utils.StringResolver
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ViewModel to validate and manage the state of the Deck entry screen.
 *
 * @property decksRepository Repository for managing decks.
 * @property stringResolver Resolver for retrieving localized strings.
 */
class DeckEntryViewModel(
    private val decksRepository: DecksRepository,
    private val stringResolver: StringResolver
): ViewModel() {
    var deckUiState by mutableStateOf(DeckUiState())
        private set

    private val _events = Channel<DeckEntryUiEvent>(Channel.BUFFERED)

    /**
     * A flow of events that can be observed by the UI to show snackbars or other one-time events.
     */
    val events = _events.receiveAsFlow()

    /**
     * Updates the [deckUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(deckDetails: DeckDetails) {
        deckUiState =
            DeckUiState(deckDetails = deckDetails, isEntryValid = validateInput(deckDetails))
    }

    /**
     * Inserts a [Deck] in the Room database
     *
     * @return `true` if the deck was successfully saved, `false` otherwise
     */
    suspend fun saveDeck(): Boolean {
        if (validateInput()) {
            try {
                decksRepository.insertDeck(deckUiState.deckDetails.toDeck())
                emitSuccessEvent()
                return true
            } catch (e: Throwable) {
                emitError(e.toDeckEntryError(), e)
                return false
            }
        } else {
            emitError(DeckEntryError.InvalidSubmission)
            return false
        }
    }

    /**
     * Emits a success event to the UI. This method sends a [DeckEntryUiEvent.ShowDeckSavedSnackbar]
     * event to the UI with a user-friendly success message.
     */
    private suspend fun emitSuccessEvent() {
        _events.send(
            DeckEntryUiEvent.ShowDeckSavedSnackbar(
                stringResolver.get(R.string.deck_saved_success_message)
            )
        )
    }

    /**
     * Validates the input values in the [DeckDetails] object. This method checks if the name of the
     * deck is not blank.
     *
     * @param uiState The [DeckDetails] object to validate. Defaults to the current [deckUiState].
     *
     * @return `true` if the input values are valid, `false` otherwise.
     */
    private fun validateInput(uiState: DeckDetails = deckUiState.deckDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }

    /**
     * Emits an error event to the UI. This method logs the error and sends a
     * [DeckEntryUiEvent.ShowErrorSnackbar] event to the UI with a user-friendly error message.
     *
     * @param error The [DeckEntryError] to be emitted.
     * @param throwable An optional [Throwable] that caused the error, for logging purposes.
     */
    private fun emitError(
        error: DeckEntryError,
        throwable: Throwable? = null
    ) {
        if (error is DeckEntryError.InvalidSubmission) {
            Log.w(TAG, "Invalid submission: ${deckUiState.deckDetails}")
        } else {
            Log.e(TAG, "Deck entry error: $error", throwable)
        }
        viewModelScope.launch {
            _events.send(
                DeckEntryUiEvent.ShowErrorSnackbar(
                    message = stringResolver.messageFor(error)
                )
            )
        }
    }

    /**
     * Converts a [Throwable] to a [DeckEntryError] that can be emitted to the UI.
     *
     * @return A [DeckEntryError] representing the error.
     */
    private fun Throwable.toDeckEntryError(): DeckEntryError {
        return when (this) {
            is SQLiteException, is IOException -> DeckEntryError.DeckSaveFailed
            else -> DeckEntryError.Unknown(this)
        }
    }

    companion object {
        private const val TAG = "DeckEntryViewModel"
    }
}

/**
 * Represents the UI state for the Deck entry screen.
 *
 * @property deckDetails The details of the deck being created or edited.
 * @property isEntryValid A boolean indicating whether the current input values are valid.
 * @property isLoading A boolean indicating whether a loading operation is in progress. Defaults to
 * `false` and is only used when loading an existing deck for editing.
 * @property hasLoadError A boolean indicating whether there was an error loading the deck for
 * editing. Defaults to `false` and is only used when loading an existing deck for editing.
 */
data class DeckUiState(
    val deckDetails: DeckDetails = DeckDetails(),
    val isEntryValid: Boolean = false,
    val isLoading: Boolean = false,
    val hasLoadError: Boolean = false
)


/**
 * Represents events that can be emitted to the UI from the Deck entry screen.
 */
sealed interface DeckEntryUiEvent {
    /**
     * Represents an event to show an error snackbar with a given message.
     *
     * @property message The message to be displayed in the snackbar.
     */
    data class ShowErrorSnackbar(val message: String): DeckEntryUiEvent

    /**
     * Represents an event to show a snackbar indicating that a deck was successfully saved.
     *
     * @property message The message to be displayed in the snackbar.
     */
    data class ShowDeckSavedSnackbar(val message: String): DeckEntryUiEvent
}