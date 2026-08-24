package io.github.robinphillips98.nofussflashcards.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Repository for managing user preferences using DataStore.
 *
 * For now, it manages the last opened deck ID, but can be extended to include other user
 * preferences in the future, such as theme settings.
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    val lastOpenedDeckId: Flow<Int?> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LAST_OPENED_DECK_ID]
        }

    suspend fun saveLastOpenedDeckId(deckId: Int) {
        dataStore.edit { preferences ->
            preferences[LAST_OPENED_DECK_ID] = deckId
        }
    }

    private companion object {
        const val TAG = "UserPreferencesRepo"
        val LAST_OPENED_DECK_ID = intPreferencesKey("last_opened_deck_id")
    }
}