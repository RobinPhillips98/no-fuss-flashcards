package io.github.robinphillips98.flashcards.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.github.robinphillips98.flashcards.data.decks.DecksRepository
import io.github.robinphillips98.flashcards.data.decks.RoomDeckRepository
import io.github.robinphillips98.flashcards.data.flashcards.FlashcardsRepository
import io.github.robinphillips98.flashcards.data.flashcards.RoomFlashcardsRepository

private const val PREFERENCES_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)

interface AppContainer {
    val decksRepository: DecksRepository
    val flashcardsRepository: FlashcardsRepository
    val userPreferencesRepository: UserPreferencesRepository
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

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}