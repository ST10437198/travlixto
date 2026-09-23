package com.travlixto.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = TravCyan,
    secondary = TravBlue,
    background = TravBackground,
    surface = TravBackground,
    onPrimary = Color.White,
    onBackground = TravDark,
    onSurface = TravDark
)

val TravTypography = Typography(
    headlineSmall = TextStyle(fontSize = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelSmall = TextStyle(fontSize = 12.sp)
)

@Composable
fun TravlixtoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = TravTypography,
        content = content
    )
}
