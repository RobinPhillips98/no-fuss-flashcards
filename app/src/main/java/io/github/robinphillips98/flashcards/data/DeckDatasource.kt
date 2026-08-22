package io.github.robinphillips98.flashcards.data

import io.github.robinphillips98.flashcards.model.DeckInfo

class DeckDatasource {
    private val sampleDeck = DeckInfo(
        id = 1,
        name = "Sample Deck",
        description = "This is a sample deck for testing purposes."
    )

    fun loadDecks(): List<DeckInfo> {
        return listOf(sampleDeck)
    }

    fun loadDeckById(deckId: Int): DeckInfo? {
        return if (deckId == sampleDeck.id) {
            sampleDeck
        } else {
            null
        }
    }
}