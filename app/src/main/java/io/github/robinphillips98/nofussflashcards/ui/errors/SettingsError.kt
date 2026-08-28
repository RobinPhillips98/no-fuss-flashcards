package io.github.robinphillips98.nofussflashcards.ui.errors

import io.github.robinphillips98.nofussflashcards.R
import io.github.robinphillips98.nofussflashcards.utils.StringResolver

/**
 * Represents errors that can occur during settings operations (import/export).
 */
sealed interface SettingsError {
    data object ExportFailed : SettingsError
    data object ImportFailed : SettingsError
    data object NoDataToExport : SettingsError
    data class Unknown(val cause: Throwable) : SettingsError
}

/**
 * Fetches a user-friendly error message for the given [SettingsError].
 *
 * @param error The [SettingsError] for which to fetch the message.
 *
 * @return A user-friendly error message corresponding to the [SettingsError].
 */
fun StringResolver.messageFor(error: SettingsError): String {
    return when (error) {
        is SettingsError.ExportFailed -> get(R.string.export_failed)
        is SettingsError.ImportFailed -> get(R.string.import_failed)
        is SettingsError.NoDataToExport -> get(R.string.no_data_to_export)
        is SettingsError.Unknown -> get(R.string.error_unknown, error.cause.message ?: "Unknown error")
    }
}

