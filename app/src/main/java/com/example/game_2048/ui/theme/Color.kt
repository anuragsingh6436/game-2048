package com.example.game_2048.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Light Palette ────────────────────────────────────────────────
val LightBackground = Color(0xFFFBF8F1)       // warm off-white
val LightSurface = Color(0xFFFFFFFF)
val LightEmptyCell = Color(0xFFD6CDC4)         // muted warm grey
val LightTextPrimary = Color(0xFF6E6356)       // soft charcoal-brown
val LightTextSecondary = Color(0xFFA69C91)     // muted label
val LightTextOnTile = Color(0xFFF9F6F2)        // white-ish for dark tiles

// ─── Dark Palette ─────────────────────────────────────────────────
val DarkBackground = Color(0xFF121218)         // true dark, warm undertone
val DarkSurface = Color(0xFF1C1C24)
val DarkEmptyCell = Color(0xFF2C2C38)          // subtle separator
val DarkTextPrimary = Color(0xFFF0EDE8)        // warm white
val DarkTextSecondary = Color(0xFF8A8690)       // muted label
val DarkTextOnTile = Color(0xFFF9F6F2)

// ─── Board ────────────────────────────────────────────────────────
val BoardBackgroundLight = Color(0xFFC2B5A7)   // warm taupe
val BoardBackgroundDark = Color(0xFF252530)     // charcoal slate

// ─── Score Cards ──────────────────────────────────────────────────
val ScoreCardLight = Color(0xFFC2B5A7)
val ScoreCardDark = Color(0xFF2A2A36)

// ─── Buttons ──────────────────────────────────────────────────────
val RestartButtonLight = Color(0xFF8F7A66)
val RestartButtonDark = Color(0xFF4A4A5E)

// ─── Accents ──────────────────────────────────────────────────────
val AccentGold = Color(0xFFF2C94C)
val AccentOrange = Color(0xFFED8A5A)
val AccentWin = Color(0xFFF5D96E)              // celebratory gold

// ─── Tile Colors ──────────────────────────────────────────────────
object TileColors {

    // Light mode — classic warm progression
    private val tile2L = Color(0xFFEEE4DA)
    private val tile4L = Color(0xFFECDFC6)
    private val tile8L = Color(0xFFF4B17A)
    private val tile16L = Color(0xFFF59768)
    private val tile32L = Color(0xFFF67E61)
    private val tile64L = Color(0xFFF65D3B)
    private val tile128L = Color(0xFFEDCF72)
    private val tile256L = Color(0xFFEDCC61)
    private val tile512L = Color(0xFFEDC850)
    private val tile1024L = Color(0xFFEDC53F)
    private val tile2048L = Color(0xFFEDC22E)
    private val tileSuperL = Color(0xFF3C3A33)

    // Dark mode — rich jewel-tone progression
    private val tile2D = Color(0xFF38384C)
    private val tile4D = Color(0xFF434360)
    private val tile8D = Color(0xFFC47A3F)
    private val tile16D = Color(0xFFD47240)
    private val tile32D = Color(0xFFD65E3E)
    private val tile64D = Color(0xFFDA4430)
    private val tile128D = Color(0xFFDCB644)
    private val tile256D = Color(0xFFDCB034)
    private val tile512D = Color(0xFFDCAC26)
    private val tile1024D = Color(0xFFDCA818)
    private val tile2048D = Color(0xFFDCA40A)
    private val tileSuperD = Color(0xFFF0DFA8)

    fun getBackgroundColor(value: Int, isDark: Boolean): Color =
        if (isDark) getDarkBg(value) else getLightBg(value)

    fun getTextColor(value: Int, isDark: Boolean): Color = when {
        isDark && value <= 4 -> Color(0xFFB8B4BE)          // muted on low-value dark tiles
        isDark -> Color(0xFFF9F6F2)
        value <= 4 -> Color(0xFF776E65)                     // classic brown
        else -> Color(0xFFF9F6F2)
    }

    private fun getLightBg(value: Int): Color = when (value) {
        2 -> tile2L;  4 -> tile4L;  8 -> tile8L
        16 -> tile16L; 32 -> tile32L; 64 -> tile64L
        128 -> tile128L; 256 -> tile256L; 512 -> tile512L
        1024 -> tile1024L; 2048 -> tile2048L
        else -> tileSuperL
    }

    private fun getDarkBg(value: Int): Color = when (value) {
        2 -> tile2D;  4 -> tile4D;  8 -> tile8D
        16 -> tile16D; 32 -> tile32D; 64 -> tile64D
        128 -> tile128D; 256 -> tile256D; 512 -> tile512D
        1024 -> tile1024D; 2048 -> tile2048D
        else -> tileSuperD
    }

    fun getFontSize(value: Int): Float = when {
        value < 10 -> 34f
        value < 100 -> 30f
        value < 1000 -> 24f
        value < 10000 -> 19f
        else -> 15f
    }
}
