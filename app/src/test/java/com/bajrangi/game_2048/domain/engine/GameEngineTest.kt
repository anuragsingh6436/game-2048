package com.bajrangi.game_2048.domain.engine

import com.bajrangi.game_2048.domain.model.Direction
import com.bajrangi.game_2048.domain.model.GameState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {

    private lateinit var engine: GameEngine

    @Before
    fun setup() {
        engine = GameEngine()
    }

    // ==================== MERGE LINE TESTS ====================

    @Test
    fun `mergeLine - empty line stays empty`() {
        val (result, score) = engine.mergeLine(listOf(0, 0, 0, 0))
        assertEquals(listOf(0, 0, 0, 0), result)
        assertEquals(0, score)
    }

    @Test
    fun `mergeLine - single tile slides to start`() {
        val (result, score) = engine.mergeLine(listOf(0, 0, 2, 0))
        assertEquals(listOf(2, 0, 0, 0), result)
        assertEquals(0, score)
    }

    @Test
    fun `mergeLine - two equal tiles merge`() {
        val (result, score) = engine.mergeLine(listOf(2, 2, 0, 0))
        assertEquals(listOf(4, 0, 0, 0), result)
        assertEquals(4, score)
    }

    @Test
    fun `mergeLine - two equal tiles with gap merge`() {
        val (result, score) = engine.mergeLine(listOf(2, 0, 2, 0))
        assertEquals(listOf(4, 0, 0, 0), result)
        assertEquals(4, score)
    }

    @Test
    fun `mergeLine - three same tiles merges first pair`() {
        val (result, score) = engine.mergeLine(listOf(2, 2, 2, 0))
        assertEquals(listOf(4, 2, 0, 0), result)
        assertEquals(4, score)
    }

    @Test
    fun `mergeLine - four same tiles merges into two pairs`() {
        val (result, score) = engine.mergeLine(listOf(2, 2, 2, 2))
        assertEquals(listOf(4, 4, 0, 0), result)
        assertEquals(8, score)
    }

    @Test
    fun `mergeLine - different tiles don't merge`() {
        val (result, score) = engine.mergeLine(listOf(2, 4, 8, 16))
        assertEquals(listOf(2, 4, 8, 16), result)
        assertEquals(0, score)
    }

    @Test
    fun `mergeLine - tiles slide and merge correctly`() {
        val (result, score) = engine.mergeLine(listOf(0, 2, 0, 2))
        assertEquals(listOf(4, 0, 0, 0), result)
        assertEquals(4, score)
    }

    @Test
    fun `mergeLine - no double merge in single move`() {
        val (result, score) = engine.mergeLine(listOf(4, 4, 8, 0))
        assertEquals(listOf(8, 8, 0, 0), result)
        assertEquals(8, score)
    }

    @Test
    fun `mergeLine - large values merge correctly`() {
        val (result, score) = engine.mergeLine(listOf(1024, 1024, 0, 0))
        assertEquals(listOf(2048, 0, 0, 0), result)
        assertEquals(2048, score)
    }

    // ==================== MOVE GRID TESTS ====================

    @Test
    fun `moveGrid LEFT - tiles slide left`() {
        val grid = listOf(
            listOf(0, 0, 0, 2),
            listOf(0, 0, 2, 0),
            listOf(0, 2, 0, 0),
            listOf(2, 0, 0, 0)
        )
        val result = engine.moveGrid(grid, Direction.LEFT)
        assertEquals(
            listOf(
                listOf(2, 0, 0, 0),
                listOf(2, 0, 0, 0),
                listOf(2, 0, 0, 0),
                listOf(2, 0, 0, 0)
            ),
            result.grid
        )
        assertTrue(result.moved)
    }

    @Test
    fun `moveGrid RIGHT - tiles slide right`() {
        val grid = listOf(
            listOf(2, 0, 0, 0),
            listOf(0, 2, 0, 0),
            listOf(0, 0, 2, 0),
            listOf(0, 0, 0, 2)
        )
        val result = engine.moveGrid(grid, Direction.RIGHT)
        assertEquals(
            listOf(
                listOf(0, 0, 0, 2),
                listOf(0, 0, 0, 2),
                listOf(0, 0, 0, 2),
                listOf(0, 0, 0, 2)
            ),
            result.grid
        )
        assertTrue(result.moved)
    }

    @Test
    fun `moveGrid UP - tiles slide up`() {
        val grid = listOf(
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(2, 4, 8, 16)
        )
        val result = engine.moveGrid(grid, Direction.UP)
        assertEquals(
            listOf(
                listOf(2, 4, 8, 16),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            ),
            result.grid
        )
        assertTrue(result.moved)
    }

    @Test
    fun `moveGrid DOWN - tiles slide down`() {
        val grid = listOf(
            listOf(2, 4, 8, 16),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0)
        )
        val result = engine.moveGrid(grid, Direction.DOWN)
        assertEquals(
            listOf(
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(2, 4, 8, 16)
            ),
            result.grid
        )
        assertTrue(result.moved)
    }

    @Test
    fun `moveGrid LEFT - merges tiles in same row`() {
        val grid = listOf(
            listOf(2, 2, 4, 4),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0)
        )
        val result = engine.moveGrid(grid, Direction.LEFT)
        assertEquals(listOf(4, 8, 0, 0), result.grid[0])
        assertEquals(12, result.scoreGained)
        assertTrue(result.moved)
    }

    @Test
    fun `moveGrid UP - merges tiles in same column`() {
        val grid = listOf(
            listOf(2, 0, 0, 0),
            listOf(2, 0, 0, 0),
            listOf(4, 0, 0, 0),
            listOf(4, 0, 0, 0)
        )
        val result = engine.moveGrid(grid, Direction.UP)
        assertEquals(4, result.grid[0][0])
        assertEquals(8, result.grid[1][0])
        assertEquals(0, result.grid[2][0])
        assertEquals(0, result.grid[3][0])
        assertEquals(12, result.scoreGained)
    }

    @Test
    fun `moveGrid - no movement when already packed`() {
        val grid = listOf(
            listOf(2, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0)
        )
        val result = engine.moveGrid(grid, Direction.LEFT)
        assertFalse(result.moved)
    }

    @Test
    fun `moveGrid RIGHT - no movement when already packed right`() {
        val grid = listOf(
            listOf(0, 0, 0, 2),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0)
        )
        val result = engine.moveGrid(grid, Direction.RIGHT)
        assertFalse(result.moved)
    }

    // ==================== SCORE CALCULATION TESTS ====================

    @Test
    fun `score accumulates from merges`() {
        val grid = listOf(
            listOf(2, 2, 4, 4),
            listOf(8, 8, 16, 16),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0)
        )
        val result = engine.moveGrid(grid, Direction.LEFT)
        // Row 0: 2+2=4, 4+4=8 -> score = 4+8 = 12
        // Row 1: 8+8=16, 16+16=32 -> score = 16+32 = 48
        assertEquals(60, result.scoreGained)
    }

    @Test
    fun `score is zero when no merges`() {
        val grid = listOf(
            listOf(2, 4, 8, 16),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0)
        )
        val result = engine.moveGrid(grid, Direction.LEFT)
        assertEquals(0, result.scoreGained)
    }

    // ==================== ADD RANDOM TILE TESTS ====================

    @Test
    fun `addRandomTile - adds tile to empty cell`() {
        val grid = engine.emptyGrid()
        val seededRandom = Random(42)
        val newGrid = engine.addRandomTile(grid, seededRandom)

        val nonZeroCells = newGrid.flatten().count { it != 0 }
        assertEquals(1, nonZeroCells)

        val value = newGrid.flatten().first { it != 0 }
        assertTrue(value == 2 || value == 4)
    }

    @Test
    fun `addRandomTile - returns same grid when full`() {
        val grid = listOf(
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256),
            listOf(512, 1024, 2, 4),
            listOf(8, 16, 32, 64)
        )
        val newGrid = engine.addRandomTile(grid)
        assertEquals(grid, newGrid)
    }

    @Test
    fun `addRandomTile - mostly generates 2s`() {
        val grid = engine.emptyGrid()
        val random = Random(0)
        var twos = 0
        var fours = 0

        repeat(1000) {
            val newGrid = engine.addRandomTile(grid, random)
            val value = newGrid.flatten().first { it != 0 }
            if (value == 2) twos++ else fours++
        }

        // ~90% should be 2s
        assertTrue(twos > 800)
        assertTrue(fours < 200)
    }

    // ==================== GAME OVER DETECTION TESTS ====================

    @Test
    fun `canMove - returns true with empty cells`() {
        val grid = listOf(
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256),
            listOf(512, 1024, 2, 4),
            listOf(8, 16, 32, 0)
        )
        assertTrue(engine.canMove(grid))
    }

    @Test
    fun `canMove - returns true with horizontal merge possible`() {
        val grid = listOf(
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256),
            listOf(512, 1024, 2, 4),
            listOf(8, 16, 32, 32)
        )
        assertTrue(engine.canMove(grid))
    }

    @Test
    fun `canMove - returns true with vertical merge possible`() {
        val grid = listOf(
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256),
            listOf(512, 1024, 2, 4),
            listOf(8, 16, 2, 64)
        )
        assertTrue(engine.canMove(grid))
    }

    @Test
    fun `canMove - returns false when no moves possible`() {
        val grid = listOf(
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256),
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256)
        )
        assertFalse(engine.canMove(grid))
    }

    @Test
    fun `canMove - returns true for empty grid`() {
        assertTrue(engine.canMove(engine.emptyGrid()))
    }

    // ==================== INITIAL STATE TESTS ====================

    @Test
    fun `createInitialState - score starts at 0`() {
        val state = engine.createInitialState()
        assertEquals(0, state.score)
    }

    @Test
    fun `createInitialState - game is not over`() {
        val state = engine.createInitialState()
        assertFalse(state.isGameOver)
    }

    @Test
    fun `createInitialState - has not won`() {
        val state = engine.createInitialState()
        assertFalse(state.hasWon)
    }

    // ==================== FULL MOVE TESTS ====================

    @Test
    fun `move - updates score correctly`() {
        val initialState = GameState(
            grid = listOf(
                listOf(2, 2, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            ),
            score = 10
        )
        val newState = engine.move(initialState, Direction.LEFT, Random(42))
        assertEquals(14, newState.score) // 10 + 4
    }

    @Test
    fun `move - adds random tile after movement`() {
        val initialState = GameState(
            grid = listOf(
                listOf(2, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 4)
            )
        )
        val newState = engine.move(initialState, Direction.LEFT, Random(42))
        val nonZero = newState.grid.flatten().count { it != 0 }
        assertEquals(3, nonZero) // 2 original + 1 new
    }

    @Test
    fun `move - returns same state when no movement possible`() {
        val initialState = GameState(
            grid = listOf(
                listOf(2, 4, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
        )
        val newState = engine.move(initialState, Direction.LEFT)
        assertSame(initialState, newState)
    }

    @Test
    fun `move - detects win when 2048 tile created`() {
        val initialState = GameState(
            grid = listOf(
                listOf(1024, 1024, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
        )
        val newState = engine.move(initialState, Direction.LEFT, Random(42))
        assertTrue(newState.hasWon)
        assertEquals(2048, newState.grid[0][0])
    }

    @Test
    fun `move - detects game over when no moves left`() {
        // Create a nearly full board where the move will cause game over
        val initialState = GameState(
            grid = listOf(
                listOf(2, 4, 8, 16),
                listOf(32, 64, 128, 256),
                listOf(2, 4, 8, 16),
                listOf(32, 64, 128, 0)
            )
        )
        // Move right to fill the last cell
        val newState = engine.move(initialState, Direction.RIGHT, Random(42))
        // After move, the board might or might not be game over depending on random tile
        // The important thing is game over detection works
        if (engine.getEmptyCellCount(newState.grid) == 0) {
            assertEquals(!engine.canMove(newState.grid), newState.isGameOver)
        }
    }

    @Test
    fun `move - increments move count`() {
        val initialState = GameState(
            grid = listOf(
                listOf(0, 0, 0, 2),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            ),
            moveCount = 5
        )
        val newState = engine.move(initialState, Direction.LEFT, Random(42))
        assertEquals(6, newState.moveCount)
    }

    @Test
    fun `move - updates best score`() {
        val initialState = GameState(
            grid = listOf(
                listOf(1024, 1024, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            ),
            score = 0,
            bestScore = 100
        )
        val newState = engine.move(initialState, Direction.LEFT, Random(42))
        assertEquals(2048, newState.bestScore)
    }

    @Test
    fun `move - preserves best score when current is lower`() {
        val initialState = GameState(
            grid = listOf(
                listOf(2, 2, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            ),
            score = 0,
            bestScore = 10000
        )
        val newState = engine.move(initialState, Direction.LEFT, Random(42))
        assertEquals(10000, newState.bestScore)
    }

    // ==================== TRANSPOSE TESTS ====================

    @Test
    fun `transpose - correctly transposes grid`() {
        val grid = listOf(
            listOf(1, 2, 3, 4),
            listOf(5, 6, 7, 8),
            listOf(9, 10, 11, 12),
            listOf(13, 14, 15, 16)
        )
        val transposed = engine.transpose(grid)
        assertEquals(listOf(1, 5, 9, 13), transposed[0])
        assertEquals(listOf(2, 6, 10, 14), transposed[1])
        assertEquals(listOf(3, 7, 11, 15), transposed[2])
        assertEquals(listOf(4, 8, 12, 16), transposed[3])
    }

    @Test
    fun `transpose - double transpose returns original`() {
        val grid = listOf(
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256),
            listOf(512, 1024, 2, 4),
            listOf(8, 16, 32, 64)
        )
        assertEquals(grid, engine.transpose(engine.transpose(grid)))
    }

    // ==================== UTILITY TESTS ====================

    @Test
    fun `emptyGrid - creates 4x4 grid of zeros`() {
        val grid = engine.emptyGrid()
        assertEquals(4, grid.size)
        grid.forEach { row ->
            assertEquals(4, row.size)
            row.forEach { cell -> assertEquals(0, cell) }
        }
    }

    @Test
    fun `getEmptyCellCount - counts correctly`() {
        val grid = listOf(
            listOf(2, 0, 0, 0),
            listOf(0, 4, 0, 0),
            listOf(0, 0, 8, 0),
            listOf(0, 0, 0, 16)
        )
        assertEquals(12, engine.getEmptyCellCount(grid))
    }

    @Test
    fun `getEmptyCellCount - zero for full grid`() {
        val grid = listOf(
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256),
            listOf(512, 1024, 2, 4),
            listOf(8, 16, 32, 64)
        )
        assertEquals(0, engine.getEmptyCellCount(grid))
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    fun `mergeLine - handles single non-zero at end`() {
        val (result, score) = engine.mergeLine(listOf(0, 0, 0, 8))
        assertEquals(listOf(8, 0, 0, 0), result)
        assertEquals(0, score)
    }

    @Test
    fun `mergeLine - handles alternating values`() {
        val (result, score) = engine.mergeLine(listOf(2, 4, 2, 4))
        assertEquals(listOf(2, 4, 2, 4), result)
        assertEquals(0, score)
    }

    @Test
    fun `move preserves hasWon across moves`() {
        val initialState = GameState(
            grid = listOf(
                listOf(2048, 2, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            ),
            hasWon = true
        )
        val newState = engine.move(initialState, Direction.LEFT, Random(42))
        assertTrue(newState.hasWon)
    }

    // ==================== TILE TRACKING TESTS ====================

    @Test
    fun `move LEFT - tiles have correct previousCol`() {
        val state = GameState(
            grid = listOf(
                listOf(0, 0, 0, 2),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
        )
        val newState = engine.move(state, Direction.LEFT, Random(42))
        // The 2 at col 3 should move to col 0
        val movedTile = newState.tiles.find { it.value == 2 && !it.isNew }
        assertNotNull("Should find the moved tile", movedTile)
        movedTile!!
        assertEquals(0, movedTile.row)
        assertEquals(0, movedTile.col)
        assertEquals(3, movedTile.previousCol)
        assertEquals(0, movedTile.previousRow)
    }

    @Test
    fun `move RIGHT - tiles have correct previousCol`() {
        val state = GameState(
            grid = listOf(
                listOf(2, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
        )
        val newState = engine.move(state, Direction.RIGHT, Random(42))
        val movedTile = newState.tiles.find { it.value == 2 && !it.isNew }
        assertNotNull(movedTile)
        movedTile!!
        assertEquals(0, movedTile.row)
        assertEquals(3, movedTile.col)
        assertEquals(0, movedTile.previousCol)
    }

    @Test
    fun `move UP - tiles have correct previousRow`() {
        val state = GameState(
            grid = listOf(
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(4, 0, 0, 0)
            )
        )
        val newState = engine.move(state, Direction.UP, Random(42))
        val movedTile = newState.tiles.find { it.value == 4 && !it.isNew }
        assertNotNull(movedTile)
        movedTile!!
        assertEquals(0, movedTile.row)
        assertEquals(0, movedTile.col)
        assertEquals(3, movedTile.previousRow)
    }

    @Test
    fun `move DOWN - tiles have correct previousRow`() {
        val state = GameState(
            grid = listOf(
                listOf(8, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
        )
        val newState = engine.move(state, Direction.DOWN, Random(42))
        val movedTile = newState.tiles.find { it.value == 8 && !it.isNew }
        assertNotNull(movedTile)
        movedTile!!
        assertEquals(3, movedTile.row)
        assertEquals(0, movedTile.col)
        assertEquals(0, movedTile.previousRow)
    }

    @Test
    fun `merged tiles are marked mergedFrom`() {
        val state = GameState(
            grid = listOf(
                listOf(2, 2, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
        )
        val newState = engine.move(state, Direction.LEFT, Random(42))
        val mergedTile = newState.tiles.find { it.value == 4 && it.mergedFrom }
        assertNotNull("Should have a merged tile with value 4", mergedTile)
    }

    @Test
    fun `new random tile is marked isNew`() {
        val state = GameState(
            grid = listOf(
                listOf(2, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 4)
            )
        )
        val newState = engine.move(state, Direction.LEFT, Random(42))
        val newTile = newState.tiles.find { it.isNew }
        assertNotNull("Should have a newly spawned tile", newTile)
        assertTrue(newTile!!.value == 2 || newTile.value == 4)
    }

    @Test
    fun `tiles list contains all non-zero cells`() {
        val state = GameState(
            grid = listOf(
                listOf(2, 4, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 8)
            )
        )
        val newState = engine.move(state, Direction.LEFT, Random(42))
        val gridNonZero = newState.grid.flatten().count { it != 0 }
        assertEquals(gridNonZero, newState.tiles.size)
    }

    // ==================== SCORE GAINED TESTS ====================

    @Test
    fun `scoreGained reflects merge points`() {
        val state = GameState(
            grid = listOf(
                listOf(4, 4, 8, 8),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
        )
        val newState = engine.move(state, Direction.LEFT, Random(42))
        assertEquals(24, newState.scoreGained) // 4+4=8, 8+8=16 → 8+16=24
    }

    @Test
    fun `scoreGained is zero on move without merge`() {
        val state = GameState(
            grid = listOf(
                listOf(0, 0, 0, 2),
                listOf(0, 0, 0, 4),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
        )
        val newState = engine.move(state, Direction.LEFT, Random(42))
        assertEquals(0, newState.scoreGained)
    }

    // ==================== WIN DISMISSED STATE TESTS ====================

    @Test
    fun `showWinOverlay computed property works correctly`() {
        val base = GameState()
        assertFalse(base.showWinOverlay)

        val won = base.copy(hasWon = true)
        assertTrue(won.showWinOverlay)

        val dismissed = won.copy(winDismissed = true)
        assertFalse(dismissed.showWinOverlay)

        val wonAndOver = won.copy(isGameOver = true)
        assertFalse(wonAndOver.showWinOverlay)
    }
}
