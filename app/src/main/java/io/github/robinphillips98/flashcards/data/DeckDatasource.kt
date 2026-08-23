package io.github.robinphillips98.flashcards.data

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