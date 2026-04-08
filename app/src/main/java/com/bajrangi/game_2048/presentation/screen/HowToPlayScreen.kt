package com.bajrangi.game_2048.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bajrangi.game_2048.R
import com.bajrangi.game_2048.presentation.components.AppBackground
import com.bajrangi.game_2048.presentation.components.GlassSurface
import com.bajrangi.game_2048.ui.theme.LocalGameColors

@Composable
fun HowToPlayScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    BackHandler(onBack = onBack)

    AppBackground(
        isDark = gameColors.isDark,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val backCd = stringResource(R.string.cd_back)
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.semantics { contentDescription = backCd }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = gameColors.textPrimary.copy(alpha = 0.85f)
                    )
                }
                Spacer(Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.howto_title),
                    color = gameColors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            GlassSurface(
                isDark = gameColors.isDark,
                cornerRadius = 24.dp,
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HowToItem(index = 1, text = stringResource(R.string.howto_goal))
                    HowToItem(index = 2, text = stringResource(R.string.howto_sizes))
                    HowToItem(index = 3, text = stringResource(R.string.howto_start))
                    HowToItem(index = 4, text = stringResource(R.string.howto_swipe))
                    HowToItem(index = 5, text = stringResource(R.string.howto_merge))
                    HowToItem(index = 6, text = stringResource(R.string.howto_spawn))
                    HowToItem(index = 7, text = stringResource(R.string.howto_lose))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HowToItem(index: Int, text: String) {
    val gameColors = LocalGameColors.current
    val shape = RoundedCornerShape(16.dp)
    val bg = if (gameColors.isDark)
        Color(0xFF121A36).copy(alpha = 0.55f)
    else
        Color.White.copy(alpha = 0.65f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bg)
            .border(
                width = 1.dp,
                color = gameColors.textPrimary.copy(alpha = 0.10f),
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(gameColors.textPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index.toString(),
                color = gameColors.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = text,
            color = gameColors.textPrimary.copy(alpha = 0.9f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp
        )
    }
}
