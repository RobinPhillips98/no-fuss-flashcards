package io.github.robinphillips98.nofussflashcards.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.theme.AppThemeOptions

object SettingsDestination: NavigationDestination {
    override val route = "settings"
    override val titleResId = R.string.settings_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    selectedThemeName: String,
    onThemeSelect: (AppThemeOptions) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(SettingsDestination.titleResId),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        SettingsContent(
            selectedThemeName = selectedThemeName,
            onThemeSelect = onThemeSelect,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun SettingsContent(
    selectedThemeName: String,
    onThemeSelect: (AppThemeOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource((R.string.settings_title)),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        HorizontalDivider()

        ThemeDropdownMenu(
            selectedThemeName = selectedThemeName,
            onThemeSelect = onThemeSelect,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeDropdownMenu(
    selectedThemeName: String,
    onThemeSelect: (AppThemeOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedThemeName,
            onValueChange = {},
            label = { Text(stringResource(R.string.settings_theme_label)) },
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
            AppThemeOptions.entries.forEach { themeOption ->
                DropdownMenuItem(
                    text = { Text(stringResource(themeOption.titleResId)) },
                    onClick = {
                        onThemeSelect(themeOption)
                        expanded = false
                    }
                )
            }
        }
    }
}