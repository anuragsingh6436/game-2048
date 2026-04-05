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

private val TileShape = RoundedCornerShape(10.dp)

@Composable
fun TileView(
    tile: Tile,
    cellSize: Dp,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val bgColor = TileColors.getBackgroundColor(tile.value, gameColors.isDark)
    val textColor = TileColors.getTextColor(tile.value, gameColors.isDark)
    val fontSize = TileColors.getFontSize(tile.value)

    // Appear animation — gentle scale from 0
    val appearScale = remember(tile.id) {
        Animatable(if (tile.isNew) 0f else 1f)
    }

    // Merge animation — subtle pop from 1.15
    val mergeScale = remember(tile.id) {
        Animatable(if (tile.mergedFrom) 1.15f else 1f)
    }

    LaunchedEffect(tile.id) {
        if (tile.isNew) {
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
            mergeScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.55f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    // Value-proportional elevation: higher tiles feel more prominent
    val elevation = when {
        tile.value >= 2048 -> 10.dp
        tile.value >= 512 -> 6.dp
        tile.value >= 64 -> 4.dp
        tile.value >= 8 -> 2.dp
        else -> 0.dp
    }

    // Subtle top highlight for depth
    val highlightBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = if (gameColors.isDark) 0.06f else 0.15f),
            Color.Transparent
        ),
        startY = 0f,
        endY = 80f
    )

    Box(
        modifier = modifier
            .size(cellSize)
            .graphicsLayer {
                val combinedScale = appearScale.value * mergeScale.value
                scaleX = combinedScale
                scaleY = combinedScale
            }
            .shadow(
                elevation = elevation,
                shape = TileShape,
                ambientColor = bgColor.copy(alpha = 0.25f),
                spotColor = bgColor.copy(alpha = 0.20f)
            )
            .clip(TileShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        // Inner highlight layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(highlightBrush)
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
