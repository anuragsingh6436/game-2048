package com.example.game_2048.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game_2048.domain.model.Tile
import com.example.game_2048.ui.theme.LocalGameColors
import com.example.game_2048.ui.theme.TileColors

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

    // Scale animation for new tiles
    val scaleAnim = remember(tile.id) { Animatable(if (tile.isNew) 0f else 1f) }

    // Pop animation for merged tiles
    val mergeScale = remember(tile.id) { Animatable(if (tile.mergedFrom) 1.3f else 1f) }

    LaunchedEffect(tile.id) {
        if (tile.isNew) {
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    LaunchedEffect(tile.id) {
        if (tile.mergedFrom) {
            mergeScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    val elevation = when {
        tile.value >= 2048 -> 12.dp
        tile.value >= 128 -> 8.dp
        tile.value >= 8 -> 4.dp
        else -> 2.dp
    }

    Box(
        modifier = modifier
            .size(cellSize)
            .graphicsLayer {
                scaleX = scaleAnim.value * mergeScale.value
                scaleY = scaleAnim.value * mergeScale.value
            }
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(12.dp),
                ambientColor = bgColor.copy(alpha = 0.3f),
                spotColor = bgColor.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tile.value.toString(),
            color = textColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}
