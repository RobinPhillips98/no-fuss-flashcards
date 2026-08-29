package com.nofussflashcards.app.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nofussflashcards.app.NoFussFlashcardsApplication
import com.nofussflashcards.app.ui.decks.DeckDetailsViewModel
import com.nofussflashcards.app.ui.decks.forms.DeckEditViewModel
import com.nofussflashcards.app.ui.decks.forms.DeckEntryViewModel
import com.nofussflashcards.app.ui.flashcards.forms.FlashcardEditViewModel
import com.nofussflashcards.app.ui.flashcards.forms.FlashcardEntryViewModel
import com.nofussflashcards.app.ui.flashcards.FlashcardsPagerViewModel
import com.nofussflashcards.app.ui.home.HomeViewModel
import com.nofussflashcards.app.ui.settings.SettingsViewModel
import com.nofussflashcards.app.ui.theme.AppThemeViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Flashcards app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for AppThemeViewModel
        initializer {
            AppThemeViewModel(
                noFussFlashcardsApplication().container.userPreferencesRepository
            )
        }
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                noFussFlashcardsApplication().container.decksRepository,
                noFussFlashcardsApplication().container.userPreferencesRepository,
                noFussFlashcardsApplication().container.stringResolver
            )
        }

        // Initializer for DeckDetailsViewModel
        initializer {
            DeckDetailsViewModel(
                this.createSavedStateHandle(),
                noFussFlashcardsApplication().container.decksRepository,
                noFussFlashcardsApplication().container.flashcardsRepository,
                noFussFlashcardsApplication().container.stringResolver
            )
        }

        // Initializer for FlashcardsPagerViewModel
        initializer {
            FlashcardsPagerViewModel(
                this.createSavedStateHandle(),
                noFussFlashcardsApplication().container.decksRepository,
                noFussFlashcardsApplication().container.flashcardsRepository,
                noFussFlashcardsApplication().container.userPreferencesRepository,
                noFussFlashcardsApplication().container.stringResolver
            )
        }

        // Initializer for DeckEntryViewModel
        initializer {
            DeckEntryViewModel(
                noFussFlashcardsApplication().container.decksRepository,
                noFussFlashcardsApplication().container.stringResolver
            )
        }

        // Initializer for DeckEditViewModel
        initializer {
            DeckEditViewModel(
                this.createSavedStateHandle(),
                noFussFlashcardsApplication().container.decksRepository,
                noFussFlashcardsApplication().container.stringResolver
            )
        }

        // Initializer for FlashcardEntryViewModel
        initializer {
            FlashcardEntryViewModel(
                this.createSavedStateHandle(),
                noFussFlashcardsApplication().container.flashcardsRepository,
                noFussFlashcardsApplication().container.stringResolver
            )
        }

        // Initializer for FlashcardEditViewModel
        initializer {
            FlashcardEditViewModel(
                this.createSavedStateHandle(),
                noFussFlashcardsApplication().container.flashcardsRepository,
                noFussFlashcardsApplication().container.decksRepository,
                noFussFlashcardsApplication().container.stringResolver
            )
        }

        // Initializer for SettingsViewModel
        initializer {
            SettingsViewModel(
                noFussFlashcardsApplication().container.decksRepository,
                noFussFlashcardsApplication().container.flashcardsRepository,
                noFussFlashcardsApplication().container.stringResolver
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [NoFussFlashcardsApplication].
 */
fun CreationExtras.noFussFlashcardsApplication(): NoFussFlashcardsApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NoFussFlashcardsApplication)