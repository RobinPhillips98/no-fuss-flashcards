package io.github.robinphillips98.nofussflashcards.ui.errors

import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.errors.AppError
import io.github.robinphillips98.nofussflashcards.ui.utils.StringResolver

sealed interface DeckDetailsError: AppError {
    data object DeckLoadFailed : DeckDetailsError
    data object DeckDeleteFailed : DeckDetailsError
    data object FlashcardListLoadFailed : DeckDetailsError
    data object FlashcardDeleteFailed : DeckDetailsError
    data object ImageDeleteFailed : DeckDetailsError
    data class Unknown(val cause: Throwable) : DeckDetailsError
}

fun StringResolver.messageFor(error: DeckDetailsError): String {
    return when (error) {
        is DeckDetailsError.DeckLoadFailed -> get(R.string.deck_list_load_failed)
        is DeckDetailsError.DeckDeleteFailed -> get(R.string.deck_delete_failed)
        is DeckDetailsError.FlashcardListLoadFailed -> get(R.string.flashcard_list_load_failed)
        is DeckDetailsError.FlashcardDeleteFailed -> get(R.string.flashcard_delete_failed)
        is DeckDetailsError.ImageDeleteFailed -> get(R.string.image_delete_failed)
        is DeckDetailsError.Unknown -> get(R.string.error_unknown, error.cause.message ?: "Unknown error")
    }
}