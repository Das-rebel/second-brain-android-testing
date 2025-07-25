package com.secondbrain.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp
)

val MaterialTheme.spacing: Spacing
    @Composable
    get() = Spacing()

val Purple10 = Color(0xFF2B0B3A)
val Purple20 = Color(0xFF42275E)
val Purple30 = Color(0xFF5C3D7D)
val Purple40 = Color(0xFF7B579D)
val Purple80 = Color(0xFFD0BCFF)
val Purple90 = Color(0xFFEADDFF)

val DarkPurpleGray10 = Color(0xFF1E1A1F)
val DarkPurpleGray90 = Color(0xFFE6E1E5)
val DarkPurpleGray99 = Color(0xFFFDF8FF)

val PurpleGray30 = Color(0xFF4A4458)
val PurpleGray50 = Color(0xFF7F7597)
val PurpleGray60 = Color(0xFF998EA4)
val PurpleGray80 = Color(0xFFCCC2DC)
val PurpleGray90 = Color(0xFFE8DEF8)

val Red10 = Color(0xFF410001)
val Red20 = Color(0xFF690004)
val Red30 = Color(0xFF930006)
val Red40 = Color(0xFFBA1B1B)
val Red80 = Color(0xFFFFB4A9)
val Red90 = Color(0xFFFFDAD4)

val Green10 = Color(0xFF00210B)
val Green20 = Color(0xFF003919)
val Green30 = Color(0xFF005227)
val Green40 = Color(0xFF006D36)
val Green80 = Color(0xFF5CFF9B)
val Green90 = Color(0xFF7AFFB1)

@Composable
fun backgroundColor(isDark: Boolean = isSystemInDarkTheme()): Color {
    return if (isDark) DarkPurpleGray10 else Color.White
}

@Composable
fun onBackgroundColor(isDark: Boolean = isSystemInDarkTheme()): Color {
    return if (isDark) DarkPurpleGray90 else Color.Black
}

@Composable
fun cardBackgroundColor(isDark: Boolean = isSystemInDarkTheme()): Color {
    return if (isDark) PurpleGray30 else Color.White
}

@Composable
fun errorColor(): Color {
    return MaterialTheme.colorScheme.error
}

@Composable
fun successColor(): Color {
    return Green40
}

@Composable
fun warningColor(): Color {
    return Color(0xFFFFA000)
}

@Composable
fun disabledColor(): Color {
    return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
}

@Composable
fun shimmerColor(isDark: Boolean = isSystemInDarkTheme()): Color {
    return if (isDark) {
        Color.White.copy(alpha = 0.1f)
    } else {
        Color.Black.copy(alpha = 0.1f)
    }
}
