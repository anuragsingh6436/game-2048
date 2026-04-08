package com.bajrangi.game_2048.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bajrangi.game_2048.R
import com.bajrangi.game_2048.ui.theme.AccentGold
import com.bajrangi.game_2048.ui.theme.AccentWin
import com.bajrangi.game_2048.ui.theme.LocalGameColors
import com.bajrangi.game_2048.ui.theme.OverlayButtonText

@Composable
fun GameOverOverlay(
    isGameOver: Boolean,
    showWinOverlay: Boolean,
    score: Int,
    onRestart: () -> Unit,
    onKeepPlaying: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    AnimatedVisibility(
        visible = isGameOver || showWinOverlay,
        enter = fadeIn(animationSpec = tween(400)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
        exit = fadeOut(animationSpec = tween(200)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gameColors.overlayScrim)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (showWinOverlay) stringResource(R.string.you_win)
                           else stringResource(R.string.game_over),
                    color = if (showWinOverlay) AccentWin else gameColors.textPrimary,
                    fontSize = if (showWinOverlay) 44.sp else 38.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.score_label, score),
                    color = gameColors.textSecondary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(28.dp))

                if (showWinOverlay) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        OverlayPillButton(
                            onClick = onKeepPlaying,
                            backgroundColor = AccentGold,
                            textColor = OverlayButtonText,
                            text = stringResource(R.string.keep_playing)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        OverlayPillButton(
                            onClick = onRestart,
                            backgroundColor = gameColors.restartButton,
                            textColor = Color.White,
                            text = stringResource(R.string.restart)
                        )
                    }
                } else {
                    OverlayPillButton(
                        onClick = onRestart,
                        backgroundColor = gameColors.restartButton,
                        textColor = Color.White,
                        text = stringResource(R.string.try_again)
                    )
                }
            }
        }
    }
}

@Composable
private fun OverlayPillButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    text: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        pressScale.animateTo(
            if (isPressed) 0.93f else 1f,
            spring(
                dampingRatio = if (isPressed) Spring.DampingRatioNoBouncy
                               else Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pressScale.value
                scaleY = pressScale.value
            }
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 28.dp, vertical = 14.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp
        )
    }
}
