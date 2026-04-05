package com.example.game_2048.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

@Stable
data class GameColors(
    val background: Color,
    val backgroundGradientEnd: Color,
    val boardBackground: Color,
    val boardGlass: Color,
    val boardBorder: Color,
    val boardFrost: Color,
    val emptyCell: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textOnTile: Color,
    val scoreCard: Color,
    val scoreCardGlass: Color,
    val scoreCardBorder: Color,
    val scoreLabel: Color,
    val scoreValue: Color,
    val restartButton: Color,
    val overlayScrim: Color,
    val glowTopRight: Color,
    val glowBottomLeft: Color,
    val glowCenter: Color,
    val glowBoard: Color,
    val vignette: Color,
    val isDark: Boolean
)

val LocalGameColors = compositionLocalOf {
    GameColors(
        background = LightBackground,
        backgroundGradientEnd = LightBgGradientEnd,
        boardBackground = BoardBackgroundLight,
        boardGlass = BoardGlassLight,
        boardBorder = BoardBorderLight,
        boardFrost = BoardFrostLight,
        emptyCell = LightEmptyCell,
        textPrimary = LightTextPrimary,
        textSecondary = LightTextSecondary,
        textOnTile = LightTextOnTile,
        scoreCard = ScoreCardLight,
        scoreCardGlass = ScoreCardGlassLight,
        scoreCardBorder = ScoreCardBorderLight,
        scoreLabel = ScoreLabelLight,
        scoreValue = ScoreValueLight,
        restartButton = RestartButtonLight,
        overlayScrim = OverlayScrimLight,
        glowTopRight = GlowTopRightLight,
        glowBottomLeft = GlowBottomLeftLight,
        glowCenter = GlowCenterLight,
        glowBoard = GlowBoardLight,
        vignette = VignetteLight,
        isDark = false
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = AccentGold,
    secondary = AccentOrange,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = OverlayButtonText,
    onSecondary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = AccentGold,
    secondary = AccentOrange,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

@Composable
fun Game2048Theme(
    themeMode: com.example.game_2048.domain.model.ThemeMode =
        com.example.game_2048.domain.model.ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        com.example.game_2048.domain.model.ThemeMode.LIGHT -> false
        com.example.game_2048.domain.model.ThemeMode.DARK -> true
        com.example.game_2048.domain.model.ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val gameColors = if (darkTheme) {
        GameColors(
            background = DarkBgGradientStart,
            backgroundGradientEnd = DarkBgGradientEnd,
            boardBackground = BoardBackgroundDark,
            boardGlass = BoardGlassDark,
            boardBorder = BoardBorderDark,
            boardFrost = BoardFrostDark,
            emptyCell = DarkEmptyCell,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            textOnTile = DarkTextOnTile,
            scoreCard = ScoreCardDark,
            scoreCardGlass = ScoreCardGlassDark,
            scoreCardBorder = ScoreCardBorderDark,
            scoreLabel = ScoreLabelDark,
            scoreValue = ScoreValueDark,
            restartButton = RestartButtonDark,
            overlayScrim = OverlayScrimDark,
            glowTopRight = GlowTopRightDark,
            glowBottomLeft = GlowBottomLeftDark,
            glowCenter = GlowCenterDark,
            glowBoard = GlowBoardDark,
            vignette = VignetteDark,
            isDark = true
        )
    } else {
        GameColors(
            background = LightBackground,
            backgroundGradientEnd = LightBgGradientEnd,
            boardBackground = BoardBackgroundLight,
            boardGlass = BoardGlassLight,
            boardBorder = BoardBorderLight,
            boardFrost = BoardFrostLight,
            emptyCell = LightEmptyCell,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            textOnTile = LightTextOnTile,
            scoreCard = ScoreCardLight,
            scoreCardGlass = ScoreCardGlassLight,
            scoreCardBorder = ScoreCardBorderLight,
            scoreLabel = ScoreLabelLight,
            scoreValue = ScoreValueLight,
            restartButton = RestartButtonLight,
            overlayScrim = OverlayScrimLight,
            glowTopRight = GlowTopRightLight,
            glowBottomLeft = GlowBottomLeftLight,
            glowCenter = GlowCenterLight,
            glowBoard = GlowBoardLight,
            vignette = VignetteLight,
            isDark = false
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = gameColors.background.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = gameColors.backgroundGradientEnd.toArgb()
            val controller = WindowInsetsControllerCompat(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalGameColors provides gameColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
