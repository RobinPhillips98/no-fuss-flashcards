package io.github.robinphillips98.flashcards.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.robinphillips98.flashcards.FlashcardApplication
import io.github.robinphillips98.flashcards.ui.home.HomeViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Flashcards app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(flashcardsApplication().container.decksRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [FlashcardApplication].
 */
fun CreationExtras.flashcardsApplication(): FlashcardApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlashcardApplication)