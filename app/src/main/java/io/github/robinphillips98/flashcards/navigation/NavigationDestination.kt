package io.github.robinphillips98.flashcards.navigation

/**
 * Interface to describe the navigation destinations for the app
 */
interface NavigationDestination {
   /**
    * Unique name to define the path for a composable
    */
    val route: String

    /**
     * Human-readable title for the destination
     */
    val title: String
}