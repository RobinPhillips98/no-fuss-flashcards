package io.github.robinphillips98.flashcards.data

data class Flashcard(
    val id: Int,
    val term: String,
    val definition: String,
    val acceptableAnswers: List<String> = emptyList(),
    val image: String? = null, // TODO: Add image support
    val deckId: Int? = 1, // TODO: Add deck support
)