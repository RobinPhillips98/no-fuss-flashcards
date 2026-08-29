package com.nofussflashcards.app.utils

fun String.toTitleCase(): String {
    return this.lowercase()
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.titlecase() }
        }
}