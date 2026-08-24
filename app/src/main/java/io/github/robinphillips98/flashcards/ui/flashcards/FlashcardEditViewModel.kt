package io.github.robinphillips98.flashcards.ui.flashcards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.flashcards.data.flashcards.Flashcard
import io.github.robinphillips98.flashcards.data.flashcards.FlashcardsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FlashcardEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val flashcardsRepository: FlashcardsRepository
): ViewModel() {
    var flashcardUiState by mutableStateOf(FlashcardUiState())
        private set

    private val flashcardId: Int = checkNotNull(savedStateHandle[FlashcardEditDestination.FLASHCARD_ID_ARG])

    init {
        viewModelScope.launch {
            flashcardUiState = flashcardsRepository.getFlashcardStream(flashcardId)
                .first()
                ?.toFlashcardUiState(true)
                ?: FlashcardUiState(flashcardDetails = FlashcardDetails())
        }
    }

    /**
     * Updates the [Flashcard] in the Room database
     */
    suspend fun updateFlashcard() {
        if (validateInput()) {
            flashcardsRepository.updateFlashcard(flashcardUiState.flashcardDetails.toFlashcard())
        }
    }

    /**
     * Updates the [flashcardUiState] with the value provided in the argument. This method also
     * triggers a validation for input values.
     */
    fun updateUiState(flashcardDetails: FlashcardDetails) {
        flashcardUiState =
            FlashcardUiState(
                flashcardDetails = flashcardDetails,
                isEntryValid = validateInput(flashcardDetails)
            )
    }

    private fun validateInput(uiState: FlashcardDetails = flashcardUiState.flashcardDetails): Boolean {
        return with(uiState) {
            term.isNotBlank() && definition.isNotBlank()
        }
    }
}

private fun Flashcard.toFlashcardUiState(isEntryValid: Boolean = false): FlashcardUiState =
    FlashcardUiState(
        flashcardDetails = this.toFlashcardDetails(),
        isEntryValid = isEntryValid
    )