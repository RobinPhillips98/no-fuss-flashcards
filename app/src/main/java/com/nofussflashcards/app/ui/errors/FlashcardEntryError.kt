package com.nofussflashcards.app.ui.errors

import com.nofussflashcards.app.R
import com.nofussflashcards.app.utils.StringResolver
import com.nofussflashcards.app.data.errors.AppError

/**
 * Represents errors that can occur during flashcard entry operations.
 */
sealed interface FlashcardEntryError: AppError {
    data object FlashcardSaveFailed : FlashcardEntryError
    data object ImageSaveFailed : FlashcardEntryError
    data object InvalidSubmission : FlashcardEntryError
    data class Unknown(val cause: Throwable) : FlashcardEntryError
}

/**
 * Fetches a user-friendly error message for the given [FlashcardEntryError].
 */
fun StringResolver.messageFor(error: FlashcardEntryError): String {
    return when (error) {
        is FlashcardEntryError.FlashcardSaveFailed -> get(R.string.flashcard_save_failed)
        is FlashcardEntryError.ImageSaveFailed -> get(R.string.image_save_failed)
        is FlashcardEntryError.InvalidSubmission -> get(R.string.invalid_submission)
        is FlashcardEntryError.Unknown -> get(
            R.string.error_unknown,
            error.cause.message ?: "Unknown error"
        )
    }
}

