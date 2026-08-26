package io.github.robinphillips98.nofussflashcards.ui.flashcards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.data.UserPreferencesRepository
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.data.flashcards.FlashcardsRepository
import io.github.robinphillips98.nofussflashcards.ui.decks.DeckDetails
import io.github.robinphillips98.nofussflashcards.ui.decks.toDeckDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlashcardsPagerViewModel(
    savedStateHandle: SavedStateHandle,
    decksRepository: DecksRepository,
    flashcardsRepository: FlashcardsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    private val deckId: Int = checkNotNull(savedStateHandle[FlashcardsPagerDestination.DECK_ID_ARG]) {
        "Deck ID is required"
    }

    private val selectedFlashcardId: Int? =
        savedStateHandle[FlashcardsPagerDestination.FLASHCARD_ID_ARG]

    private val sourceFlashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    private val shuffledFlashcards = MutableStateFlow<List<Flashcard>>(emptyList())

    private val shuffleGeneration = MutableStateFlow(0)

    private val initialSelectedIndex = MutableStateFlow(0)

    /**
     * Initializes the ViewModel by collecting flashcards for the specified deck ID.
     * Updates the source and shuffled flashcards, and resolves the initial selected index.
     */
    init {
        viewModelScope.launch {
            flashcardsRepository.getFlashcardsByDeckIdStream(deckId).collect { flashcards ->
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
     * Represents the UI state for the FlashcardsPager screen.
     * Combines data from the decks repository, shuffled flashcards, initial selected index,
     * shuffle generation, and user preferences to create a unified state flow.
     */
    val uiState: StateFlow<FlashcardsPagerUiState> =
        combine(
            decksRepository.getDeckStream(deckId),
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
                hasFlippedCard = hasFlippedCard
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = FlashcardsPagerUiState()
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
            userPreferencesRepository.saveHasFlippedCard(hasFlipped)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
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
    val hasFlippedCard: Boolean = false
)