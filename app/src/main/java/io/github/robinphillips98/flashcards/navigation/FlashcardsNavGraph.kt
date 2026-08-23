package io.github.robinphillips98.flashcards.navigation

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.robinphillips98.flashcards.data.DeckDatasource
import io.github.robinphillips98.flashcards.data.FlashcardDatasource
import io.github.robinphillips98.flashcards.data.Screens
import io.github.robinphillips98.flashcards.ui.decks.DeckDetailsScreen
import io.github.robinphillips98.flashcards.ui.flashcards.FlashcardsPagerScreen
import io.github.robinphillips98.flashcards.ui.home.HomeDestination
import io.github.robinphillips98.flashcards.ui.home.HomeScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun FlashcardsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val deckDatasource = DeckDatasource()
    val flashcardDatasource = FlashcardDatasource()

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
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
                DeckDetailsScreen(
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
                FlashcardsPagerScreen(
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