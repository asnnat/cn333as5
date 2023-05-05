package com.example.phonebook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color.White,
    primaryVariant = Color(red = 107, green = 200, blue = 167),
    secondary = Color(red = 227, green = 242, blue = 193),
    onPrimary = Color.White
)

private val LightColorPalette = lightColors(
    primary = Color(red = 33, green = 42, blue = 62),
    primaryVariant = Color(red = 155, green = 164, blue = 187),
    secondary = Color(red = 57, green = 72, blue = 103)
)

@Composable
fun PhoneBookTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

object PhonesThemeSettings {
    var isDarkThemeEnabled by mutableStateOf(false)
}