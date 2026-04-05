package com.example.game_2048.domain.model

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    fun next(): ThemeMode = when (this) {
        SYSTEM -> LIGHT
        LIGHT -> DARK
        DARK -> SYSTEM
    }
}
