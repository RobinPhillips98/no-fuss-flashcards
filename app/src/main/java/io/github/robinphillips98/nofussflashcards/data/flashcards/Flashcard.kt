package io.github.robinphillips98.nofussflashcards.data.flashcards

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import io.github.robinphillips98.nofussflashcards.data.decks.Deck

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["deck_id"],
            childColumns = ["deck_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["deck_id"])]
)
data class Flashcard(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "flashcard_id")
    val flashcardId: Int,
    val term: String,
    val definition: String? = null,
    @ColumnInfo(name = "deck_id")
    val deckId: Int,
    @ColumnInfo(name = "image_path")
    val imagePath: String? = null
)