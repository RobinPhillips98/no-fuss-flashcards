package io.github.robinphillips98.flashcards.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.flashcards.data.UserPreferencesRepository
import io.github.robinphillips98.flashcards.data.decks.Deck
import io.github.robinphillips98.flashcards.data.decks.DecksRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    decksRepository: DecksRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    /**
     * Holds home ui state. The list of decks are retrieved from [DecksRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> =
        combine(
            decksRepository.getAllDecksStream(),
            userPreferencesRepository.lastOpenedDeckId,
        ) { deckList, lastOpenedDeckId ->
            HomeUiState(deckList, lastOpenedDeckId)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    fun updateLastOpenedDeckId(deckId: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveLastOpenedDeckId(deckId)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class HomeUiState(
    val deckList: List<Deck> = listOf(),
    val lastOpenedDeckId: Int? = null,
)