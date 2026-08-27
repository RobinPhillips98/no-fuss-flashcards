package io.github.robinphillips98.nofussflashcards.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import io.github.robinphillips98.nofussflashcards.R

val Provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val ShantellSans = FontFamily(
    Font(googleFont = GoogleFont("Shantell Sans"), fontProvider = Provider)
)

val ChakraPetch = FontFamily(
    Font(googleFont = GoogleFont("Chakra Petch"), fontProvider = Provider)
)

val IMFellEnglish = FontFamily(
    Font(googleFont = GoogleFont("IM Fell English"), fontProvider = Provider)
)

val Quicksand = FontFamily(
    Font(googleFont = GoogleFont("Quicksand"), fontProvider = Provider)
)

val PlayfairDisplay = FontFamily(
    Font(googleFont = GoogleFont("Playfair Display"), fontProvider = Provider)
)

val Montserrat = FontFamily(
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = Provider)
)

val SourceSansPro = FontFamily(
    Font(googleFont = GoogleFont("Source Sans Pro"), fontProvider = Provider)
)

val Nunito = FontFamily(
    Font(googleFont = GoogleFont("Nunito"), fontProvider = Provider)
)

val Ubuntu = FontFamily(
    Font(googleFont = GoogleFont("Ubuntu"), fontProvider = Provider)
)

val Poppins = FontFamily(
    Font(googleFont = GoogleFont("Poppins"), fontProvider = Provider)
)

val DMSans = FontFamily(
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = Provider)
)

val Rubik = FontFamily(
    Font(googleFont = GoogleFont("Rubik"), fontProvider = Provider)
)

val JosefinSans = FontFamily(
    Font(googleFont = GoogleFont("Josefin Sans"), fontProvider = Provider)
)

val Manrope = FontFamily(
    Font(googleFont = GoogleFont("Manrope"), fontProvider = Provider)
)

val OpenSans = FontFamily(
    Font(googleFont = GoogleFont("Open Sans"), fontProvider = Provider)
)

val Inter = FontFamily(
    Font(googleFont = GoogleFont("Inter"), fontProvider = Provider)
)

val Comfortaa = FontFamily(
    Font(googleFont = GoogleFont("Comfortaa"), fontProvider = Provider)
)

val PublicSans = FontFamily(
    Font(googleFont = GoogleFont("Public Sans"), fontProvider = Provider)
)

val WinkyRough = FontFamily(
    Font(R.font.winky_rough_regular),
    Font(R.font.winky_rough_bold, FontWeight.Bold),
    Font(R.font.winky_rough_italic, style = FontStyle.Italic),
    Font(R.font.winky_rough_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.winky_rough_light, FontWeight.Light),
    Font(R.font.winky_rough_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.winky_rough_medium, FontWeight.Medium),
    Font(R.font.winky_rough_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.winky_rough_semi_bold, FontWeight.SemiBold),
    Font(R.font.winky_rough_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.winky_rough_extra_bold, FontWeight.ExtraBold),
    Font(R.font.winky_rough_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.winky_rough_black, FontWeight.Black),
    Font(R.font.winky_rough_black_italic, FontWeight.Black, FontStyle.Italic)
)

val OpenDyslexic = FontFamily(
    Font(R.font.open_dyslexic_regular),
    Font(R.font.open_dyslexic_bold, FontWeight.Bold),
    Font(R.font.open_dyslexic_italic, style = FontStyle.Italic),
    Font(R.font.open_dyslexic_bold_italic, FontWeight.Bold, FontStyle.Italic)
)


/* Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
 */

fun buildTypography(fontFamily: FontFamily = FontFamily.Default): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 45.sp,
            lineHeight = 52.sp
        ),
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 40.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 36.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodySmall= TextStyle(
            fontFamily=fontFamily,
            fontWeight=FontWeight.Normal,
            fontSize=12.sp,
            lineHeight=16.sp
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    )
}