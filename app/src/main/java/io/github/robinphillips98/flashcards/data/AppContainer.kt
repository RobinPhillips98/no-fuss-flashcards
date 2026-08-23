package io.github.robinphillips98.flashcards.data

import android.content.Context

interface AppContainer {
    val deckDatasource: DeckDatasource // TODO: Change to DeckRepository when I implement a database
    val flashcardDatasource: FlashcardDatasource // TODO: Change to FlashcardRepository when I implement a database
}

class AppDataContainer(private val context: Context): AppContainer {
    override val deckDatasource: DeckDatasource by lazy {
        DeckDatasource()
    }

    override val flashcardDatasource: FlashcardDatasource by lazy {
        FlashcardDatasource()
    }


}