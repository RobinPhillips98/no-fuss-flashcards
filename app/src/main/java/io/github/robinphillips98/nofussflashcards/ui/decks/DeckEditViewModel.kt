package io.github.robinphillips98.nofussflashcards.ui.decks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DeckEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val decksRepository: DecksRepository
): ViewModel() {

    /**
     * Holds current deck ui state
     */
    var deckUiState by mutableStateOf(DeckUiState())
        private set

    private val deckId: Int = checkNotNull(savedStateHandle[DeckEditDestination.DECK_ID_ARG]) {
        "Deck ID is required"
    }

    init {
        viewModelScope.launch {
            deckUiState = decksRepository.getDeckStream(deckId)
                .filterNotNull()
                .first()
                .toDeckUiState(true)
        }
    }

    /**
     * Update the deck in the [DecksRepository]'s data source
     */
    suspend fun updateDeck() {
        if (validateInput(deckUiState.deckDetails)) {
            decksRepository.updateDeck(deckUiState.deckDetails.toDeck())
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
}

private fun Deck.toDeckUiState(isEntryValid: Boolean = false): DeckUiState = DeckUiState(
    deckDetails = this.toDeckDetails(),
    isEntryValid = isEntryValid
)