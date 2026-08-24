package io.github.robinphillips98.nofussflashcards.ui.decks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository

/**
 * ViewModel to validate and manage the state of the Deck entry screen.
 */
class DeckEntryViewModel(private val decksRepository: DecksRepository): ViewModel() {
    var deckUiState by mutableStateOf(DeckUiState())
        private set

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
     */
    suspend fun saveDeck() {
        if (validateInput()) {
            decksRepository.insertDeck(deckUiState.deckDetails.toDeck())
        }
    }

    private fun validateInput(uiState: DeckDetails = deckUiState.deckDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }
}

data class DeckUiState(
    val deckDetails: DeckDetails = DeckDetails(),
    val isEntryValid: Boolean = false
)