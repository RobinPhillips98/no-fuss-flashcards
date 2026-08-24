package io.github.robinphillips98.flashcards.ui.flashcards

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.github.robinphillips98.flashcards.data.flashcards.Flashcard
import io.github.robinphillips98.flashcards.data.flashcards.FlashcardsRepository
import io.github.robinphillips98.flashcards.ui.utils.saveImageToInternalStorage

class FlashcardEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val flashcardsRepository: FlashcardsRepository
): ViewModel() {

    /**
     * Holds current flashcard ui state
     */
    var flashcardUiState by mutableStateOf(FlashcardUiState())
        private set

    private val deckId: Int = checkNotNull(savedStateHandle[FlashcardEntryDestination.DECK_ID_ARG])

    init {
        flashcardUiState = flashcardUiState.copy(
            flashcardDetails = flashcardUiState.flashcardDetails.copy(deckId = deckId)
        )
    }

    /**
     * Updates the [flashcardUiState] with the value provided in the argument. This method also
     * triggers a validation for input values.
     */
    fun updateUiState(flashcardDetails: FlashcardDetails, imageUri: Uri? = null) {
        flashcardUiState =
            FlashcardUiState(
                flashcardDetails = flashcardDetails,
                imageUri = imageUri ?: flashcardUiState.imageUri,
                isEntryValid = validateInput(flashcardDetails)
            )
    }

    /**
     * Inserts a [Flashcard] in the Room database
     */
    suspend fun saveFlashcard(context: Context) {
        if (validateInput()) {
            val imagePath = if (flashcardUiState.imageUri != null) {
                saveImageToInternalStorage(context, flashcardUiState.imageUri!!)
            } else {
                null
            }
            flashcardsRepository.insertFlashcard(
                flashcardUiState.flashcardDetails.toFlashcard(imagePath)
            )
        }
    }

    private fun validateInput(uiState: FlashcardDetails = flashcardUiState.flashcardDetails): Boolean {
        return with(uiState) {
            term.isNotBlank() && definition.isNotBlank()
        }
    }

}

/**
 * Represents Ui State for a Flashcard.
 */
data class FlashcardUiState(
    val flashcardDetails: FlashcardDetails = FlashcardDetails(),
    val imageUri: Uri? = null,
    val isEntryValid: Boolean = false
)

/**
 * Represents the details of a Flashcard.
 */
data class FlashcardDetails(
    val id: Int = 0,
    val term: String = "",
    val definition: String = "",
    val deckId: Int = 0
)

/**
 * Extension function to convert a [FlashcardDetails] to [Flashcard].
 */
fun FlashcardDetails.toFlashcard(imagePath: String? = null): Flashcard {
    return Flashcard(
        flashcardId = id,
        term = term,
        definition = definition,
        deckId = deckId,
        imagePath = imagePath
    )
}

/**
 * Extension function to convert a [Flashcard] to [FlashcardDetails].
 */
fun Flashcard.toFlashcardDetails(): FlashcardDetails {
    return FlashcardDetails(
        id = flashcardId,
        term = term,
        definition = definition,
        deckId = deckId
    )
}