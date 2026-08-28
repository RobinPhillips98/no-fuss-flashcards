package io.github.robinphillips98.nofussflashcards.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.AppViewModelProvider
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
    appThemeViewModel: AppThemeViewModel,
    navigateToAbout: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val themeOption by appThemeViewModel.themeOption.collectAsState()
    val fontOption by appThemeViewModel.fontOption.collectAsState()
    val uiState by settingsViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-shot events and show snackbars
    LaunchedEffect(Unit) {
        settingsViewModel.events.collect { event ->
            when (event) {
                is SettingsUiEvent.ShowSuccessSnackbar ->
                    snackbarHostState.showSnackbar(event.message, withDismissAction = true)
                is SettingsUiEvent.ShowErrorSnackbar ->
                    snackbarHostState.showSnackbar(event.message, withDismissAction = true)
            }
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { settingsViewModel.exportData(context, it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { settingsViewModel.importData(context, it) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(SettingsDestination.titleResId),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        SettingsContent(
            selectedThemeName = stringResource(themeOption.titleResId),
            selectedFontName = fontOption.title,
            isBackupLoading = uiState.isBackupLoading,
            exportWarningMessage = uiState.exportWarningMessage,
            onThemeSelect = appThemeViewModel::updateTheme,
            onFontSelect = appThemeViewModel::updateFont,
            onExportClick = {
                exportLauncher.launch("nofuss_flashcards_backup.json")
            },
            onImportClick = {
                importLauncher.launch(arrayOf("application/json"))
            },
            navigateToAbout = navigateToAbout,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun SettingsContent(
    selectedThemeName: String,
    selectedFontName: String,
    isBackupLoading: Boolean,
    exportWarningMessage: String?,
    onThemeSelect: (AppThemeOptions) -> Unit,
    onFontSelect: (AppFontOptions) -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
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

        DataSettings(
            isLoading = isBackupLoading,
            exportWarningMessage = exportWarningMessage,
            onExportClick = onExportClick,
            onImportClick = onImportClick
        )

        HorizontalDivider()

        Other(navigateToAbout = navigateToAbout)
    }
}

@Composable
private fun DataSettings(
    isLoading: Boolean,
    exportWarningMessage: String?,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var contentHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.onGloballyPositioned { coordinates ->
                contentHeightPx = coordinates.size.height
            }
        ) {
            Text(
                text = stringResource(R.string.settings_data_title),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.padding_small),
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium)
                )
            )
            Text(
                text = stringResource(R.string.settings_images_not_exported_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.padding_small),
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium)
                )
            )
            ClickableTextRow(
                text = stringResource(R.string.settings_export_label),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.FileUpload,
                        contentDescription = null,
                        modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
                    )
                },
                onClick = onExportClick,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )
            exportWarningMessage?.let { warningMessage ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        top = dimensionResource(R.dimen.padding_small),
                        start = dimensionResource(R.dimen.padding_medium),
                        end = dimensionResource(R.dimen.padding_medium)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
                    )
                    Text(
                        text = warningMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            ClickableTextRow(
                text = stringResource(R.string.settings_import_label),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
                    )
                },
                onClick = onImportClick
            )
        }

        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { contentHeightPx.toDp() })
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
            ) {
                CircularProgressIndicator()
            }
        }
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
        isBackupLoading = false,
        exportWarningMessage = null,
        onThemeSelect = {},
        onFontSelect = {},
        onExportClick = {},
        onImportClick = {},
        navigateToAbout = {}
    )
}