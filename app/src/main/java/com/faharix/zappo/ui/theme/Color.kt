package com.faharix.zappo.ui.theme

import androidx.compose.ui.graphics.Color

// Couleurs principales FahariX (basées sur le logo)
val FahariXBlue = Color(0xFF1E88E5) // Bleu principal du logo
val FahariXCyan = Color(0xFF00BCD4) // Cyan du dégradé
val FahariXDeepBlue = Color(0xFF3F51B5) // Bleu profond
val FahariXPurple = Color(0xFF673AB7) // Violet du dégradé

// Palette Light Theme
val FahariXLight = androidx.compose.material3.lightColorScheme(
    primary = FahariXBlue,
    onPrimary = Color.White,
    primaryContainer = FahariXBlue.copy(alpha = 0.1f),
    onPrimaryContainer = FahariXDeepBlue,

    secondary = FahariXCyan,
    onSecondary = Color.White,
    secondaryContainer = FahariXCyan.copy(alpha = 0.1f),
    onSecondaryContainer = FahariXDeepBlue,

    tertiary = FahariXPurple,
    onTertiary = Color.White,
    tertiaryContainer = FahariXPurple.copy(alpha = 0.1f),
    onTertiaryContainer = FahariXDeepBlue,

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF3F3F3),
    onSurfaceVariant = Color(0xFF49454F),

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

// Palette Dark Theme
val FahariXDark = androidx.compose.material3.darkColorScheme(
    primary = FahariXBlue.copy(alpha = 0.8f),
    onPrimary = Color(0xFF003258),
    primaryContainer = FahariXBlue.copy(alpha = 0.2f),
    onPrimaryContainer = FahariXBlue.copy(alpha = 0.9f),

    secondary = FahariXCyan.copy(alpha = 0.8f),
    onSecondary = Color(0xFF003037),
    secondaryContainer = FahariXCyan.copy(alpha = 0.2f),
    onSecondaryContainer = FahariXCyan.copy(alpha = 0.9f),

    tertiary = FahariXPurple.copy(alpha = 0.8f),
    onTertiary = Color(0xFF332D41),
    tertiaryContainer = FahariXPurple.copy(alpha = 0.2f),
    onTertiaryContainer = FahariXPurple.copy(alpha = 0.9f),

    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFCAC4D0),

    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// Couleurs de gradient pour les éléments spéciaux
val FahariXGradientColors = listOf(
    FahariXCyan,
    FahariXBlue,
    FahariXDeepBlue,
    FahariXPurple
)