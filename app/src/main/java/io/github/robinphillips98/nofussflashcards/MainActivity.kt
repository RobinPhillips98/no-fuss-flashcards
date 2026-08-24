package io.github.robinphillips98.nofussflashcards

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.robinphillips98.nofussflashcards.ui.theme.FlashcardsTheme

class MainActivity : androidx.activity.ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardsTheme {
                NoFussFlashcardsApp()
            }
        }
    }
}