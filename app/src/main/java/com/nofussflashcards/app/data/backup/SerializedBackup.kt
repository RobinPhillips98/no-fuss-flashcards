package com.nofussflashcards.app.data.backup

import kotlinx.serialization.Serializable

@Serializable
data class SerializedBackup(
    val decks: List<SerializedDeck>,
    val flashcards: List<SerializedFlashcard>
)