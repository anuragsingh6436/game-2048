package com.example.game_2048.ui.theme

import androidx.compose.ui.graphics.Color

// Premium game palette - Light mode
val LightBackground = Color(0xFFFAF8EF)
val LightSurface = Color(0xFFBBADA0)
val LightEmptyCell = Color(0xFFCDC1B4)
val LightTextPrimary = Color(0xFF776E65)
val LightTextSecondary = Color(0xFFF9F6F2)

// Premium game palette - Dark mode
val DarkBackground = Color(0xFF1A1A2E)
val DarkSurface = Color(0xFF16213E)
val DarkEmptyCell = Color(0xFF2A2A4A)
val DarkTextPrimary = Color(0xFFE8E8E8)
val DarkTextSecondary = Color(0xFFF9F6F2)

// Board colors
val BoardBackgroundLight = Color(0xFFBBADA0)
val BoardBackgroundDark = Color(0xFF2D2D4A)

// Score card colors
val ScoreCardLight = Color(0xFFBBADA0)
val ScoreCardDark = Color(0xFF2D2D4A)

// Tile colors - carefully designed gradient progression
object TileColors {
    // Light mode tile backgrounds
    val tile2Light = Color(0xFFEEE4DA)
    val tile4Light = Color(0xFFEDE0C8)
    val tile8Light = Color(0xFFF2B179)
    val tile16Light = Color(0xFFF59563)
    val tile32Light = Color(0xFFF67C5F)
    val tile64Light = Color(0xFFF65E3B)
    val tile128Light = Color(0xFFEDCF72)
    val tile256Light = Color(0xFFEDCC61)
    val tile512Light = Color(0xFFEDC850)
    val tile1024Light = Color(0xFFEDC53F)
    val tile2048Light = Color(0xFFEDC22E)
    val tileSuperLight = Color(0xFF3C3A32)

    // Dark mode tile backgrounds
    val tile2Dark = Color(0xFF3D3D5C)
    val tile4Dark = Color(0xFF4A4A6A)
    val tile8Dark = Color(0xFFB06840)
    val tile16Dark = Color(0xFFBF6540)
    val tile32Dark = Color(0xFFC05535)
    val tile64Dark = Color(0xFFCC4025)
    val tile128Dark = Color(0xFFD4A840)
    val tile256Dark = Color(0xFFD4A530)
    val tile512Dark = Color(0xFFD4A220)
    val tile1024Dark = Color(0xFFD49F10)
    val tile2048Dark = Color(0xFFD49C00)
    val tileSuperDark = Color(0xFFE8D5A0)

    fun getBackgroundColor(value: Int, isDark: Boolean): Color {
        return if (isDark) getDarkBackground(value) else getLightBackground(value)
    }

    fun getTextColor(value: Int, isDark: Boolean): Color {
        return if (isDark) {
            Color.White
        } else {
            if (value <= 4) Color(0xFF776E65) else Color(0xFFF9F6F2)
        }
    }

    private fun getLightBackground(value: Int): Color = when (value) {
        2 -> tile2Light
        4 -> tile4Light
        8 -> tile8Light
        16 -> tile16Light
        32 -> tile32Light
        64 -> tile64Light
        128 -> tile128Light
        256 -> tile256Light
        512 -> tile512Light
        1024 -> tile1024Light
        2048 -> tile2048Light
        else -> tileSuperLight
    }

    private fun getDarkBackground(value: Int): Color = when (value) {
        2 -> tile2Dark
        4 -> tile4Dark
        8 -> tile8Dark
        16 -> tile16Dark
        32 -> tile32Dark
        64 -> tile64Dark
        128 -> tile128Dark
        256 -> tile256Dark
        512 -> tile512Dark
        1024 -> tile1024Dark
        2048 -> tile2048Dark
        else -> tileSuperDark
    }

    fun getFontSize(value: Int): Float = when {
        value < 100 -> 36f
        value < 1000 -> 28f
        value < 10000 -> 22f
        else -> 18f
    }
}

// Button colors
val RestartButtonLight = Color(0xFF8F7A66)
val RestartButtonDark = Color(0xFF5C5C8A)

// Accent
val AccentGold = Color(0xFFEDC22E)
val AccentOrange = Color(0xFFF59563)
