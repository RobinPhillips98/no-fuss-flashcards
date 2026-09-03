package com.nofussflashcards.app.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nofussflashcards.app.NoFussFlashCardsTopAppBar
import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.flashcards.Flashcard
import com.nofussflashcards.app.navigation.NavigationDestination
import com.nofussflashcards.app.ui.AppViewModelProvider
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
    windowSize: WindowWidthSizeClass,
    isTablet: Boolean,
    navigateToFlashcards: () -> Unit,
    navigateToFlashcardWithId: (id: Int) -> Unit,
    navigateToEditScreen: (id: Int) -> Unit,
    navigateToFlashcardEntryScreen: (deckId: Int) -> Unit,
    navigateToFlashcardEditScreen: (flashcardId: Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeckDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val layoutDirection = LocalLayoutDirection.current

    val flashcardToDelete by viewModel.flashcardToDelete.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val loadedSuccessfully = !uiState.isLoading && !uiState.hasDeckLoadError && !uiState.hasFlashcardsLoadError

    fun onDeleteDeck() {
        coroutineScope.launch {
            val deckDeletedSuccessfully = viewModel.deleteDeck()
            if (deckDeletedSuccessfully)
                navigateBack()
        }
    }

    fun onDeleteFlashcard(flashcard: Flashcard) {
        coroutineScope.launch {
            viewModel.deleteFlashcard(flashcard)
        }
    }

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
            if (loadedSuccessfully && !isTablet) {
                ExtendedFloatingActionButton(
                    onClick = { navigateToFlashcardEntryScreen(uiState.deckDetails.deckId) },
                    modifier = Modifier
                        .padding(
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(layoutDirection)
                        ),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_flashcard_button)
                        )
                    },
                    text = { Text(stringResource(R.string.add_flashcard_button)) }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        val bodyModifier = Modifier
            .padding(
                top = innerPadding.calculateTopPadding() + dimensionResource(R.dimen.padding_medium),
                start = innerPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.padding_medium),
                end = innerPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.padding_medium),
                bottom = dimensionResource(R.dimen.padding_medium)
            )

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
         } else if (isTablet) {
             DeckDetailsBodyTablet(
                 deckDetails = uiState.deckDetails.toDeck(),
                 flashCards = uiState.flashcards,
                 windowSize = windowSize,
                 flashcardToDelete = flashcardToDelete,
                 hasFlashcardsLoadError = uiState.hasFlashcardsLoadError,
                 navigateToFlashcards = navigateToFlashcards,
                 navigateToFlashcardWithId = navigateToFlashcardWithId,
                 retryLoadFlashcards = viewModel::retryFlashcardsLoad,
                 onDeleteDeck = ::onDeleteDeck,
                 onDeleteFlashcard = ::onDeleteFlashcard,
                 navigateToEditScreen = navigateToEditScreen,
                 navigateToFlashcardEntryScreen = navigateToFlashcardEntryScreen,
                 navigateToFlashcardEditScreen = navigateToFlashcardEditScreen,
                 setFlashCardToDelete = viewModel::setFlashcardToDelete,
                 modifier = bodyModifier
             )
         } else {
             DeckDetailsBody(
                 deckDetails = uiState.deckDetails.toDeck(),
                 flashCards = uiState.flashcards,
                 flashcardToDelete = flashcardToDelete,
                 hasFlashcardsLoadError = uiState.hasFlashcardsLoadError,
                 navigateToFlashcards = navigateToFlashcards,
                 navigateToFlashcardWithId = navigateToFlashcardWithId,
                 retryLoadFlashcards = viewModel::retryFlashcardsLoad,
                 onDeleteDeck = ::onDeleteDeck,
                 onDeleteFlashcard = ::onDeleteFlashcard,
                 navigateToEditScreen = navigateToEditScreen,
                 navigateToFlashcardEditScreen = navigateToFlashcardEditScreen,
                 setFlashCardToDelete = viewModel::setFlashcardToDelete,
                 modifier = bodyModifier
             )
         }
    }
}