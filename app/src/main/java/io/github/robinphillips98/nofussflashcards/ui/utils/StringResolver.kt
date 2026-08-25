package io.github.robinphillips98.nofussflashcards.ui.utils

import androidx.annotation.StringRes

/**
 * Interface for resolving string resources.
 */
interface StringResolver {
    fun get(@StringRes resId: Int, vararg args: Any): String
}