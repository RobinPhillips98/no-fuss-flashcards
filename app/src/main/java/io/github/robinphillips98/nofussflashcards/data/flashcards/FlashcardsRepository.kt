package io.github.robinphillips98.nofussflashcards.data.flashcards

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Flashcard] from a given data source.
 */
interface FlashcardsRepository {
    /**
     * Retrieve all the flashcards from the given data source.
     *
     * @return a list of all flashcards in the data source
     */
    suspend fun getAllFlashcards(): List<Flashcard>
    /**
     * Retrieve all the flashcards from the given data source as a flow.
     *
     * @return a flow emitting a list of all flashcards in the data source
     */
    fun getAllFlashcardsStream(): Flow<List<Flashcard>>

    /**
     * Retrieve a flashcard from the given data source that matches with the [flashcardId].
     *
     * @param flashcardId the ID of the flashcard to retrieve
     * @return a flow emitting the flashcard that matches the [flashcardId], or null if no such flashcard exists
     */
    fun getFlashcardStream(flashcardId: Int): Flow<Flashcard?>

    /**
     * Retrieve all the flashcards from the given data source that match with the [deckId].
     *
     * @param deckId the ID of the deck to retrieve flashcards for
     * @return a flow emitting a list of flashcards that match the [deckId]
     */
    fun getFlashcardsByDeckIdStream(deckId: Int): Flow<List<Flashcard>>

    /**
     * Insert flashcard in the data source
     *
     * @param flashcard the flashcard to insert
     */
    suspend fun insertFlashcard(flashcard: Flashcard)

    /**
     * Delete flashcard from the data source
     *
     * @param flashcard the flashcard to delete
     */
    suspend fun deleteFlashcard(flashcard: Flashcard)

    /**
     * Update flashcard in the data source
     *
     * @param flashcard the flashcard to update
     */
    suspend fun updateFlashcard(flashcard: Flashcard)
}