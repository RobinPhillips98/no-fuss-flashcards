package com.nofussflashcards.app.ui.errors

import com.nofussflashcards.app.R
import com.nofussflashcards.app.data.errors.AppError
import com.nofussflashcards.app.utils.StringResolver

/**
 * Represents errors that can occur during settings operations (import/export).
 */
sealed interface SettingsError: AppError {
    data object ExportFailed : SettingsError
    data object ImportFailed : SettingsError
    data object InvalidImportFile : SettingsError
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
        is SettingsError.InvalidImportFile -> get(R.string.invalid_import_file)
        is SettingsError.NoDataToExport -> get(R.string.no_data_to_export)
        is SettingsError.Unknown -> get(R.string.error_unknown, error.cause.message ?: "Unknown error")
    }
}

