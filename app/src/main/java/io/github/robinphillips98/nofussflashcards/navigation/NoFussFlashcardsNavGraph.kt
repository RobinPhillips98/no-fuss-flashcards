package io.github.robinphillips98.nofussflashcards.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.robinphillips98.nofussflashcards.ui.about.AboutDestination
import io.github.robinphillips98.nofussflashcards.ui.about.AboutScreen
import io.github.robinphillips98.nofussflashcards.ui.about.LicenseDestination
import io.github.robinphillips98.nofussflashcards.ui.about.LicenseScreen
import io.github.robinphillips98.nofussflashcards.ui.about.OpenSourceLicensesDestination
import io.github.robinphillips98.nofussflashcards.ui.about.OpenSourceLicensesScreen
import io.github.robinphillips98.nofussflashcards.ui.about.PrivacyPolicyDestination
import io.github.robinphillips98.nofussflashcards.ui.about.PrivacyPolicyScreen
import io.github.robinphillips98.nofussflashcards.ui.about.TermsOfServiceDestination
import io.github.robinphillips98.nofussflashcards.ui.about.TermsOfServiceScreen
import io.github.robinphillips98.nofussflashcards.ui.decks.DeckDetailsDestination
import io.github.robinphillips98.nofussflashcards.ui.decks.DeckDetailsScreen
import io.github.robinphillips98.nofussflashcards.ui.decks.forms.DeckEditDestination
import io.github.robinphillips98.nofussflashcards.ui.decks.forms.DeckEditScreen
import io.github.robinphillips98.nofussflashcards.ui.decks.forms.DeckEntryDestination
import io.github.robinphillips98.nofussflashcards.ui.decks.forms.DeckEntryScreen
import io.github.robinphillips98.nofussflashcards.ui.flashcards.forms.FlashcardEditDestination
import io.github.robinphillips98.nofussflashcards.ui.flashcards.forms.FlashcardEditScreen
import io.github.robinphillips98.nofussflashcards.ui.flashcards.forms.FlashcardEntryDestination
import io.github.robinphillips98.nofussflashcards.ui.flashcards.forms.FlashcardEntryScreen
import io.github.robinphillips98.nofussflashcards.ui.flashcards.FlashcardsPagerDestination
import io.github.robinphillips98.nofussflashcards.ui.flashcards.FlashcardsPagerScreen
import io.github.robinphillips98.nofussflashcards.ui.home.HomeDestination
import io.github.robinphillips98.nofussflashcards.ui.home.HomeScreen
import io.github.robinphillips98.nofussflashcards.ui.settings.SettingsDestination
import io.github.robinphillips98.nofussflashcards.ui.settings.SettingsScreen
import io.github.robinphillips98.nofussflashcards.ui.theme.AppThemeViewModel

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun NoFussFlashcardsNavHost(
    navController: NavHostController,
    appThemeViewModel: AppThemeViewModel,
    modifier: Modifier = Modifier,
) {
    val durationMillis = 150
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(durationMillis, easing = EaseInOut))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(durationMillis, easing = EaseInOut))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(durationMillis, easing = EaseInOut))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(durationMillis, easing = EaseInOut))
        },
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                onCreateDeckClicked = { navController.navigate(DeckEntryDestination.route) },
                onDeckClicked = { deckId ->
                    navController.navigate("${DeckDetailsDestination.route}/$deckId")
                },
                onSettingsClicked = { navController.navigate(SettingsDestination.route) }
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
                navigateToEditScreen = { deckId ->
                    navController.navigate("${DeckEditDestination.route}/$deckId")
                },
                navigateToFlashcardEntryScreen = { deckId ->
                    navController.navigate("${FlashcardEntryDestination.route}/$deckId")
                },
                navigateToFlashcardEditScreen = { flashcardId ->
                    navController.navigate("${FlashcardEditDestination.route}/$flashcardId")
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
        ) {
            FlashcardsPagerScreen(
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(route = DeckEntryDestination.route) {
            DeckEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = DeckEditDestination.routeWithArgs,
            arguments = listOf(navArgument(DeckEditDestination.DECK_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            DeckEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = FlashcardEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(FlashcardEntryDestination.DECK_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            FlashcardEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = FlashcardEditDestination.routeWithArgs,
            arguments = listOf(
                navArgument(FlashcardEditDestination.FLASHCARD_ID_ARG) {
                    type = NavType.IntType
                }
            )
        ) {
            FlashcardEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(
                viewModel = appThemeViewModel,
                navigateToAbout = { navController.navigate(AboutDestination.route) },
                navigateBack = { navController.popBackStack() },
            )
        }

        composable(route = AboutDestination.route) {
            AboutScreen(
                navigateToLicense = { navController.navigate(LicenseDestination.route) },
                navigateToPrivacyPolicy = { navController.navigate(PrivacyPolicyDestination.route) },
                navigateToTermsOfService = { navController.navigate(TermsOfServiceDestination.route) },
                navigateToOpenSourceLibraries = { navController.navigate(OpenSourceLicensesDestination.route) },
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(route = LicenseDestination.route) {
            LicenseScreen(navigateUp = { navController.navigateUp() })
        }

        composable(route = PrivacyPolicyDestination.route) {
            PrivacyPolicyScreen(navigateUp = { navController.navigateUp() })
        }

        composable(route = TermsOfServiceDestination.route) {
            TermsOfServiceScreen(navigateUp = { navController.navigateUp() })
        }

        composable(route = OpenSourceLicensesDestination.route) {
            OpenSourceLicensesScreen(navigateBack = { navController.navigateUp() })
        }

        // TODO: Implement quiz screen
    }
}