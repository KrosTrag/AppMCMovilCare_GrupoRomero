package com.example.appmcmovilcare_gruporomero.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NightBluePrimary,
    secondary = NightBlueSecondary,
    tertiary = NightBlueAccent80,
    background = NightBlueBackground,
    surface = NightBlueSurface,
    onPrimary = NightBlueOnPrimary,
    onSecondary = NightBlueOnPrimary,
    onTertiary = NightBlueOnPrimary,
    onBackground = Color(0xFFE0E6ED),
    onSurface = Color(0xFFE0E6ED),
    primaryContainer = Color(0xFF1565C0),
    secondaryContainer = Color(0xFF1976D2),
    surfaceVariant = Color(0xFF263545)
)

private val LightColorScheme = lightColorScheme(
    primary = NightBluePrimary,
    secondary = NightBlueSecondary,
    tertiary = NightBlueAccent40,
    background = Color(0xFFF5F7FA),
    surface = Color(0xFFEAEDF2),
    onPrimary = NightBlueOnPrimary,
    onSecondary = NightBlueOnPrimary,
    onTertiary = NightBlueOnPrimary,
    onBackground = NightBlueBackground,
    onSurface = NightBlue40,
    primaryContainer = Color(0xFF1565C0),
    secondaryContainer = Color(0xFF1976D2),
    surfaceVariant = Color(0xFFDCE3ED)
)

@Composable
fun AppMCmovilCare_GrupoRomeroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}