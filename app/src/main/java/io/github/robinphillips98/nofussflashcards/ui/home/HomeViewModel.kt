package io.github.robinphillips98.nofussflashcards.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.data.UserPreferencesRepository
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import io.github.robinphillips98.nofussflashcards.ui.errors.HomeError
import io.github.robinphillips98.nofussflashcards.ui.errors.messageFor
import io.github.robinphillips98.nofussflashcards.ui.utils.StringResolver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel for the Home screen of the app.
 *
 * @param decksRepository Repository for managing decks.
 * @property userPreferencesRepository Repository for managing user preferences.
 * @property stringResolver Resolver for retrieving localized strings.
 */
class HomeViewModel(
    decksRepository: DecksRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val stringResolver: StringResolver,
): ViewModel() {

    private val _events = MutableSharedFlow<HomeUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeUiEvent> = _events

    private val _decksLoadError = MutableStateFlow(false)
    private val _lastOpenedDeckLoadError = MutableStateFlow(false)

    /**
     * Holds home ui state. The list of decks are retrieved from [DecksRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> =
        // Combine the flows for decks, last opened deck id, and error states into a single flow of HomeUiState
        combine(
            // Get the list of decks from the repository and handle errors
            decksRepository.getAllDecksStream()
                .onStart { _decksLoadError.value = false }
                .onEach { _decksLoadError.value = false }
                .retryWhen { throwable, _ ->
                    val error = throwable.toDecksLoadError()
                    _decksLoadError.value = true
                    emitError(error, throwable)
                    delay(1_000.milliseconds)
                    true
                },
            // Get the last opened deck id from the user preferences repository and handle errors
            userPreferencesRepository.lastOpenedDeckId
                .onStart { _lastOpenedDeckLoadError.value = false }
                .onEach { _lastOpenedDeckLoadError.value = false }
                .retryWhen { throwable, _ ->
                    val error = throwable.toLastOpenedDeckReadError()
                    _lastOpenedDeckLoadError.value = true
                    emitError(error, throwable)
                    delay(1_000.milliseconds)
                    true
                },
            _decksLoadError,
            _lastOpenedDeckLoadError
        ) { deckList, lastOpenedDeckId, decksError, lastOpenedDeckError ->
            HomeUiState(
                deckList = deckList,
                lastOpenedDeckId = lastOpenedDeckId,
                hasLoadError = decksError || lastOpenedDeckError,
                isLoading = false
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState(isLoading = true)
            )

    /**
     * Updates the last opened deck ID in the user preferences repository.
     *
     * @param deckId The ID of the deck that was last opened.
     */
    fun updateLastOpenedDeckId(deckId: Int) {
        viewModelScope.launch {
            runCatching {
                userPreferencesRepository.saveLastOpenedDeckId(deckId)
            }
                .onFailure { throwable ->
                    emitError(HomeError.LastOpenedDeckWriteFailed, throwable)
                }
        }
    }

    /**
     * Emits a HomeError event to be observed by the UI.
     *
     * @param error The HomeError to emit.
     * @param throwable An optional Throwable associated with the error.
     */
    private fun emitError(error: HomeError, throwable: Throwable? = null) {
        Log.e(TAG, "Home error: $error", throwable)
        viewModelScope.launch {
            _events.emit(HomeUiEvent.ShowSnackbar(stringResolver.messageFor(error)))
        }
    }

    /**
     * Converts a Throwable to a HomeError related to loading decks.
     *
     * @return A HomeError representing the error.
     */
    private fun Throwable.toDecksLoadError(): HomeError =
        when (this) {
            is IOException -> HomeError.DecksLoadFailed
            else -> HomeError.Unknown(this)
        }

    /**
     * Converts a Throwable to a HomeError related to reading the last opened deck.
     *
     * @return A HomeError representing the error.
     */
    private fun Throwable.toLastOpenedDeckReadError(): HomeError =
        when (this) {
            is IOException -> HomeError.LastOpenedDeckReadFailed
            else -> HomeError.Unknown(this)
        }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        private const val TAG = "HomeViewModel"
    }

}

/**
 * Represents the UI state for the Home screen.
 *
 * @property deckList The list of decks to display.
 * @property lastOpenedDeckId The ID of the last opened deck, if any.
 * @property isLoading Whether the data is currently being loaded.
 * @property hasLoadError Whether there was an error loading the data.
 */
data class HomeUiState(
    val deckList: List<Deck> = emptyList(),
    val lastOpenedDeckId: Int? = null,
    val isLoading: Boolean = false,
    val hasLoadError: Boolean = false,
)

/**
 * Represents UI events for the Home screen.
 */
sealed interface HomeUiEvent {
    data class ShowSnackbar(val message: String): HomeUiEvent
}