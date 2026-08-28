package io.github.robinphillips98.nofussflashcards.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.robinphillips98.nofussflashcards.NoFussFlashCardsTopAppBar
import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.navigation.NavigationDestination

object PrivacyPolicyDestination: NavigationDestination {
    override val route = "privacy_policy"
    override val titleResId = R.string.privacy_policy_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(PrivacyPolicyDestination.titleResId),
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Last updated: August 27, 2026",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )
            Text(
                text = "No Fuss Flashcards does not collect, store, or share any personal information or user data.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = "\u2022 Data Collection: We do not track, collect, or transmit any data from your device. All app operations run locally.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium_small))
            )
            Text(
                text = "\u2022 Third-Party Services: We do not use third-party analytics, advertising networks, or data miners.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )
            Text(
                text = "\u2022 Contact Us: If you have any questions about this privacy policy, please contact us at nofussflashcards.app@gmail.com.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )
        }
    }
}

@Preview
@Composable
fun PrivacyPolicyScreenPreview() {
    PrivacyPolicyScreen(
        navigateUp = {}
    )
}