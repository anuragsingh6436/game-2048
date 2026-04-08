package com.bajrangi.game_2048.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bajrangi.game_2048.R
import com.bajrangi.game_2048.ui.theme.DarkSurface
import com.bajrangi.game_2048.ui.theme.LightSurface
import com.bajrangi.game_2048.ui.theme.LocalGameColors
import kotlinx.coroutines.launch

/**
 * XOMaster-inspired settings affordance.
 *
 * Top-right row hosts two icons — HelpOutline (How to play) and
 * Settings (theme + sound bottom sheet). How-to-play is *not* part of
 * the sheet any more; it's a dedicated route so users can reach it
 * from any screen including the splash.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsOverlay(
    isSoundEnabled: Boolean,
    onToggleTheme: () -> Unit,
    onToggleSound: () -> Unit,
    onHowToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    var showSettings by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    fun closeAfter(action: () -> Unit) {
        // Hide the sheet *first*, then apply the action so the flip
        // happens after the sheet is gone — matches XOMaster.
        scope.launch {
            sheetState.hide()
            showSettings = false
            action()
        }
    }

    val cdSettings = stringResource(R.string.cd_settings)
    val cdHowToPlay = stringResource(R.string.settings_how_to_play)

    Box(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onHowToPlay,
                modifier = Modifier.semantics { contentDescription = cdHowToPlay }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null,
                    tint = gameColors.textPrimary.copy(alpha = 0.85f)
                )
            }
            IconButton(
                onClick = { showSettings = true },
                modifier = Modifier.semantics { contentDescription = cdSettings }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = gameColors.textPrimary.copy(alpha = 0.85f)
                )
            }
        }

        if (showSettings) {
            ModalBottomSheet(
                onDismissRequest = { showSettings = false },
                sheetState = sheetState,
                containerColor = if (gameColors.isDark) DarkSurface else LightSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.settings_title),
                        color = gameColors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 3.sp
                    )
                    Spacer(Modifier.height(20.dp))

                    SettingsRow(
                        label = if (gameColors.isDark) stringResource(R.string.settings_light_mode)
                                else stringResource(R.string.settings_dark_mode),
                        icon = if (gameColors.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        onClick = { closeAfter(onToggleTheme) }
                    )
                    Spacer(Modifier.height(12.dp))
                    SettingsRow(
                        label = if (isSoundEnabled) stringResource(R.string.settings_sound_on)
                                else stringResource(R.string.settings_sound_off),
                        icon = if (isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp
                               else Icons.AutoMirrored.Filled.VolumeOff,
                        onClick = { closeAfter(onToggleSound) }
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val gameColors = LocalGameColors.current
    val shape = RoundedCornerShape(16.dp)
    val bg = if (gameColors.isDark)
        Color(0xFF121A36).copy(alpha = 0.7f)
    else
        Color.White.copy(alpha = 0.7f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bg)
            .border(
                width = 1.dp,
                color = gameColors.textPrimary.copy(alpha = 0.12f),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .semantics { contentDescription = label },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = gameColors.textPrimary
        )
        Text(
            text = label,
            color = gameColors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
