package io.github.robinphillips98.flashcards.data

data class Deck(
    val id: Int,
    val name: String,
    val description: String? = null,
)