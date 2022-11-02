package ro.ase.dam.yeapauctions.ui.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ro.ase.dam.yeapauctions.R


val Typography = Typography(
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22. sp,
        lineHeight = 28. sp,
        letterSpacing = 0. sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16. sp,
        lineHeight = 24. sp,
        letterSpacing = 0.15.sp
    ),

    displayLarge = TextStyle(

    ),
    displayMedium = TextStyle(

    ),

    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 22. sp,
        lineHeight = 28. sp,
        letterSpacing = 0. sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 16. sp,
        lineHeight = 24. sp,
        letterSpacing = 0.15.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.W100,
        fontSize = 14. sp,
        lineHeight = 20. sp,
        letterSpacing = 0.11.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28. sp,
        lineHeight = 34. sp,
        letterSpacing = 0. sp
    ),
    bodyMedium = TextStyle(

    ),
    bodySmall = TextStyle(

    ),

    labelLarge = TextStyle(

    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16. sp,
        lineHeight = 24. sp,
        letterSpacing = 0.15.sp
    ),
    labelSmall = TextStyle(

    )

)
