package com.nofussflashcards.app.ui.decks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.flashcards.Flashcard


/**
 * Displays a grid of flashcards.
 *
 * Intended for use on the DeckDetailsScreen to show the flashcards in a deck.
 *
 * If there are no flashcards, displays a message indicating that the deck is empty.
 * If there was an error loading the flashcards, displays an error message with a retry button.
 */
@Composable
fun FlashcardsGrid(
    flashcards: List<Flashcard>,
    flashcardsAvailable: Boolean,
    hasFlashcardsLoadError: Boolean,
    onFlashCardClicked: (id: Int) -> Unit,
    onEditClicked: (id: Int) -> Unit,
    onDelete: (flashcard: Flashcard) -> Unit,
    onRetryLoad: () -> Unit,
    modifier: Modifier = Modifier,
    numOfColumns: Int = 2
) {
    if (flashcardsAvailable) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(numOfColumns),
            modifier = modifier.padding(dimensionResource(R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.footer_height))
        ) {
            items(flashcards) { flashcard ->
                FlashcardItem(
                    flashcard = flashcard,
                    onFlashCardClicked = onFlashCardClicked,
                    onEditClicked = onEditClicked,
                    onDelete = { onDelete(flashcard) },
                )
            }
        }
    } else if (hasFlashcardsLoadError) {
        Column(
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.padding_medium))
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.flashcard_list_load_failed),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetryLoad,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium_small))
            ) {
                Text(stringResource(R.string.retry_button))
            }
        }
    } else {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.deck_empty))
        }
    }
}

@Composable
private fun FlashcardItem(
    flashcard: Flashcard,
    onFlashCardClicked: (id: Int) -> Unit,
    onEditClicked: (id: Int) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onFlashCardClicked(flashcard.flashcardId) },
        modifier = modifier.height(dimensionResource(R.dimen.flashcard_item_height)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = flashcard.term,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = stringResource(R.string.open_flashcard_button),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_small)
                )
            )

            if (!flashcard.definition.isNullOrBlank()) {
                Text(
                    text = flashcard.definition,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            } else if (flashcard.imagePath != null) {
                AsyncImage(
                    model = flashcard.imagePath,
                    contentDescription = stringResource(
                        R.string.image_content_description,
                        flashcard.term
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(dimensionResource(R.dimen.image_size))
                        .padding(dimensionResource(R.dimen.padding_small))
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledIconButton(
                    onClick = { onEditClicked(flashcard.flashcardId) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_flashcard_button),
                    )
                }
                FilledIconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_flashcard_button),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun FlashcardItemPreview() {
    val sampleFlashcard = Flashcard(
        flashcardId = 1,
        deckId = 1,
        term = "Sample Term",
        definition = "This is a sample definition for the flashcard. It can be quite long and " +
                "should demonstrate how the text will be displayed in the UI."
    )
    FlashcardItem(
        flashcard = sampleFlashcard,
        onFlashCardClicked = {},
        onEditClicked = {},
        onDelete = {}
    )
}