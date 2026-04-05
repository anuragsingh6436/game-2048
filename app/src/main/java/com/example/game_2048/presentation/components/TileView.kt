package com.example.game_2048.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game_2048.domain.model.Tile
import com.example.game_2048.ui.theme.LocalGameColors
import com.example.game_2048.ui.theme.TileColors
import kotlinx.coroutines.delay

private val TileShape = RoundedCornerShape(14.dp)

@Composable
fun TileView(
    tile: Tile,
    cellSize: Dp,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val isDark = gameColors.isDark
    val bgColor = TileColors.getBackgroundColor(tile.value, isDark)
    val gradientStart = TileColors.getGradientStart(tile.value, isDark)
    val textColor = TileColors.getTextColor(tile.value, isDark)
    val fontSize = TileColors.getFontSize(tile.value)
    val hasGlow = TileColors.hasGlow(tile.value)
    val glowAlpha = TileColors.getGlowAlpha(tile.value, isDark)

    // Appear: scale from 0 → 1
    val appearScale = remember(tile.id) {
        Animatable(if (tile.isNew) 0f else 1f)
    }

    // Merge pop: scale from 1.2 → 1 (starts after slide finishes)
    val mergeScale = remember(tile.id) {
        Animatable(1f)
    }

    LaunchedEffect(tile.id) {
        if (tile.isNew) {
            delay(80)
            appearScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.6f,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    LaunchedEffect(tile.id) {
        if (tile.mergedFrom) {
            delay(100)
            mergeScale.snapTo(1.2f)
            mergeScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.5f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    // Value-proportional elevation
    val elevation = when {
        tile.value >= 2048 -> 12.dp
        tile.value >= 512 -> 8.dp
        tile.value >= 64 -> 5.dp
        tile.value >= 8 -> 3.dp
        else -> 1.dp
    }

    // Tile gradient: lighter top-left → saturated bottom-right
    val tileGradient = Brush.linearGradient(
        colors = listOf(gradientStart, bgColor),
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // Top frost highlight
    val frostBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = if (isDark) 0.07f else 0.18f),
            Color.Transparent
        ),
        startY = 0f,
        endY = 60f
    )

    Box(
        modifier = modifier
            .size(cellSize)
            .graphicsLayer {
                val s = appearScale.value * mergeScale.value
                scaleX = s
                scaleY = s
                clip = false
            }
            .drawBehind {
                // Glow halo for high-value tiles (128+)
                if (hasGlow) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                bgColor.copy(alpha = glowAlpha),
                                bgColor.copy(alpha = glowAlpha * 0.3f),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension * 0.72f
                    )
                }
            }
            .shadow(
                elevation = elevation,
                shape = TileShape,
                ambientColor = bgColor.copy(alpha = 0.3f),
                spotColor = bgColor.copy(alpha = 0.25f)
            )
            .clip(TileShape)
            .background(tileGradient),
        contentAlignment = Alignment.Center
    ) {
        // Frost highlight overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(frostBrush)
        )

        Text(
            text = tile.value.toString(),
            color = textColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.5).sp,
            maxLines = 1
        )
    }
}
