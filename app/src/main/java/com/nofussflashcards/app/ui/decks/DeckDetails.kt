package com.nofussflashcards.app.ui.decks

import com.nofussflashcards.app.data.decks.Deck

data class DeckDetails(
    val deckId: Int = 0,
    val name: String = "",
    val description: String? = null,
)

fun DeckDetails.toDeck(): Deck =
    Deck(
        deckId = deckId,
        name = name,
        description = description.takeIf { it?.isNotBlank() ?: false }
    )

fun Deck.toDeckDetails(): DeckDetails = DeckDetails(
    deckId = deckId,
    name = name,
    description = description.takeIf { it?.isNotBlank() ?: false },
)