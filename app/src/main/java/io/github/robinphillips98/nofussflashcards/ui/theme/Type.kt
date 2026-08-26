package io.github.robinphillips98.nofussflashcards.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import io.github.robinphillips98.nofussflashcards.R
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle

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


// Set of Material typography styles to start with
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