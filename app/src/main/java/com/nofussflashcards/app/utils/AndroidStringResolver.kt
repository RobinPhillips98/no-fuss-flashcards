package com.nofussflashcards.app.utils

import android.content.Context

/**
 * Implementation of [StringResolver] that uses Android's [Context] to resolve string resources.
 */
class AndroidStringResolver(
    private val context: Context
) : StringResolver {
    override fun get(resId: Int, vararg args: Any): String =
        context.getString(resId, *args)
}