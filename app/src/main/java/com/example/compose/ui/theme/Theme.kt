package com.example.compose.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Esquema de colores basado en la paleta
private val AppColorScheme = lightColorScheme(
    primary =  Color1,
    onPrimary = TextDark,
    primaryContainer = Color2,          // la usa el TopAppBar
    onPrimaryContainer = TextDark,

    secondary = Color2,
    onSecondary = TextDark,
    secondaryContainer = Color3,
    onSecondaryContainer = TextDark,

    background = Color5,                // fondo de la pantalla
    onBackground = TextDark,

    surface = Color5,
    onSurface = TextDark,

    surfaceVariant = Color4,            // la usa el Card
    onSurfaceVariant = TextMuted,       // etiquetas de ProfileInfoRow

    outline = Color1,                   // bordes del campo de texto y botón
    outlineVariant = Color3             // HorizontalDivider
)

@Composable
fun EjemploComposeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography, // viene de Type.kt (ya existe en tu proyecto)
        content = content
    )
}