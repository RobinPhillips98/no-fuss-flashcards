package io.github.robinphillips98.flashcards.ui.decks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.flashcards.data.decks.DecksRepository
import io.github.robinphillips98.flashcards.data.flashcards.Flashcard
import io.github.robinphillips98.flashcards.data.flashcards.FlashcardsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to manage the deck details screen.
 *
 * It retrieves the deck details and flashcards from the repositories and exposes them as a
 * [StateFlow] of [DeckDetailsUiState].
 */
class DeckDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val decksRepository: DecksRepository,
    flashcardsRepository: FlashcardsRepository
): ViewModel() {
    private val deckId: Int = checkNotNull(savedStateHandle[DeckDetailsDestination.DECK_ID_ARG]) {
        "Deck ID is required"
    }

    val uiState: StateFlow<DeckDetailsUiState> =
        combine(
            decksRepository.getDeckStream(deckId),
            flashcardsRepository.getFlashcardsByDeckIdStream(deckId)
        ) { deck, flashcards ->
            if (deck == null) {
                DeckDetailsUiState(isDeckMissing = true)
            } else {
                DeckDetailsUiState(
                    deckDetails = deck.toDeckDetails(),
                    flashcards = flashcards
                )
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DeckDetailsUiState()
            )

    /**
     * Deletes the deck from the [DecksRepository]'s data source.
     */
    suspend fun deleteDeck() {
        decksRepository.deleteDeck(uiState.value.deckDetails.toDeck())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UiState for [DeckDetailsScreen]
 */
data class DeckDetailsUiState(
    val deckDetails: DeckDetails = DeckDetails(),
    val flashcards: List<Flashcard> = emptyList(),
    val isDeckMissing: Boolean = false
)