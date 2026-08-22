@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.robinphillips98.flashcards

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.robinphillips98.flashcards.data.DeckDatasource
import io.github.robinphillips98.flashcards.data.FlashcardDatasource
import io.github.robinphillips98.flashcards.data.Screens
import io.github.robinphillips98.flashcards.ui.HomeScreen
import io.github.robinphillips98.flashcards.ui.decks.DeckFlashcards
import io.github.robinphillips98.flashcards.ui.decks.DeckOverview

@Composable
fun FlashcardsApp(
    navController: NavHostController = rememberNavController()
) {
    val deckDatasource = DeckDatasource()
    val flashcardDatasource = FlashcardDatasource()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val screenRoute = backStackEntry?.destination?.route ?: Screens.HomeScreen.name
    val baseRoute = screenRoute.substringBefore("/")

    val topBarTitle: String = when (val currentScreen = Screens.valueOf(baseRoute)) {
        Screens.DeckOverview, Screens.DeckFlashcards -> {
            val deckId = backStackEntry?.arguments?.getInt("deckId") ?: 0
            val deck = deckDatasource.loadDeckById(deckId)
            deck?.name ?: currentScreen.title
        }
        else -> currentScreen.title
    }

    Scaffold(
        topBar = {
            FlashCardsAppTopBar(
                title = topBarTitle,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.HomeScreen.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(route = Screens.HomeScreen.name) {
                HomeScreen(
                    decks = deckDatasource.loadDecks(),
                    onCreateDeckClicked = { },
                    onSettingsClicked = { },
                    onDeckClicked = { deckId ->
                        navController.navigate("${Screens.DeckOverview.name}/$deckId")
                    }
                )
            }

            composable(
                route = "${Screens.DeckOverview.name}/{deckId}",
                arguments = listOf(navArgument("deckId") { type = NavType.IntType })
            ) {
                val deckId = it.arguments?.getInt("deckId") ?: 0
                val deck = deckDatasource.loadDeckById(deckId)
                val flashCards = flashcardDatasource.loadFlashcardsByDeckId(deckId)

                if (deck != null) {
                    DeckOverview(
                        deck = deck,
                        flashCards = flashCards,
                        onOpenCardsClicked = {
                            navController.navigate("${Screens.DeckFlashcards.name}/$deckId")
                        },
                        onFlashCardClicked = { flashcardId ->
                            val flashcard = flashcardDatasource.getFlashcardById(flashcardId)
                            if (flashcard != null) {
                                navController.navigate(
                                    "${Screens.DeckFlashcards.name}/$deckId?flashcardId=$flashcardId"
                                )
                            } else {
                                Toast.makeText(
                                    navController.context,
                                    "Flashcard not found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                } else {
                    Text("Deck not found")
                }
            }

            composable(
                route = "${Screens.DeckFlashcards.name}/{deckId}?flashcardId={flashcardId}",
                arguments = listOf(
                    navArgument("deckId") { type = NavType.IntType },
                    navArgument("flashcardId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { entry ->
                val deckId = entry.arguments?.getInt("deckId") ?: 0
                val flashcardId = entry.arguments?.getInt("flashcardId") ?: -1
                val selectedFlashcardId = flashcardId.takeIf { it != -1 }
                val deck = deckDatasource.loadDeckById(deckId)
                val flashcards = flashcardDatasource.loadFlashcardsByDeckId(deckId)

                if (deck != null) {
                    DeckFlashcards(
                        deck = deck,
                        flashcards = flashcards,
                        selectedFlashcardId = selectedFlashcardId
                    )
                } else {
                    Text("Deck not found")
                }
            }

            // TODO: Implement other screens in the navigation graph
        }
    }
}

@Composable
private fun FlashCardsAppTopBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}