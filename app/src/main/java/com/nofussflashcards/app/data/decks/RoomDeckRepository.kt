package com.nofussflashcards.app.data.decks

import kotlinx.coroutines.flow.Flow

class RoomDeckRepository(private val deckDao: DeckDao): DecksRepository {
    override suspend fun getAllDecks(): List<Deck> = deckDao.getAllDecksOnce()

    override fun getAllDecksStream(): Flow<List<Deck>> = deckDao.getAllDecks()

    override fun getDeckStream(id: Int): Flow<Deck?> = deckDao.getDeckById(id)

    override suspend fun insertDeck(deck: Deck): Long = deckDao.insertDeck(deck)

    override suspend fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)

    override suspend fun updateDeck(deck: Deck) = deckDao.updateDeck(deck)
}