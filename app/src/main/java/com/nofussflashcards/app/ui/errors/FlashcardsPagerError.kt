package com.nofussflashcards.app.ui.errors

import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.errors.AppError
import com.nofussflashcards.app.utils.StringResolver

/**
 * Represents errors that can occur during flashcards pager operations.
 */
sealed interface FlashcardsPagerError: AppError {
    data object DeckLoadFailed : FlashcardsPagerError
    data object FlashcardListLoadFailed : FlashcardsPagerError
    data class Unknown(val cause: Throwable) : FlashcardsPagerError
}

/**
 * Fetches a user-friendly error message for the given [FlashcardsPagerError].
 */
fun StringResolver.messageFor(error: FlashcardsPagerError): String {
    return when (error) {
        is FlashcardsPagerError.DeckLoadFailed -> get(R.string.deck_load_failed)
        is FlashcardsPagerError.FlashcardListLoadFailed -> get(R.string.flashcard_list_load_failed)
        is FlashcardsPagerError.Unknown -> get(R.string.error_unknown, error.cause.message ?: "Unknown error")
    }
}

