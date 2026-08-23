package io.github.robinphillips98.flashcards.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.flashcards.data.decks.Deck
import io.github.robinphillips98.flashcards.data.decks.DecksRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(decksRepository: DecksRepository): ViewModel() {

    /**
     * Holds home ui state. The list of decks are retrieved from [DecksRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> =
        decksRepository.getAllDecksStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class HomeUiState(val deckList: List<Deck> = listOf())