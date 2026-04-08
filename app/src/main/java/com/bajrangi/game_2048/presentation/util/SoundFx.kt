package com.bajrangi.game_2048.presentation.util

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

/**
 * Lightweight sound effects for the game — uses built-in ToneGenerator
 * constants so no audio assets are required. Chosen tones are the
 * softer/musical ones (PIP, PROMPT, CONFIRM) rather than the raw BEEP.
 */
class SoundFx internal constructor(
    private val gen: ToneGenerator?,
    private val enabled: () -> Boolean
) {
    /** UI tap — Play button, size option, icon buttons, etc. */
    fun click() {
        if (!enabled()) return
        gen?.runCatching { startTone(ToneGenerator.TONE_CDMA_PIP, 60) }
    }

    /** Plain slide where no merge happened. */
    fun move() {
        if (!enabled()) return
        gen?.runCatching { startTone(ToneGenerator.TONE_PROP_PROMPT, 45) }
    }

    /** Tile merge — crisper, slightly longer. */
    fun merge() {
        if (!enabled()) return
        gen?.runCatching { startTone(ToneGenerator.TONE_CDMA_CONFIRM, 120) }
    }

    internal fun release() {
        gen?.runCatching { release() }
    }
}

@Composable
fun rememberSoundFx(enabled: Boolean): SoundFx {
    val enabledState = rememberUpdatedState(enabled)
    val fx = remember {
        val gen = runCatching {
            ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        }.getOrNull()
        SoundFx(gen) { enabledState.value }
    }
    DisposableEffect(Unit) {
        onDispose { fx.release() }
    }
    return fx
}
