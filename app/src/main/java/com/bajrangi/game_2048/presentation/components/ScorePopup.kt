package com.bajrangi.game_2048.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bajrangi.game_2048.R
import com.bajrangi.game_2048.ui.theme.AccentGold
import com.bajrangi.game_2048.ui.theme.AccentGoldBright
import com.bajrangi.game_2048.ui.theme.LocalGameColors
import kotlinx.coroutines.launch

@Composable
fun ScorePopup(
    scoreGained: Int,
    moveCount: Int,
    modifier: Modifier = Modifier
) {
    if (scoreGained <= 0) return

    val isDark = LocalGameColors.current.isDark
    val popupColor = if (isDark) AccentGoldBright else AccentGold

    val alpha = remember(moveCount) { Animatable(1f) }
    val offsetY = remember(moveCount) { Animatable(-8f) }

    LaunchedEffect(moveCount) {
        launch {
            offsetY.animateTo(
                targetValue = -44f,
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(700, delayMillis = 200)
            )
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.score_gained, scoreGained),
            color = popupColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.3).sp,
            modifier = Modifier
                .offset(y = offsetY.value.dp)
                .graphicsLayer { this.alpha = alpha.value }
        )
    }
}
