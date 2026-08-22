package com.example.flashcards.ui.decks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.example.flashcards.model.FlashcardInfo
import androidx.compose.ui.Modifier
import com.example.flashcards.model.DeckInfo
import com.example.flashcards.ui.flashcards.Flashcard

@Composable
fun DeckFlashcards(
    deck: DeckInfo,
    flashcards: List<FlashcardInfo>,
    modifier: Modifier = Modifier
) {
    if (flashcards.isNotEmpty()) {
        Text(
            text = "Deck: ${deck.name}",
            style = MaterialTheme.typography.titleMedium
        )

        HorizontalDivider()

        FlashcardsPager(flashcards)
    } else {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No flashcards available in this deck.")
        }
    }
}

@Composable
private fun FlashcardsPager(flashcards: List<FlashcardInfo>) {
    val pageCount = flashcards.size * 400
    val pagerState = rememberPagerState(initialPage = pageCount / 2, pageCount = { pageCount })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val flashcard = flashcards[page % flashcards.size]
        Flashcard(flashcardData = flashcard, modifier = Modifier.fillMaxSize())
    }
}