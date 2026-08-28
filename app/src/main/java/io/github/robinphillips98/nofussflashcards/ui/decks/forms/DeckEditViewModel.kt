package io.github.robinphillips98.nofussflashcards.ui.decks.forms

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import io.github.robinphillips98.nofussflashcards.ui.decks.DeckDetails
import io.github.robinphillips98.nofussflashcards.ui.decks.toDeck
import io.github.robinphillips98.nofussflashcards.ui.decks.toDeckDetails
import io.github.robinphillips98.nofussflashcards.ui.errors.DeckEditError
import io.github.robinphillips98.nofussflashcards.ui.errors.messageFor
import io.github.robinphillips98.nofussflashcards.utils.StringResolver
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException

class DeckEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val decksRepository: DecksRepository,
    private val stringResolver: StringResolver
): ViewModel() {

    /**
     * Holds current deck ui state
     */
    var deckUiState by mutableStateOf(DeckUiState())
        private set

    private val deckId: Int = checkNotNull(savedStateHandle[DeckEditDestination.DECK_ID_ARG]) {
        "Deck ID is required"
    }

    private val _events = Channel<DeckEditUiEvent>(Channel.BUFFERED)

    /**
     * A flow of events that can be observed by the UI to show snackbars or other one-time events.
     */
    val events = _events.receiveAsFlow()

    init {
        loadDeck()
    }

    /**
     * Loads the deck from the [DecksRepository] and updates the [deckUiState]. If an error occurs
     * during loading, it emits an error event to the UI.
     */
    fun loadDeck() {
        viewModelScope.launch {
            deckUiState = deckUiState.copy(isLoading = true, hasLoadError = false)
            deckUiState = try {
                decksRepository.getDeckStream(deckId)
                    .filterNotNull()
                    .first()
                    .toDeckUiState()
            } catch (throwable: Throwable) {
                emitError(
                    throwable.toDeckLoadError(),
                    throwable,
                    shouldRetryLoad = true
                )
                deckUiState.copy(hasLoadError = true)
            }
        }
    }

    /**
     * Update the deck in the [DecksRepository]'s data source
     *
     * @return `true` if the deck was updated successfully, `false` otherwise
     */
    suspend fun updateDeck(): Boolean {
        if (validateInput()) {
            try {
                decksRepository.updateDeck(deckUiState.deckDetails.toDeck())
                emitSuccessEvent()
                return true
            } catch (throwable: Throwable) {
                emitError(throwable.toDeckSaveError(), throwable)
                return false
            }
        } else {
            emitError(DeckEditError.InvalidSubmission)
            return false
        }
    }

    /**
     * Updates the [deckUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(deckDetails: DeckDetails) {
        deckUiState =
            DeckUiState(deckDetails = deckDetails, isEntryValid = validateInput(deckDetails))
    }

    private fun validateInput(uiState: DeckDetails = deckUiState.deckDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }

    /**
     * Emits a success event to the UI. This method sends a [DeckEditUiEvent.ShowDeckSavedSnackbar]
     * event to the UI with a user-friendly success message.
     */
    private suspend fun emitSuccessEvent() {
        _events.send(DeckEditUiEvent.ShowDeckSavedSnackbar(
            stringResolver.get(R.string.deck_saved_success_message)
        ))
    }

    /**
     * Emits an error event to the UI, logging the error and sending a snackbar message.
     *
     * @param error The DeckEditError to emit.
     * @param throwable An optional Throwable associated with the error.
     */
    private fun emitError(
        error: DeckEditError,
        throwable: Throwable? = null,
        shouldRetryLoad: Boolean = false
    ) {
        if (error is DeckEditError.InvalidSubmission) {
          Log.w(TAG, "Invalid submission: ${deckUiState.deckDetails}")
        } else {
            Log.e(TAG, "Deck edit error: $error", throwable)
        }
        viewModelScope.launch {
            _events.send(
                DeckEditUiEvent.ShowErrorSnackbar(
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
     * Converts a Throwable to a DeckEditError related to saving the deck.
     *
     * @return A DeckEditError representing the error.
     */
    private fun Throwable.toDeckSaveError(): DeckEditError {
        return when (this) {
            is IOException, is SQLiteException -> DeckEditError.DeckSaveFailed
            else -> DeckEditError.Unknown(this)
        }
    }

    /**
     * Converts a Throwable to a DeckEditError related to loading the deck.
     *
     * @return A DeckEditError representing the error.
     */
    private fun Throwable.toDeckLoadError(): DeckEditError {
        return when (this) {
            is IOException, is SQLiteException -> DeckEditError.DeckLoadFailed
            else -> DeckEditError.Unknown(this)
        }
    }

    companion object {
        private const val TAG = "DeckEditViewModel"
    }
}

/**
 * Converts a [Deck] to a [DeckUiState].
 *
 * @return A [DeckUiState] representing the current state of the deck.
 */
private fun Deck.toDeckUiState(): DeckUiState =
    DeckUiState(
        deckDetails = this.toDeckDetails(),
        isEntryValid = true,
        isLoading = false,
        hasLoadError = false
    )

/**
 * Represents events that can be emitted to the UI from the Deck edit screen.
 */
sealed interface DeckEditUiEvent {
    /**
     * Represents an event to show an error snackbar with a given message.
     *
     * @property message The message to be displayed in the snackbar.
     * @property actionLabel An optional label for the action button in the snackbar.
     * @property shouldRetryLoad Indicates whether the snackbar should provide an option to retry loading the deck.
     */
    data class ShowErrorSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val shouldRetryLoad: Boolean = false
    ) : DeckEditUiEvent

    /**
     * Represents an event to show a snackbar indicating that a deck was successfully saved.
     *
     * @property message The message to be displayed in the snackbar.
     */
    data class ShowDeckSavedSnackbar(val message: String):  DeckEditUiEvent
}
