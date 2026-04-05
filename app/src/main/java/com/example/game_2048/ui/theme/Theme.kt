package com.example.game_2048.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class GameColors(
    val background: Color,
    val boardBackground: Color,
    val emptyCell: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val scoreCard: Color,
    val restartButton: Color,
    val isDark: Boolean
)

val LocalGameColors = compositionLocalOf {
    GameColors(
        background = LightBackground,
        boardBackground = BoardBackgroundLight,
        emptyCell = LightEmptyCell,
        textPrimary = LightTextPrimary,
        textSecondary = LightTextSecondary,
        scoreCard = ScoreCardLight,
        restartButton = RestartButtonLight,
        isDark = false
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = AccentGold,
    secondary = AccentOrange,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = AccentGold,
    secondary = AccentOrange,
    background = LightBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

@Composable
fun Game2048Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val gameColors = if (darkTheme) {
        GameColors(
            background = DarkBackground,
            boardBackground = BoardBackgroundDark,
            emptyCell = DarkEmptyCell,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            scoreCard = ScoreCardDark,
            restartButton = RestartButtonDark,
            isDark = true
        )
    } else {
        GameColors(
            background = LightBackground,
            boardBackground = BoardBackgroundLight,
            emptyCell = LightEmptyCell,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            scoreCard = ScoreCardLight,
            restartButton = RestartButtonLight,
            isDark = false
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalGameColors provides gameColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
