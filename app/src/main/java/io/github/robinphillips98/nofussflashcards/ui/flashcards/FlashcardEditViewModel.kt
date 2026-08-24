package io.github.robinphillips98.nofussflashcards.ui.flashcards

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.decks.DecksRepository
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.data.flashcards.FlashcardsRepository
import io.github.robinphillips98.nofussflashcards.ui.utils.saveImageToInternalStorage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlashcardEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val flashcardsRepository: FlashcardsRepository,
    decksRepository: DecksRepository
): ViewModel() {
    var flashcardUiState by mutableStateOf(FlashcardUiState())
        private set

    private val flashcardId: Int =
        checkNotNull(savedStateHandle[FlashcardEditDestination.FLASHCARD_ID_ARG])

    // Holds the original image URI loaded from the database. Never changes after init, so
    // it can always be used to restore the image and to get the path for deletion on save.
    private var originalImageUri: Uri? = null

    // Exposes whether the flashcard originally had an image, so the screen can decide
    // whether to show the Restore button.
    val hasOriginalImage: Boolean get() = originalImageUri != null

    val availableDecks: StateFlow<List<Deck>> =
        decksRepository.getAllDecksStream().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    init {
        viewModelScope.launch {
            val loadedFlashcardUiState = flashcardsRepository.getFlashcardStream(flashcardId)
                .first()
                ?.toFlashcardUiState(true)
                ?: FlashcardUiState(flashcardDetails = FlashcardDetails())
            originalImageUri = loadedFlashcardUiState.existingImageUri
            flashcardUiState = loadedFlashcardUiState
        }
    }

    /**
     * Updates the [Flashcard] in the Room database
     */
    suspend fun updateFlashcard(context: Context) {
        if (validateInput()) {
            val oldImagePath = originalImageUri?.path
            val imagePath = when {
                // New image selected — save it
                flashcardUiState.selectedImageUri != null ->
                    saveImageToInternalStorage(context, flashcardUiState.selectedImageUri!!)
                // Existing image kept
                flashcardUiState.existingImageUri != null ->
                    oldImagePath
                // Cleared — null
                else -> null
            }

            if (oldImagePath != null && oldImagePath != imagePath) {
                // Delete the old image from internal storage
                val file = java.io.File(oldImagePath)
                if (file.exists()) {
                    file.delete()
                }
            }

            flashcardsRepository.updateFlashcard(
                flashcardUiState.flashcardDetails.toFlashcard(imagePath)
            )
        }
    }

    /**
     * Updates the [flashcardUiState] with the value provided in the argument. This method also
     * triggers a validation for input values.
     */
    fun updateUiState(flashcardDetails: FlashcardDetails) {
        flashcardUiState = flashcardUiState.copy(
            flashcardDetails = flashcardDetails,
            isEntryValid = validateInput(flashcardDetails)
        )
    }

    fun onImageSelected(uri: Uri?) {
        val newExistingUri = if (uri == null) null else flashcardUiState.existingImageUri
        flashcardUiState = flashcardUiState.copy(
            selectedImageUri = uri,
            // Null out existingImageUri so the preview disappears when cleared
            existingImageUri = newExistingUri,
            isEntryValid = validateInput(imageUri = uri, existingUri = newExistingUri)
        )
    }

    fun restoreExistingImage() {
        flashcardUiState = flashcardUiState.copy(
            selectedImageUri = null,
            existingImageUri = originalImageUri,
            isEntryValid = validateInput(existingUri = originalImageUri)
        )
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private fun validateInput(
        uiState: FlashcardDetails = flashcardUiState.flashcardDetails,
        imageUri: Uri? = flashcardUiState.selectedImageUri,
        existingUri: Uri? = flashcardUiState.existingImageUri
    ): Boolean {
        return uiState.term.isNotBlank() &&
            (!uiState.definition.isNullOrBlank() || imageUri != null || existingUri != null)
    }
}

private fun Flashcard.toFlashcardUiState(isEntryValid: Boolean = false): FlashcardUiState =
    FlashcardUiState(
        flashcardDetails = this.toFlashcardDetails(),
        isEntryValid = isEntryValid,
        existingImageUri = this.imagePath?.toUri()
    )