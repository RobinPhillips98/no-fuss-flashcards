package com.nofussflashcards.app.data.backup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerializedFlashcard(
    val term: String,
    val definition: String? = null,
    @SerialName("deck_id")
    val deckId: Int,
)