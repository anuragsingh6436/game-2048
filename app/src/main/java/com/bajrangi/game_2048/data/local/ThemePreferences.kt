package com.bajrangi.game_2048.data.local

import android.content.Context
import com.bajrangi.game_2048.domain.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("numra_theme", Context.MODE_PRIVATE)

    fun getThemeMode(): ThemeMode {
        val ordinal = prefs.getInt(KEY_THEME, ThemeMode.DARK.ordinal)
        return ThemeMode.entries.getOrElse(ordinal) { ThemeMode.DARK }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putInt(KEY_THEME, mode.ordinal).apply()
    }

    companion object {
        private const val KEY_THEME = "theme_mode"
    }
}
