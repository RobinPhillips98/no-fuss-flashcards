package io.github.robinphillips98.nofussflashcards.ui.flashcards.forms

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.data.flashcards.FlashcardsRepository
import io.github.robinphillips98.nofussflashcards.ui.errors.FlashcardEntryError
import io.github.robinphillips98.nofussflashcards.ui.errors.messageFor
import io.github.robinphillips98.nofussflashcards.utils.StringResolver
import io.github.robinphillips98.nofussflashcards.ui.utils.deleteImageFromInternalStorage
import io.github.robinphillips98.nofussflashcards.ui.utils.saveImageToInternalStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException

class FlashcardEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val flashcardsRepository: FlashcardsRepository,
    private val stringResolver: StringResolver
): ViewModel() {

    /**
     * Holds current flashcard ui state
     */
    var flashcardUiState by mutableStateOf(FlashcardUiState())
        private set

    private val _events = Channel<FlashcardEntryUiEvent>(Channel.BUFFERED)

    /**
     * A flow of events that can be observed by the UI to show snackbars or other one-time events.
     */
    val events = _events.receiveAsFlow()

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
        val finalImageUri = imageUri ?: flashcardUiState.selectedImageUri
        flashcardUiState =
            FlashcardUiState(
                flashcardDetails = flashcardDetails,
                selectedImageUri = finalImageUri,
                existingImageUri = flashcardUiState.existingImageUri,
                isEntryValid = validateInput(flashcardDetails, finalImageUri)
            )
    }

    /**
     * Inserts a [Flashcard] in the Room database
     *
     * @return `true` if the flashcard was successfully saved, `false` otherwise
     */
    suspend fun saveFlashcard(context: Context): Boolean {
        if (validateInput()) {
            val imagePath = if (flashcardUiState.selectedImageUri != null) {
                /*
                 Attempt to save image and record the path. If saving the image fails, emit an
                 error and return false early to abort saving flashcard.
                 */
                try {
                    saveImageToInternalStorage(context, flashcardUiState.selectedImageUri!!)
                } catch (throwable: Throwable) {
                    emitError(throwable.toImageSaveError(), throwable)
                    return false
                }
            } else {
                null
            }
            try {
                flashcardsRepository.insertFlashcard(
                    flashcardUiState.flashcardDetails.toFlashcard(imagePath)
                )
                emitSuccessEvent()
                return true
            } catch (throwable: Throwable) {
                if (imagePath != null) {
                    val imageDeletedSuccessfully = deleteImageFromInternalStorage(imagePath)
                    if (!imageDeletedSuccessfully) {
                        Log.e(TAG,"Failed to delete image file after flashcard save failure: $imagePath")
                    }
                }
                emitError(throwable.toFlashcardEntryError(), throwable)
                return false
            }
        } else {
            emitError(FlashcardEntryError.InvalidSubmission)
            return false
        }
    }

    /**
     * Emits a success event to the UI with a user-friendly success message.
     */
    private suspend fun emitSuccessEvent() {
        _events.send(
            FlashcardEntryUiEvent.ShowFlashcardSavedSnackbar(
                stringResolver.get(R.string.flashcard_saved_success_message)
            )
        )
    }

    /**
     * Emits an error event to the UI, logging the error and sending a snackbar message.
     */
    private fun emitError(
        error: FlashcardEntryError,
        throwable: Throwable? = null
    ) {
        if (error is FlashcardEntryError.InvalidSubmission) {
            Log.w(TAG, "Invalid submission: ${flashcardUiState.flashcardDetails}")
        } else {
            Log.e(TAG, "Flashcard entry error: $error", throwable)
        }
        viewModelScope.launch {
            _events.send(
                FlashcardEntryUiEvent.ShowErrorSnackbar(
                    message = stringResolver.messageFor(error)
                )
            )
        }
    }

    /**
     * Converts a [Throwable] to a [FlashcardEntryError] that can be emitted to the UI.
     */
    private fun Throwable.toFlashcardEntryError(): FlashcardEntryError {
        return when (this) {
            is SQLiteException, is IOException -> FlashcardEntryError.FlashcardSaveFailed
            else -> FlashcardEntryError.Unknown(this)
        }
    }

    private fun Throwable.toImageSaveError(): FlashcardEntryError {
        return when (this) {
            is IOException -> FlashcardEntryError.ImageSaveFailed
            else -> FlashcardEntryError.Unknown(this)
        }
    }

    private fun validateInput(
        uiState: FlashcardDetails = flashcardUiState.flashcardDetails,
        imageUri: Uri? = flashcardUiState.selectedImageUri
    ): Boolean {
        return with(uiState) {
            term.isNotBlank() &&
            (!definition.isNullOrBlank() || imageUri != null)
        }
    }

    companion object {
        private const val TAG = "FlashcardEntryViewModel"
    }

}

/**
 * Represents events that can be emitted to the UI from the flashcard entry screen.
 */
sealed interface FlashcardEntryUiEvent {
    /**
     * Represents an event to show an error snackbar with a given message.
     */
    data class ShowErrorSnackbar(val message: String): FlashcardEntryUiEvent

    /**
     * Represents an event to show a snackbar indicating that a flashcard was successfully saved.
     */
    data class ShowFlashcardSavedSnackbar(val message: String): FlashcardEntryUiEvent
}

/**
 * Represents Ui State for a Flashcard.
 */
data class FlashcardUiState(
    val flashcardDetails: FlashcardDetails = FlashcardDetails(),
    val selectedImageUri: Uri? = null,
    val existingImageUri: Uri? = null,
    val isEntryValid: Boolean = false,
    val isLoading: Boolean = false,
    val hasLoadError: Boolean = false
)

/**
 * Represents the details of a Flashcard.
 */
data class FlashcardDetails(
    val id: Int = 0,
    val term: String = "",
    val definition: String? = "",
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