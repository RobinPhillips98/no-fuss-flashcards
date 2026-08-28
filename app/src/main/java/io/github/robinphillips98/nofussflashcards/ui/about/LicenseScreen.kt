package io.github.robinphillips98.nofussflashcards.ui.about

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
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
        text = buildAnnotatedString {
            appendLine("Copyright 2026 Robin Phillips")
            appendLine()
            append("Licensed under the Apache License, Version 2.0 (the \"License\"); ")
            append("you may not use this file except in compliance with the License. ")
            appendLine("You may obtain a copy of the License at")
            appendLine()
            append("    ")
            withLink(LinkAnnotation.Url("http://www.apache.org/licenses/LICENSE-2.0")) {
                appendLine("http://www.apache.org/licenses/LICENSE-2.0")
            }
            appendLine()
            append("Unless required by applicable law or agreed to in writing, software ")
            append("distributed under the License is distributed on an \"AS IS\" BASIS, ")
            append("WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. ")
            append("See the License for the specific language governing permissions and ")
            append("limitations under the License.")
        },
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