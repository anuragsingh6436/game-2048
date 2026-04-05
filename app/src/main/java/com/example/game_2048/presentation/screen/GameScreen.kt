package com.example.game_2048.presentation.screen

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game_2048.R
import com.example.game_2048.domain.model.Direction
import com.example.game_2048.presentation.GameViewModel
import com.example.game_2048.presentation.components.GameBoard
import com.example.game_2048.presentation.components.GameOverOverlay
import com.example.game_2048.presentation.components.ScoreCard
import com.example.game_2048.presentation.components.ScorePopup
import com.example.game_2048.ui.theme.LocalGameColors
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameColors = LocalGameColors.current
    val view = LocalView.current

    var totalDragX by remember { mutableFloatStateOf(0f) }
    var totalDragY by remember { mutableFloatStateOf(0f) }

    // Board shake on invalid swipe
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Haptic on game over
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    // Haptic on win
    LaunchedEffect(gameState.hasWon) {
        if (gameState.hasWon) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(gameColors.background, gameColors.backgroundGradientEnd)
                )
            )
    ) {
        // ── Layer 0: Ambient blobs + vignette ───────────────────
        AmbientBackground()

        // ── Layer 1: Content ─────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Header(
                score = gameState.score,
                bestScore = gameState.bestScore,
                scoreGained = gameState.scoreGained,
                moveCount = gameState.moveCount,
                canUndo = viewModel.canUndo(),
                showUndoButton = viewModel.featureFlags.isUndoEnabled(),
                onRestart = {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                    viewModel.startNewGame()
                },
                onUndo = {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    viewModel.undoMove()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationX = shakeOffset.value
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                totalDragX = 0f
                                totalDragY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                totalDragX += dragAmount.x
                                totalDragY += dragAmount.y
                            },
                            onDragEnd = {
                                val threshold = 40f
                                val absX = abs(totalDragX)
                                val absY = abs(totalDragY)

                                if (absX > threshold || absY > threshold) {
                                    val direction = if (absX > absY) {
                                        if (totalDragX > 0) Direction.RIGHT else Direction.LEFT
                                    } else {
                                        if (totalDragY > 0) Direction.DOWN else Direction.UP
                                    }

                                    val prevScore = gameState.score
                                    val prevMoveCount = gameState.moveCount
                                    viewModel.onSwipe(direction)

                                    val moved =
                                        viewModel.gameState.value.moveCount > prevMoveCount
                                    if (moved) {
                                        // Sound click on valid move
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        if (viewModel.gameState.value.score > prevScore) {
                                            view.performHapticFeedback(
                                                HapticFeedbackConstants.VIRTUAL_KEY
                                            )
                                        } else {
                                            view.performHapticFeedback(
                                                HapticFeedbackConstants.CLOCK_TICK
                                            )
                                        }
                                    } else {
                                        // Board shake on invalid swipe
                                        scope.launch {
                                            shakeOffset.snapTo(12f)
                                            shakeOffset.animateTo(
                                                0f,
                                                spring(
                                                    dampingRatio = 0.3f,
                                                    stiffness = Spring.StiffnessHigh
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
            ) {
                GameBoard(state = gameState)

                GameOverOverlay(
                    isGameOver = gameState.isGameOver,
                    showWinOverlay = gameState.showWinOverlay,
                    score = gameState.score,
                    onRestart = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        viewModel.startNewGame()
                    },
                    onKeepPlaying = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        viewModel.dismissWin()
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.game_hint),
                color = gameColors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.1.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            if (gameState.moveCount > 0) {
                Text(
                    text = stringResource(R.string.moves_count, gameState.moveCount),
                    color = gameColors.textSecondary.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ─── Background System ──────────────────────────────────────────────
//
// 3 layers drawn in a single Canvas pass:
//   1. Ambient blobs — color warmth / depth
//   2. Center focus spot — draws eye toward the board
//   3. Edge vignette — subtle darkening at periphery
//
// All radial gradients, zero allocation, renders once per theme change.

@Composable
private fun AmbientBackground() {
    val gameColors = LocalGameColors.current
    val isDark = gameColors.isDark
    val blobAlpha = if (isDark) 0.40f else 0.50f
    val vignetteAlpha = if (isDark) 0.35f else 0.06f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val short = min(w, h)

        // ── 1. Ambient blobs ────────────────────────────────────
        // Top-right: warm wheat (light) / deep plum (dark)
        val blobRadius = short * 0.6f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    gameColors.glowTopRight.copy(alpha = blobAlpha),
                    gameColors.glowTopRight.copy(alpha = blobAlpha * 0.3f),
                    Color.Transparent
                ),
                center = Offset(w * 0.88f, h * 0.06f),
                radius = blobRadius
            ),
            radius = blobRadius,
            center = Offset(w * 0.88f, h * 0.06f)
        )

        // Bottom-left: warm sand (light) / slate blue (dark)
        val blobRadius2 = short * 0.5f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    gameColors.glowBottomLeft.copy(alpha = blobAlpha * 0.6f),
                    gameColors.glowBottomLeft.copy(alpha = blobAlpha * 0.15f),
                    Color.Transparent
                ),
                center = Offset(w * 0.08f, h * 0.88f),
                radius = blobRadius2
            ),
            radius = blobRadius2,
            center = Offset(w * 0.08f, h * 0.88f)
        )

        // ── 2. Center focus spot ────────────────────────────────
        // Soft glow at ~42% vertical (where the board sits)
        val focusRadius = short * 0.45f
        val focusCenter = Offset(w * 0.5f, h * 0.42f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    gameColors.glowCenter.copy(alpha = blobAlpha * 0.5f),
                    Color.Transparent
                ),
                center = focusCenter,
                radius = focusRadius
            ),
            radius = focusRadius,
            center = focusCenter
        )

        // ── 3. Edge vignette ────────────────────────────────────
        // Radial: transparent center → darkened edges
        val vignetteRadius = max(w, h) * 0.75f
        val vignetteCenter = Offset(w * 0.5f, h * 0.4f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Transparent,
                    gameColors.vignette.copy(alpha = vignetteAlpha)
                ),
                center = vignetteCenter,
                radius = vignetteRadius
            ),
            radius = vignetteRadius * 1.5f,
            center = vignetteCenter
        )
    }
}

// ─── Header ──────────────────────────────────────────────────────────

@Composable
private fun Header(
    score: Int,
    bestScore: Int,
    scoreGained: Int,
    moveCount: Int,
    canUndo: Boolean,
    showUndoButton: Boolean,
    onRestart: () -> Unit,
    onUndo: () -> Unit
) {
    val gameColors = LocalGameColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = stringResource(R.string.game_title),
            color = gameColors.textPrimary,
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-2).sp,
            lineHeight = 52.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box {
                ScoreCard(label = stringResource(R.string.label_score), score = score)
                ScorePopup(
                    scoreGained = scoreGained,
                    moveCount = moveCount,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
            ScoreCard(label = stringResource(R.string.label_best), score = bestScore)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.swipe_to_play),
            color = gameColors.textSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (showUndoButton) {
                PressableButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    backgroundColor = gameColors.restartButton,
                    contentPadding = PressableButtonPadding.Icon
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = stringResource(R.string.cd_undo),
                        tint = Color.White.copy(alpha = if (canUndo) 1f else 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            PressableButton(
                onClick = onRestart,
                backgroundColor = gameColors.restartButton
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = stringResource(R.string.new_game),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ─── Pressable Button ────────────────────────────────────────────────

private enum class PressableButtonPadding {
    Default, Icon
}

@Composable
private fun PressableButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    enabled: Boolean = true,
    contentPadding: PressableButtonPadding = PressableButtonPadding.Default,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            pressScale.animateTo(
                0.92f,
                spring(stiffness = Spring.StiffnessMediumLow)
            )
        } else {
            pressScale.animateTo(
                1f,
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    val padding = when (contentPadding) {
        PressableButtonPadding.Default -> Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        PressableButtonPadding.Icon -> Modifier.padding(10.dp)
    }

    val buttonShape = RoundedCornerShape(10.dp)

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pressScale.value
                scaleY = pressScale.value
            }
            .shadow(
                elevation = 2.dp,
                shape = buttonShape,
                ambientColor = backgroundColor.copy(alpha = 0.2f),
                spotColor = backgroundColor.copy(alpha = 0.15f)
            )
            .clip(buttonShape)
            .background(
                if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.35f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .then(padding),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
