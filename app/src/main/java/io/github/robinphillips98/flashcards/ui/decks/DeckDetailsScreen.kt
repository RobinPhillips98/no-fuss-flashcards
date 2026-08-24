package io.github.robinphillips98.flashcards.ui.decks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.flashcards.FlashCardsAppTopBar
import io.github.robinphillips98.flashcards.data.decks.Deck
import io.github.robinphillips98.flashcards.data.flashcards.Flashcard
import io.github.robinphillips98.flashcards.navigation.NavigationDestination
import io.github.robinphillips98.flashcards.ui.AppViewModelProvider
import io.github.robinphillips98.flashcards.ui.utils.DeleteConfirmationDialog
import kotlinx.coroutines.launch

object DeckDetailsDestination: NavigationDestination {
    override val route = "deck_details"
    override val title = "Deck Details"
    const val DECK_ID_ARG = "deckId"
    val routeWithArgs = "$route/{$DECK_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailsScreen(
    navigateToFlashcards: () -> Unit,
    navigateToFlashcardWithId: (id: Int) -> Unit,
    navigateToEditScreen: (id: Int) -> Unit,
    navigateToFlashCardEntryScreen: (deckId: Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeckDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            FlashCardsAppTopBar(
                title = DeckDetailsDestination.title,
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToFlashCardEntryScreen(uiState.deckDetails.deckId) },
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    ),
            ) {
                Icon(Icons.Default.Add,contentDescription = "Add Flashcard")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isDeckMissing) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Deck not found.")
            }
        } else {
            DeckDetailsBody(
                deckDetails = uiState.deckDetails.toDeck(),
                flashCards = uiState.flashcards,
                navigateToFlashcards = navigateToFlashcards,
                navigateToFlashcardWithId = navigateToFlashcardWithId,
                onDelete = {
                    coroutineScope.launch {
                        viewModel.deleteDeck()
                        navigateBack()
                    }
                },
                navigateToEditScreen = navigateToEditScreen,
                modifier = modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun DeckDetailsBody(
    deckDetails: Deck,
    flashCards: List<Flashcard>,
    navigateToFlashcards: () -> Unit,
    navigateToFlashcardWithId: (id: Int) -> Unit,
    navigateToEditScreen: (id: Int) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var deleteConfirmationOpen by remember { mutableStateOf(false) }
    val flashcardsAvailable = flashCards.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Deck: ${deckDetails.name}",
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
                Text(text = "Open Cards")
            }

            Button(
                onClick = { navigateToEditScreen(deckDetails.deckId) }
            ) {
                Text("Edit Deck")
            }

            Button(
                onClick = { deleteConfirmationOpen = true }
            ) {
                Text("Delete Deck")
            }
        }

        HorizontalDivider()

        if (flashcardsAvailable) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(flashCards) { flashcard ->
                    FlashcardItem(
                        flashcard = flashcard,
                        onFlashCardClicked = navigateToFlashcardWithId
                    )
                }
            }
        } else {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No flashcards available in this deck.")
            }
        }
    }

    if (deleteConfirmationOpen) {
        DeleteConfirmationDialog(
            objectType = "deck",
            onDeleteConfirm = {
                deleteConfirmationOpen = false
                onDelete()
            },
            onDeleteCancel = { deleteConfirmationOpen = false },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun FlashcardItem(
    flashcard: Flashcard,
    onFlashCardClicked: (id: Int) -> Unit,
    modifier: Modifier = Modifier
) {


    Card(
        onClick = { onFlashCardClicked(flashcard.flashcardId) },
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