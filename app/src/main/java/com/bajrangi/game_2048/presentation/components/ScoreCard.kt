package com.bajrangi.game_2048.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bajrangi.game_2048.ui.theme.LocalGameColors

@Composable
fun ScoreCard(
    label: String,
    score: Int,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(score) {
        if (score > 0) {
            pulseScale.snapTo(1.08f)
            pulseScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    GlassSurface(
        isDark = gameColors.isDark,
        cornerRadius = 14.dp,
        elevation = 2.dp,
        modifier = modifier
            .graphicsLayer {
                scaleX = pulseScale.value
                scaleY = pulseScale.value
            }
            .widthIn(min = 76.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = gameColors.scoreLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            AnimatedContent(
                targetState = score,
                transitionSpec = {
                    slideInVertically(
                        animationSpec = tween(180),
                        initialOffsetY = { -it }
                    ) togetherWith slideOutVertically(
                        animationSpec = tween(180),
                        targetOffsetY = { it }
                    )
                },
                label = "score"
            ) { targetScore ->
                Text(
                    text = targetScore.toString(),
                    color = gameColors.scoreValue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    maxLines = 1
                )
            }
        }
    }
}
