package io.github.robinphillips98.nofussflashcards.data.decks

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Deck] from a given data source.
 */
interface DecksRepository {
    /**
     * Retrieve all the decks from the given data source.
     *
     * @return a list of all decks in the data source
     */
    suspend fun getAllDecks(): List<Deck>

    /**
     * Retrieve all the decks from  the given data source as a [Flow].
     *
     * @return a [Flow] emitting a list of all decks in the data source
     */
    fun getAllDecksStream(): Flow<List<Deck>>

    /**
     * Retrieve a deck from the given data source that matches with the [id].
     *
     * @param id the ID of the deck to retrieve
     *
     * @return a [Flow] emitting the deck that matches the [id], or null if no such deck exists
     */
    fun getDeckStream(id: Int): Flow<Deck?>

    /**
     * Insert deck in the data source
     *
     * @param deck the deck to insert
     *
     * @return the row ID of the newly inserted deck, or -1 if an error occurred
     */
    suspend fun insertDeck(deck: Deck): Long

    /**
     * Delete deck from the data source
     *
     * @param deck the deck to delete
     */
    suspend fun deleteDeck(deck: Deck)

    /**
     * Update deck in the data source
     *
     * @param deck the deck to update
     */
    suspend fun updateDeck(deck: Deck)
}