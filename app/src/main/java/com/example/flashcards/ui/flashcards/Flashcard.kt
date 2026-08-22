package com.example.flashcards.ui.flashcards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flashcards.model.FlashcardInfo

@Composable
fun Flashcard(
    flashcardData: FlashcardInfo,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    val cardText = if (isFlipped) flashcardData.term else flashcardData.definition
    val cardStyle = if (isFlipped)
        MaterialTheme.typography.titleLarge
    else
        MaterialTheme.typography.bodyLarge
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.size(width = 320.dp, height = 200.dp)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
            ) {
                Text(
                    text = cardText,
                    style = cardStyle,
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    Button(
                        onClick = { isFlipped = !isFlipped },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text(text = "Flip")
                    }
                }
            }
        }
    }
}