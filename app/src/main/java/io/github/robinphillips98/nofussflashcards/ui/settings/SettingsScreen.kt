package io.github.robinphillips98.nofussflashcards.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.theme.AppFontOptions
import io.github.robinphillips98.nofussflashcards.ui.theme.AppThemeOptions
import io.github.robinphillips98.nofussflashcards.ui.theme.AppThemeViewModel
import io.github.robinphillips98.nofussflashcards.ui.utils.ClickableTextRow

object SettingsDestination: NavigationDestination {
    override val route = "settings"
    override val titleResId = R.string.settings_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AppThemeViewModel,
    navigateToAbout: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeOption by viewModel.themeOption.collectAsState()
    val fontOption by viewModel.fontOption.collectAsState()

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
            selectedThemeName = stringResource(themeOption.titleResId),
            selectedFontName = fontOption.title,
            onThemeSelect = viewModel::updateTheme,
            onFontSelect = viewModel::updateFont,
            navigateToAbout = navigateToAbout,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun SettingsContent(
    selectedThemeName: String,
    selectedFontName: String,
    onThemeSelect: (AppThemeOptions) -> Unit,
    onFontSelect: (AppFontOptions) -> Unit,
    navigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource((R.string.settings_title)),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
        )

        HorizontalDivider()

        AppSettings(selectedThemeName, onThemeSelect, selectedFontName, onFontSelect)

        HorizontalDivider()

        Other(navigateToAbout = navigateToAbout)
    }
}

@Composable
private fun AppSettings(
    selectedThemeName: String,
    onThemeSelect: (AppThemeOptions) -> Unit,
    selectedFontName: String,
    onFontSelect: (AppFontOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.settings_app_settings_title),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(
                top = dimensionResource(R.dimen.padding_small),
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium)
            )
        )
        ThemeDropdownMenu(
            selectedThemeName = selectedThemeName,
            onThemeSelect = onThemeSelect,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
        )

        FontDropdownMenu(
            selectedFontName = selectedFontName,
            onFontSelect = onFontSelect,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
        )
    }
}

@Composable
private fun Other(
    navigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.settings_other_title),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(
                top = dimensionResource(R.dimen.padding_small),
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium)
            )
        )
        ClickableTextRow(
            text = stringResource(R.string.about_title),
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
                )
            },
            onClick = navigateToAbout,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontDropdownMenu(
    selectedFontName: String,
    onFontSelect: (AppFontOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedFontName,
                onValueChange = {},
                label = { Text(stringResource(R.string.settings_font_label)) },
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
                AppFontOptions.entries.forEach { fontOption ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = fontOption.title,
                                fontFamily = fontOption.fontFamily
                            )
                        },
                        onClick = {
                            onFontSelect(fontOption)
                            expanded = false
                        }
                    )
                }
            }
        }
        Text(
            text = stringResource(R.string.settings_font_download_warning),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsContent(
        selectedThemeName = stringResource(AppThemeOptions.DEFAULT.titleResId),
        selectedFontName = AppFontOptions.DEFAULT.title,
        onThemeSelect = {},
        onFontSelect = {},
        navigateToAbout = {}
    )
}