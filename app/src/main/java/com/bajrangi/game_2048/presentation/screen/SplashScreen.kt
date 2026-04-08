package com.bajrangi.game_2048.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bajrangi.game_2048.R
import com.bajrangi.game_2048.presentation.components.AppBackground
import com.bajrangi.game_2048.presentation.components.GlassSurface
import com.bajrangi.game_2048.ui.theme.AuroraAccentDark
import com.bajrangi.game_2048.ui.theme.AuroraAccentLight
import com.bajrangi.game_2048.ui.theme.LocalGameColors
import com.bajrangi.game_2048.ui.theme.TileColors
import kotlinx.coroutines.delay

// XOMaster Play-pill palette — ice blue → rose gold
private val IceBlue  = Color(0xFF7FD8FF)
private val RoseGold = Color(0xFFE8B4A0)

/**
 * Elegant title intro for Numra.
 *
 * Layered entrance: hero wordmark plaque → tagline → cascade of preview
 * tiles → XOMaster-style Play pill. Tapping Play opens a board-size
 * picker (4×4 / 5×5 / 6×6), SudokuPro-style, which hands the chosen
 * size back to the host.
 */
@Composable
fun SplashScreen(
    onStart: (Int) -> Unit,
    isSoundEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val accent = if (gameColors.isDark) AuroraAccentDark else AuroraAccentLight
    var showSizePicker by rememberSaveable { mutableStateOf(false) }
    val sfx = com.bajrangi.game_2048.presentation.util.rememberSoundFx(isSoundEnabled)

    // ── Entrance animations ──
    val plaqueAlpha = remember { Animatable(0f) }
    val plaqueScale = remember { Animatable(0.88f) }
    val taglineAlpha = remember { Animatable(0f) }
    val playAlpha = remember { Animatable(0f) }
    val playScale = remember { Animatable(0.9f) }
    val previewValues = listOf(2, 4, 8, 16)
    val previewProgress = previewValues.map { remember { Animatable(0f) } }

    LaunchedEffect(Unit) {
        plaqueAlpha.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        plaqueScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
        )
    }
    LaunchedEffect(Unit) {
        delay(320)
        taglineAlpha.animateTo(1f, tween(500))
    }
    LaunchedEffect(Unit) {
        delay(480)
        previewProgress.forEachIndexed { i, anim ->
            delay(90L * i)
            anim.animateTo(
                1f,
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }
    LaunchedEffect(Unit) {
        delay(980)
        playAlpha.animateTo(1f, tween(450))
    }
    LaunchedEffect(Unit) {
        delay(980)
        playScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        )
    }

    AppBackground(
        isDark = gameColors.isDark,
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ── Hero wordmark plaque ──
                GlassSurface(
                    isDark = gameColors.isDark,
                    cornerRadius = 28.dp,
                    elevation = 16.dp,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = plaqueAlpha.value
                            scaleX = plaqueScale.value
                            scaleY = plaqueScale.value
                        }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 56.dp, vertical = 28.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.game_title),
                            color = gameColors.textPrimary,
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-2).sp,
                            lineHeight = 68.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(width = 64.dp, height = 3.dp)
                                .clip(RoundedCornerShape(50))
                                .background(accent.copy(alpha = 0.75f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // ── Tagline ──
                Text(
                    text = stringResource(R.string.splash_tagline),
                    color = gameColors.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp,
                    modifier = Modifier.graphicsLayer { alpha = taglineAlpha.value }
                )

                Spacer(modifier = Modifier.height(40.dp))

                // ── Mini preview tile row ──
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    previewValues.forEachIndexed { index, value ->
                        PreviewTile(
                            value = value,
                            progress = previewProgress[index].value,
                            isDark = gameColors.isDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(56.dp))

                // ── XOMaster-style Play pill ──
                PlayPill(
                    onClick = {
                        sfx.click()
                        showSizePicker = true
                    },
                    modifier = Modifier.graphicsLayer {
                        alpha = playAlpha.value
                        scaleX = playScale.value
                        scaleY = playScale.value
                    }
                )
            }
        }
    }

    // ── Board-size picker (SudokuPro-style modal dialog) ──
    AnimatedVisibility(
        visible = showSizePicker,
        enter = fadeIn(tween(180)),
        exit = fadeOut(tween(160))
    ) {
        BoardSizePicker(
            onDismiss = { showSizePicker = false },
            onPick = { size ->
                sfx.click()
                showSizePicker = false
                onStart(size)
            }
        )
    }
}

@Composable
private fun PlayPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(IceBlue.copy(alpha = 0.9f), RoseGold.copy(alpha = 0.9f))
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 40.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            text = stringResource(R.string.splash_play),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp
        )
    }
}

@Composable
private fun BoardSizePicker(
    onDismiss: () -> Unit,
    onPick: (Int) -> Unit
) {
    val gameColors = LocalGameColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        // Stop click propagation on the card itself.
        GlassSurface(
            isDark = gameColors.isDark,
            cornerRadius = 28.dp,
            elevation = 18.dp,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clickable(
                    enabled = true,
                    onClick = {}, // absorb clicks so dismiss only fires on scrim
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.size_picker_title),
                    color = gameColors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp
                )
                Spacer(Modifier.height(20.dp))
                SizeOptionRow(
                    size = 4,
                    label = stringResource(R.string.size_4x4),
                    subtitle = stringResource(R.string.size_4x4_subtitle),
                    onClick = { onPick(4) }
                )
                Spacer(Modifier.height(12.dp))
                SizeOptionRow(
                    size = 5,
                    label = stringResource(R.string.size_5x5),
                    subtitle = stringResource(R.string.size_5x5_subtitle),
                    onClick = { onPick(5) }
                )
                Spacer(Modifier.height(12.dp))
                SizeOptionRow(
                    size = 6,
                    label = stringResource(R.string.size_6x6),
                    subtitle = stringResource(R.string.size_6x6_subtitle),
                    onClick = { onPick(6) }
                )
            }
        }
    }
}

@Composable
private fun SizeOptionRow(
    size: Int,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val gameColors = LocalGameColors.current
    val shape = RoundedCornerShape(18.dp)
    val bg = if (gameColors.isDark)
        Color(0xFF121A36).copy(alpha = 0.72f)
    else
        Color.White.copy(alpha = 0.72f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mini grid preview
        MiniGridBadge(size = size)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                color = gameColors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = subtitle,
                color = gameColors.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MiniGridBadge(size: Int) {
    val gameColors = LocalGameColors.current
    val cell = when (size) {
        4 -> 7.dp
        5 -> 5.5.dp
        else -> 4.5.dp
    }
    val gap = 1.5.dp
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (gameColors.isDark) Color(0xFF1A2246) else Color(0xFFE5ECF6)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            repeat(size) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    repeat(size) {
                        Box(
                            modifier = Modifier
                                .size(cell)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (gameColors.isDark) Color(0xFF3A4775)
                                    else Color(0xFFB9C3D8)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewTile(
    value: Int,
    progress: Float,
    isDark: Boolean
) {
    val base = TileColors.getBackgroundColor(value, isDark)
    val top = TileColors.getGradientStart(value, isDark)
    val text = TileColors.getTextColor(value, isDark)
    val fontSize = when (value) {
        2, 4, 8 -> 18.sp
        else -> 16.sp
    }
    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = progress
                scaleX = 0.6f + 0.4f * progress
                scaleY = 0.6f + 0.4f * progress
                translationY = (1f - progress) * 12.dp.toPx()
            }
            .size(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(top, base)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            color = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )
    }
}
