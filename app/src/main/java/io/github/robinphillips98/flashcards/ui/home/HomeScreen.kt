package io.github.robinphillips98.flashcards.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.flashcards.FlashCardsAppTopBar
import io.github.robinphillips98.flashcards.data.decks.Deck
import io.github.robinphillips98.flashcards.navigation.NavigationDestination
import io.github.robinphillips98.flashcards.ui.AppViewModelProvider

object HomeDestination: NavigationDestination {
    override val route = "home"
    override val title = "Flashcards App"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateDeckClicked: () -> Unit,
    onDeckClicked: (deckId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            FlashCardsAppTopBar(
                title = HomeDestination.title,
                canNavigateBack = false,
            )
        },
        // TODO: Add FAB group for settings and create deck (Blocked until settings screen is implemented)
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateDeckClicked,
//                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    ),
                icon = { Icon(Icons.Default.Add,contentDescription = "Create Deck") },
                text = { Text("Create Deck") }
            )
        }
    ) { innerPadding ->
        HomeBody(
            deckList = homeUiState.deckList,
            onDeckClicked = onDeckClicked,
            modifier = modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun HomeBody(
    deckList: List<Deck>,
    onDeckClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to the Flashcards App!",
            style = MaterialTheme.typography.titleLarge
        )

        HorizontalDivider()

        DeckList(decks = deckList, onDeckClicked = onDeckClicked)
    }
}

@Composable
private fun DeckList(
    decks: List<Deck>,
    onDeckClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    if (decks.isNotEmpty()) {
        LazyColumn(modifier = modifier) {
            item {
                Text(
                    text = "Available Decks",
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
            text = "No decks available. Please create a deck to get started.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

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
                    contentDescription = "Open deck",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

            deck.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {},
                    enabled = false, // TODO: Implement the edit deck functionality
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Edit Deck")
                }

                Button(
                    onClick = {},
                    enabled = false // TODO: Implement the delete deck functionality
                ) {
                    Text("Delete Deck")
                }
            }
        }
    }
}