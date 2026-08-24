package io.github.robinphillips98.nofussflashcards.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.data.decks.DeckDao
import io.github.robinphillips98.nofussflashcards.data.flashcards.Flashcard
import io.github.robinphillips98.nofussflashcards.data.flashcards.FlashcardDao

@Database(
    entities = [Deck::class, Flashcard::class],
    version = 3,
    exportSchema = false
)
abstract class NoFussFlashcardAppDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao

    abstract fun flashcardDao(): FlashcardDao

    companion object {
        const val DATABASE_NAME = "flashcard_app_database"

        @Volatile
        private var Instance: NoFussFlashcardAppDatabase? = null

        fun getDatabaseInstance(context: Context): NoFussFlashcardAppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, NoFussFlashcardAppDatabase::class.java, DATABASE_NAME)
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}