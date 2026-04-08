package com.bajrangi.game_2048.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bajrangi.game_2048.ui.theme.AuroraGlassBorderDark
import com.bajrangi.game_2048.ui.theme.AuroraGlassBorderLight
import com.bajrangi.game_2048.ui.theme.AuroraGlassFillDark
import com.bajrangi.game_2048.ui.theme.AuroraGlassFillLight
import com.bajrangi.game_2048.ui.theme.AuroraGlassInnerDark
import com.bajrangi.game_2048.ui.theme.AuroraGlassInnerLight

/**
 * Premium glass panel: tinted fill + frosted top edge + subtle border.
 * Ported from XOMaster to keep both games visually consistent.
 */
@Composable
fun GlassSurface(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 4.dp,
    fillAlpha: Float = 1f,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val fill = (if (isDark) AuroraGlassFillDark else AuroraGlassFillLight).let {
        if (fillAlpha < 1f) it.copy(alpha = it.alpha * fillAlpha) else it
    }
    val borderColor = if (isDark) AuroraGlassBorderDark else AuroraGlassBorderLight
    val innerGlow = if (isDark) AuroraGlassInnerDark else AuroraGlassInnerLight

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = if (isDark) 0.4f else 0.08f),
                spotColor = Color.Black.copy(alpha = if (isDark) 0.3f else 0.06f)
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(fill, fill.copy(alpha = fill.alpha * 0.85f))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = borderColor.alpha * 0.3f)
                    )
                ),
                shape = shape
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(innerGlow, Color.Transparent),
                    startY = 0f,
                    endY = 80f
                )
            ),
        content = content
    )
}
