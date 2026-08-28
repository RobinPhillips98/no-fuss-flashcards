package io.github.robinphillips98.nofussflashcards.ui.flashcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.AppViewModelProvider
import kotlinx.coroutines.launch

object FlashcardEditDestination: NavigationDestination {
    override val route = "flashcard_edit"
    override val titleResId = R.string.flashcard_edit_title
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
    val uiState = viewModel.flashcardUiState
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val availableDecks by viewModel.availableDecks.collectAsState()

    // Collect events from the ViewModel and show snackbars for relevant events.
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FlashcardEditUiEvent.ShowFlashcardSavedSnackbar -> {
                    snackbarHostState.showSnackbar(event.message, withDismissAction = true)
                }
                is FlashcardEditUiEvent.ShowErrorSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed && event.shouldRetryLoad) {
                        viewModel.loadFlashcard()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(FlashcardEditDestination.titleResId),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.hasLoadError) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.flashcard_details_load_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { viewModel.loadFlashcard() },
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
                ) {
                    Text(stringResource(R.string.retry_button))
                }
            }
        } else if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            FlashcardEntryBody(
                flashcardUiState = uiState,
                onFlashcardValueChange = viewModel::updateUiState,
                onImageUploaded = viewModel::onImageSelected,
                onImageRestored = if (viewModel.hasOriginalImage) {
                    viewModel::restoreExistingImage
                } else {
                    null
                },
                onSaveClick = {
                    coroutineScope.launch {
                        val flashcardUpdatedSuccessfully = viewModel.updateFlashcard(context)
                        if (flashcardUpdatedSuccessfully) {
                            navigateBack()
                        }
                    }
                },
                modifier = Modifier.padding(innerPadding),
                availableDecks = availableDecks
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardEditScreenPreview() {
    val sampleDecks = listOf(
        Deck(deckId = 1, name = "Sample Deck 1"),
        Deck(deckId = 2, name = "Sample Deck 2")
    )
    FlashcardEntryBody(
        flashcardUiState = FlashcardUiState(),
        onFlashcardValueChange = {},
        onImageUploaded = {},
        onSaveClick = {},
        availableDecks = sampleDecks
    )
}