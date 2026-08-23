package io.github.robinphillips98.flashcards.data.decks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks")
    fun getAllDecks(): Flow<List<Deck>>

    @Query("SELECT * FROM decks WHERE deck_id = :deckId")
    fun getDeckById(deckId: Int): Flow<Deck>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeck(deck: Deck)

    @Update
    suspend fun updateDeck(deck: Deck)

    @Delete
    suspend fun deleteDeck(deck: Deck)
}