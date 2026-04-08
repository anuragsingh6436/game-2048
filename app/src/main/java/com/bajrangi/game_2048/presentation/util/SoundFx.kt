package com.bajrangi.game_2048.presentation.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.bajrangi.game_2048.R

/**
 * SoundPool-based SFX. Reliable across devices (unlike ToneGenerator
 * CDMA tones) and bypasses the system "Touch sounds" setting. Uses
 * bundled WAVs from res/raw.
 */
class SoundFx internal constructor(
    private val pool: SoundPool,
    private val clickId: Int,
    private val moveId: Int,
    private val mergeId: Int,
    private val enabled: () -> Boolean
) {
    fun click() = play(clickId, 1.0f)
    fun move() = play(moveId, 0.9f)
    fun merge() = play(mergeId, 1.0f)

    private fun play(id: Int, volume: Float) {
        if (!enabled() || id == 0) return
        pool.play(id, volume, volume, 1, 0, 1f)
    }

    internal fun release() {
        runCatching { pool.release() }
    }
}

@Composable
fun rememberSoundFx(enabled: Boolean): SoundFx {
    val context = LocalContext.current
    val enabledState = rememberUpdatedState(enabled)
    val fx = remember {
        buildSoundFx(context) { enabledState.value }
    }
    DisposableEffect(Unit) {
        onDispose { fx.release() }
    }
    return fx
}

private fun buildSoundFx(context: Context, enabled: () -> Boolean): SoundFx {
    val attrs = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    val pool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(attrs)
        .build()
    val click = runCatching { pool.load(context, R.raw.click, 1) }.getOrDefault(0)
    val move = runCatching { pool.load(context, R.raw.move, 1) }.getOrDefault(0)
    val merge = runCatching { pool.load(context, R.raw.merge, 1) }.getOrDefault(0)
    return SoundFx(pool, click, move, merge, enabled)
}
