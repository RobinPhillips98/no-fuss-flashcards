package io.github.robinphillips98.flashcards.ui.flashcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.flashcards.FlashCardsAppTopBar
import io.github.robinphillips98.flashcards.navigation.NavigationDestination
import io.github.robinphillips98.flashcards.ui.AppViewModelProvider
import kotlinx.coroutines.launch

object FlashcardEntryDestination: NavigationDestination {
    override val route = "flashcard_entry"
    override val title = "Create Flashcard"
    const val DECK_ID_ARG = "deckId"
    val routeWithArgs = "$route/{$DECK_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: FlashcardEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            FlashCardsAppTopBar(
                title = FlashcardEntryDestination.title,
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        FlashcardEntryBody(
            flashcardUiState = viewModel.flashcardUiState,
            onFlashcardValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveFlashcard()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun FlashcardEntryBody(
    flashcardUiState: FlashcardUiState,
    onFlashcardValueChange: (FlashcardDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        FlashcardInputForm(
            flashcardDetails = flashcardUiState.flashcardDetails,
            onValueChange = onFlashcardValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = flashcardUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@Composable
fun FlashcardInputForm(
    flashcardDetails: FlashcardDetails,
    onValueChange: (FlashcardDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = flashcardDetails.term,
            onValueChange = { onValueChange(flashcardDetails.copy(term = it)) },
            label = { Text("Term*") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = flashcardDetails.definition,
            onValueChange = { onValueChange(flashcardDetails.copy(definition = it)) },
            label = { Text("Definition *") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
    }
}
