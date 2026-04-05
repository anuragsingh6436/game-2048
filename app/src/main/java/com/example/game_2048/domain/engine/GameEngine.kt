package com.example.game_2048.domain.engine

import com.example.game_2048.domain.model.Direction
import com.example.game_2048.domain.model.GameState
import com.example.game_2048.domain.model.GameState.Companion.GRID_SIZE
import com.example.game_2048.domain.model.Tile
import javax.inject.Inject
import kotlin.random.Random

data class MoveResult(
    val grid: List<List<Int>>,
    val scoreGained: Int,
    val moved: Boolean
)

class GameEngine @Inject constructor() {

    private var nextTileId: Long = 0

    fun resetIdCounter() {
        nextTileId = 0
    }

    private fun nextId(): Long = nextTileId++

    fun createInitialState(random: Random = Random): GameState {
        resetIdCounter()
        var grid = emptyGrid()
        grid = addRandomTile(grid, random)
        grid = addRandomTile(grid, random)
        val tiles = gridToTiles(grid, isNew = true)
        return GameState(grid = grid, tiles = tiles)
    }

    fun move(state: GameState, direction: Direction, random: Random = Random): GameState {
        val result = moveGrid(state.grid, direction)

        if (!result.moved) return state

        val movedGrid = addRandomTile(result.grid, random)
        val newScore = state.score + result.scoreGained
        val bestScore = maxOf(newScore, state.bestScore)
        val hasWon = state.hasWon || movedGrid.any { row -> row.any { it == 2048 } }
        val tiles = buildTilesFromMove(state.grid, movedGrid, direction, result.grid)
        val isGameOver = !canMove(movedGrid)

        return state.copy(
            grid = movedGrid,
            tiles = tiles,
            score = newScore,
            bestScore = bestScore,
            scoreGained = result.scoreGained,
            isGameOver = isGameOver,
            hasWon = hasWon,
            moveCount = state.moveCount + 1
        )
    }

    fun moveGrid(grid: List<List<Int>>, direction: Direction): MoveResult {
        return when (direction) {
            Direction.LEFT -> moveLeft(grid)
            Direction.RIGHT -> moveRight(grid)
            Direction.UP -> moveUp(grid)
            Direction.DOWN -> moveDown(grid)
        }
    }

    private fun moveLeft(grid: List<List<Int>>): MoveResult {
        var totalScore = 0
        var moved = false
        val newGrid = grid.map { row ->
            val result = mergeLine(row)
            totalScore += result.second
            if (result.first != row) moved = true
            result.first
        }
        return MoveResult(newGrid, totalScore, moved)
    }

    private fun moveRight(grid: List<List<Int>>): MoveResult {
        var totalScore = 0
        var moved = false
        val newGrid = grid.map { row ->
            val result = mergeLine(row.reversed())
            totalScore += result.second
            val newRow = result.first.reversed()
            if (newRow != row) moved = true
            newRow
        }
        return MoveResult(newGrid, totalScore, moved)
    }

    private fun moveUp(grid: List<List<Int>>): MoveResult {
        val transposed = transpose(grid)
        val result = moveLeft(transposed)
        return MoveResult(transpose(result.grid), result.scoreGained, result.moved)
    }

    private fun moveDown(grid: List<List<Int>>): MoveResult {
        val transposed = transpose(grid)
        val result = moveRight(transposed)
        return MoveResult(transpose(result.grid), result.scoreGained, result.moved)
    }

    fun mergeLine(line: List<Int>): Pair<List<Int>, Int> {
        val filtered = line.filter { it != 0 }
        val merged = mutableListOf<Int>()
        var score = 0
        var i = 0

        while (i < filtered.size) {
            if (i + 1 < filtered.size && filtered[i] == filtered[i + 1]) {
                val mergedValue = filtered[i] * 2
                merged.add(mergedValue)
                score += mergedValue
                i += 2
            } else {
                merged.add(filtered[i])
                i++
            }
        }

        while (merged.size < GRID_SIZE) {
            merged.add(0)
        }

        return Pair(merged.toList(), score)
    }

    fun transpose(grid: List<List<Int>>): List<List<Int>> {
        return List(GRID_SIZE) { row ->
            List(GRID_SIZE) { col ->
                grid[col][row]
            }
        }
    }

    fun addRandomTile(grid: List<List<Int>>, random: Random = Random): List<List<Int>> {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                if (grid[r][c] == 0) emptyCells.add(Pair(r, c))
            }
        }
        if (emptyCells.isEmpty()) return grid

        val (row, col) = emptyCells[random.nextInt(emptyCells.size)]
        val value = if (random.nextFloat() < 0.9f) 2 else 4

        return grid.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (r == row && c == col) value else cell
            }
        }
    }

    fun canMove(grid: List<List<Int>>): Boolean {
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                if (grid[r][c] == 0) return true
            }
        }
        for (r in grid.indices) {
            for (c in 0 until GRID_SIZE - 1) {
                if (grid[r][c] == grid[r][c + 1]) return true
            }
        }
        for (r in 0 until GRID_SIZE - 1) {
            for (c in grid[r].indices) {
                if (grid[r][c] == grid[r + 1][c]) return true
            }
        }
        return false
    }

    fun emptyGrid(): List<List<Int>> = List(GRID_SIZE) { List(GRID_SIZE) { 0 } }

    fun getEmptyCellCount(grid: List<List<Int>>): Int {
        return grid.sumOf { row -> row.count { it == 0 } }
    }

    // ─── Tracked merge for slide animation ──────────────────────────

    private data class TrackedCell(
        val value: Int,
        val sourceIndex: Int,
        val isMerged: Boolean
    )

    private data class TileSource(
        val sourceRow: Int,
        val sourceCol: Int,
        val isMerged: Boolean
    )

    private fun mergeLineTracked(line: List<Int>): List<TrackedCell> {
        val filtered = line.mapIndexedNotNull { i, v ->
            if (v != 0) Pair(i, v) else null
        }
        val result = mutableListOf<TrackedCell>()
        var i = 0

        while (i < filtered.size) {
            if (i + 1 < filtered.size && filtered[i].second == filtered[i + 1].second) {
                val merged = filtered[i].second * 2
                result.add(TrackedCell(merged, filtered[i].first, true))
                i += 2
            } else {
                result.add(TrackedCell(filtered[i].second, filtered[i].first, false))
                i++
            }
        }

        while (result.size < GRID_SIZE) {
            result.add(TrackedCell(0, -1, false))
        }
        return result
    }

    private fun computeSourcePositions(
        oldGrid: List<List<Int>>,
        direction: Direction
    ): Map<Pair<Int, Int>, TileSource> {
        val sources = mutableMapOf<Pair<Int, Int>, TileSource>()

        when (direction) {
            Direction.LEFT -> {
                for (r in oldGrid.indices) {
                    val tracked = mergeLineTracked(oldGrid[r])
                    for ((destCol, cell) in tracked.withIndex()) {
                        if (cell.value != 0) {
                            sources[Pair(r, destCol)] =
                                TileSource(r, cell.sourceIndex, cell.isMerged)
                        }
                    }
                }
            }
            Direction.RIGHT -> {
                for (r in oldGrid.indices) {
                    val tracked = mergeLineTracked(oldGrid[r].reversed())
                    for ((i, cell) in tracked.withIndex()) {
                        val destCol = GRID_SIZE - 1 - i
                        if (cell.value != 0) {
                            val sourceCol = GRID_SIZE - 1 - cell.sourceIndex
                            sources[Pair(r, destCol)] =
                                TileSource(r, sourceCol, cell.isMerged)
                        }
                    }
                }
            }
            Direction.UP -> {
                val transposed = transpose(oldGrid)
                for (c in transposed.indices) {
                    val tracked = mergeLineTracked(transposed[c])
                    for ((destRow, cell) in tracked.withIndex()) {
                        if (cell.value != 0) {
                            sources[Pair(destRow, c)] =
                                TileSource(cell.sourceIndex, c, cell.isMerged)
                        }
                    }
                }
            }
            Direction.DOWN -> {
                val transposed = transpose(oldGrid)
                for (c in transposed.indices) {
                    val tracked = mergeLineTracked(transposed[c].reversed())
                    for ((i, cell) in tracked.withIndex()) {
                        val destRow = GRID_SIZE - 1 - i
                        if (cell.value != 0) {
                            val sourceRow = GRID_SIZE - 1 - cell.sourceIndex
                            sources[Pair(destRow, c)] =
                                TileSource(sourceRow, c, cell.isMerged)
                        }
                    }
                }
            }
        }

        return sources
    }

    // ─── Tile building ──────────────────────────────────────────────

    private fun buildTilesFromMove(
        oldGrid: List<List<Int>>,
        newGridWithNewTile: List<List<Int>>,
        direction: Direction,
        newGridBeforeNewTile: List<List<Int>>
    ): List<Tile> {
        val sources = computeSourcePositions(oldGrid, direction)
        val tiles = mutableListOf<Tile>()

        // Moved / merged tiles
        for (r in newGridBeforeNewTile.indices) {
            for (c in newGridBeforeNewTile[r].indices) {
                val value = newGridBeforeNewTile[r][c]
                if (value != 0) {
                    val source = sources[Pair(r, c)]
                    tiles.add(
                        Tile(
                            id = nextId(),
                            value = value,
                            row = r,
                            col = c,
                            previousRow = source?.sourceRow ?: r,
                            previousCol = source?.sourceCol ?: c,
                            mergedFrom = source?.isMerged ?: false
                        )
                    )
                }
            }
        }

        // Newly spawned tile
        for (r in newGridWithNewTile.indices) {
            for (c in newGridWithNewTile[r].indices) {
                if (newGridWithNewTile[r][c] != 0 && newGridBeforeNewTile[r][c] == 0) {
                    tiles.add(
                        Tile(
                            id = nextId(),
                            value = newGridWithNewTile[r][c],
                            row = r,
                            col = c,
                            isNew = true
                        )
                    )
                }
            }
        }

        return tiles
    }

    private fun gridToTiles(grid: List<List<Int>>, isNew: Boolean = false): List<Tile> {
        val tiles = mutableListOf<Tile>()
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                if (grid[r][c] != 0) {
                    tiles.add(
                        Tile(
                            id = nextId(),
                            value = grid[r][c],
                            row = r,
                            col = c,
                            isNew = isNew
                        )
                    )
                }
            }
        }
        return tiles
    }
}
