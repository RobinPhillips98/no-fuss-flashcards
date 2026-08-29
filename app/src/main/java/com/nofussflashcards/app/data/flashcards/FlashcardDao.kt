package com.nofussflashcards.app.data.flashcards

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards")
    suspend fun getAllFlashcardsOnce(): List<Flashcard>

    @Query("SELECT * FROM flashcards ORDER BY term ASC")
    fun getAllFlashcards(): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE flashcard_id = :flashcardId")
    fun getFlashcardById(flashcardId: Int): Flow<Flashcard>

    @Query("SELECT * FROM flashcards WHERE deck_id = :deckId ORDER BY term ASC")
    fun getFlashcardsByDeckId(deckId: Int): Flow<List<Flashcard>>

    @Insert
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)
}