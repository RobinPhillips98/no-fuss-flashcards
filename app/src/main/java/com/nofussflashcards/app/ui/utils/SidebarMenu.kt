package com.nofussflashcards.app.ui.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.nofussflashcards.app.R

@Composable
fun SidebarMenu(
    headerTitle: String,
    modifier: Modifier = Modifier,
    headerSubtitle: String? = null,
    content: @Composable () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
            .widthIn(min = 240.dp, max = 320.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        ) {
            Text(
                text = headerTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )

            headerSubtitle?.let { subTitle ->
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium)),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

            content()
        }
    }
}


@Composable
fun SidebarActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDangerous: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        colors = if (isDangerous) {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        } else {
            ButtonDefaults.filledTonalButtonColors()
        },
        modifier = modifier.fillMaxWidth()
    ) {
        if (icon != null) {
            icon()
        }
        Text(text)
    }
}