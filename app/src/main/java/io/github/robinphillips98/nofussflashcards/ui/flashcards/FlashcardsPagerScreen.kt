package io.github.robinphillips98.nofussflashcards.ui.flashcards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.AppViewModelProvider
import io.github.robinphillips98.nofussflashcards.ui.decks.toDeck

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
    modifier: Modifier = Modifier,
    viewModel: FlashcardsPagerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = FlashcardsPagerDestination.title,
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        FlashcardsPagerBody(
            deck = uiState.deckDetails.toDeck(),
            flashcards = uiState.flashcards,
            modifier = modifier.padding(innerPadding),
            initialSelectedIndex = uiState.initialSelectedIndex
        )
    }
}

@Composable
private fun FlashcardsPagerBody(
    deck: Deck,
    flashcards: List<Flashcard>,
    initialSelectedIndex: Int,
    modifier: Modifier = Modifier,
) {
    if (flashcards.isNotEmpty()) {

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
                    modifier = Modifier.fillMaxWidth()
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
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight
        val pageWidth = (availableWidth * 0.88f).coerceIn(280.dp, 560.dp)
        val cardHeight = (availableHeight * 0.67f).coerceIn(320.dp, 600.dp)
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
                    flashcardData = flashcard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardsPagerBodyPreview() {
    val sampleDeck = Deck(
        deckId = 1,
        name = "Sample Deck",
        description = "A sample deck for preview purposes."
    )
    val sampleFlashcards = listOf(
        Flashcard(flashcardId = 1, deckId = 1, term = "Term 1", definition = "Definition 1"),
        Flashcard(flashcardId = 2, deckId = 1, term = "Term 2", definition = "Definition 2"),
        Flashcard(flashcardId = 3, deckId = 1, term = "Term 3", definition = "Definition 3")
    )
    FlashcardsPagerBody(
        deck = sampleDeck,
        flashcards = sampleFlashcards,
        initialSelectedIndex = 0
    )
}

@Preview(showBackground = true)
@Composable
fun FlashcardPagerBodyEmptyPreview() {
    val sampleDeck = Deck(
        deckId = 1,
        name = "Sample Deck",
        description = "A sample deck for preview purposes."
    )
    FlashcardsPagerBody(
        deck = sampleDeck,
        flashcards = emptyList(),
        initialSelectedIndex = 0
    )
}