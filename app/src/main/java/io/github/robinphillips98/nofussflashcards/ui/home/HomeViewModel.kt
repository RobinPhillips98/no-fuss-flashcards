package io.github.robinphillips98.nofussflashcards.ui.home

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.UserPreferencesRepository
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import io.github.robinphillips98.nofussflashcards.ui.errors.HomeError
import io.github.robinphillips98.nofussflashcards.ui.errors.messageFor
import io.github.robinphillips98.nofussflashcards.ui.utils.StringResolver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest

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

    private val _events = Channel<HomeUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _decksLoadError = MutableStateFlow(false)
    private val _lastOpenedDeckLoadError = MutableStateFlow(false)

    private val decksReloadTrigger = MutableSharedFlow<Unit>(replay = 1)

    init {
        decksReloadTrigger.tryEmit(Unit) // initial load
    }

    /**
     * Flow that emits the list of decks from the repository.
     * It handles loading errors and emits an empty list in case of failure.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val decksFlow = decksReloadTrigger
        .flatMapLatest {
            decksRepository.getAllDecksStream()
                .onStart {
                    _decksLoadError.value = false
                    emit(emptyList())
                }
                .onEach {
                    _decksLoadError.value = false
                }
                .catch { throwable ->
                    val error = throwable.toDecksLoadError()
                    _decksLoadError.value = true
                    emitError(error, throwable, shouldRetryDecks = true)
                    emit(emptyList())
                }
        }

    /**
     * Flow that emits the last opened deck ID from the user preferences repository.
     * It handles loading errors and emits null in case of failure.
     */
    private val lastOpenedDeckIdFlow = userPreferencesRepository.lastOpenedDeckId
        .onStart {
            _lastOpenedDeckLoadError.value = false
            emit(null)
        }
        .onEach {
            _lastOpenedDeckLoadError.value = false
        }
        .catch { throwable ->
            val error = throwable.toLastOpenedDeckReadError()
            _lastOpenedDeckLoadError.value = true
            emitError(error, throwable)
            emit(null)
        }

    /**
     * StateFlow that combines the decks flow, last opened deck ID flow, and error states
     * to produce a HomeUiState representing the current state of the Home screen.
     */
    val homeUiState: StateFlow<HomeUiState> =
        combine(
            decksFlow,
            lastOpenedDeckIdFlow,
            _decksLoadError,
            _lastOpenedDeckLoadError
        ) { deckList, lastOpenedDeckId, decksError, lastOpenedDeckError ->
            HomeUiState(
                deckList = deckList,
                lastOpenedDeckId = lastOpenedDeckId,
                hasDecksLoadError = decksError,
                hasLastOpenedDeckLoadError = lastOpenedDeckError,
                isLoading = false
            )
        }.stateIn(
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
     * Retries loading the decks by emitting a trigger to reload the decks flow.
     * Resets the decks load error state before retrying.
     */
    fun retryDecksLoad() {
        viewModelScope.launch {
            _decksLoadError.value = false
            decksReloadTrigger.emit(Unit)
        }
    }

    /**
     * Emits an error event to the UI, logging the error and sending a snackbar message.
     *
     * @param error The HomeError to emit.
     * @param throwable An optional Throwable associated with the error.
     * @param shouldRetryDecks Whether the snackbar should include a retry action for loading decks.
     */
    private fun emitError(
        error: HomeError,
        throwable: Throwable? = null,
        shouldRetryDecks: Boolean = false
    ) {
        Log.e(TAG, "Home error: $error", throwable)
        viewModelScope.launch {
            _events.send(
                HomeUiEvent.ShowSnackbar(
                    message = stringResolver.messageFor(error),
                    actionLabel =
                        if (shouldRetryDecks) stringResolver.get(R.string.retry_button)
                        else null,
                    shouldRetryDecks = shouldRetryDecks
                )
            )
        }
    }

    /**
     * Converts a Throwable to a HomeError related to loading decks.
     *
     * @return A HomeError representing the error.
     */
    private fun Throwable.toDecksLoadError(): HomeError =
        when (this) {
            is SQLiteException, is IOException -> HomeError.DecksLoadFailed
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
 * @property hasDecksLoadError Whether there was an error loading the decks.
 * @property hasLastOpenedDeckLoadError Whether there was an error loading the last opened deck.
 */
data class HomeUiState(
    val deckList: List<Deck> = emptyList(),
    val lastOpenedDeckId: Int? = null,
    val isLoading: Boolean = false,
    val hasDecksLoadError: Boolean = false,
    val hasLastOpenedDeckLoadError: Boolean = false,
)

/**
 * Represents UI events for the Home screen.
 */
sealed interface HomeUiEvent {
    /**
     * Event to show a snackbar message in the UI.
     *
     * @property message The message to display in the snackbar.
     * @property actionLabel An optional label for an action button in the snackbar.
     * @property shouldRetryDecks Whether the snackbar should include a retry action for loading decks.
     */
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val shouldRetryDecks: Boolean = false
    ): HomeUiEvent
}