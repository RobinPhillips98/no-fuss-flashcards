package io.github.robinphillips98.flashcards.ui.flashcards

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.robinphillips98.flashcards.data.decks.Deck
import io.github.robinphillips98.flashcards.data.Flashcard

@Composable
fun FlashcardsPagerScreen(
    deck: Deck,
    flashcards: List<Flashcard>,
    modifier: Modifier = Modifier,
    selectedFlashcardId: Int? = null
) {
    if (flashcards.isNotEmpty()) {
        val selectedIndex = if (selectedFlashcardId != null) {
            flashcards.indexOfFirst { it.id == selectedFlashcardId }.takeIf { it >= 0 } ?: 0
        } else {
            0
        }

        val pageCount = flashcards.size * 400
        val base = pageCount / 2
        val startPage = base - (base % flashcards.size) + selectedIndex
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
                    flashcardData = flashcard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight)
                )
            }
        }
    }
}