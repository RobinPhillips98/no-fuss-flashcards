package com.example.flashcards.model

data class FlashcardInfo(
    val id: Int,
    val term: String,
    val definition: String,
    val acceptableAnswers: List<String> = emptyList(),
    val image: String? = null, // TODO: Add image support
    val deckId: Int? = null, // TODO: Add deck support
)
