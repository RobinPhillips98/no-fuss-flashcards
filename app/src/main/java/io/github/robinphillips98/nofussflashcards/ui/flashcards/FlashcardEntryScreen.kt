package io.github.robinphillips98.nofussflashcards.ui.flashcards

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.data.decks.Deck
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.AppViewModelProvider
import io.github.robinphillips98.nofussflashcards.ui.utils.ImageUploader
import kotlinx.coroutines.launch

object FlashcardEntryDestination: NavigationDestination {
    override val route = "flashcard_entry"
    override val title = "Create Flashcard"
    const val DECK_ID_ARG = "deckId"
    val routeWithArgs = "$route/{$DECK_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: FlashcardEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val flashcardUiState = viewModel.flashcardUiState

    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = FlashcardEntryDestination.title,
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        FlashcardEntryBody(
            flashcardUiState = flashcardUiState,
            onFlashcardValueChange = viewModel::updateUiState,
            onImageUploaded = {
                imageUri -> viewModel.updateUiState(
                    flashcardUiState.flashcardDetails,
                    imageUri
                )
            },
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveFlashcard(context)
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun FlashcardEntryBody(
    flashcardUiState: FlashcardUiState,
    onFlashcardValueChange: (FlashcardDetails) -> Unit,
    onImageUploaded: (imageUri: Uri?) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    availableDecks: List<Deck>? = null,
    onImageRestored: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        FlashcardInputForm(
            flashcardDetails = flashcardUiState.flashcardDetails,
            availableDecks = availableDecks,
            selectedImageUri = flashcardUiState.selectedImageUri,
            existingImageUri = flashcardUiState.existingImageUri,
            onValueChange = onFlashcardValueChange,
            onImageUploaded = onImageUploaded,
            onImageRestored = onImageRestored,
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = onSaveClick,
            enabled = flashcardUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        if (!flashcardUiState.isEntryValid) {
            Text(
                text = "Must provide a term and either a definition or an image.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun FlashcardInputForm(
    flashcardDetails: FlashcardDetails,
    availableDecks: List<Deck>?,
    selectedImageUri: Uri?,
    existingImageUri: Uri?,
    onValueChange: (FlashcardDetails) -> Unit,
    onImageUploaded: (imageUri: Uri?) -> Unit,
    modifier: Modifier = Modifier,
    onImageRestored: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = flashcardDetails.term,
            onValueChange = { onValueChange(flashcardDetails.copy(term = it)) },
            label = { Text("Term *") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = flashcardDetails.definition ?: "",
            onValueChange = { onValueChange(flashcardDetails.copy(definition = it)) },
            label = { Text("Definition") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        /*
        By making availableDecks null by default, we can make the deck selection optional. If
        availableDecks is provided, the dropdown will be displayed; otherwise, it will be omitted.

        For example, when creating a new flashcard, the card is simply associated with the deck
        that was selected when navigating to the FlashcardEntryScreen. In this case, we don't need
        to show the dropdown, so we can leave availableDecks as null. However, when editing an
        existing flashcard, we want to allow the user to change the deck, so we provide the list of
        available decks and display the dropdown.
         */
        if (availableDecks != null) {
            FlashcardDeckDropdown(
                selectedDeck = flashcardDetails.deckId,
                availableDecks = availableDecks,
                onDeckSelected = { onValueChange(flashcardDetails.copy(deckId = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ImageUploader(
            objectDescription = "flashcard",
            onImageUploaded = onImageUploaded,
            selectedImageUri = selectedImageUri,
            existingImageUri = existingImageUri,
            onImageRestored = onImageRestored,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlashcardDeckDropdown(
    selectedDeck: Int,
    availableDecks: List<Deck>,
    onDeckSelected: (id: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedDeckName = availableDecks
        .firstOrNull { it.deckId == selectedDeck }
        ?.name
        .orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedDeckName,
            onValueChange = {},
            label = { Text("Deck") },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableDecks.forEach { deck ->
                DropdownMenuItem(
                    text = { Text(deck.name) },
                    onClick = {
                        onDeckSelected(deck.deckId)
                        expanded = false
                    }
                )
            }
        }
    }
}