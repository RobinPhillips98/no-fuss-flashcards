package io.github.robinphillips98.flashcards.data.decks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String? = null,
)