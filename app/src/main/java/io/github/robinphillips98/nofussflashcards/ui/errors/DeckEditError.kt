package io.github.robinphillips98.nofussflashcards.ui.errors

import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.ui.utils.StringResolver

/**
 * Represents errors that can occur during deck entry operations.
 */
sealed interface DeckEditError {
    data object DeckSaveFailed : DeckEditError
    data object DeckLoadFailed : DeckEditError
    data object InvalidSubmission : DeckEditError
    data class Unknown(val cause: Throwable) : DeckEditError
}

/**
 * Fetches a user-friendly error message for the given [DeckEditError].
 *
 * @param error The [DeckEditError] for which to fetch the message.
 *
 * @return A user-friendly error message corresponding to the [DeckEditError].
 */
fun StringResolver.messageFor(error: DeckEditError): String {
    return when (error) {
        is DeckEditError.DeckSaveFailed -> get(R.string.deck_save_failed)
        is DeckEditError.DeckLoadFailed -> get(R.string.deck_load_failed)
        is DeckEditError.InvalidSubmission -> get(R.string.invalid_submission)
        is DeckEditError.Unknown -> get(R.string.error_unknown, error.cause.message ?: "Unknown error")
    }
}