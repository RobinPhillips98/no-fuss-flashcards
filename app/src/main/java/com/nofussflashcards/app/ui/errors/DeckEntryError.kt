package com.nofussflashcards.app.ui.errors

import com.nofussflashcards.app.R
import com.nofussflashcards.app.utils.StringResolver
import com.nofussflashcards.app.data.errors.AppError

/**
 * Represents errors that can occur during deck entry operations.
 */
sealed interface DeckEntryError: AppError {
    data object DeckSaveFailed : DeckEntryError
    data object InvalidSubmission : DeckEntryError
    data class Unknown(val cause: Throwable) : DeckEntryError
}

/**
 * Fetches a user-friendly error message for the given [DeckEntryError].
 *
 * @param error The [DeckEntryError] for which to fetch the message.
 *
 * @return A user-friendly error message corresponding to the [DeckEntryError].
 */
fun StringResolver.messageFor(error: DeckEntryError): String {
    return when (error) {
        is DeckEntryError.DeckSaveFailed -> get(R.string.deck_save_failed)
        is DeckEntryError.InvalidSubmission -> get(R.string.invalid_submission)
        is DeckEntryError.Unknown -> get(R.string.error_unknown, error.cause.message ?: "Unknown error")
    }
}