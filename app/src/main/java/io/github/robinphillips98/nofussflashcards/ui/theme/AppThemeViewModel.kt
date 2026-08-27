package io.github.robinphillips98.nofussflashcards.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.robinphillips98.nofussflashcards.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppThemeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /**
     * A [StateFlow] that emits the currently selected theme option.
     * It is initialized with the default theme option and updates whenever the user changes their preference.
     */
    val themeOption: StateFlow<AppThemeOptions> =
        userPreferencesRepository.selectedThemeOption
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppThemeOptions.DEFAULT
            )

    /**
     * A [StateFlow] that emits the currently selected font option.
     * It is initialized with the default font option and updates whenever the user changes their preference.
     */
    val fontOption: StateFlow<AppFontOptions> =
        userPreferencesRepository.selectedFontOption
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppFontOptions.DEFAULT
            )

    /**
     * Updates the selected theme option in the user preferences.
     *
     * @param themeOption The new theme option to be saved.
     */
    fun updateTheme(themeOption: AppThemeOptions) {
        viewModelScope.launch {
            userPreferencesRepository.saveSelectedThemeOption(themeOption)
        }
    }

    /**
     * Updates the selected font option in the user preferences.
     *
     * @param fontOption The new font option to be saved.
     */
    fun updateFont(fontOption: AppFontOptions) {
        viewModelScope.launch {
            userPreferencesRepository.saveSelectedFontOption(fontOption)
        }
    }
}