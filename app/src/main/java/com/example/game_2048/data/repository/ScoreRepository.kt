package com.example.game_2048.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_2048_prefs")

@Singleton
class ScoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val BEST_SCORE_KEY = intPreferencesKey("best_score")
    }

    suspend fun getBestScore(): Int {
        return context.dataStore.data.map { preferences ->
            preferences[BEST_SCORE_KEY] ?: 0
        }.first()
    }

    suspend fun saveBestScore(score: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[BEST_SCORE_KEY] ?: 0
            if (score > current) {
                preferences[BEST_SCORE_KEY] = score
            }
        }
    }
}
