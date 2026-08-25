package io.github.robinphillips98.nofussflashcards.data.errors

sealed interface AppError {
    data object DecksLoadFailed : AppError
    data object LastOpenedDeckReadFailed : AppError
    data object LastOpenedDeckWriteFailed : AppError
    data class Unknown(val cause: Throwable) : AppError
}