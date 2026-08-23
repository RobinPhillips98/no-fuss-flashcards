package io.github.robinphillips98.flashcards.ui.flashcards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.flashcards.data.decks.DecksRepository
import io.github.robinphillips98.flashcards.data.flashcards.Flashcard
import io.github.robinphillips98.flashcards.data.flashcards.FlashcardsRepository
import io.github.robinphillips98.flashcards.ui.decks.DeckDetails
import io.github.robinphillips98.flashcards.ui.decks.toDeckDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FlashcardsPagerViewModel(
    savedStateHandle: SavedStateHandle,
    decksRepository: DecksRepository,
    flashcardsRepository: FlashcardsRepository
): ViewModel() {
    private val deckId: Int = checkNotNull(savedStateHandle[FlashcardsPagerDestination.DECK_ID_ARG]) {
        "Deck ID is required"
    }

    private val selectedFlashcardId: Int? =
        savedStateHandle[FlashcardsPagerDestination.FLASHCARD_ID_ARG]

    private fun resolveInitialIndex(
        flashcards: List<Flashcard>,
        selectedFlashcardId: Int?
    ): Int {
        if (flashcards.isEmpty()) return 0
        if (selectedFlashcardId == null) return 0
        val index = flashcards.indexOfFirst { it.flashcardId == selectedFlashcardId }
        return if (index >= 0) index else 0
    }

    val uiState: StateFlow<FlashcardsPagerUiState> =
        combine(
            decksRepository.getDeckStream(deckId),
            flashcardsRepository.getFlashcardsByDeckIdStream(deckId)
        ) { deck, flashcards ->
            FlashcardsPagerUiState(
                deckDetails = deck?.toDeckDetails() ?: DeckDetails(),
                flashcards = flashcards,
                selectedFlashcardId = selectedFlashcardId,
                initialSelectedIndex = resolveInitialIndex(flashcards, selectedFlashcardId)
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FlashcardsPagerUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}


data class FlashcardsPagerUiState(
    val deckDetails: DeckDetails = DeckDetails(),
    val flashcards: List<Flashcard> = emptyList(),
    val selectedFlashcardId: Int? = null,
    val initialSelectedIndex: Int = 0
)