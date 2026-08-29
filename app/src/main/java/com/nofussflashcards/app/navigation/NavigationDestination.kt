package com.nofussflashcards.app.navigation

import androidx.annotation.StringRes

/**
 * Interface to describe the navigation destinations for the app
 */
interface NavigationDestination {
   /**
    * Unique name to define the path for a composable
    */
    val route: String

    /**
     * String resource ID for the title of the destination
     */
    @get:StringRes
    val titleResId: Int
}