package io.github.robinphillips98.flashcards.ui.decks

import io.github.robinphillips98.flashcards.data.decks.Deck

data class DeckDetails(
    val deckId: Int,
    val name: String,
    val description: String?,
)

fun DeckDetails.toDeck(): Deck = Deck(
    deckId = deckId,
    name = name,
    description = description
)

fun Deck.toDeckDetails(): DeckDetails = DeckDetails(
    deckId = deckId,
    name = name,
    description = description,
)