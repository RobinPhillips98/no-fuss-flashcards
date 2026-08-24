package io.github.robinphillips98.flashcards.ui.flashcards

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.flashcards.FlashCardsAppTopBar
import io.github.robinphillips98.flashcards.navigation.NavigationDestination
import io.github.robinphillips98.flashcards.ui.AppViewModelProvider
import kotlinx.coroutines.launch

object FlashcardEditDestination: NavigationDestination {
    override val route = "flashcard_edit"
    override val title = "Edit Flashcard"
    const val FLASHCARD_ID_ARG = "flashcardId"
    val routeWithArgs = "$route/{$FLASHCARD_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FlashcardEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val availableDecks by viewModel.availableDecks.collectAsState()

    Scaffold(
        topBar = {
            FlashCardsAppTopBar(
                title = FlashcardEditDestination.title,
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        FlashcardEntryBody(
            flashcardUiState = viewModel.flashcardUiState,
            onFlashcardValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateFlashcard()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding),
            availableDecks = availableDecks
        )
    }
}