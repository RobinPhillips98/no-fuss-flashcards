package com.nofussflashcards.app.ui.errors

import com.nofussflashcards.app.R
import com.nofussflashcards.app.utils.StringResolver
import com.nofussflashcards.app.data.errors.AppError

/**
 * Represents errors that can occur during flashcard edit operations.
 */
sealed interface FlashcardEditError: AppError {
    data object FlashcardSaveFailed : FlashcardEditError
    data object FlashcardLoadFailed : FlashcardEditError
    data object ImageSaveFailed : FlashcardEditError
    data object OldImageDeleteFailed : FlashcardEditError
    data object InvalidSubmission : FlashcardEditError
    data class Unknown(val cause: Throwable) : FlashcardEditError
}

/**
 * Fetches a user-friendly error message for the given [FlashcardEditError].
 */
fun StringResolver.messageFor(error: FlashcardEditError): String {
    return when (error) {
        is FlashcardEditError.FlashcardSaveFailed -> get(R.string.flashcard_save_failed)
        is FlashcardEditError.FlashcardLoadFailed -> get(R.string.flashcard_load_failed)
        is FlashcardEditError.ImageSaveFailed -> get(R.string.image_save_failed)
        is FlashcardEditError.OldImageDeleteFailed -> get(R.string.old_image_delete_failed)
        is FlashcardEditError.InvalidSubmission -> get(R.string.invalid_submission)
        is FlashcardEditError.Unknown -> get(
            R.string.error_unknown,
            error.cause.message ?: "Unknown error"
        )
    }
}

