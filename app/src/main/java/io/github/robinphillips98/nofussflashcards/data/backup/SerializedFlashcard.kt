package io.github.robinphillips98.nofussflashcards.data.backup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerializedFlashcard(
//    @SerialName("flashcard_id")
//    val flashcardId: Int,
    val term: String,
    val definition: String? = null,
    @SerialName("deck_id")
    val deckId: Int,
)