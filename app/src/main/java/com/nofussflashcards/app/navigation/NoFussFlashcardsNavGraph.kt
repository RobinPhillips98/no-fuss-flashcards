package com.nofussflashcards.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nofussflashcards.app.ui.about.AboutDestination
import com.nofussflashcards.app.ui.about.AboutScreen
import com.nofussflashcards.app.ui.about.LicenseDestination
import com.nofussflashcards.app.ui.about.LicenseScreen
import com.nofussflashcards.app.ui.about.OpenSourceLicensesDestination
import com.nofussflashcards.app.ui.about.OpenSourceLicensesScreen
import com.nofussflashcards.app.ui.about.PrivacyPolicyDestination
import com.nofussflashcards.app.ui.about.PrivacyPolicyScreen
import com.nofussflashcards.app.ui.about.TermsOfServiceDestination
import com.nofussflashcards.app.ui.about.TermsOfServiceScreen
import com.nofussflashcards.app.ui.decks.DeckDetailsDestination
import com.nofussflashcards.app.ui.decks.DeckDetailsScreen
import com.nofussflashcards.app.ui.decks.forms.DeckEditDestination
import com.nofussflashcards.app.ui.decks.forms.DeckEditScreen
import com.nofussflashcards.app.ui.decks.forms.DeckEntryDestination
import com.nofussflashcards.app.ui.decks.forms.DeckEntryScreen
import com.nofussflashcards.app.ui.flashcards.forms.FlashcardEditDestination
import com.nofussflashcards.app.ui.flashcards.forms.FlashcardEditScreen
import com.nofussflashcards.app.ui.flashcards.forms.FlashcardEntryDestination
import com.nofussflashcards.app.ui.flashcards.forms.FlashcardEntryScreen
import com.nofussflashcards.app.ui.flashcards.FlashcardsPagerDestination
import com.nofussflashcards.app.ui.flashcards.FlashcardsPagerScreen
import com.nofussflashcards.app.ui.home.HomeDestination
import com.nofussflashcards.app.ui.home.HomeScreen
import com.nofussflashcards.app.ui.settings.SettingsDestination
import com.nofussflashcards.app.ui.settings.SettingsScreen
import com.nofussflashcards.app.ui.theme.AppThemeViewModel

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun NoFussFlashcardsNavHost(
    navController: NavHostController,
    appThemeViewModel: AppThemeViewModel,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
) {
    val durationMillis = 150
    val isTablet = windowSize == WindowWidthSizeClass.Medium || windowSize == WindowWidthSizeClass.Expanded

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
                isTablet = isTablet,
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
                windowSize = windowSize,
                isTablet = isTablet,
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
                windowSize = windowSize,
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
                isTablet = isTablet,
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
                isTablet = isTablet,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(
                appThemeViewModel = appThemeViewModel,
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
    }
}