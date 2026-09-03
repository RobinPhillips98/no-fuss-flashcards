package com.nofussflashcards.app.ui.flashcards

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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import com.nofussflashcards.app.NoFussFlashCardsTopAppBar
import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.decks.Deck
import com.nofussflashcards.app.data.flashcards.Flashcard
import com.nofussflashcards.app.navigation.NavigationDestination
import com.nofussflashcards.app.ui.AppViewModelProvider
import com.nofussflashcards.app.ui.decks.toDeck

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
    windowSize: WindowWidthSizeClass,
    isTablet: Boolean,
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
                initialSelectedIndex = uiState.initialSelectedIndex,
                hasFlippedCard = uiState.hasFlippedCard,
                shuffleGeneration = uiState.shuffleGeneration,
                windowSize = windowSize,
                isTablet = isTablet,
                onFlashcardClicked = {
                    viewModel.updateHasFlippedCard(true)
                },
                onReshuffleClicked = { viewModel.reshuffleFlashcards() },
                retryLoadFlashcards = { viewModel.retryFlashcardsLoad() },
                modifier = modifier.padding(innerPadding)
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
    windowSize: WindowWidthSizeClass,
    isTablet: Boolean,
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

            if (!hasFlippedCard) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding_small)),
                    contentAlignment = Alignment.Center,
                ) {
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
                        pagerState = pagerState,
                        windowSize = windowSize,
                        isTablet = isTablet,
                        onFlashcardClicked = onFlashcardClicked,
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
    pagerState: PagerState,
    windowSize: WindowWidthSizeClass,
    isTablet: Boolean,
    onFlashcardClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight
        val isLandscape = availableWidth > availableHeight

        val sidePeek = when (windowSize) {
            WindowWidthSizeClass.Compact -> 20.dp
            WindowWidthSizeClass.Medium -> if (isLandscape) 24.dp else 32.dp
            WindowWidthSizeClass.Expanded -> if (isLandscape) 32.dp else 40.dp
            else -> 24.dp
        }

        val pageWidth = (availableWidth - sidePeek * 2)
            .coerceAtLeast(280.dp)

        val cardHeight = when {
            isTablet && isLandscape -> (availableHeight * 0.92f).coerceAtLeast(360.dp)
            isLandscape -> minOf(pageWidth * 0.78f, availableHeight * 0.90f).coerceAtLeast(320.dp)
            isTablet -> minOf(pageWidth * 1.08f, availableHeight * 0.88f).coerceAtLeast(360.dp)
            else -> minOf(pageWidth * 1.28f, availableHeight * 0.84f).coerceAtLeast(320.dp)
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = dimensionResource(R.dimen.padding_medium_small),
                    bottom = dimensionResource(R.dimen.padding_small)
                ),
            pageSize = PageSize.Fixed(pageWidth),
            contentPadding = PaddingValues(horizontal = sidePeek),
            pageSpacing = dimensionResource(R.dimen.padding_medium_small)
        ) { page ->
            val flashcard = flashcards[page % flashcards.size]
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                FlashcardDetail(
                    flashcardData = flashcard,
                    flashcardIndex = page + 1,
                    isTablet = isTablet,
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
        windowSize = WindowWidthSizeClass.Compact,
        isTablet = false,
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
        windowSize = WindowWidthSizeClass.Compact,
        isTablet = false,
        onFlashcardClicked = {},
        onReshuffleClicked = {},
        retryLoadFlashcards = {}
    )
}