package io.github.robinphillips98.flashcards.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.robinphillips98.flashcards.ui.decks.DeckDetailsDestination
import io.github.robinphillips98.flashcards.ui.decks.DeckDetailsScreen
import io.github.robinphillips98.flashcards.ui.flashcards.FlashcardsPagerDestination
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
                    navController.navigate("${DeckDetailsDestination.route}/$deckId")
                }
            )
        }

        composable(
            route = DeckDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(DeckDetailsDestination.DECK_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            val deckId = it.arguments?.getInt(DeckDetailsDestination.DECK_ID_ARG) ?: 0

            DeckDetailsScreen(
                navigateToFlashcards = {
                    navController.navigate("${FlashcardsPagerDestination.route}/$deckId")
                },
                navigateToFlashcardWithId = { flashcardId ->
                    navController.navigate(
                        route = FlashcardsPagerDestination.route +
                                "/$deckId" +
                                "?${FlashcardsPagerDestination.FLASHCARD_ID_ARG}=$flashcardId"
                    )
                },
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = FlashcardsPagerDestination.routeWithArgs,
            arguments = listOf(
                navArgument(FlashcardsPagerDestination.DECK_ID_ARG) {
                    type = NavType.IntType
                },
                navArgument(FlashcardsPagerDestination.FLASHCARD_ID_ARG) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {  FlashcardsPagerScreen(navigateBack = { navController.navigateUp() }) }

        // TODO: Implement deck create/edit, flashcard create/edit, quiz, and settings screens
    }
}