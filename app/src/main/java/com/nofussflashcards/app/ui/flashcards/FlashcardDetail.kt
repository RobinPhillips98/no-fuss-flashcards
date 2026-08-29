package com.nofussflashcards.app.ui.flashcards

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.flashcards.Flashcard

@Composable
fun FlashcardDetail(
    flashcardData: Flashcard,
    flashcardIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }

    val cardRotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "cardFlip"
    )

    // Which side should be visible at the current animation frame?
    val showBackSide = cardRotationY > 90f

    val sideLabel =
        if (showBackSide) stringResource(R.string.term)
        else stringResource(R.string.definition)
    val cardText = if (showBackSide) flashcardData.term else flashcardData.definition
    val cardStyle = if (showBackSide) {
        MaterialTheme.typography.displaySmall
    } else {
        MaterialTheme.typography.titleMedium
    }

    val density = LocalDensity.current

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Card(
            onClick = {
                onClick()
                isFlipped = !isFlipped
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .heightIn(min = 320.dp)
                .graphicsLayer {
                    rotationY = cardRotationY
                    // Compose cameraDistance uses "px-like" units; larger = less perspective distortion.
                    cameraDistance = 12f * density.density * 100f
                }
        ) {
            // Counter-rotate content on the back half so text is not mirrored.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = if (showBackSide) 180f else 0f
                    }
                    .padding(dimensionResource(R.dimen.padding_medium))
            ) {
                Text(
                    text = sideLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (cardText != null) {
                        Text(
                            text = cardText,
                            style = cardStyle,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Show image only on definition/front side.
                    if (!showBackSide && flashcardData.imagePath != null) {
                        Spacer(modifier = Modifier.height(
                            dimensionResource(R.dimen.padding_medium_small)
                        ))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.86f)
                                .heightIn(min = 120.dp, max = 220.dp)
                                .aspectRatio(1.35f),
                            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = dimensionResource(R.dimen.padding_small)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = flashcardData.imagePath,
                                    contentDescription = stringResource(
                                        R.string.flashcard_image_content_description,
                                        flashcardIndex
                                    ),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FlashcardDetailPreview() {
    val sampleFlashcard = Flashcard(
        flashcardId = 1,
        deckId = 1,
        term = "Sample Term",
        definition = "Sample Definition",
        imagePath = null
    )
    FlashcardDetail(flashcardData = sampleFlashcard, flashcardIndex = 1, onClick = {})
}