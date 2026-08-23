package io.github.robinphillips98.flashcards.data

import io.github.robinphillips98.flashcards.data.decks.Deck

// TODO: Wire up proper repository for decks, this is just a sample implementation for testing purposes.

class DeckDatasource {
    private val sampleDeck = Deck(
        id = 1,
        name = "Sample Deck",
        description = "This is a sample deck for testing purposes."
    )

    fun loadDecks(): List<Deck> {
        return listOf(sampleDeck)
    }

    fun loadDeckById(deckId: Int): Deck? {
        return if (deckId == sampleDeck.id) {
            sampleDeck
        } else {
            null
        }
    }
}