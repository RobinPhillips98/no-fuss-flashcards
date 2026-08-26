package io.github.robinphillips98.nofussflashcards.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.robinphillips98.nofussflashcards.ui.theme.AppThemeOptions
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

    val hasFlippedCard: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[HAS_FLIPPED_CARD] ?: false
        }

    val selectedThemeOption: Flow<AppThemeOptions> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val ordinal = preferences[SELECTED_THEME_OPTION] ?: AppThemeOptions.DEFAULT.ordinal
            AppThemeOptions.entries.getOrElse(ordinal) { AppThemeOptions.DEFAULT }
        }

    suspend fun saveLastOpenedDeckId(deckId: Int) {
        dataStore.edit { preferences ->
            preferences[LAST_OPENED_DECK_ID] = deckId
        }
    }

    suspend fun saveHasFlippedCard(hasFlipped: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAS_FLIPPED_CARD] = hasFlipped
        }
    }

    suspend fun saveSelectedThemeOption(themeOption: AppThemeOptions) {
        dataStore.edit { preferences ->
            preferences[SELECTED_THEME_OPTION] = themeOption.ordinal
        }
    }

    private companion object {
        const val TAG = "UserPreferencesRepo"
        val LAST_OPENED_DECK_ID = intPreferencesKey("last_opened_deck_id")
        val HAS_FLIPPED_CARD = booleanPreferencesKey("has_flipped_card")
        val SELECTED_THEME_OPTION = intPreferencesKey("selected_theme_option")
    }
}