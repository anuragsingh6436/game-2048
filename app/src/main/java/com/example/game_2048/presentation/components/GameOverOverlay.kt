package com.example.game_2048.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game_2048.R
import com.example.game_2048.ui.theme.AccentGold
import com.example.game_2048.ui.theme.AccentWin
import com.example.game_2048.ui.theme.LocalGameColors
import com.example.game_2048.ui.theme.OverlayButtonText

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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(AccentGold)
                                .clickable(onClick = onKeepPlaying)
                                .padding(horizontal = 28.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.keep_playing),
                                color = OverlayButtonText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.3.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(gameColors.restartButton)
                                .clickable(onClick = onRestart)
                                .padding(horizontal = 28.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.restart),
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(gameColors.restartButton)
                            .clickable(onClick = onRestart)
                            .padding(horizontal = 36.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.try_again),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }
        }
    }
}
