package com.nofussflashcards.app.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A reusable composable that displays a row of text with an optional icon and a trailing chevron.
 * The entire row is clickable, and the provided onClick lambda is invoked when the row is clicked.
 *
 * @param text The text to display in the row.
 * @param onClick The lambda to invoke when the row is clicked.
 * @param modifier The modifier to apply to the ListItem.
 * @param icon An optional composable to display as an icon before the text.
 */
@Composable
fun ClickableTextRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    ListItem(
        headlineContent = {
            Row {
                icon?.invoke()
                Text(text)
            }
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
            )
        },
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Preview
@Composable
fun ClickableTextRowPreview() {
    ClickableTextRow(
        text = "Sample Text",
        onClick = {},
        icon = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    )
}