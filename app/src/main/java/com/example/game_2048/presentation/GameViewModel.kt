package com.example.game_2048.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.game_2048.config.FeatureFlags
import com.example.game_2048.data.repository.ScoreRepository
import com.example.game_2048.domain.engine.GameEngine
import com.example.game_2048.domain.model.Direction
import com.example.game_2048.domain.model.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameEngine: GameEngine,
    private val scoreRepository: ScoreRepository,
    val featureFlags: FeatureFlags
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var previousState: GameState? = null

    init {
        startNewGame()
    }

    fun startNewGame() {
        viewModelScope.launch {
            val bestScore = scoreRepository.getBestScore()
            val initialState = gameEngine.createInitialState()
            _gameState.value = initialState.copy(bestScore = bestScore)
            previousState = null
        }
    }

    fun onSwipe(direction: Direction) {
        val currentState = _gameState.value
        if (currentState.isGameOver) return

        previousState = currentState
        val newState = gameEngine.move(currentState, direction)

        if (newState !== currentState) {
            _gameState.value = newState
            if (newState.score > currentState.bestScore) {
                viewModelScope.launch {
                    scoreRepository.saveBestScore(newState.score)
                }
            }
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
}
