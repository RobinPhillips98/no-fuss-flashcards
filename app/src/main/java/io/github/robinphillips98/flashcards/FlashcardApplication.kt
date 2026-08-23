package io.github.robinphillips98.flashcards

import android.app.Application
import io.github.robinphillips98.flashcards.data.AppContainer
import io.github.robinphillips98.flashcards.data.AppDataContainer

class FlashcardApplication: Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}