package io.github.robinphillips98.flashcards.data

class FlashcardDatasource {
    private val flashCards = listOf(
        Flashcard(
            id = 1,
            term = "AHA - American Hospital Organization",
            definition = "A national organization that represents hospitals, health systems, and other healthcare organizations and advocates for improvements in healthcare.",
            acceptableAnswers = listOf("AHA", "American Hospital Organization"),
        ),
        Flashcard(
            id = 2,
            term = "Ambulatory",
            definition = "Able to walk or move about independently; also describes healthcare provided to patients who do not require hospitalization.",
        ),
        Flashcard(
            id = 3,
            term = "Blood Bank",
            definition = "A laboratory department responsible for collecting, processing, testing, storing, and preparing blood and blood components for transfusion.",
        ),
        Flashcard(
            id = 4,
            term = "Immunohematology",
            definition = "The study of antigens and antibodies associated with blood, particularly those involved in blood typing, compatibility testing, and transfusion medicine.",
        ),
        Flashcard(
            id = 5,
            term = "C & S - Culture and Sensitivity",
            definition = "Laboratory testing used to identify microorganisms causing an infection and determine which antimicrobial medications are effective against them.",
            acceptableAnswers = listOf("C & S", "C&S", "Culture and Sensitivity"),
        ),
        Flashcard(
            id = 6,
            term = "CE - Continuing Education",
            definition = "Education completed after initial training to maintain or improve professional knowledge and skills and, when applicable, meet certification or licensure requirements.",
            acceptableAnswers = listOf("CE", "Continuing Education"),
        ),
        Flashcard(
            id = 7,
            term = "Certificate of Completion",
            definition = "A document verifying that an individual has successfully completed a particular educational or training program. It is not the same as professional certification or licensure.",
        ),
        Flashcard(
            id = 8,
            term = "Chemistry",
            definition = "The laboratory department that analyzes blood and other bodily fluids for substances such as glucose, electrolytes, enzymes, hormones, and proteins.",
        ),
        Flashcard(
            id = 9,
            term = "CLIA '88 - Clinical Laboratory Improvement Amendments of 1998",
            definition = "Federal regulations establishing quality standards for laboratory testing performed on human specimens to help ensure accurate, reliable, and timely test results.",
            acceptableAnswers = listOf("CLIA '88", "CLIA 88", "Clinical Laboratory Improvement Amendments of 1998"),
        ),
    )

    fun loadFlashcards(): List<Flashcard> {
        return flashCards
    }

    fun loadFlashcardsByDeckId(deckId: Int): List<Flashcard> {
        return flashCards.filter { it.deckId == deckId }
    }

    fun getFlashcardById(id: Int): Flashcard? {
        return try {
            flashCards[id - 1]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }
}