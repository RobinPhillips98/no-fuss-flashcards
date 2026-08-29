package com.nofussflashcards.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nofussflashcards.app.ui.AppViewModelProvider
import com.nofussflashcards.app.ui.theme.AppThemeViewModel
import com.nofussflashcards.app.ui.theme.FlashcardsTheme

class MainActivity : ComponentActivity() {

    private val appThemeViewModel: AppThemeViewModel by viewModels {
        AppViewModelProvider.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val themeOption by appThemeViewModel.themeOption.collectAsState()
            val fontOption by appThemeViewModel.fontOption.collectAsState()

            FlashcardsTheme(themeOption = themeOption, fontOption = fontOption) {
                NoFussFlashcardsApp(appThemeViewModel = appThemeViewModel)
            }
        }
    }
}