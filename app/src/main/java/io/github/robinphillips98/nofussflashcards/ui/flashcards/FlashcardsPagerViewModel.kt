package io.github.robinphillips98.nofussflashcards.ui.flashcards

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.UserPreferencesRepository
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.data.flashcards.FlashcardsRepository
import io.github.robinphillips98.nofussflashcards.ui.decks.DeckDetails
import io.github.robinphillips98.nofussflashcards.ui.decks.toDeckDetails
import io.github.robinphillips98.nofussflashcards.ui.errors.FlashcardsPagerError
import io.github.robinphillips98.nofussflashcards.ui.errors.messageFor
import io.github.robinphillips98.nofussflashcards.ui.utils.StringResolver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

class FlashcardsPagerViewModel(
    savedStateHandle: SavedStateHandle,
    private val decksRepository: DecksRepository,
    private val flashcardsRepository: FlashcardsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val stringResolver: StringResolver
): ViewModel() {
    private val deckId: Int = checkNotNull(savedStateHandle[FlashcardsPagerDestination.DECK_ID_ARG]) {
        "Deck ID is required"
    }

    private val selectedFlashcardId: Int? =
        savedStateHandle[FlashcardsPagerDestination.FLASHCARD_ID_ARG]

    private val _events = Channel<FlashcardsPagerUiEvent>(Channel.BUFFERED)

    /**
     * A flow of events that can be observed by the UI to show snackbars or other one-time events.
     */
    val events = _events.receiveAsFlow()

    private val sourceFlashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    private val shuffledFlashcards = MutableStateFlow<List<Flashcard>>(emptyList())

    private val shuffleGeneration = MutableStateFlow(0)

    private val initialSelectedIndex = MutableStateFlow(0)

    private val deckLoadError = MutableStateFlow(false)
    private val flashcardsLoadError = MutableStateFlow(false)

    private val deckReloadTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val flashcardsReloadTrigger = MutableSharedFlow<Unit>(replay = 1)

    /**
     * Emits deck updates and handles deck load errors with retry support.
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
     * Emits flashcards updates and handles flashcards load errors with retry support.
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

    init {
        deckReloadTrigger.tryEmit(Unit)
        flashcardsReloadTrigger.tryEmit(Unit)

        viewModelScope.launch {
            flashcardsFlow.collect { flashcards ->
                sourceFlashcards.value = flashcards

                val shuffled = flashcards.shuffled()
                shuffledFlashcards.value = shuffled

                initialSelectedIndex.value = resolveInitialIndex(shuffled, selectedFlashcardId)
            }
        }
    }

    /**
     * Resolves the initial selected index for the flashcards.
     *
     * @param flashcards The list of flashcards.
     * @param selectedFlashcardId The ID of the selected flashcard, if any.
     *
     * @return The index of the selected flashcard, or 0 if not found.
     */
    private fun resolveInitialIndex(
        flashcards: List<Flashcard>,
        selectedFlashcardId: Int?
    ): Int {
        if (flashcards.isEmpty()) return 0
        if (selectedFlashcardId == null) return 0
        val index = flashcards.indexOfFirst { it.flashcardId == selectedFlashcardId }
        return if (index >= 0) index else 0
    }

    /**
     * Combines the deck and flashcards flows to create a unified UI state for the pager.
     */
    private val pagerContentState = combine(
        deckFlow,
        shuffledFlashcards,
        initialSelectedIndex,
        shuffleGeneration,
        userPreferencesRepository.hasFlippedCard
    ) { deck, flashcards, initialIndex, shuffleGeneration, hasFlippedCard ->
        FlashcardsPagerUiState(
            deckDetails = deck?.toDeckDetails() ?: DeckDetails(),
            flashcards = flashcards,
            initialSelectedIndex = initialIndex,
            shuffleGeneration = shuffleGeneration,
            hasFlippedCard = hasFlippedCard,
            isLoading = false
        )
    }

    /**
     * The UI state for the FlashcardsPager screen, combining content state and error states.
     */
    val uiState: StateFlow<FlashcardsPagerUiState> =
        combine(
            pagerContentState,
            deckLoadError,
            flashcardsLoadError
        ) { contentState, hasDeckLoadError, hasFlashcardsLoadError ->
            contentState.copy(
                hasDeckLoadError = hasDeckLoadError,
                hasFlashcardsLoadError = hasFlashcardsLoadError,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = FlashcardsPagerUiState(isLoading = true)
        )

    /**
     * Reshuffles the flashcards and resets the initial selected index to 0.
     * Increments the shuffle generation to trigger recomposition in the UI.
     */
    fun reshuffleFlashcards() {
        shuffledFlashcards.value = sourceFlashcards.value.shuffled()
        initialSelectedIndex.value = 0
        shuffleGeneration.value++
    }

    /**
     * Updates the user preference indicating whether the user has flipped a card.
     *
     * @param hasFlipped True if the user has flipped a card, false otherwise.
     */
    fun updateHasFlippedCard(hasFlipped: Boolean) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.saveHasFlippedCard(hasFlipped)
            } catch (throwable: Throwable) {
                emitError(FlashcardsPagerError.Unknown(throwable), throwable)
            }
        }
    }

    /**
     * Retries loading the deck.
     */
    fun retryDeckLoad() {
        viewModelScope.launch {
            deckLoadError.value = false
            deckReloadTrigger.emit(Unit)
        }
    }

    /**
     * Retries loading flashcards.
     */
    fun retryFlashcardsLoad() {
        viewModelScope.launch {
            flashcardsLoadError.value = false
            flashcardsReloadTrigger.emit(Unit)
        }
    }

    /** Emits an error event and logs throwable details where available. */
    private fun emitError(
        error: FlashcardsPagerError,
        throwable: Throwable? = null,
        shouldRetryDeck: Boolean = false,
        shouldRetryFlashcards: Boolean = false
    ) {
        Log.e(TAG, "Flashcards pager error: $error", throwable)
        viewModelScope.launch {
            _events.send(
                FlashcardsPagerUiEvent.ShowErrorSnackbar(
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

    private fun Throwable.toDeckLoadError(): FlashcardsPagerError {
        return when (this) {
            is SQLiteException, is IOException -> FlashcardsPagerError.DeckLoadFailed
            else -> FlashcardsPagerError.Unknown(this)
        }
    }

    private fun Throwable.toFlashcardsLoadError(): FlashcardsPagerError {
        return when (this) {
            is SQLiteException, is IOException -> FlashcardsPagerError.FlashcardListLoadFailed
            else -> FlashcardsPagerError.Unknown(this)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        private const val TAG = "FlashcardsPagerViewModel"
    }
}

/**
 * Represents the UI state for the FlashcardsPager screen.
 *
 * @property deckDetails The details of the deck being viewed.
 * @property flashcards The list of flashcards to display.
 * @property initialSelectedIndex The index of the initially selected flashcard.
 * @property shuffleGeneration A counter that increments each time the flashcards are reshuffled.
 * @property hasFlippedCard Indicates whether the user has flipped a card.
 */
data class FlashcardsPagerUiState(
    val deckDetails: DeckDetails = DeckDetails(),
    val flashcards: List<Flashcard> = emptyList(),
    val initialSelectedIndex: Int = 0,
    val shuffleGeneration: Int = 0,
    val hasFlippedCard: Boolean = false,
    val hasDeckLoadError: Boolean = false,
    val hasFlashcardsLoadError: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * Represents UI events for the flashcards pager screen.
 */
sealed interface FlashcardsPagerUiEvent {
    data class ShowErrorSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val shouldRetryDeck: Boolean = false,
        val shouldRetryFlashcards: Boolean = false,
    ) : FlashcardsPagerUiEvent
}
