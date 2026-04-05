package com.example.game_2048.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game_2048.ui.theme.LocalGameColors

@Composable
fun ScoreCard(
    label: String,
    score: Int,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    Column(
        modifier = modifier
            .widthIn(min = 80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(gameColors.scoreCard)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = Color(0xFFEEE4DA).copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )

        AnimatedContent(
            targetState = score,
            transitionSpec = {
                slideInVertically(
                    animationSpec = tween(200),
                    initialOffsetY = { -it }
                ) togetherWith slideOutVertically(
                    animationSpec = tween(200),
                    targetOffsetY = { it }
                )
            },
            label = "score"
        ) { targetScore ->
            Text(
                text = targetScore.toString(),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
