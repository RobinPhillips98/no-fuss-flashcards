package io.github.robinphillips98.nofussflashcards.data.decks

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Deck] from a given data source.
 */
interface DecksRepository {
    /**
     * Retrieve all the decks from  the given data source.
     */
    fun getAllDecksStream(): Flow<List<Deck>>

    /**
     * Retrieve a deck from the given data source that matches with the [id].
     */
    fun getDeckStream(id: Int): Flow<Deck?>

    /**
     * Insert deck in the data source
     */
    suspend fun insertDeck(deck: Deck)

    /**
     * Delete deck from the data source
     */
    suspend fun deleteDeck(deck: Deck)

    /**
     * Update deck in the data source
     */
    suspend fun updateDeck(deck: Deck)
}