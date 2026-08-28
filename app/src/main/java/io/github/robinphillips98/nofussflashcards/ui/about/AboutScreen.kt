package io.github.robinphillips98.nofussflashcards.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.robinphillips98.nofussflashcards.BuildConfig
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination
import io.github.robinphillips98.nofussflashcards.ui.utils.ClickableTextRow

const val GITHUB_REPO_URL = "https://github.com/RobinPhillips98/no-fuss-flashcards"
const val BUG_REPORT_URL = "$GITHUB_REPO_URL/issues"
const val KOFI_URL = "https://ko-fi.com/robinphillips98"

object AboutDestination: NavigationDestination {
    override val route = "about"
    override val titleResId = R.string.about_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navigateToLicense: () -> Unit,
    navigateToPrivacyPolicy: () -> Unit,
    navigateToTermsOfService: () -> Unit,
    navigateToOpenSourceLibraries: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(AboutDestination.titleResId),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        AboutContent(
            navigateToLicense = navigateToLicense,
            navigateToPrivacyPolicy = navigateToPrivacyPolicy,
            navigateToTermsOfService = navigateToTermsOfService,
            navigateToOpenSourceLibraries = navigateToOpenSourceLibraries,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun AboutContent(
    navigateToLicense: () -> Unit,
    navigateToPrivacyPolicy: () -> Unit,
    navigateToTermsOfService: () -> Unit,
    navigateToOpenSourceLibraries: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AboutTheApp(modifier = Modifier.padding(top = 8.dp))
        HorizontalDivider()
        DeveloperInfo()
        HorizontalDivider()
        LegalDocuments(
            navigateToLicense = navigateToLicense,
            navigateToPrivacyPolicy = navigateToPrivacyPolicy,
            navigateToTermsOfService = navigateToTermsOfService,
            navigateToOpenSourceLibraries = navigateToOpenSourceLibraries,
        )
    }
}

@Composable
private fun AboutTheApp(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.about_the_app_title),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )
        Text(
            text = stringResource(R.string.about_the_app_description),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.about_version_label),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = BuildConfig.VERSION_NAME,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun DeveloperInfo(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.developer_info_title),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )
        ClickableTextRow(
            text = stringResource(R.string.github_title),
            onClick = { uriHandler.openUri(GITHUB_REPO_URL) }
        )
        ClickableTextRow(
            text = stringResource(R.string.bug_report_title),
            onClick = { uriHandler.openUri(BUG_REPORT_URL) }
        )
        ClickableTextRow(
            text = stringResource(R.string.kofi_title),
            onClick = { uriHandler.openUri(KOFI_URL) }
        )
    }
}

@Composable
private fun LegalDocuments(
    navigateToLicense: () -> Unit,
    navigateToPrivacyPolicy: () -> Unit,
    navigateToTermsOfService: () -> Unit,
    navigateToOpenSourceLibraries: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.legal_documents_title),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )
        ClickableTextRow(
            text = stringResource(R.string.privacy_policy_title),
            onClick = navigateToPrivacyPolicy
        )
        ClickableTextRow(
            text = stringResource(R.string.terms_of_service_title),
            onClick = navigateToTermsOfService
        )
        ClickableTextRow(
            text = stringResource(R.string.license_title),
            onClick = navigateToLicense
        )
        ClickableTextRow(
            text = stringResource(R.string.open_source_licenses_title),
            onClick = navigateToOpenSourceLibraries
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutContent(
        navigateToLicense = {},
        navigateToPrivacyPolicy = {},
        navigateToTermsOfService = {},
        navigateToOpenSourceLibraries = {}
    )
}