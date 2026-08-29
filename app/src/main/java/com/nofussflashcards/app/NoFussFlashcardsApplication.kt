package com.nofussflashcards.app

import android.app.Application
import com.nofussflashcards.app.data.AppContainer
import com.nofussflashcards.app.data.AppDataContainer

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