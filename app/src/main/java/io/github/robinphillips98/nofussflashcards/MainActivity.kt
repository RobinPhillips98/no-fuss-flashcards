package io.github.robinphillips98.nofussflashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import io.github.robinphillips98.nofussflashcards.ui.AppViewModelProvider
import io.github.robinphillips98.nofussflashcards.ui.theme.AppThemeViewModel
import io.github.robinphillips98.nofussflashcards.ui.theme.FlashcardsTheme

class MainActivity : ComponentActivity() {

    private val appThemeViewModel: AppThemeViewModel by viewModels {
        AppViewModelProvider.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val themeOption by appThemeViewModel.themeOption.collectAsState()

            FlashcardsTheme(themeOption = themeOption) {
                NoFussFlashcardsApp(
                    selectedThemeName = stringResource(themeOption.titleResId),
                    onThemeSelected = appThemeViewModel::updateTheme
                )
            }
        }
    }
}