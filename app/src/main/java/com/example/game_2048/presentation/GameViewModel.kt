package com.example.game_2048.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.game_2048.config.FeatureFlags
import com.example.game_2048.domain.model.Direction
import com.example.game_2048.domain.model.GameState
import com.example.game_2048.domain.usecase.GetBestScoreUseCase
import com.example.game_2048.domain.usecase.MoveTilesUseCase
import com.example.game_2048.domain.usecase.SaveBestScoreUseCase
import com.example.game_2048.domain.usecase.StartNewGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val startNewGameUseCase: StartNewGameUseCase,
    private val moveTilesUseCase: MoveTilesUseCase,
    private val getBestScoreUseCase: GetBestScoreUseCase,
    private val saveBestScoreUseCase: SaveBestScoreUseCase,
    val featureFlags: FeatureFlags
) : ViewModel() {

    private val _gameState = MutableStateFlow(startNewGameUseCase())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var previousState: GameState? = null
    private var loadBestScoreJob: Job? = null

    init {
        loadBestScore()
    }

    fun startNewGame() {
        loadBestScoreJob?.cancel()
        _gameState.value = startNewGameUseCase()
        previousState = null
        loadBestScore()
    }

    fun onSwipe(direction: Direction) {
        val currentState = _gameState.value
        if (currentState.isGameOver) return

        previousState = currentState
        val newState = moveTilesUseCase(currentState, direction)

        if (newState !== currentState) {
            _gameState.value = newState
            if (newState.score > currentState.bestScore) {
                viewModelScope.launch {
                    try {
                        saveBestScoreUseCase(newState.score)
                    } catch (_: Exception) {
                        // Persist failed — best score not saved this time
                    }
                }
            }
        }
    }

    fun dismissWin() {
        val current = _gameState.value
        if (current.hasWon && !current.winDismissed) {
            _gameState.value = current.copy(winDismissed = true)
        }
    }

    fun undoMove() {
        if (!featureFlags.isUndoEnabled()) return
        previousState?.let { prev ->
            _gameState.value = prev
            previousState = null
        }
    }

    fun canUndo(): Boolean = featureFlags.isUndoEnabled() && previousState != null

    private fun loadBestScore() {
        loadBestScoreJob = viewModelScope.launch {
            try {
                val bestScore = getBestScoreUseCase()
                val current = _gameState.value
                if (bestScore > current.bestScore) {
                    _gameState.value = current.copy(bestScore = bestScore)
                }
            } catch (_: Exception) {
                // DB read failed — keep bestScore at 0
            }
        }
    }
}
