package com.nofussflashcards.app.ui.about

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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.nofussflashcards.app.NoFussFlashCardsTopAppBar
import com.nofussflashcards.app.R
import com.nofussflashcards.app.navigation.NavigationDestination

object TermsOfServiceDestination : NavigationDestination {
    override val route = "terms_of_service"
    override val titleResId = R.string.terms_of_service_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            NoFussFlashCardsTopAppBar(
                title = stringResource(TermsOfServiceDestination.titleResId),
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        val boldStyle = MaterialTheme.typography.bodyLarge.toSpanStyle().copy(
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Last Updated: September 2, 2026",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )
            Text(
                text = buildAnnotatedString {
                    append("These terms of service (\"")
                    withStyle(style = boldStyle) {
                        append("Terms")
                    }
                    append("\") govern your use of No Fuss Flashcards (\"")
                    withStyle(style = boldStyle) {
                        append("App")
                    }
                    append("\"). By downloading, installing, or using the App, you agree to be " +
                            "bound by these Terms. If you do not agree to these Terms, do not use " +
                            "the App.")
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )

            Text(
                text = "Definitions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = buildAnnotatedString {
                  append("1. \"")
                    withStyle(style = boldStyle) {
                            append("User")
                        }
                        append("\" refers to any person who downloads, installs, or uses the App.")
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )
            Text(
                text = buildAnnotatedString {
                  append("2. \"")
                    withStyle(style = boldStyle) {
                            append("Content")
                        }
                        append("\" refers to any text, images, video, audio, or other media " +
                                "available through the App.")
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )
            Text(
                text = buildAnnotatedString {
                  append("3. \"")
                    withStyle(style = boldStyle) {
                            append("Developer")
                        }
                        append("\" refers to the individual or entity responsible for creating and " +
                                "maintaining the App.")
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )

            Text(
                text = "License",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = "Subject to your compliance with these Terms, the Developer grants you a " +
                        "limited, non-exclusive, non-transferable, revocable license to download, " +
                        "install, and use the App for your personal, non-commercial purposes.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )

            Text(
                text = "User Conduct",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = "You agree not to use the App for any unlawful or fraudulent purposes.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )

            Text(
                text = "Disclaimer",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = "THE APP IS PROVIDED \"AS IS\" AND \"AS AVAILABLE,\" WITHOUT WARRANTIES OF " +
                        "ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO WARRANTIES OF " +
                        "MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. " +
                        "THE DEVELOPER DOES NOT WARRANT THAT THE APP WILL BE UNINTERRUPTED, " +
                        "ERROR-FREE, OR COMPLETELY SECURE.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )

            Text(
                text = "Limitation of Liability",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = "TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, THE DEVELOPER SHALL NOT " +
                        "BE LIABLE FOR ANY INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, OR " +
                        "EXEMPLARY DAMAGES ARISING OUT OF OR IN CONNECTION WITH THE USE OF THE APP, " +
                        "EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )

            Text(
                text = "Governing Law",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = "These Terms shall be governed by and construed in accordance with the laws " +
                        "of the United States, without regard to its conflict of laws principles.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )

            Text(
                text = "Modifications",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = "The Developer reserves the right to modify these Terms at any time, in its " +
                        "sole discretion. Your continued use of the App following any modification " +
                        "constitutes your acceptance of the modified Terms.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
            )

            Text(
                text = "Contact Information",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = "If you have any questions or concerns about these Terms or the App, please " +
                        "contact the Developer at ${stringResource(R.string.support_email)}.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_medium)
                )
            )
        }
    }
}

@Preview
@Composable
fun TermsOfServiceScreenPreview() {
    TermsOfServiceScreen(
        navigateUp = {}
    )
}

