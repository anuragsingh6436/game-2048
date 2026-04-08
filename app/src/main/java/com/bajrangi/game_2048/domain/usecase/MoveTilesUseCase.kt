package com.bajrangi.game_2048.domain.usecase

import com.bajrangi.game_2048.domain.engine.GameEngine
import com.bajrangi.game_2048.domain.model.Direction
import com.bajrangi.game_2048.domain.model.GameState
import javax.inject.Inject

class MoveTilesUseCase @Inject constructor(
    private val engine: GameEngine
) {
    operator fun invoke(state: GameState, direction: Direction): GameState =
        engine.move(state, direction)
}
