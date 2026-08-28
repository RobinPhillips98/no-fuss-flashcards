package io.github.robinphillips98.nofussflashcards.data.flashcards

import kotlinx.coroutines.flow.Flow

class RoomFlashcardsRepository(private val flashcardDao: FlashcardDao) : FlashcardsRepository {
    override suspend fun getAllFlashcards(): List<Flashcard> = flashcardDao.getAllFlashcardsOnce()

    override fun getAllFlashcardsStream(): Flow<List<Flashcard>> = flashcardDao.getAllFlashcards()

    override fun getFlashcardStream(flashcardId: Int): Flow<Flashcard?> = flashcardDao.getFlashcardById(flashcardId)

    override fun getFlashcardsByDeckIdStream(deckId: Int): Flow<List<Flashcard>> = flashcardDao.getFlashcardsByDeckId(deckId)

    override suspend fun insertFlashcard(flashcard: Flashcard) = flashcardDao.insertFlashcard(flashcard)

    override suspend fun deleteFlashcard(flashcard: Flashcard) = flashcardDao.deleteFlashcard(flashcard)

    override suspend fun updateFlashcard(flashcard: Flashcard) = flashcardDao.updateFlashcard(flashcard)
}