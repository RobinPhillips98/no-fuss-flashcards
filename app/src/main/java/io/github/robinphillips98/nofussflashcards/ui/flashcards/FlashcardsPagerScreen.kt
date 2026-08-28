package io.github.robinphillips98.nofussflashcards.ui.flashcards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.AppViewModelProvider
import io.github.robinphillips98.nofussflashcards.ui.decks.toDeck

object FlashcardsPagerDestination: NavigationDestination {
    override val route = "flashcards_pager"
    override val titleResId = R.string.flashcards_pager_title
    const val DECK_ID_ARG = "deckId"
    const val FLASHCARD_ID_ARG = "flashcardId"
    val routeWithArgs = "$route/{$DECK_ID_ARG}?$FLASHCARD_ID_ARG={$FLASHCARD_ID_ARG}"
}

/**
 * Represents the state of the flashcards pager animation.
 *
 * @property generation The current generation of the flashcards shuffle.
 * @property flashcards The list of flashcards to display in the pager.
 */
private data class FlashcardsPagerAnimationState(
    val generation: Int,
    val flashcards: List<Flashcard>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsPagerScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FlashcardsPagerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }


    // Collect events from the ViewModel and show snackbars for relevant events.
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FlashcardsPagerUiEvent.ShowErrorSnackbar -> {
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
            }
        }
    }

    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(FlashcardsPagerDestination.titleResId),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
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
                    stringResource(R.string.deck_load_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { viewModel.retryDeckLoad() },
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
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
            FlashcardsPagerBody(
                deck = uiState.deckDetails.toDeck(),
                flashcards = uiState.flashcards,
                hasFlashcardsLoadError = uiState.hasFlashcardsLoadError,
                modifier = modifier.padding(innerPadding),
                initialSelectedIndex = uiState.initialSelectedIndex,
                hasFlippedCard = uiState.hasFlippedCard,
                shuffleGeneration = uiState.shuffleGeneration,
                onFlashcardClicked = {
                    viewModel.updateHasFlippedCard(true)
                },
                onReshuffleClicked = { viewModel.reshuffleFlashcards() },
                retryLoadFlashcards = { viewModel.retryFlashcardsLoad() }
            )
        }
    }
}

@Composable
private fun FlashcardsPagerBody(
    deck: Deck,
    flashcards: List<Flashcard>,
    hasFlashcardsLoadError: Boolean,
    initialSelectedIndex: Int,
    hasFlippedCard: Boolean,
    shuffleGeneration: Int,
    onFlashcardClicked: () -> Unit,
    onReshuffleClicked: () -> Unit,
    retryLoadFlashcards: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (hasFlashcardsLoadError) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_medium_small)
                )
            ) {
                Text(
                    text = stringResource(R.string.flashcard_list_load_failed),
                    textAlign = TextAlign.Center,
                )
                Button(onClick = retryLoadFlashcards) {
                    Text(stringResource(R.string.retry_button))
                }
            }
        }
    } else if (flashcards.isNotEmpty()) {
        val pageCount = flashcards.size * 400
        val base = pageCount / 2
        val safeInitialIndex = initialSelectedIndex.coerceIn(0, flashcards.lastIndex)
        val startPage = base - (base % flashcards.size) + safeInitialIndex
        val pagerState = rememberPagerState(
            initialPage = startPage,
            pageCount = { pageCount }
        )
        LaunchedEffect(startPage, shuffleGeneration) {
            pagerState.scrollToPage(startPage)
        }
        val currentCard = (pagerState.currentPage % flashcards.size) + 1

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = dimensionResource(R.dimen.padding_small))
        ) {
            Text(
                text = stringResource(R.string.deck_name_label, deck.name),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_medium),
                        vertical = dimensionResource(R.dimen.padding_extra_small)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.flashcard_count_label,
                        currentCard,
                        flashcards.size
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )

                AssistChip(
                    onClick = onReshuffleClicked,
                    label = { Text(stringResource(R.string.reshuffle_button)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.reshuffle_button)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_extra_small)))
            HorizontalDivider()

            // If the user has never flipped a card, show a hint to flip the card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (!hasFlippedCard) {
                    Text(
                        text = stringResource(R.string.flashcard_flip_hint),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = FlashcardsPagerAnimationState(
                        generation = shuffleGeneration,
                        flashcards = flashcards
                    ),
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.96f)) togetherWith
                                (fadeOut() + scaleOut(targetScale = 1.04f))
                    },
                    label = "flashcards_shuffle_animation"
                ) { animatedState ->
                    FlashcardsPager(
                        flashcards = animatedState.flashcards,
                        onFlashcardClicked = onFlashcardClicked,
                        pagerState = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    } else {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.deck_empty),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FlashcardsPager(
    flashcards: List<Flashcard>,
    onFlashcardClicked: () -> Unit,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight
        val pageWidth = (availableWidth * 0.88f).coerceIn(280.dp, 560.dp)
        val cardHeight = (availableHeight * 0.67f).coerceIn(320.dp, 600.dp)
        val horizontalInset =
            ((availableWidth - pageWidth) / 2)
                .coerceAtLeast(dimensionResource(R.dimen.padding_small))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = dimensionResource(R.dimen.padding_medium_small),
                    bottom = dimensionResource(R.dimen.padding_small)
                ),
            pageSize = PageSize.Fixed(pageWidth),
            contentPadding = PaddingValues(horizontal = horizontalInset),
            pageSpacing = dimensionResource(R.dimen.padding_medium_small)
        ) { page ->
            val flashcard = flashcards[page % flashcards.size]
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                FlashcardDetail(
                    flashcardData = flashcard,
                    flashcardIndex = page + 1,
                    onClick = onFlashcardClicked,
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
        hasFlashcardsLoadError = false,
        initialSelectedIndex = 0,
        hasFlippedCard = false,
        shuffleGeneration = 0,
        onFlashcardClicked = {},
        onReshuffleClicked = {},
        retryLoadFlashcards = {}
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
        hasFlashcardsLoadError = false,
        initialSelectedIndex = 0,
        hasFlippedCard = true,
        shuffleGeneration = 0,
        onFlashcardClicked = {},
        onReshuffleClicked = {},
        retryLoadFlashcards = {}
    )
}