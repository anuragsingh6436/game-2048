package com.example.game_2048.domain.usecase

import com.example.game_2048.domain.engine.GameEngine
import com.example.game_2048.domain.model.Direction
import com.example.game_2048.domain.model.GameState
import javax.inject.Inject

class MoveTilesUseCase @Inject constructor(
    private val engine: GameEngine
) {
    operator fun invoke(state: GameState, direction: Direction): GameState =
        engine.move(state, direction)
}
