package io.github.robinphillips98.flashcards.ui.flashcards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.flashcards.FlashCardsAppTopBar
import io.github.robinphillips98.flashcards.data.decks.Deck
import io.github.robinphillips98.flashcards.data.flashcards.Flashcard
import io.github.robinphillips98.flashcards.navigation.NavigationDestination
import io.github.robinphillips98.flashcards.ui.AppViewModelProvider
import io.github.robinphillips98.flashcards.ui.decks.toDeck
import io.github.robinphillips98.flashcards.ui.utils.DeleteConfirmationDialog
import kotlinx.coroutines.launch

object FlashcardsPagerDestination: NavigationDestination {
    override val route = "flashcards_pager"
    override val title = "Flashcards Viewer"
    const val DECK_ID_ARG = "deckId"
    const val FLASHCARD_ID_ARG = "flashcardId"
    val routeWithArgs = "$route/{$DECK_ID_ARG}?$FLASHCARD_ID_ARG={$FLASHCARD_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsPagerScreen(
    navigateBack: () -> Unit,
    navigateToFlashCardEntryScreen: (deckId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FlashcardsPagerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val flashcardToDelete by viewModel.flashcardToDelete.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            FlashCardsAppTopBar(
                title = FlashcardsPagerDestination.title,
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navigateToFlashCardEntryScreen(uiState.deckDetails.deckId) },
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    ),
                icon = { Icon(Icons.Default.Add,contentDescription = "Add Flashcard") },
                text = { Text("Add Flashcard") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        FlashcardsPagerBody(
            deck = uiState.deckDetails.toDeck(),
            flashcards = uiState.flashcards,
            flashcardToDelete = flashcardToDelete,
            onDeleteFlashcard = { flashcard ->
                coroutineScope.launch {
                    viewModel.deleteFlashcard(flashcard)
                }
            },
            setFlashCardToDelete = { flashcard -> viewModel.setFlashcardToDelete(flashcard) },
            modifier = modifier.padding(innerPadding),
            initialSelectedIndex = uiState.initialSelectedIndex
        )
    }
}

@Composable
private fun FlashcardsPagerBody(
    deck: Deck,
    flashcards: List<Flashcard>,
    flashcardToDelete: Flashcard?,
    initialSelectedIndex: Int,
    onDeleteFlashcard: (flashcard: Flashcard) -> Unit,
    setFlashCardToDelete: (flashcard: Flashcard?) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (flashcards.isNotEmpty()) {
        var deleteFlashcardConfirmationOpen by remember { mutableStateOf(false) }

        val pageCount = flashcards.size * 400
        val base = pageCount / 2
        val safeInitialIndex = initialSelectedIndex.coerceIn(0, flashcards.lastIndex)
        val startPage = base - (base % flashcards.size) + safeInitialIndex
        val pagerState = rememberPagerState(initialPage = startPage, pageCount = { pageCount })
        val currentCard = (pagerState.currentPage % flashcards.size) + 1

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            Text(
                text = "Deck: ${deck.name}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = "Card $currentCard/${flashcards.size}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                FlashcardsPager(
                    flashcards = flashcards,
                    pagerState = pagerState,
                    onDelete = { flashcard ->
                        setFlashCardToDelete(flashcard)
                        deleteFlashcardConfirmationOpen = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (deleteFlashcardConfirmationOpen) {
                DeleteConfirmationDialog(
                    objectType = "flashcard",
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
    } else {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No flashcards available in this deck.")
        }
    }
}

@Composable
private fun FlashcardsPager(
    flashcards: List<Flashcard>,
    pagerState: PagerState,
    onDelete: (Flashcard) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight
        val pageWidth = (availableWidth * 0.88f).coerceIn(280.dp, 560.dp)
        val cardHeight = (availableHeight * 0.45f).coerceIn(240.dp, 420.dp)
        val horizontalInset = ((availableWidth - pageWidth) / 2).coerceAtLeast(8.dp)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp, bottom = 8.dp),
            pageSize = PageSize.Fixed(pageWidth),
            contentPadding = PaddingValues(horizontal = horizontalInset),
            pageSpacing = 12.dp
        ) { page ->
            val flashcard = flashcards[page % flashcards.size]
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                FlashcardDetail(
                    flashcard = flashcard,
                    onDelete = { onDelete(flashcard) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight)
                )
            }
        }
    }
}