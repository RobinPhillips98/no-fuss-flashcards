package com.nofussflashcards.app.ui.errors

import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.errors.AppError
import com.nofussflashcards.app.utils.StringResolver

/**
 * Represents errors that can occur in the Home screen of the app.
 */
sealed interface HomeError: AppError {
    data object DecksLoadFailed : HomeError
    data object LastOpenedDeckReadFailed : HomeError
    data object LastOpenedDeckWriteFailed : HomeError
    data class Unknown(val cause: Throwable) : HomeError
}

/**
 * Extension function to get a user-friendly message for a given HomeError.
 *
 * @param error The HomeError for which to get the message.
 *
 * @return A user-friendly message corresponding to the error.
 */
fun StringResolver.messageFor(error: HomeError): String {
    return when (error) {
        is HomeError.DecksLoadFailed -> get(R.string.deck_list_load_failed)
        is HomeError.LastOpenedDeckReadFailed -> get(R.string.last_deck_read_failed)
        is HomeError.LastOpenedDeckWriteFailed -> get(R.string.last_deck_write_failed)
        is HomeError.Unknown -> get(R.string.error_unknown, error.cause.message ?: "Unknown error")
    }
}