package com.nofussflashcards.app.ui.utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nofussflashcards.app.R
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Composable function that provides an image uploader UI component for a form.
 *
 * @param objectDescription A string describing the object for which the image is being uploaded,
 *  used in the UI text to provide context to the user.
 * @param onImageUploaded A callback function that is invoked when the user selects an image from the
 *  photo picker. It receives the URI of the selected image, or null if the selection was cleared.
 * @param selectedImageUri The URI of the image that the user has selected, if any. This is used to
 * display a preview of the selected image in the UI.
 * @param modifier Optional [Modifier] for styling the image uploader component.
 * @param existingImageUri The URI of an existing image associated with the object, if any. This is
 * used to display a preview of the existing image in the UI if no new image has been selected.
 * @param onImageRestored An optional callback invoked when the user taps the Restore button to
 * revert to the original existing image. When null, the Restore button is not shown.
 */
@Composable
fun ImageUploader(
    objectDescription: String,
    onImageUploaded: (Uri?) -> Unit,
    selectedImageUri: Uri?,
    modifier: Modifier = Modifier,
    existingImageUri: Uri? = null,
    onImageRestored: (() -> Unit)? = null,
) {
    // Will set previewImage to null if there is no selected or existing image URI
    val previewImage = selectedImageUri ?: existingImageUri

    /*
        The photo picker launcher allows the user to select an image from their device. When the
        user clicks the "Choose Image" button, it launches the system photo picker filtered to show
        only images. When the user selects an image, the resulting URI is passed to the
        onImageUploaded callback, which updates the form state in the view model.
     */
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onImageUploaded(uri)
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.image_label),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(
                        R.string.image_upload_instructions,
                        objectDescription
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // Launches the photo picker for images only
                            photoPickerLauncher.launch(
                                 PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    ) {
                        Text(stringResource(R.string.image_choose_button))
                    }

                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onImageUploaded(null) },
                        // Enabled if there's a newly selected image, or an existing image that
                        // can be cleared (only relevant when onImageRestored is provided)
                        enabled = selectedImageUri != null ||
                            (existingImageUri != null && onImageRestored != null)
                    ) {
                        Text(stringResource(R.string.image_clear_button))
                    }

                    if (onImageRestored != null) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onImageRestored,
                            // Enabled when there's something to restore (i.e. the image was
                            // cleared or replaced with a new one)
                            enabled = selectedImageUri != null || existingImageUri == null
                        ) {
                            Text(stringResource(R.string.image_restore_button))
                        }
                    }
                }

                Text(
                    text = if (previewImage != null) {
                        if (selectedImageUri != null) stringResource(R.string.image_new_image_selected_message)
                        else stringResource(R.string.image_existing_image_selected_message)
                    } else {
                        stringResource(R.string.image_no_image_selected_message)
                    },
                    color = if (previewImage != null) {
                        colorScheme.primary
                    } else {
                        colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        ImagePreviewContainer(previewImage)
    }
}

@Composable
private fun ImagePreviewContainer(previewImage: Uri?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .heightIn(
                min = dimensionResource(R.dimen.image_preview_min_height),
                max = dimensionResource(R.dimen.image_preview_max_height)
            )
    ) {
        Text(
            text = stringResource(R.string.image_preview_label),
            modifier = Modifier.padding(start = 12.dp, top = 12.dp),
            style = MaterialTheme.typography.labelLarge
        )
        if (previewImage != null) {
            AsyncImage(
                model = previewImage,
                contentDescription = stringResource(R.string.image_preview_content_description),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.image_preview_placeholder),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * Saves an image to internal storage given its URI.
 *
 * @param context The context used to access internal storage.
 * @param uri The URI of the image to be saved.
 *
 * @return The absolute path of the saved image file, or `null` if the save operation failed.
 */
fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val fileExtension = context.contentResolver.getType(uri)
            ?.substringAfterLast("/")
            ?: "jpg"
        // Create a unique file name in internal storage
        val fileName = "${UUID.randomUUID()}.$fileExtension"
        val file = File(context.filesDir, fileName)

        // Copy the input stream data to the local file
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        file.absolutePath // This string is what we save to Room
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Deletes an image file from internal storage given its path.
 *
 * @param imagePath The absolute path of the image file to be deleted.
 *
 * @return `true` if the file was successfully deleted, `false` otherwise.
 */
fun deleteImageFromInternalStorage(imagePath: String): Boolean {
    return try {
        val file = File(imagePath)
        if (file.exists()) {
            file.delete()
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}