package com.nofussflashcards.app.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.decks.Deck
import com.nofussflashcards.app.data.flashcards.Flashcard
import com.nofussflashcards.app.ui.utils.DeleteConfirmationDialog
import com.nofussflashcards.app.ui.utils.SidebarActionButton
import com.nofussflashcards.app.ui.utils.SidebarMenu


/**
 * Composable function that displays the details of a deck and a grid of its flashcards.
 *
 * Body of the deck details screen, intended for use on smaller screens, such as phones.
 *
 * @param deckDetails The details of the deck to display.
 * @param flashCards The list of flashcards associated with the deck.
 * @param flashcardToDelete The flashcard that is currently selected for deletion (if any).
 * @param hasFlashcardsLoadError A flag indicating whether there was an error loading the flashcards.
 * @param navigateToFlashcards A callback function to navigate to the flashcards screen.
 * @param navigateToFlashcardWithId A callback function to navigate to a specific flashcard by its ID.
 * @param navigateToEditScreen A callback function to navigate to the edit screen for the deck.
 * @param navigateToFlashcardEditScreen A callback function to navigate to the edit screen for a specific flashcard.
 * @param retryLoadFlashcards A callback function to retry loading the flashcards in case of an error.
 * @param onDeleteDeck A callback function to handle the deletion of the deck.
 * @param onDeleteFlashcard A callback function to handle the deletion of a specific flashcard.
 * @param setFlashCardToDelete A callback function to set the flashcard that is currently selected for deletion.
 * @param modifier An optional [Modifier] for styling and layout adjustments.
 */
@Composable
fun DeckDetailsBody(
    deckDetails: Deck,
    flashCards: List<Flashcard>,
    flashcardToDelete: Flashcard?,
    hasFlashcardsLoadError: Boolean,
    navigateToFlashcards: () -> Unit,
    navigateToFlashcardWithId: (id: Int) -> Unit,
    navigateToEditScreen: (id: Int) -> Unit,
    navigateToFlashcardEditScreen: (flashcardId: Int) -> Unit,
    retryLoadFlashcards: () -> Unit,
    onDeleteDeck: () -> Unit,
    onDeleteFlashcard: (flashcard: Flashcard) -> Unit,
    setFlashCardToDelete: (flashcard: Flashcard?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var deleteDeckConfirmationOpen by remember { mutableStateOf(false) }
    var deleteFlashcardConfirmationOpen by remember { mutableStateOf(false) }
    val flashcardsAvailable = flashCards.isNotEmpty() && !hasFlashcardsLoadError

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.deck_name_label, deckDetails.name),
            style = MaterialTheme.typography.titleMedium
        )

        deckDetails.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = navigateToFlashcards,
                enabled = flashcardsAvailable,
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small))
            ) {
                Text(stringResource(R.string.open_deck_button))
            }

            Button(
                onClick = { navigateToEditScreen(deckDetails.deckId) }
            ) {
                Text(stringResource(R.string.edit_deck_button))
            }

            Button(
                onClick = { deleteDeckConfirmationOpen = true }
            ) {
                Text(stringResource(R.string.delete_deck_button))
            }
        }

        HorizontalDivider()

        FlashcardsGrid(
            flashcards = flashCards,
            flashcardsAvailable = flashcardsAvailable,
            hasFlashcardsLoadError = hasFlashcardsLoadError,
            onFlashCardClicked = navigateToFlashcardWithId,
            onEditClicked = navigateToFlashcardEditScreen,
            onDelete = { flashcard ->
                setFlashCardToDelete(flashcard)
                deleteFlashcardConfirmationOpen = true
            },
            onRetryLoad = retryLoadFlashcards,
            modifier = Modifier.weight(1f)
        )
    }

    if (deleteDeckConfirmationOpen) {
        DeleteConfirmationDialog(
            objectType = stringResource(R.string.deck_object_type),
            onDeleteConfirm = {
                deleteDeckConfirmationOpen = false
                onDeleteDeck()
            },
            onDeleteCancel = { deleteDeckConfirmationOpen = false },
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        )
    }

    if (deleteFlashcardConfirmationOpen) {
        DeleteConfirmationDialog(
            objectType = stringResource(R.string.flashcard_object_type),
            onDeleteConfirm = {
                deleteFlashcardConfirmationOpen = false
                flashcardToDelete?.let { onDeleteFlashcard(it) }
                setFlashCardToDelete(null)
            },
            onDeleteCancel = { deleteFlashcardConfirmationOpen = false },
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}

/**
 * Composable function that displays the details of a deck and a grid of its flashcards.
 *
 * Body of the deck details screen, intended for use on larger screens, such as tablets.
 *
 * @param deckDetails The details of the deck to display.
 * @param flashCards The list of flashcards associated with the deck.
 * @param flashcardToDelete The flashcard that is currently selected for deletion (if any).
 * @param hasFlashcardsLoadError A flag indicating whether there was an error loading the flashcards.
 * @param navigateToFlashcards A callback function to navigate to the flashcards screen.
 * @param navigateToFlashcardWithId A callback function to navigate to a specific flashcard by its ID.
 * @param navigateToEditScreen A callback function to navigate to the edit screen for the deck.
 * @param navigateToFlashcardEntryScreen A callback function to navigate to the entry screen for a new flashcard.
 * @param navigateToFlashcardEditScreen A callback function to navigate to the edit screen for a specific flashcard.
 * @param retryLoadFlashcards A callback function to retry loading the flashcards in case of an error.
 * @param onDeleteDeck A callback function to handle the deletion of the deck.
 * @param onDeleteFlashcard A callback function to handle the deletion of a specific flashcard.
 * @param setFlashCardToDelete A callback function to set the flashcard that is currently selected for deletion.
 * @param modifier An optional [Modifier] for styling and layout adjustments.
 */
@Composable
fun DeckDetailsBodyTablet(
    deckDetails: Deck,
    flashCards: List<Flashcard>,
    windowSize: WindowWidthSizeClass,
    flashcardToDelete: Flashcard?,
    hasFlashcardsLoadError: Boolean,
    navigateToFlashcards: () -> Unit,
    navigateToFlashcardWithId: (id: Int) -> Unit,
    navigateToEditScreen: (id: Int) -> Unit,
    navigateToFlashcardEntryScreen: (deckId: Int) -> Unit,
    navigateToFlashcardEditScreen: (flashcardId: Int) -> Unit,
    retryLoadFlashcards: () -> Unit,
    onDeleteDeck: () -> Unit,
    onDeleteFlashcard: (flashcard: Flashcard) -> Unit,
    setFlashCardToDelete: (flashcard: Flashcard?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var deleteDeckConfirmationOpen by remember { mutableStateOf(false) }
    var deleteFlashcardConfirmationOpen by remember { mutableStateOf(false) }
    val flashcardsAvailable = flashCards.isNotEmpty() && !hasFlashcardsLoadError
    val numOfColumns = if (windowSize == WindowWidthSizeClass.Expanded) 3 else 2

    Row(modifier = modifier) {
        DeckDetailsSidebar(
            deckName = deckDetails.name,
            deckDescription = deckDetails.description,
            flashcardsAvailable = flashcardsAvailable,
            navigateToFlashcards = navigateToFlashcards,
            navigateToEditScreen = { navigateToEditScreen(deckDetails.deckId) },
            navigateToFlashcardEntryScreen = { navigateToFlashcardEntryScreen(deckDetails.deckId) },
            onDeleteDeck = { deleteDeckConfirmationOpen = true },
            modifier = Modifier.weight(1f)
        )

        FlashcardsGrid(
            flashcards = flashCards,
            flashcardsAvailable = flashcardsAvailable,
            numOfColumns = numOfColumns,
            hasFlashcardsLoadError = hasFlashcardsLoadError,
            onFlashCardClicked = navigateToFlashcardWithId,
            onEditClicked = navigateToFlashcardEditScreen,
            onDelete = { flashcard ->
                setFlashCardToDelete(flashcard)
                deleteFlashcardConfirmationOpen = true
            },
            onRetryLoad = retryLoadFlashcards,
            modifier = Modifier.weight(3f)
        )
    }

    if (deleteDeckConfirmationOpen) {
        DeleteConfirmationDialog(
            objectType = stringResource(R.string.deck_object_type),
            onDeleteConfirm = {
                deleteDeckConfirmationOpen = false
                onDeleteDeck()
            },
            onDeleteCancel = { deleteDeckConfirmationOpen = false },
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        )
    }

    if (deleteFlashcardConfirmationOpen) {
        DeleteConfirmationDialog(
            objectType = stringResource(R.string.flashcard_object_type),
            onDeleteConfirm = {
                deleteFlashcardConfirmationOpen = false
                flashcardToDelete?.let { onDeleteFlashcard(it) }
                setFlashCardToDelete(null)
            },
            onDeleteCancel = { deleteFlashcardConfirmationOpen = false },
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}

@Composable
private fun DeckDetailsSidebar(
    deckName: String,
    deckDescription: String?,
    flashcardsAvailable: Boolean,
    navigateToFlashcards: () -> Unit,
    navigateToEditScreen: () -> Unit,
    navigateToFlashcardEntryScreen: () -> Unit,
    onDeleteDeck: () -> Unit,
    modifier: Modifier = Modifier
) {
    SidebarMenu(
        headerTitle = stringResource(R.string.deck_name_label, deckName),
        modifier = modifier,
        headerSubtitle = deckDescription,
    ) {
        SidebarActionButton(
            text = stringResource(R.string.open_deck_button),
            onClick = navigateToFlashcards,
            enabled = flashcardsAvailable
        )

        SidebarActionButton(
            text = stringResource(R.string.edit_deck_button),
            onClick = navigateToEditScreen
        )

        SidebarActionButton(
            text = stringResource(R.string.add_flashcard_button),
            onClick = navigateToFlashcardEntryScreen
        )

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))

        SidebarActionButton(
            text = stringResource(R.string.delete_deck_button),
            onClick = onDeleteDeck,
            isDangerous = true
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DeckDetailsBodyPreview() {
    val sampleDeck = Deck(
        deckId = 1,
        name = "Sample Deck",
        description = "This is a sample deck description."
    )
    val sampleFlashcards = listOf(
        Flashcard(flashcardId = 1, deckId = 1, term = "Term 1", definition = "Definition 1"),
        Flashcard(flashcardId = 2, deckId = 1, term = "Term 2", definition = "Definition 2"),
        Flashcard(flashcardId = 3, deckId = 1, term = "Term 3", definition = "Definition 3"),
        Flashcard(flashcardId = 4, deckId = 1, term = "Term 4", definition = "Definition 4"),
        Flashcard(flashcardId = 5, deckId = 1, term = "Term 5", definition = "Definition 5"),
        Flashcard(flashcardId = 6, deckId = 1, term = "Term 6", definition = "Definition 6"),
        Flashcard(flashcardId = 7, deckId = 1, term = "Term 7", definition = "Definition 7: " +
                "This is a longer definition that should take up a few lines. Text should overflow " +
                "with ellipsis if it exceeds the maximum number of lines allowed in the UI."),
    )
    DeckDetailsBody(
        deckDetails = sampleDeck,
        flashCards = sampleFlashcards,
        flashcardToDelete = null,
        hasFlashcardsLoadError = false,
        navigateToFlashcards = {},
        navigateToFlashcardWithId = {},
        navigateToEditScreen = {},
        navigateToFlashcardEditScreen = {},
        retryLoadFlashcards = {},
        onDeleteDeck = {},
        onDeleteFlashcard = {},
        setFlashCardToDelete = {},
    )
}

@Preview(showBackground = true)
@Composable
fun DeckDetailsBodyNoFlashcardsPreview() {
    val sampleDeck = Deck(
        deckId = 1,
        name = "Sample Deck",
        description = "This is a sample deck description."
    )
    DeckDetailsBody(
        deckDetails = sampleDeck,
        flashCards = emptyList(),
        flashcardToDelete = null,
        hasFlashcardsLoadError = false,
        navigateToFlashcards = {},
        navigateToFlashcardWithId = {},
        navigateToEditScreen = {},
        navigateToFlashcardEditScreen = {},
        retryLoadFlashcards = {},
        onDeleteDeck = {},
        onDeleteFlashcard = {},
        setFlashCardToDelete = {}
    )
}