package io.github.robinphillips98.flashcards.ui.utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
 */
@Composable
fun ImageUploader(
    objectDescription: String,
    onImageUploaded: (Uri?) -> Unit,
    selectedImageUri: Uri?,
    modifier: Modifier = Modifier,
    existingImageUri: Uri? = null,
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
                    text = "Image *",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Select a JPEG or PNG image to represent this $objectDescription.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = spacedBy(12.dp)
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
                        Text("Choose Image")
                    }

                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onImageUploaded(null) },
                        enabled = selectedImageUri != null
                    ) {
                        Text("Clear")
                    }
                }

                Text(
                    text = if (previewImage != null) {
                        "Image selected"
                    } else {
                        "No image selected yet"
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

        // Show selected image preview if there is a selected or existing image URI
        previewImage?.let { model ->
            Card(
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Image Preview",
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp),
                    style = MaterialTheme.typography.labelLarge
                )
                AsyncImage(
                    model = model,
                    contentDescription = "Selected Image Preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

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