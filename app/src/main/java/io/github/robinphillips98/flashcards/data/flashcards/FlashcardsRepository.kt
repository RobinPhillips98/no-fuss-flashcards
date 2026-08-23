package io.github.robinphillips98.flashcards.data.flashcards

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Flashcard] from a given data source.
 */
interface FlashcardsRepository {
    /**
     * Retrieve all the flashcards from the given data source.
     */
    fun getAllFlashcardsStream(): Flow<List<Flashcard>>

    /**
     * Retrieve a flashcard from the given data source that matches with the [flashcardId].
     */
    fun getFlashcardStream(flashcardId: Int): Flow<Flashcard?>

    /**
     * Retrieve all the flashcards from the given data source that match with the [deckId].
     */
    fun getFlashcardsByDeckIdStream(deckId: Int): Flow<List<Flashcard>>

    /**
     * Insert flashcard in the data source
     */
    suspend fun insertFlashcard(flashcard: Flashcard)

    /**
     * Delete flashcard from the data source
     */
    suspend fun deleteFlashcard(flashcard: Flashcard)

    /**
     * Update flashcard in the data source
     */
    suspend fun updateFlashcard(flashcard: Flashcard)
}