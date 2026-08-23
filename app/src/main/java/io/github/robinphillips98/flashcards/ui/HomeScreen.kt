package io.github.robinphillips98.flashcards.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.robinphillips98.flashcards.data.Deck
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon

@Composable
fun HomeScreen(
    decks: List<Deck>,
    onCreateDeckClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onDeckClicked: (deckId: Int) -> Unit,
    modifier: Modifier = Modifier
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

        Text(
            text = "Select an option below to get started:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(16.dp)
        )

        // TODO: Implement the create deck functionality
        Button(
            onClick = onCreateDeckClicked,
            modifier = Modifier.widthIn(min = 200.dp),
            enabled = false
        ) {
            Text("Create New Deck")
        }

        // TODO: Implement the settings functionality
        Button(
            onClick = onSettingsClicked,
            modifier = Modifier.widthIn(min = 200.dp),
            enabled = false
        ) {
            Text("Settings")
        }

        DeckList(decks = decks, onDeckClicked = onDeckClicked)
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
                DeckItem(deck = deck, onClick = { onDeckClicked(deck.id) })
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