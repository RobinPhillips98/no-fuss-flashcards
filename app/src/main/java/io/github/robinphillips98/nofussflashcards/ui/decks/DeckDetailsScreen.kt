package io.github.robinphillips98.nofussflashcards.ui.decks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.AppViewModelProvider
import io.github.robinphillips98.nofussflashcards.ui.utils.DeleteConfirmationDialog
import kotlinx.coroutines.launch

object DeckDetailsDestination: NavigationDestination {
    override val route = "deck_details"
    override val titleResId = R.string.deck_detail_title
    const val DECK_ID_ARG = "deckId"
    val routeWithArgs = "$route/{$DECK_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailsScreen(
    navigateToFlashcards: () -> Unit,
    navigateToFlashcardWithId: (id: Int) -> Unit,
    navigateToEditScreen: (id: Int) -> Unit,
    navigateToFlashcardEntryScreen: (deckId: Int) -> Unit,
    navigateToFlashcardEditScreen: (flashcardId: Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeckDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val flashcardToDelete by viewModel.flashcardToDelete.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val loadedSuccessfully = !uiState.isLoading && !uiState.hasDeckLoadError && !uiState.hasFlashcardsLoadError

    // Collect events from the ViewModel and show snackbars for relevant events.
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DeckDetailsUiEvent.ShowErrorSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        if (event.shouldRetryDeck) viewModel.retryDeckLoad()
                        if (event.shouldRetryFlashcards) viewModel.retryFlashcardsLoad()
                    }
                }
                is DeckDetailsUiEvent.ShowDeletionSnackbar -> {
                    snackbarHostState.showSnackbar(event.message, withDismissAction = true)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(DeckDetailsDestination.titleResId),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            if (loadedSuccessfully) {
                FloatingActionButton(
                    onClick = { navigateToFlashcardEntryScreen(uiState.deckDetails.deckId) },
                    modifier = Modifier
                        .padding(
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(LocalLayoutDirection.current)
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_flashcard_button)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
         if (uiState.hasDeckLoadError) {
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
                    onClick = { viewModel.retryDeckLoad() },
                    modifier = Modifier.padding(top = 16.dp)
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
            DeckDetailsBody(
                deckDetails = uiState.deckDetails.toDeck(),
                flashCards = uiState.flashcards,
                flashcardToDelete = flashcardToDelete,
                hasFlashcardsLoadError = uiState.hasFlashcardsLoadError,
                navigateToFlashcards = navigateToFlashcards,
                navigateToFlashcardWithId = navigateToFlashcardWithId,
                retryLoadFlashcards = { viewModel.retryFlashcardsLoad() },
                onDeleteDeck = {
                    coroutineScope.launch {
                        val deckDeletedSuccessfully = viewModel.deleteDeck()
                        if (deckDeletedSuccessfully)
                            navigateBack()
                    }
                },
                onDeleteFlashcard = { flashcard ->
                    coroutineScope.launch {
                        viewModel.deleteFlashcard(flashcard)
                    }
                },
                navigateToEditScreen = navigateToEditScreen,
                navigateToFlashcardEditScreen = navigateToFlashcardEditScreen,
                setFlashCardToDelete = { flashcard ->
                    viewModel.setFlashcardToDelete(flashcard)
                },
                innerPadding = innerPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun DeckDetailsBody(
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
                top = innerPadding.calculateTopPadding() + 16.dp,
                start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                bottom = 16.dp
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
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = navigateToFlashcards,
                enabled = flashcardsAvailable,
                modifier = Modifier.padding(vertical = 8.dp)
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

        if (flashcardsAvailable) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 72.dp)
            ) {
                items(flashCards) { flashcard ->
                    FlashcardItem(
                        flashcard = flashcard,
                        onFlashCardClicked = navigateToFlashcardWithId,
                        onEditClicked = navigateToFlashcardEditScreen,
                        onDelete = {
                            setFlashCardToDelete(flashcard)
                            deleteFlashcardConfirmationOpen = true
                        }
                    )
                }
            }
        } else if (hasFlashcardsLoadError) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
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
                    onClick = { retryLoadFlashcards() },
                    modifier = Modifier.padding(top = 12.dp)
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

    if (deleteDeckConfirmationOpen) {
        DeleteConfirmationDialog(
            objectType = stringResource(R.string.deck_object_type),
            onDeleteConfirm = {
                deleteDeckConfirmationOpen = false
                onDeleteDeck()
            },
            onDeleteCancel = { deleteDeckConfirmationOpen = false },
            modifier = Modifier.padding(16.dp)
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
            modifier = Modifier.padding(16.dp)
        )
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
        modifier = modifier.height(160.dp),
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
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            if (flashcard.definition != null) {
                Text(
                    text = flashcard.definition,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
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
        Flashcard(flashcardId = 7, deckId = 1, term = "Term 7", definition = "Definition 7"),
        Flashcard(flashcardId = 8, deckId = 1, term = "Term 8", definition = "Definition 8"),
        Flashcard(flashcardId = 9, deckId = 1, term = "Term 9", definition = "Definition 9: " +
                "This is a longer definition that should take up a few lines. " +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor " +
                "incididunt ut labore et dolore magna aliqua.")
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