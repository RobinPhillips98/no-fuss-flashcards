package io.github.robinphillips98.flashcards.ui.decks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.robinphillips98.flashcards.data.Deck
import io.github.robinphillips98.flashcards.data.Flashcard

@Composable
fun DeckOverview(
    deck: Deck,
    flashCards: List<Flashcard>,
    onOpenCardsClicked: () -> Unit,
    onFlashCardClicked: (id: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (flashCards.isNotEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Deck: ${deck.name}",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = onOpenCardsClicked,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = "Open Cards")
            }

            HorizontalDivider()

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(flashCards) { flashcard ->
                    FlashcardItem(flashcard = flashcard, onFlashCardClicked = onFlashCardClicked)
                }
            }
        }
    }
    else {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No flashcards available in this deck.")
        }
    }
}

@Composable
private fun FlashcardItem(
    flashcard: Flashcard,
    onFlashCardClicked: (id: Int) -> Unit,
    modifier: Modifier = Modifier
) {


    Card(
        onClick = { onFlashCardClicked(flashcard.id) },
        modifier = modifier.fillMaxSize(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = flashcard.term,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Open flashcard",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            ExpandableDescriptionText(text = flashcard.definition)
        }
    }
}

@Composable
private fun ExpandableDescriptionText(text: String) {
    var isExpanded by remember { mutableStateOf(false) }
    var hasOverflow by remember { mutableStateOf(false) }

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        maxLines = if (isExpanded) Int.MAX_VALUE else 4,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { textLayoutResult ->
            hasOverflow = textLayoutResult.hasVisualOverflow
        }
    )

    if (hasOverflow || isExpanded) {
        Text(
            text = if (isExpanded) "Show less" else "Show more",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable { isExpanded = !isExpanded }
        )
    }
}