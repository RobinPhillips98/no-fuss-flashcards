package io.github.robinphillips98.flashcards.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.robinphillips98.flashcards.FlashcardApplication
import io.github.robinphillips98.flashcards.ui.decks.DeckDetailsViewModel
import io.github.robinphillips98.flashcards.ui.decks.DeckEditViewModel
import io.github.robinphillips98.flashcards.ui.decks.DeckEntryViewModel
import io.github.robinphillips98.flashcards.ui.flashcards.FlashcardsPagerViewModel
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

        // Initializer for DeckDetailsViewModel
        initializer {
            DeckDetailsViewModel(
                this.createSavedStateHandle(),
                flashcardsApplication().container.decksRepository,
                flashcardsApplication().container.flashcardsRepository
            )
        }

        // Initializer for FlashcardsPagerViewModel
        initializer {
            FlashcardsPagerViewModel(
                this.createSavedStateHandle(),
                flashcardsApplication().container.decksRepository,
                flashcardsApplication().container.flashcardsRepository
            )
        }

        // Initializer for DeckEntryViewModel
        initializer {
            DeckEntryViewModel(flashcardsApplication().container.decksRepository)
        }

        // Initializer for DeckEditViewModel
        initializer {
            DeckEditViewModel(
                this.createSavedStateHandle(),
                flashcardsApplication().container.decksRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [FlashcardApplication].
 */
fun CreationExtras.flashcardsApplication(): FlashcardApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlashcardApplication)