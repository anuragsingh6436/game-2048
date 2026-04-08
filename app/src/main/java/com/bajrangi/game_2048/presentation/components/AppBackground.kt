package com.bajrangi.game_2048.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.bajrangi.game_2048.ui.theme.AuroraOrbDark
import com.bajrangi.game_2048.ui.theme.AuroraOrbLight
import com.bajrangi.game_2048.ui.theme.AuroraVignetteCoreDark
import com.bajrangi.game_2048.ui.theme.AuroraVignetteCoreLight
import com.bajrangi.game_2048.ui.theme.AuroraVignetteEdgeDark
import com.bajrangi.game_2048.ui.theme.AuroraVignetteEdgeLight
import com.bajrangi.game_2048.ui.theme.AuroraVignetteMidDark
import com.bajrangi.game_2048.ui.theme.AuroraVignetteMidLight
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Aurora-style backdrop ported from XOMaster:
 *   - Radial vignette core → mid → edge (feels game-like vs flat gradient)
 *   - A single slow-drifting ice-blue orb for atmosphere
 */
@Composable
fun AppBackground(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val drift = rememberInfiniteTransition(label = "orbDrift")
    val driftPhase by drift.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "driftPhase"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Layer 1: Radial vignette
            val vignetteCenter = Offset(w * 0.5f, h * 0.42f)
            val vignetteRadius = max(w, h) * 0.95f
            drawRect(
                brush = Brush.radialGradient(
                    colors = if (isDark) listOf(
                        AuroraVignetteCoreDark,
                        AuroraVignetteMidDark,
                        AuroraVignetteEdgeDark
                    ) else listOf(
                        AuroraVignetteCoreLight,
                        AuroraVignetteMidLight,
                        AuroraVignetteEdgeLight
                    ),
                    center = vignetteCenter,
                    radius = vignetteRadius
                )
            )

            // Layer 2: Drifting ice-blue orb
            val orbAlpha = if (isDark) 0.18f else 0.14f
            val orbX = w * 0.78f + sin(driftPhase) * w * 0.05f
            val orbY = h * 0.18f + cos(driftPhase * 0.7f) * h * 0.03f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        (if (isDark) AuroraOrbDark else AuroraOrbLight).copy(alpha = orbAlpha),
                        Color.Transparent
                    ),
                    center = Offset(orbX, orbY),
                    radius = w * 0.55f
                ),
                center = Offset(orbX, orbY),
                radius = w * 0.55f
            )
        }
        content()
    }
}
