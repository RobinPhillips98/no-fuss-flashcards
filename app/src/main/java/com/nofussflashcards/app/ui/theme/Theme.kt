package com.nofussflashcards.app.ui.theme

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import com.nofussflashcards.app.R
import com.nofussflashcards.app.ui.theme.colors.bubblegum.BubbleGumDarkColorScheme
import com.nofussflashcards.app.ui.theme.colors.bubblegum.BubbleGumDarkHighContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.bubblegum.BubbleGumDarkMediumContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.bubblegum.BubbleGumLightColorScheme
import com.nofussflashcards.app.ui.theme.colors.bubblegum.BubbleGumLightHighContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.bubblegum.BubbleGumLightMediumContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.canyon.CanyonDarkColorScheme
import com.nofussflashcards.app.ui.theme.colors.canyon.CanyonDarkHighContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.canyon.CanyonDarkMediumContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.canyon.CanyonLightColorScheme
import com.nofussflashcards.app.ui.theme.colors.canyon.CanyonLightHighContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.canyon.CanyonLightMediumContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.neon.NeonDarkColorScheme
import com.nofussflashcards.app.ui.theme.colors.neon.NeonDarkHighContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.neon.NeonDarkMediumContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.neon.NeonLightColorScheme
import com.nofussflashcards.app.ui.theme.colors.neon.NeonLightHighContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.neon.NeonLightMediumContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.ocean.OceanDarkColorScheme
import com.nofussflashcards.app.ui.theme.colors.ocean.OceanDarkHighContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.ocean.OceanDarkMediumContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.ocean.OceanLightColorScheme
import com.nofussflashcards.app.ui.theme.colors.ocean.OceanLightHighContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.ocean.OceanLightMediumContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.vampire.VampireDarkColorScheme
import com.nofussflashcards.app.ui.theme.colors.vampire.VampireDarkHighContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.vampire.VampireDarkMediumContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.vampire.VampireLightColorScheme
import com.nofussflashcards.app.ui.theme.colors.vampire.VampireLightHighContrastColorScheme
import com.nofussflashcards.app.ui.theme.colors.vampire.VampireLightMediumContrastColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun FlashcardsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    themeOption: AppThemeOptions = AppThemeOptions.DEFAULT,
    fontOption: AppFontOptions = AppFontOptions.DEFAULT,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = getColorScheme(context, darkTheme, dynamicColor, themeOption)
    val typography = buildTypography(fontOption.fontFamily)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

/**
 * Returns the appropriate color scheme based on the selected theme option and contrast option.
 *
 * @param context The context used to access system services.
 * @param darkTheme Whether the dark theme is enabled.
 * @param dynamicColor Whether dynamic color is enabled (available on Android 12+).
 * @param themeOption The selected theme option.
 *
 * @return The appropriate ColorScheme based on the selected theme and contrast options.
 */
private fun getColorScheme(
    context: Context,
    darkTheme: Boolean,
    dynamicColor: Boolean,
    themeOption: AppThemeOptions
): ColorScheme {
    val contrastOption = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        getContrast(context)
    } else {
        AppThemeContrastOptions.DEFAULT
    }

    return when (themeOption) {
         AppThemeOptions.DEFAULT -> {
             when {
                 dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                     if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                 }

                 darkTheme -> DarkColorScheme
                 else -> LightColorScheme
             }
         }

        AppThemeOptions.BUBBLEGUM -> {
            if (darkTheme) {
                when (contrastOption) {
                    AppThemeContrastOptions.DEFAULT -> BubbleGumDarkColorScheme
                    AppThemeContrastOptions.MEDIUM_CONTRAST -> BubbleGumDarkMediumContrastColorScheme
                    AppThemeContrastOptions.HIGH_CONTRAST -> BubbleGumDarkHighContrastColorScheme
                }
            } else {
                when (contrastOption) {
                    AppThemeContrastOptions.DEFAULT -> BubbleGumLightColorScheme
                    AppThemeContrastOptions.MEDIUM_CONTRAST -> BubbleGumLightMediumContrastColorScheme
                    AppThemeContrastOptions.HIGH_CONTRAST -> BubbleGumLightHighContrastColorScheme
                }
            }
        }

        AppThemeOptions.CANYON -> {
            if (darkTheme) {
                when (contrastOption) {
                    AppThemeContrastOptions.DEFAULT -> CanyonDarkColorScheme
                    AppThemeContrastOptions.MEDIUM_CONTRAST -> CanyonDarkMediumContrastColorScheme
                    AppThemeContrastOptions.HIGH_CONTRAST -> CanyonDarkHighContrastColorScheme
                }
            } else {
                when (contrastOption) {
                    AppThemeContrastOptions.DEFAULT -> CanyonLightColorScheme
                    AppThemeContrastOptions.MEDIUM_CONTRAST -> CanyonLightMediumContrastColorScheme
                    AppThemeContrastOptions.HIGH_CONTRAST -> CanyonLightHighContrastColorScheme
                }
            }
        }

        AppThemeOptions.NEON -> {
            if (darkTheme) {
                when (contrastOption) {
                    AppThemeContrastOptions.DEFAULT -> NeonDarkColorScheme
                    AppThemeContrastOptions.MEDIUM_CONTRAST -> NeonDarkMediumContrastColorScheme
                    AppThemeContrastOptions.HIGH_CONTRAST -> NeonDarkHighContrastColorScheme
                }
            } else {
                when (contrastOption) {
                    AppThemeContrastOptions.DEFAULT -> NeonLightColorScheme
                    AppThemeContrastOptions.MEDIUM_CONTRAST -> NeonLightMediumContrastColorScheme
                    AppThemeContrastOptions.HIGH_CONTRAST -> NeonLightHighContrastColorScheme
                }
            }
        }

        AppThemeOptions.OCEAN -> {
            if (darkTheme) {
                when (contrastOption) {
                    AppThemeContrastOptions.DEFAULT -> OceanDarkColorScheme
                    AppThemeContrastOptions.MEDIUM_CONTRAST -> OceanDarkMediumContrastColorScheme
                    AppThemeContrastOptions.HIGH_CONTRAST -> OceanDarkHighContrastColorScheme
                }
            } else {
                when (contrastOption) {
                    AppThemeContrastOptions.DEFAULT -> OceanLightColorScheme
                    AppThemeContrastOptions.MEDIUM_CONTRAST -> OceanLightMediumContrastColorScheme
                    AppThemeContrastOptions.HIGH_CONTRAST -> OceanLightHighContrastColorScheme
                }
            }
        }

        AppThemeOptions.VAMPIRE -> {
            if (darkTheme) {
                when (contrastOption) {
                    AppThemeContrastOptions.DEFAULT -> VampireDarkColorScheme
                    AppThemeContrastOptions.MEDIUM_CONTRAST -> VampireDarkMediumContrastColorScheme
                    AppThemeContrastOptions.HIGH_CONTRAST -> VampireDarkHighContrastColorScheme
                }
            } else {
                when (contrastOption) {
                    AppThemeContrastOptions.DEFAULT -> VampireLightColorScheme
                    AppThemeContrastOptions.MEDIUM_CONTRAST -> VampireLightMediumContrastColorScheme
                    AppThemeContrastOptions.HIGH_CONTRAST -> VampireLightHighContrastColorScheme
                }
            }
        }
    }
}

/**
 * Returns the appropriate contrast option based on the system's contrast settings.
 *
 * @param context The context used to access system services.
 *
 * @return The appropriate AppThemeContrastOptions based on the system's contrast settings.
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
private fun getContrast(context: Context): AppThemeContrastOptions {
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val contrastLevel = uiModeManager.contrast

    return when (contrastLevel) {
        in 0.0f..0.33f -> AppThemeContrastOptions.DEFAULT
        in 0.34f..0.66f -> AppThemeContrastOptions.MEDIUM_CONTRAST
        in 0.67f..1.0f -> AppThemeContrastOptions.HIGH_CONTRAST
        else -> AppThemeContrastOptions.DEFAULT
    }
}

enum class AppThemeOptions(@StringRes val titleResId: Int) {
    DEFAULT(titleResId = R.string.theme_default),
    BUBBLEGUM(titleResId = R.string.theme_bubblegum),
    CANYON(titleResId = R.string.theme_canyon),
    NEON(titleResId = R.string.theme_neon),
    OCEAN(titleResId = R.string.theme_ocean),
    VAMPIRE(titleResId = R.string.theme_vampire)
}

enum class AppThemeContrastOptions {
    DEFAULT,
    MEDIUM_CONTRAST,
    HIGH_CONTRAST
}

enum class AppFontOptions(
    val title: String,
    val fontFamily: FontFamily
) {
    DEFAULT(
        title = "Default (System Font)",
        fontFamily = FontFamily.Default
    ),
    CHAKRA_PETCH(
        title = "Chakra Petch",
        fontFamily = ChakraPetch
    ),
    COMFORTAA(
        title = "Comfortaa",
        fontFamily = Comfortaa
    ),
    DM_SANS(
        title = "DM Sans",
        fontFamily = DMSans
    ),
    IM_FELL_ENGLISH(
        title = "IM Fell English",
        fontFamily = IMFellEnglish
    ),
    INTER(
        title = "Inter",
        fontFamily = Inter
    ),
    JOSEFIN_SANS(
        title = "Josefin Sans",
        fontFamily = JosefinSans
    ),
    MANROPE(
        title = "Manrope",
        fontFamily = Manrope
    ),
    MONTSERRAT(
        title = "Montserrat",
        fontFamily = Montserrat
    ),
    NUNITO(
        title = "Nunito",
        fontFamily = Nunito
    ),
    OPEN_DYSLEXIC(
    title = "Open Dyslexic",
    fontFamily = OpenDyslexic
    ),
    OPEN_SANS(
        title = "Open Sans",
        fontFamily = OpenSans
    ),
    PLAYFAIR_DISPLAY(
        title = "Playfair Display",
        fontFamily = PlayfairDisplay
    ),
    POPPINS(
        title = "Poppins",
        fontFamily = Poppins
    ),
    PUBLIC_SANS(
        title = "Public Sans",
        fontFamily = PublicSans
    ),
    RUBIK(
        title = "Rubik",
        fontFamily = Rubik
    ),
    SHANTELL_SANS(
        title = "Shantell Sans",
        fontFamily = ShantellSans
    ),
    SOURCE_SANS_PRO(
        title = "Source Sans Pro",
        fontFamily = SourceSansPro
    ),
    QUICKSAND(
        title = "Quicksand",
        fontFamily = Quicksand
    ),
    UBUNTU(
        title = "Ubuntu",
        fontFamily = Ubuntu
    ),
    WINKY_ROUGH(
        title = "Winky Rough",
        fontFamily = WinkyRough
    )
}