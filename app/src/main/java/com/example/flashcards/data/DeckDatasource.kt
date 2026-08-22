package com.example.flashcards.data

import com.example.flashcards.model.DeckInfo

class DeckDatasource {
    private val sampleDeck = DeckInfo(
        id = 1,
        name = "Sample Deck",
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