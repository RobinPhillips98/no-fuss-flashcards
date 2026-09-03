package com.nofussflashcards.app.ui.flashcards

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.flashcards.Flashcard

@Composable
fun FlashcardDetail(
    flashcardData: Flashcard,
    flashcardIndex: Int,
    isTablet: Boolean,
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

    val density = LocalDensity.current

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Card(
            onClick = {
                onClick()
                isFlipped = !isFlipped
            },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = cardRotationY
                    cameraDistance = 12f * density.density * 100f
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Counter-rotate content on the back half so text is not mirrored mid-flip.
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

                FlashcardInfo(
                    flashcardData = flashcardData,
                    flashcardIndex = flashcardIndex,
                    isTablet = isTablet,
                    showBackSide = showBackSide,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FlashcardInfo(
    flashcardData: Flashcard,
    flashcardIndex: Int,
    isTablet: Boolean,
    showBackSide: Boolean,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val useSplitLayout = isTablet &&
                !flashcardData.definition.isNullOrBlank() &&
                !flashcardData.imagePath.isNullOrBlank() &&
                maxWidth >= 700.dp &&
                maxWidth > maxHeight

        if (showBackSide) {
            Text(
                text = flashcardData.term,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            if (useSplitLayout) {
                FlashcardInfoSplit(
                    definition = flashcardData.definition,
                    imagePath = flashcardData.imagePath,
                    flashcardIndex = flashcardIndex,
                    availableHeight = maxHeight
                )
            } else {
                FlashcardInfoStacked(
                    definition = flashcardData.definition,
                    imagePath = flashcardData.imagePath,
                    flashcardIndex = flashcardIndex,
                    availableHeight = maxHeight
                )
            }
        }
    }
}

@Composable
private fun FlashcardInfoSplit(
    definition: String,
    imagePath: String,
    flashcardIndex: Int,
    availableHeight: Dp
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_medium)
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = definition,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            FlashcardImage(
                imagePath = imagePath,
                flashcardIndex = flashcardIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp, max = availableHeight * 0.88f)
                    .aspectRatio(1.2f, matchHeightConstraintsFirst = true)
            )
        }
    }
}

@Composable
private fun FlashcardInfoStacked(
    definition: String?,
    imagePath: String?,
    flashcardIndex: Int,
    availableHeight: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!definition.isNullOrBlank()) {
            Text(
                text = definition,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_medium))
            )
        }

        if (!imagePath.isNullOrBlank()) {
            FlashcardImage(
                imagePath = imagePath,
                flashcardIndex = flashcardIndex,
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .heightIn(min = 140.dp, max = availableHeight * 0.65f)
                    .aspectRatio(1.35f, matchHeightConstraintsFirst = true)
            )
        }
    }
}

@Composable
private fun FlashcardImage(
    imagePath: String,
    flashcardIndex: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                .padding(dimensionResource(R.dimen.padding_medium_small)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imagePath,
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
    FlashcardDetail(
        flashcardData = sampleFlashcard,
        flashcardIndex = 1,
        isTablet = false,
        onClick = {}
    )
}