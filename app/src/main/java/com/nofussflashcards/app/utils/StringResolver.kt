package com.nofussflashcards.app.utils

import androidx.annotation.StringRes

/**
 * Interface for resolving string resources.
 */
interface StringResolver {
    fun get(@StringRes resId: Int, vararg args: Any): String
}