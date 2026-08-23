package io.github.robinphillips98.flashcards.data

import android.content.Context
import io.github.robinphillips98.flashcards.data.decks.DecksRepository
import io.github.robinphillips98.flashcards.data.decks.RoomDeckRepository
import io.github.robinphillips98.flashcards.data.flashcards.FlashcardsRepository
import io.github.robinphillips98.flashcards.data.flashcards.RoomFlashcardsRepository

interface AppContainer {
    val decksRepository: DecksRepository
    val flashcardsRepository: FlashcardsRepository
}

class AppDataContainer(private val context: Context): AppContainer {
    override val decksRepository: DecksRepository by lazy {
        RoomDeckRepository(
            FlashcardAppDatabase
                .getDatabaseInstance(context)
                .deckDao()
        )
    }

    override val flashcardsRepository: FlashcardsRepository by lazy {
        RoomFlashcardsRepository(
            FlashcardAppDatabase
                .getDatabaseInstance(context)
                .flashcardDao()
        )
    }
}