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

    val themeOption: StateFlow<AppThemeOptions> =
        userPreferencesRepository.selectedThemeOption
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppThemeOptions.DEFAULT
            )

    fun updateTheme(themeOption: AppThemeOptions) {
        viewModelScope.launch {
            userPreferencesRepository.saveSelectedThemeOption(themeOption)
        }
    }
}