package com.example.game_2048.presentation.screen

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlin.math.abs
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
            .background(gameColors.background)
    ) {
        // ── Layer 0: Ambient gradient blobs ──────────────────────
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
        Spacer(modifier = Modifier.height(16.dp))

        // ── Header ──────────────────────────────────────────────
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

        // ── Board with swipe gestures ───────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                                    if (viewModel.gameState.value.score > prevScore) {
                                        // Merge happened — medium haptic
                                        view.performHapticFeedback(
                                            HapticFeedbackConstants.VIRTUAL_KEY
                                        )
                                    } else {
                                        // Moved without merge — light tick
                                        view.performHapticFeedback(
                                            HapticFeedbackConstants.CLOCK_TICK
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

        Spacer(modifier = Modifier.height(28.dp))

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
            Spacer(modifier = Modifier.height(12.dp))
        }
        } // Column
    } // Box
}

// ─── Ambient Background ──────────────────────────────────────────────

@Composable
private fun AmbientBackground() {
    val gameColors = LocalGameColors.current
    val glowAlpha = if (gameColors.isDark) 0.35f else 0.45f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val radius = min(w, h) * 0.55f

        // Top-right warm/cool blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    gameColors.glowTopRight.copy(alpha = glowAlpha),
                    Color.Transparent
                ),
                center = Offset(w * 0.85f, h * 0.08f),
                radius = radius
            ),
            radius = radius,
            center = Offset(w * 0.85f, h * 0.08f)
        )

        // Bottom-left warm/cool blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    gameColors.glowBottomLeft.copy(alpha = glowAlpha * 0.7f),
                    Color.Transparent
                ),
                center = Offset(w * 0.1f, h * 0.85f),
                radius = radius * 0.9f
            ),
            radius = radius * 0.9f,
            center = Offset(w * 0.1f, h * 0.85f)
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

    Spacer(modifier = Modifier.height(14.dp))

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

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pressScale.value
                scaleY = pressScale.value
            }
            .clip(RoundedCornerShape(8.dp))
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
