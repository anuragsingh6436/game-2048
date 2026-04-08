package com.bajrangi.game_2048.ui.theme

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
val DarkEmptyCell = Color(0xFF26262F)          // darker — clear separation from tiles
val DarkTextPrimary = Color(0xFFF0EDE8)        // warm white
val DarkTextSecondary = Color(0xFF8A8690)       // muted label
val DarkTextOnTile = Color(0xFFF9F6F2)

// ─── Board ────────────────────────────────────────────────────────
val BoardBackgroundLight = Color(0xFFC2B5A7)   // warm taupe
val BoardBackgroundDark = Color(0xFF252530)     // charcoal slate

// ─── Board Glass ──────────────────────────────────────────────────
val BoardGlassLight = Color(0xE0C2B5A7)        // taupe at 88% opacity
val BoardGlassDark = Color(0xBF252530)          // charcoal at 75% opacity
val BoardBorderLight = Color(0x1FFFFFFF)        // white at 12%
val BoardBorderDark = Color(0x14FFFFFF)         // white at 8%
val BoardFrostLight = Color(0x0DFFFFFF)         // white at 5% (top highlight)
val BoardFrostDark = Color(0x08FFFFFF)          // white at 3%

// ─── Score Cards ──────────────────────────────────────────────────
val ScoreCardLight = Color(0xFFC2B5A7)
val ScoreCardDark = Color(0xFF2A2A36)
val ScoreCardGlassLight = Color(0xDDC2B5A7)    // taupe at 87%
val ScoreCardGlassDark = Color(0xCC2A2A36)     // charcoal at 80%
val ScoreCardBorderLight = Color(0x14FFFFFF)    // white at 8%
val ScoreCardBorderDark = Color(0x0FFFFFFF)     // white at 6%

// ─── Score Labels & Values ────────────────────────────────────────
val ScoreLabelLight = Color(0xA6EEE4DA)        // warm cream at 65%
val ScoreLabelDark = Color(0xFF9A96A0)         // muted lavender
val ScoreValueLight = Color(0xFFFFFFFF)        // white
val ScoreValueDark = Color(0xFFF0EDE8)         // warm white

// ─── Buttons ──────────────────────────────────────────────────────
val RestartButtonLight = Color(0xFF8F7A66)
val RestartButtonDark = Color(0xFF484854)       // neutral charcoal (was blue-tinted)

// ─── Overlays ─────────────────────────────────────────────────────
val OverlayScrimLight = Color(0xC7FFFFFF)       // white at 78%
val OverlayScrimDark = Color(0xD10A0A0E)        // near-black at 82%
val OverlayButtonText = Color(0xFF1C1A18)       // warm near-black for gold buttons

// ─── Accents ──────────────────────────────────────────────────────
val AccentGold = Color(0xFFF2C94C)
val AccentGoldBright = Color(0xFFF5D76E)       // brighter variant for dark backgrounds
val AccentOrange = Color(0xFFED8A5A)
val AccentWin = Color(0xFFF5D96E)              // celebratory gold

// ─── Background gradient endpoints ───────────────────────────────
val LightBgGradientEnd = Color(0xFFF3EDE0)     // slightly deeper warm cream
val DarkBgGradientStart = Color(0xFF161620)    // charcoal slate top
val DarkBgGradientEnd = Color(0xFF0C0C10)      // near-black bottom

// ─── Ambient Glow (background depth) ─────────────────────────────
// Light mode: warm peach/gold blobs
val GlowTopRightLight = Color(0xFFF5DEB3)     // soft wheat
val GlowBottomLeftLight = Color(0xFFE8C9A0)   // warm sand
val GlowCenterLight = Color(0xFFEDD9B8)       // warm focus spot
val GlowBoardLight = Color(0xFFC8B8A6)        // board-matching halo

// Dark mode: cool muted blobs
val GlowTopRightDark = Color(0xFF2A2540)       // deep plum
val GlowBottomLeftDark = Color(0xFF1E2838)     // slate blue
val GlowCenterDark = Color(0xFF1C1A28)        // subtle center focus
val GlowBoardDark = Color(0xFF1E1E2C)         // board-matching halo

// ─── Vignette ────────────────────────────────────────────────────
val VignetteLight = Color(0xFF8A7E70)          // warm shadow
val VignetteDark = Color(0xFF000000)           // pure black edge

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

    // Dark mode — lifted 2/4 for visibility, rich jewel-tone 8+
    private val tile2D = Color(0xFF3E3E56)     // lightened from #38384C
    private val tile4D = Color(0xFF4C4C6A)     // lightened from #434360
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

    fun getGradientStart(value: Int, isDark: Boolean): Color {
        val base = getBackgroundColor(value, isDark)
        val lift = if (value <= 4) 0.08f else 0.15f
        return Color(
            red = base.red + (1f - base.red) * lift,
            green = base.green + (1f - base.green) * lift,
            blue = base.blue + (1f - base.blue) * lift,
            alpha = base.alpha
        )
    }

    fun hasGlow(value: Int): Boolean = value >= 128

    fun getGlowAlpha(value: Int, isDark: Boolean): Float = when {
        value >= 2048 -> if (isDark) 0.40f else 0.30f
        value >= 512 -> if (isDark) 0.30f else 0.22f
        value >= 128 -> if (isDark) 0.22f else 0.15f
        else -> 0f
    }

    fun getTextColor(value: Int, isDark: Boolean): Color = when {
        isDark && value <= 4 -> Color(0xFFC8C4CE)  // brighter for readability
        isDark -> Color(0xFFF9F6F2)
        value <= 4 -> Color(0xFF776E65)             // classic brown
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
