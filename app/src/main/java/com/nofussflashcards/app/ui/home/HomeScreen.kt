package com.nofussflashcards.app.ui.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nofussflashcards.app.R
import com.nofussflashcards.app.navigation.NavigationDestination
import com.nofussflashcards.app.ui.AppViewModelProvider

object HomeDestination: NavigationDestination {
    override val route = "home"
    override val titleResId = R.string.full_app_name
}

/**
 * Composable function that represents the Home screen of the app.
 *
 * @param onCreateDeckClicked Callback function to be invoked when the "Create Deck" button is clicked.
 * @param onDeckClicked Callback function to be invoked when a deck is clicked, passing the deck ID.
 * @param modifier [Modifier] for styling and layout adjustments.
 * @param viewModel [HomeViewModel] for managing the state and logic of the Home screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    windowSize: WindowWidthSizeClass,
    onCreateDeckClicked: () -> Unit,
    onDeckClicked: (deckId: Int) -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.homeUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val layoutDirection = LocalLayoutDirection.current

    val isTablet = windowSize == WindowWidthSizeClass.Expanded || windowSize == WindowWidthSizeClass.Medium

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeUiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed && event.shouldRetryDecks) {
                        viewModel.retryDecksLoad()
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            HomeScreenTopAppBar(
                onSettingsClicked = onSettingsClicked
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading && !uiState.hasDecksLoadError && !isTablet) {
                ExtendedFloatingActionButton(
                    onClick = onCreateDeckClicked,
                    modifier = Modifier
                        .padding(
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(LocalLayoutDirection.current)
                        ),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.create_deck_button)
                        )
                    },
                    text = { Text(stringResource(R.string.create_deck_button)) }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (uiState.hasDecksLoadError) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.deck_list_load_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { viewModel.retryDecksLoad() },
                    modifier = Modifier.padding(
                        top = dimensionResource(R.dimen.padding_medium_small)
                    )
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
            HomeBodyTablet(
                deckList = uiState.deckList,
                onDeckClicked = { deckId ->
                    onDeckClicked(deckId)
                    viewModel.updateLastOpenedDeckId(deckId)
                },
                onCreateDeckClicked = onCreateDeckClicked,
                lastDeckId = uiState.lastOpenedDeckId,
                showLastOpenedError =  uiState.hasLastOpenedDeckLoadError,
                modifier = Modifier
                    .padding(
                        top = innerPadding.calculateTopPadding() + dimensionResource(R.dimen.padding_medium),
                        start = innerPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.padding_medium),
                        end = innerPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.padding_medium),
                        bottom = dimensionResource(R.dimen.padding_medium)
                    )
            )
        } else {
            HomeBody(
                deckList = uiState.deckList,
                onDeckClicked = { deckId ->
                    onDeckClicked(deckId)
                    viewModel.updateLastOpenedDeckId(deckId)
                },
                lastDeckId = uiState.lastOpenedDeckId,
                showLastOpenedError =  uiState.hasLastOpenedDeckLoadError,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}