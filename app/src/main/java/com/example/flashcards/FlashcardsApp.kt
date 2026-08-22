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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flashcards.data.Screens

@Composable
fun FlashcardsApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val screenRoute = backStackEntry?.destination?.route ?: Screens.HomeScreen.name
    val currentScreen = Screens.valueOf(screenRoute)

    val topBarTitle: String = currentScreen.title


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

                    Button(onClick = { navController.navigate(Screens.DeckOverview.name) }) {
                        Text("Go to Deck Overview")
                    }
                }
            }

            composable(Screens.DeckOverview.name) {
                // TODO: Implement DeckOverview Composable
                Text("Deck Overview Placeholder")
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