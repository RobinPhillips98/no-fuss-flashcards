package io.github.robinphillips98.nofussflashcards.ui.decks

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.AppViewModelProvider
import kotlinx.coroutines.launch

object DeckEntryDestination: NavigationDestination {
    override val route = "deck_entry"
    override val titleResId = R.string.deck_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: DeckEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DeckEntryUiEvent.ShowDeckSavedSnackbar -> {
                    snackbarHostState.showSnackbar(event.message, withDismissAction = true)
                }
                is DeckEntryUiEvent.ShowErrorSnackbar -> {
                    snackbarHostState.showSnackbar(event.message, withDismissAction = true)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(DeckEntryDestination.titleResId),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        DeckEntryBody(
            deckUiState = viewModel.deckUiState,
            onDeckValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    val deckSavedSuccessfully = viewModel.saveDeck()
                    if (deckSavedSuccessfully) {
                        navigateBack()
                    }
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun DeckEntryBody(
    deckUiState: DeckUiState,
    onDeckValueChange: (DeckDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        DeckInputForm(
            deckDetails = deckUiState.deckDetails,
            onValueChange = onDeckValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = deckUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_button))
        }
    }
}

@Composable
fun DeckInputForm(
    deckDetails: DeckDetails,
    onValueChange: (DeckDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
    modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = deckDetails.name,
            onValueChange = { onValueChange(deckDetails.copy(name = it)) },
            label = { Text(stringResource(R.string.deck_entry_name_label)) },
            placeholder = { Text(stringResource(R.string.deck_entry_name_placeholder)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = deckDetails.description ?: "",
            onValueChange = { onValueChange(deckDetails.copy(description = it)) },
            label = { Text(stringResource(R.string.deck_entry_description_label)) },
            placeholder = { Text(stringResource(R.string.deck_entry_description_placeholder)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DeckEntryScreenPreview() {
    DeckEntryBody(
        deckUiState = DeckUiState(),
        onDeckValueChange = {},
        onSaveClick = {}
    )
}