package io.github.robinphillips98.nofussflashcards.ui.decks.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.AppViewModelProvider
import kotlinx.coroutines.launch

object DeckEditDestination: NavigationDestination {
    override val route = "deck_edit"
    override val titleResId = R.string.deck_edit_title
    const val DECK_ID_ARG = "deckId"
    val routeWithArgs = "$route/{$DECK_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeckEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.deckUiState
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    //Collect events from the ViewModel and show snackbars for relevant events.
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DeckEditUiEvent.ShowDeckSavedSnackbar -> {
                    snackbarHostState.showSnackbar(event.message, withDismissAction = true)
                }
                is DeckEditUiEvent.ShowErrorSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        event.message,
                        actionLabel = event.actionLabel,
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed && event.shouldRetryLoad) {
                        viewModel.loadDeck()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(DeckEditDestination.titleResId),
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
                    stringResource(R.string.deck_details_load_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { viewModel.loadDeck() },
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
            DeckEntryBody(
                uiState = uiState,
                onDeckValueChange = viewModel::updateUiState,
                onSaveClick = {
                    coroutineScope.launch {
                        val deckUpdatedSuccessfully = viewModel.updateDeck()
                        if (deckUpdatedSuccessfully) {
                            navigateBack()
                        }
                    }
                },
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    )
                    .verticalScroll(rememberScrollState())
            )
        }
    }

}