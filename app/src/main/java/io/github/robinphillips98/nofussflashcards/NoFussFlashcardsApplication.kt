package io.github.robinphillips98.nofussflashcards

import android.app.Application
import io.github.robinphillips98.nofussflashcards.data.AppContainer
import io.github.robinphillips98.nofussflashcards.data.AppDataContainer

class NoFussFlashcardsApplication: Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}