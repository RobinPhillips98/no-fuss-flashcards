package com.nofussflashcards.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.nofussflashcards.app.data.decks.DecksRepository
import com.nofussflashcards.app.data.decks.RoomDeckRepository
import com.nofussflashcards.app.data.flashcards.FlashcardsRepository
import com.nofussflashcards.app.data.flashcards.RoomFlashcardsRepository
import com.nofussflashcards.app.utils.AndroidStringResolver
import com.nofussflashcards.app.utils.StringResolver

private const val PREFERENCES_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)

interface AppContainer {
    val decksRepository: DecksRepository
    val flashcardsRepository: FlashcardsRepository
    val userPreferencesRepository: UserPreferencesRepository
    val stringResolver: StringResolver
}

class AppDataContainer(private val context: Context): AppContainer {
    override val decksRepository: DecksRepository by lazy {
        RoomDeckRepository(
            NoFussFlashcardAppDatabase
                .getDatabaseInstance(context)
                .deckDao()
        )
    }

    override val flashcardsRepository: FlashcardsRepository by lazy {
        RoomFlashcardsRepository(
            NoFussFlashcardAppDatabase
                .getDatabaseInstance(context)
                .flashcardDao()
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }

    override val stringResolver: StringResolver by lazy {
        AndroidStringResolver(context)
    }
}