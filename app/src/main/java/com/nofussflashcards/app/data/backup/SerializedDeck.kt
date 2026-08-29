package com.nofussflashcards.app.data.backup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerializedDeck(
    @SerialName("deck_id")
    val deckId: Int,
    val name: String,
    val description: String? = null,
)