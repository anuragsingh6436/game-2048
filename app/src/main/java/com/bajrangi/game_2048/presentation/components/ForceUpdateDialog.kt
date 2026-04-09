package com.bajrangi.game_2048.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * Blocking update dialog. Non-dismissible: no back, no outside-tap.
 * The only exit is the Play Store button.
 */
@Composable
fun ForceUpdateDialog() {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { /* non-dismissible */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
        title = { Text("Update required") },
        text = {
            Text(
                "A new version of the app is available. Please update to continue playing."
            )
        },
        confirmButton = {
            Button(
                modifier = Modifier.padding(end = 8.dp),
                onClick = {
                    val pkg = context.packageName
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    runCatching { context.startActivity(intent) }
                }
            ) { Text("Update") }
        }
    )
}
