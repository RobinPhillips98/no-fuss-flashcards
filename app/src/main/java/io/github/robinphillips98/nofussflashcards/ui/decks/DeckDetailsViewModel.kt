package io.github.robinphillips98.nofussflashcards.ui.decks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.data.flashcards.FlashcardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val flashcardsRepository: FlashcardsRepository
): ViewModel() {
    private val deckId: Int = checkNotNull(savedStateHandle[DeckDetailsDestination.DECK_ID_ARG]) {
        "Deck ID is required"
    }

    private val _flashcardToDelete = MutableStateFlow<Flashcard?>(null)
    val flashcardToDelete: StateFlow<Flashcard?> = _flashcardToDelete.asStateFlow()

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
        val flashcards = uiState.value.flashcards
        /*
         Manually delete flashcards to ensure that any associated images are also deleted from
         internal storage
         */
        for (flashcard in flashcards) {
            deleteFlashcard(flashcard)
        }
        decksRepository.deleteDeck(uiState.value.deckDetails.toDeck())
    }

    /**
     * Deletes a flashcard from the [FlashcardsRepository]'s data source.
     */
    suspend fun deleteFlashcard(flashcard: Flashcard) {
        val imagePath = flashcard.imagePath
        if (imagePath != null) {
            // Delete the image from internal storage
            val file = java.io.File(imagePath)
            if (file.exists()) {
                file.delete()
            }
        }
        flashcardsRepository.deleteFlashcard(flashcard)
    }

    /**
     * Sets the flashcard to be deleted.
     */
    fun setFlashcardToDelete(flashcard: Flashcard?) {
        _flashcardToDelete.value = flashcard
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
    val isDeckMissing: Boolean = false,
)