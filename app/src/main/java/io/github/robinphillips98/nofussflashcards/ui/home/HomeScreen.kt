package io.github.robinphillips98.nofussflashcards.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.AppViewModelProvider

object HomeDestination: NavigationDestination {
    override val route = "home"
    override val titleResId = R.string.full_app_name
}

/**
 * Composable function that represents the Home screen of the app.
 *
 * @param onCreateDeckClicked Callback function to be invoked when the "Create Deck" button is clicked.
 * @param onDeckClicked Callback function to be invoked when a deck is clicked, passing the deck ID.
 * @param modifier [Modifier] for styling and layout adjustments.
 * @param viewModel [HomeViewModel] for managing the state and logic of the Home screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateDeckClicked: () -> Unit,
    onDeckClicked: (deckId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeUiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed && event.shouldRetryDecks) {
                        viewModel.retryDecksLoad()
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(HomeDestination.titleResId),
                canNavigateBack = false,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateDeckClicked,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    ),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.create_deck_button)
                    )
                },
                text = { Text(stringResource(R.string.create_deck_button)) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (homeUiState.hasDecksLoadError) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.deck_list_load_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { viewModel.retryDecksLoad() },
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(stringResource(R.string.retry_button))
                }
            }
        } else {
            HomeBody(
                deckList = homeUiState.deckList,
                onDeckClicked = { deckId ->
                    onDeckClicked(deckId)
                    viewModel.updateLastOpenedDeckId(deckId)
                },
                modifier = modifier.padding(innerPadding),
                lastDeckId = homeUiState.lastOpenedDeckId,
                showLastOpenedError =  homeUiState.hasLastOpenedDeckLoadError
            )
        }
    }
}

/**
 * Composable function that represents the body of the Home screen.
 *
 * @param deckList List of decks to be displayed.
 * @param onDeckClicked Callback function to be invoked when a deck is clicked, passing the deck ID.
 * @param modifier [Modifier] for styling and layout adjustments.
 * @param lastDeckId ID of the last opened deck, if any.
 */
@Composable
private fun HomeBody(
    deckList: List<Deck>,
    onDeckClicked: (deckId: Int) -> Unit,
    modifier: Modifier = Modifier,
    lastDeckId: Int? = null,
    showLastOpenedError: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                R.string.home_welcome_text,
                stringResource(R.string.full_app_name)
            ),
            style = MaterialTheme.typography.titleLarge
        )

        if (lastDeckId != null) {
            val lastDeckName = deckList.find { it.deckId == lastDeckId }?.name
            lastDeckName?.let {
                Button(
                    onClick = { onDeckClicked(lastDeckId) },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.jump_in, lastDeckName))
                }
            }
        }

        if (showLastOpenedError) {
            Text(
                text = stringResource(R.string.last_deck_read_failed),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider()

        DeckList(decks = deckList, onDeckClicked = onDeckClicked)
    }
}

/**
 * Composable function that represents the list of decks.
 *
 * @param decks List of decks to be displayed.
 * @param onDeckClicked Callback function to be invoked when a deck is clicked, passing the deck ID.
 * @param modifier [Modifier] for styling and layout adjustments.
 */
@Composable
private fun DeckList(
    decks: List<Deck>,
    onDeckClicked: (deckId: Int) -> Unit,
    modifier: Modifier = Modifier
) {

    if (decks.isNotEmpty()) {
        LazyColumn(modifier = modifier) {
            item {
                Text(
                    text = stringResource(R.string.deck_list_header),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            items(decks.size) { index ->
                val deck = decks[index]
                DeckItem(deck = deck, onClick = { onDeckClicked(deck.deckId) })
            }
        }
    } else {
        Text(
            text = stringResource(R.string.deck_list_empty),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Composable function that represents a single deck item.
 *
 * @param deck Deck to be displayed.
 * @param onClick Callback function to be invoked when the deck is clicked.
 * @param modifier [Modifier] for styling and layout adjustments.
 */
@Composable
private fun DeckItem(
    deck: Deck,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deck.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = stringResource(R.string.open_deck_button),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            deck.description?.let {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

/*
 * Previews for the Home screen and its components
 */

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    val sampleDecks = listOf(
        Deck(deckId = 1, name = "Deck 1", description = "Description for Deck 1"),
        Deck(deckId = 2, name = "Deck 2", description = "Description for Deck 2"),
        Deck(deckId = 3, name = "Deck 3", description = null)
    )
    HomeBody(
        deckList = sampleDecks,
        onDeckClicked = {},
        lastDeckId = 2
    )
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyPreview() {
    HomeBody(
        deckList = emptyList(),
        onDeckClicked = {},
        lastDeckId = null
    )
}

@Preview
@Composable
fun DeckItemPreview() {
    val sampleDeck = Deck(deckId = 1, name = "Sample Deck", description = "This is a sample deck description.")
    DeckItem(deck = sampleDeck, onClick = {})
}