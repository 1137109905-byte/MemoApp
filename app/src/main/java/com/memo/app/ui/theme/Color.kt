package com.memo.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MemoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val palette = ThemeManager.currentPalette(darkTheme)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = palette.accent,
            onPrimary = palette.text,
            primaryContainer = palette.accent.copy(alpha = 0.2f),
            secondary = palette.accentDark,
            background = palette.background,
            surface = palette.surface,
            surfaceVariant = palette.backgroundAlt,
            onBackground = palette.text,
            onSurface = palette.text,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.divider,
            error = palette.error,
        )
    } else {
        lightColorScheme(
            primary = palette.accent,
            onPrimary = palette.text,
            primaryContainer = palette.accent.copy(alpha = 0.15f),
            secondary = palette.accentDark,
            background = palette.background,
            surface = palette.surface,
            surfaceVariant = palette.backgroundAlt,
            onBackground = palette.text,
            onSurface = palette.text,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.divider,
            error = palette.error,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !palette.statusBarDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppleTypography,
        content = content
    )
}
