package io.github.robinphillips98.nofussflashcards.data.backup

import kotlinx.serialization.Serializable

@Serializable
data class SerializedBackup(
    val decks: List<SerializedDeck>,
    val flashcards: List<SerializedFlashcard>
)