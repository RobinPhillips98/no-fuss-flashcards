package io.github.robinphillips98.flashcards.model

data class DeckInfo(
    val id: Int,
    val name: String,
    val description: String? = null,
)
