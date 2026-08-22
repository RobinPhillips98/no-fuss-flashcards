@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.flashcards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.example.flashcards.data.DeckDatasource
import com.example.flashcards.data.FlashcardDatasource
import com.example.flashcards.data.Screens
import com.example.flashcards.ui.decks.DeckOverview

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
        Screens.DeckOverview -> {
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
            composable (Screens.HomeScreen.name) {
                Column {
                    // TODO: Implement HomeScreen Composable
                    Text("Home Screen Placeholder")

                    Button(
                        onClick = {
                            navController.navigate("${Screens.DeckOverview.name}/1")
                        }
                    ) {
                        Text("Go to Deck Overview")
                    }
                }
            }

            composable(
                route = "${Screens.DeckOverview.name}/{deckId}",
                arguments = listOf(navArgument("deckId") { type = NavType.IntType })
            ) {
                val deckId = it.arguments?.getInt("deckId") ?: 0
                val deck = deckDatasource.loadDeckById(deckId)
                val flashCards = flashcardDatasource.loadFlashcards()

                if (deck != null) {
                    DeckOverview(deck, flashCards)
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