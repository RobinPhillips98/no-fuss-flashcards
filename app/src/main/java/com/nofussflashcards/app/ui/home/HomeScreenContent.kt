package com.nofussflashcards.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.decks.Deck
import com.nofussflashcards.app.ui.theme.PlayfairDisplay
import com.nofussflashcards.app.ui.utils.SidebarActionButton
import com.nofussflashcards.app.ui.utils.SidebarMenu

/**
 * Composable function that represents the top app bar of the Home screen.
 *
 * @param onSettingsClicked Callback function to be invoked when the settings button is clicked.
 * @param modifier [Modifier] for styling and layout adjustments.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopAppBar(
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_size))
                        .padding(dimensionResource(R.dimen.padding_small)),
                    painter = painterResource(R.drawable.no_fuss_flashcards_icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = stringResource(R.string.full_app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = PlayfairDisplay
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            IconButton(onClick = onSettingsClicked) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_button)
                )
            }
        }
    )
}


/**
 * Composable function that represents the body of the Home screen.
 *
 * @param deckList List of decks to be displayed.
 * @param onDeckClicked Callback function to be invoked when a deck is clicked, passing the deck ID.
 * @param modifier [Modifier] for styling and layout adjustments.
 * @param lastDeckId ID of the last opened deck, if any.
 */
@Composable
fun HomeBody(
    deckList: List<Deck>,
    onDeckClicked: (deckId: Int) -> Unit,
    modifier: Modifier = Modifier,
    lastDeckId: Int? = null,
    showLastOpenedError: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_large),
                vertical = dimensionResource(R.dimen.padding_small)
            ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (lastDeckId != null) {
            val lastDeckName = deckList.find { it.deckId == lastDeckId }?.name
            lastDeckName?.let {
                Button(
                    onClick = { onDeckClicked(lastDeckId) }
                ) {
                    Text(stringResource(R.string.jump_in, lastDeckName))
                }
            }
        }

        if (showLastOpenedError) {
            Text(
                text = stringResource(R.string.last_deck_read_failed),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.padding_small))
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider()

        DeckList(decks = deckList, onDeckClicked = onDeckClicked)
    }
}

@Composable
fun HomeBodyTablet(
    deckList: List<Deck>,
    onDeckClicked: (deckId: Int) -> Unit,
    onCreateDeckClicked: () -> Unit,
    modifier: Modifier = Modifier,
    lastDeckId: Int? = null,
    showLastOpenedError: Boolean = false,
) {
    Row(modifier = modifier) {
        HomeSideBar(
            onCreateDeckClicked = onCreateDeckClicked,
            onLastDeckClicked = { lastDeckId?.let { onDeckClicked(it) } },
            lastDeckName = deckList.find { it.deckId == lastDeckId }?.name,
            showLastOpenedError = showLastOpenedError,
            modifier = Modifier.weight(1f)
        )

        DeckList(
            decks = deckList,
            onDeckClicked = onDeckClicked,
            modifier = Modifier.weight(3f)
        )
    }
}

@Composable
private fun HomeSideBar(
    onCreateDeckClicked: () -> Unit,
    onLastDeckClicked: () -> Unit,
    lastDeckName: String?,
    showLastOpenedError: Boolean,
    modifier: Modifier = Modifier
) {
    SidebarMenu(
        headerTitle = stringResource(R.string.home_sidebar_title),
        modifier = modifier
    ) {
        if (lastDeckName != null) {
            SidebarActionButton(
                text = stringResource(R.string.jump_in, lastDeckName),
                onClick = onLastDeckClicked,
            )
        }
        else if (showLastOpenedError) {
            SidebarActionButton(
                text = stringResource(R.string.last_deck_read_failed),
                onClick = {},
                enabled = false
            )
        }
        SidebarActionButton(
            text = stringResource(R.string.create_deck_button),
            onClick = onCreateDeckClicked
        )

    }

}


/**
 * Composable function that represents the list of decks.
 *
 * @param decks List of decks to be displayed.
 * @param onDeckClicked Callback function to be invoked when a deck is clicked, passing the deck ID.
 * @param modifier [Modifier] for styling and layout adjustments.
 */
@Composable
private fun DeckList(
    decks: List<Deck>,
    onDeckClicked: (deckId: Int) -> Unit,
    modifier: Modifier = Modifier
) {

    if (decks.isNotEmpty()) {
        LazyColumn(modifier = modifier) {
            item {
                Text(
                    text = stringResource(R.string.deck_list_header),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            items(decks.size) { index ->
                val deck = decks[index]
                DeckItem(deck = deck, onClick = { onDeckClicked(deck.deckId) })
            }
        }
    } else {
        Text(
            text = stringResource(R.string.deck_list_empty),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Composable function that represents a single deck item.
 *
 * @param deck Deck to be displayed.
 * @param onClick Callback function to be invoked when the deck is clicked.
 * @param modifier [Modifier] for styling and layout adjustments.
 */
@Composable
private fun DeckItem(
    deck: Deck,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deck.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = stringResource(R.string.open_deck_button),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            deck.description?.let {
                HorizontalDivider(modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.padding_small))
                )

                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium_small))
                )
            }
        }
    }
}

/*
 * Previews for the Home screen and its components
 */

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    val sampleDecks = listOf(
        Deck(deckId = 1, name = "Deck 1", description = "Description for Deck 1"),
        Deck(deckId = 2, name = "Deck 2", description = "Description for Deck 2"),
        Deck(deckId = 3, name = "Deck 3", description = null)
    )
    HomeBody(
        deckList = sampleDecks,
        onDeckClicked = {},
        lastDeckId = 2
    )
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyPreview() {
    HomeBody(
        deckList = emptyList(),
        onDeckClicked = {},
        lastDeckId = null
    )
}

@Preview
@Composable
fun DeckItemPreview() {
    val sampleDeck = Deck(
        deckId = 1,
        name = "Sample Deck",
        description = "This is a sample deck description."
    )
    DeckItem(deck = sampleDeck, onClick = {})
}