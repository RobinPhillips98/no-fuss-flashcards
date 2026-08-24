package io.github.robinphillips98.nofussflashcards.ui.utils

fun String.toTitleCase(): String {
    return this.lowercase()
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.titlecase() }
        }
}