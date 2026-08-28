package io.github.robinphillips98.nofussflashcards.ui.about

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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

object LicenseDestination: NavigationDestination {
    override val route = "license"
    override val titleResId = R.string.license_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(LicenseDestination.titleResId),
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LicenseContent(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun LicenseContent(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Copyright 2026 Robin Phillips\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\"); " +
                "you may not use this file except in compliance with the License. " +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software " +
                "distributed under the License is distributed on an \"AS IS\" BASIS, " +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. " +
                "See the License for the specific language governing permissions and " +
                "limitations under the License.",
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    )
}

@Preview
@Composable
fun LicenseScreenPreview() {
    LicenseScreen(
        navigateUp = {}
    )
}