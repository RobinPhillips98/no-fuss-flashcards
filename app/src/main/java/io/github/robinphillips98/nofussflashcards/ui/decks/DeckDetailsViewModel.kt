package io.github.robinphillips98.nofussflashcards.ui.decks

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.data.flashcards.FlashcardsRepository
import io.github.robinphillips98.nofussflashcards.ui.errors.DeckDetailsError
import io.github.robinphillips98.nofussflashcards.ui.errors.messageFor
import io.github.robinphillips98.nofussflashcards.ui.utils.StringResolver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ViewModel to manage the deck details screen.
 *
 * It retrieves the deck details and flashcards from the repositories and exposes them as a
 * [StateFlow] of [DeckDetailsUiState].
 */
class DeckDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val decksRepository: DecksRepository,
    private val flashcardsRepository: FlashcardsRepository,
    private val stringResolver: StringResolver
): ViewModel() {
    private val deckId: Int = checkNotNull(savedStateHandle[DeckDetailsDestination.DECK_ID_ARG]) {
        "Deck ID is required"
    }

    private val _events = Channel<DeckDetailsUiEvent>(Channel.BUFFERED)

    /**
     * A [Flow] of [DeckDetailsUiEvent] that emits events to be observed by the UI.
     */
    val events = _events.receiveAsFlow()

    private val deckLoadError = MutableStateFlow(false)
    private val flashcardsLoadError = MutableStateFlow(false)

    private val _flashcardToDelete = MutableStateFlow<Flashcard?>(null)

    /**
     * A [StateFlow] that holds the flashcard to be deleted.
     */
    val flashcardToDelete: StateFlow<Flashcard?> = _flashcardToDelete.asStateFlow()

    private val deckReloadTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val flashcardsReloadTrigger = MutableSharedFlow<Unit>(replay = 1)

    init {
        deckReloadTrigger.tryEmit(Unit)
        flashcardsReloadTrigger.tryEmit(Unit)
    }

    /**
     * [Flow] that emits the deck details from the repository.
     * It handles loading errors and emits null in case of failure.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val deckFlow = deckReloadTrigger
        .flatMapLatest {
            decksRepository.getDeckStream(deckId)
                .onStart {
                    deckLoadError.value = false
                    emit(null)
                }
                .onEach { deckLoadError.value = false }
                .catch { throwable ->
                    val error = throwable.toDeckLoadError()
                    deckLoadError.value = true
                    emitError(error, throwable, shouldRetryDeck = true)
                    emit(null)
                }
        }

    /**
     * [Flow] that emits the list of flashcards for the deck from the repository.
     * It handles loading errors and emits an empty list in case of failure.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val flashcardsFlow = flashcardsReloadTrigger
        .flatMapLatest {
            flashcardsRepository.getFlashcardsByDeckIdStream(deckId)
                .onStart {
                    flashcardsLoadError.value = false
                    emit(emptyList())
                }
                .onEach { flashcardsLoadError.value = false }
                .catch { throwable ->
                    val error = throwable.toFlashcardsLoadError()
                    flashcardsLoadError.value = true
                    emitError(error, throwable, shouldRetryFlashcards = true)
                    emit(emptyList())
                }
        }

    /**
     * [StateFlow] that combines the deck flow, flashcards flow, and error states
     * to produce a [DeckDetailsUiState] representing the current state of the deck details screen.
     */
    val uiState: StateFlow<DeckDetailsUiState> =
        combine(
            deckFlow,
            flashcardsFlow,
            deckLoadError,
            flashcardsLoadError
        ) { deck, flashcards, deckLoadError, flashcardsLoadError ->
            DeckDetailsUiState(
                deckDetails = deck?.toDeckDetails() ?: DeckDetails(),
                flashcards = flashcards,
                hasDeckLoadError = deckLoadError,
                hasFlashcardsLoadError = flashcardsLoadError,
                isLoaded = false
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DeckDetailsUiState()
            )

    /**
     * Deletes the deck from the [DecksRepository]'s data source.
     *
     * @return `true` if the deck was successfully deleted, `false` otherwise.
     */
    suspend fun deleteDeck(): Boolean {
        val flashcards = uiState.value.flashcards
        /*
         Manually delete flashcards to ensure that any associated images are also deleted from
         internal storage
         */
        return try {
            for (flashcard in flashcards) {
                deleteFlashcard(flashcard)
            }

            decksRepository.deleteDeck(uiState.value.deckDetails.toDeck())
            _events.send(
                DeckDetailsUiEvent.ShowDeletionSnackbar(
                    stringResolver.get(R.string.deck_deletion_success_message)
                )
            )
            true
        } catch (e: Exception) {
            emitError(DeckDetailsError.DeckDeleteFailed, e)
            false
        }
    }

    /**
     * Deletes a flashcard from the [FlashcardsRepository]'s data source.
     */
    suspend fun deleteFlashcard(flashcard: Flashcard) {
        val imagePath = flashcard.imagePath
        if (imagePath != null) {
            // Delete the image from internal storage
            try {
                val file = java.io.File(imagePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                emitError(DeckDetailsError.ImageDeleteFailed, e)
                return
            }
        }
        try {
            flashcardsRepository.deleteFlashcard(flashcard)
            _events.send(
                DeckDetailsUiEvent.ShowDeletionSnackbar(
                    stringResolver.get(R.string.flashcard_deletion_success_message)
                )
            )
        } catch (e: Exception) {
            emitError(DeckDetailsError.FlashcardDeleteFailed, e)
        }
    }

    /**
     * Sets the flashcard to be deleted.
     */
    fun setFlashcardToDelete(flashcard: Flashcard?) {
        _flashcardToDelete.value = flashcard
    }

    /**
     * Retries loading the deck details by resetting the deck load error state.
     */
    fun retryDeckLoad() {
        viewModelScope.launch {
            deckLoadError.value = false
            deckReloadTrigger.emit(Unit)
        }
    }

    /**
     * Retries loading the flashcards by resetting the flashcards load error state.
     */
    fun retryFlashcardsLoad() {
        viewModelScope.launch {
            flashcardsLoadError.value = false
            flashcardsReloadTrigger.emit(Unit)
        }
    }

    /**
     * Emits an error event to the UI, logging the error and sending a snackbar message.
     *
     * @param error The [DeckDetailsError] to emit.
     * @param throwable An optional [Throwable] associated with the error.
     * @param shouldRetryDeck Whether the snackbar should include a retry action for loading the deck.
     * @param shouldRetryFlashcards Whether the snackbar should include a retry action for loading the flashcards.
     */
    private fun emitError(
        error: DeckDetailsError,
        throwable: Throwable? = null,
        shouldRetryDeck: Boolean = false,
        shouldRetryFlashcards: Boolean = false,
    ) {
        Log.e(TAG, "Deck details error: $error", throwable)
        viewModelScope.launch {
            _events.send(
                DeckDetailsUiEvent.ShowErrorSnackbar(
                    message = stringResolver.messageFor(error),
                    actionLabel =
                        if (shouldRetryDeck || shouldRetryFlashcards) stringResolver.get(R.string.retry_button)
                        else null,
                    shouldRetryDeck = shouldRetryDeck,
                    shouldRetryFlashcards = shouldRetryFlashcards
                )
            )
        }
    }

    /**
     * Converts a [Throwable] to a [DeckDetailsError] related to loading deck details.
     *
     * @return A [DeckDetailsError] representing the error.
     */
    private fun Throwable.toDeckLoadError(): DeckDetailsError {
        return when (this) {
            is IOException -> DeckDetailsError.DeckLoadFailed
            else -> DeckDetailsError.Unknown(this)
        }
    }

    /**
     * Converts a [Throwable] to a [DeckDetailsError] related to loading flashcards.
     *
     * @return A [DeckDetailsError] representing the error.
     */
    private fun Throwable.toFlashcardsLoadError(): DeckDetailsError {
        return when (this) {
            is IOException -> DeckDetailsError.FlashcardListLoadFailed
            else -> DeckDetailsError.Unknown(this)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        private const val TAG = "DeckDetailsViewModel"
    }
}

/**
 * UiState for [DeckDetailsScreen]
 *
 * @property deckDetails The details of the deck.
 * @property flashcards The list of flashcards in the deck.
 * @property hasDeckLoadError Whether there was an error loading the deck.
 * @property hasFlashcardsLoadError Whether there was an error loading the flashcards.
 * @property isLoaded Whether the deck and flashcards have been successfully loaded.
 */
data class DeckDetailsUiState(
    val deckDetails: DeckDetails = DeckDetails(),
    val flashcards: List<Flashcard> = emptyList(),
    val hasDeckLoadError: Boolean = false,
    val hasFlashcardsLoadError: Boolean = false,
    val isLoaded: Boolean = false
)

/**
 * Represents UI events for the deck details screen.
 */
sealed interface DeckDetailsUiEvent {
    data class ShowErrorSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val shouldRetryDeck: Boolean = false,
        val shouldRetryFlashcards: Boolean = false,
    ) : DeckDetailsUiEvent
    data class ShowDeletionSnackbar(val message: String) : DeckDetailsUiEvent
}