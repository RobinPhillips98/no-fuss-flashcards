package io.github.robinphillips98.nofussflashcards.ui.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.utils.toTitleCase

@Composable
fun DeleteConfirmationDialog(
    objectType: String,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        title = {
            Text(stringResource(
                id = R.string.delete_confirmation_title,
                objectType.toTitleCase()
            ))
        },
        text = {
            Text(stringResource(
                id = R.string.delete_confirmation_message,
                objectType
            ))
       },
        modifier = modifier,
        onDismissRequest = onDeleteCancel,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(id = R.string.no_button))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(id = R.string.yes_button))
            }
        }
    )
}