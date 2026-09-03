package com.nofussflashcards.app.ui.decks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.decks.Deck
import com.nofussflashcards.app.data.flashcards.Flashcard
import com.nofussflashcards.app.ui.utils.DeleteConfirmationDialog


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
 * @param innerPadding An optional [PaddingValues] for inner padding adjustments.
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
    innerPadding: PaddingValues = PaddingValues(),
) {
    var deleteDeckConfirmationOpen by remember { mutableStateOf(false) }
    var deleteFlashcardConfirmationOpen by remember { mutableStateOf(false) }
    val flashcardsAvailable = flashCards.isNotEmpty() && !hasFlashcardsLoadError
    val layoutDirection = LocalLayoutDirection.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding() + dimensionResource(R.dimen.padding_medium),
                start = innerPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.padding_medium),
                end = innerPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.padding_medium),
                bottom = dimensionResource(R.dimen.padding_medium)
            )
    ) {
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
 * @param innerPadding An optional [PaddingValues] for inner padding adjustments.
 */
@Composable
fun DeckDetailsBodyTablet(
    deckDetails: Deck,
    flashCards: List<Flashcard>,
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
    innerPadding: PaddingValues = PaddingValues(),
) {
    var deleteDeckConfirmationOpen by remember { mutableStateOf(false) }
    var deleteFlashcardConfirmationOpen by remember { mutableStateOf(false) }
    val flashcardsAvailable = flashCards.isNotEmpty() && !hasFlashcardsLoadError
    val layoutDirection = LocalLayoutDirection.current
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding() + dimensionResource(R.dimen.padding_medium),
                start = innerPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.padding_medium),
                end = innerPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.padding_medium),
                bottom = dimensionResource(R.dimen.padding_medium)
            )
    ) {
        DeckDetailsSidebar(
            deckName = deckDetails.name,
            deckDescription = deckDetails.description,
            flashcardsAvailable = flashcardsAvailable,
            navigateToFlashcards = navigateToFlashcards,
            navigateToEditScreen = { navigateToEditScreen(deckDetails.deckId) },
            navigateToFlashcardEntryScreen = { navigateToFlashcardEntryScreen(deckDetails.deckId) },
            onDeleteDeck = { deleteDeckConfirmationOpen = true }
        )

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
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
            .widthIn(min = 240.dp, max = 320.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        ) {
            Text(
                text = stringResource(R.string.deck_name_label, deckName),
                style = MaterialTheme.typography.titleMedium
            )

            deckDescription?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium)),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

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
}

@Composable
private fun SidebarActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDangerous: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        colors = if (isDangerous) {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        } else {
            ButtonDefaults.filledTonalButtonColors()
        },
        modifier = modifier.fillMaxWidth()
    ) {
        if (icon != null) {
            icon()
        }
        Text(text)
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