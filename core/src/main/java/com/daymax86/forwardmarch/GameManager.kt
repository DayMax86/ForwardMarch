package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.async.AsyncExecutor
import com.daymax86.forwardmarch.EnemyManager.enemyPieces
import com.daymax86.forwardmarch.EnemyManager.traps
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.BlackPawn
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.board_objects.pieces.defaults.RookDefault
import com.daymax86.forwardmarch.board_objects.traps.TrapTypes
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.boards.VeryEasyBoard1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import ktx.async.KTX
import ktx.async.KtxAsync
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object GameManager {

    const val ENVIRONMENT_WIDTH = 2000f
    const val ENVIRONMENT_HEIGHT = 3000f
    const val SQUARE_WIDTH = 120f
    const val SQUARE_HEIGHT = 120f
    const val EDGE_BUFFER: Float = (ENVIRONMENT_WIDTH / 20)
    const val DIMENSIONS: Int = 8
    const val DEFAULT_ANIMATION_DURATION: Float = 0.033f
    const val BOARD_STARTING_Y = ((ENVIRONMENT_HEIGHT / 2)).toInt()
    var aspectRatio = 1920 / 1080f

    // Collections
    val pieces: MutableList<Piece> = mutableListOf()
    val boards: MutableList<Board> = mutableListOf()
    val pickups: MutableList<BoardObject> = mutableListOf()
    val activeAnimations: MutableList<SpriteAnimation> = mutableListOf()

    var selectedPiece: Piece? = null
    var freezeHighlights: Boolean = false

    var difficultyModifier: Int = 1
    var forwardMarchCounter: Int = 0
    var marchInProgress = false
    var moveCounter: Int = 0
    var moveLimit: Int = 3
    var moveLimitReached: Boolean = false
    var firstMoveComplete: Boolean = false
    private var gameState = GameState()

    var cameraTargetInX: Float = 0f
    var cameraTargetInY: Float = 0f

    init {
        val testBoard = StandardBoard(environmentYPos = BOARD_STARTING_Y)
        val testBoard2 =
            VeryEasyBoard1(environmentYPos = BOARD_STARTING_Y + (DIMENSIONS * SQUARE_HEIGHT).toInt())
        val testBoard3 =
            VeryEasyBoard1(environmentYPos = BOARD_STARTING_Y + ((DIMENSIONS * SQUARE_HEIGHT) * 2).toInt())

        boards.add(testBoard)
        boards.add(testBoard2)
        boards.add(testBoard3)

        setStartingLayout()
        setEnemyPieces()
        saveGameState()
    }

    private fun saveGameState() {
        gameState.updateGameState(
            new_pieces = pieces,
            new_boards = boards,
            new_pickups = pickups,
            new_activeAnimations = activeAnimations,
            new_selectedPiece = selectedPiece,
            new_freezeHighlights = freezeHighlights,
            new_difficultyModifier = difficultyModifier,
            new_forwardMarchCounter = forwardMarchCounter,
            new_marchInProgress = marchInProgress,
            new_moveCounter = moveCounter,
            new_moveLimit = moveLimit,
            new_moveLimitReached = moveLimitReached,
            new_firstMoveComplete = firstMoveComplete,
        )
    }

    fun forwardMarch(distance: Int) {
        KtxAsync.launch {
            marchInProgress = true
            forwardMarchCounter++
            val movementQueue: MutableList<() -> Unit> = mutableListOf()
            // Make the player pieces move forward one at a time
            // -> Resolve collisions in each of the squares they now occupy
            // Once all movements have been resolved...
            // -> Get valid moves for the enemy pieces
            // -> -> Resolve attacks for the pieces in their new positions.

            // Move all pieces up by one square
            pieces.forEach { piece ->
                val yMovement =
                    if (piece.boardYpos + distance > 8) piece.boardYpos + distance - 8 else piece.boardYpos + distance
                val newBoard =
                    if (piece.boardYpos + distance > 8) boards[boards.indexOf(piece.associatedBoard) + 1] else null
                movementQueue.add {
                    piece.move(
                        piece.boardXpos,
                        yMovement,
                        newBoard
                    )
                }
            }

            movementQueue.forEach {
                it.invoke()
            }

            moveWithinEnvironment()

            enemyPieces.forEach { enemy ->
                enemy.getValidMoves { enemy.attack() }
            }

            marchInProgress = false

            checkBoardsStatus()

            moveLimitReached = false
            moveCounter = 0
        }.invokeOnCompletion {
            saveGameState()
        }
    }

    fun revertToLastSavedState() { // TODO elements in the lists are pass by reference? We need value
        pieces.clear()
        gameState.state_pieces.forEach { pieces.add(it) }
        boards.clear()
        gameState.state_boards.forEach { boards.add(it) }
        pickups.clear()
        gameState.state_pickups.forEach { pickups.add(it) }
        activeAnimations.clear()
        gameState.state_activeAnimations.forEach { activeAnimations.add(it) }
        selectedPiece = gameState.state_selectedPiece
        freezeHighlights = gameState.state_freezeHighlights
        difficultyModifier = gameState.state_difficultyModifier
        forwardMarchCounter = gameState.state_forwardMarchCounter
        marchInProgress = gameState.state_marchInProgress
        moveCounter = gameState.state_moveCounter
        moveLimit = gameState.state_moveLimit
        moveLimitReached = gameState.state_moveLimitReached
        firstMoveComplete = gameState.state_firstMoveComplete
        saveGameState()
    }

    private fun checkBoardsStatus() {
        // See if any boards need to be removed, or any new boards appended
        if (boards[0].environmentYPos <= ENVIRONMENT_HEIGHT / 6) {
            removeBoard()
            appendBoard(difficultyModifier)
        }

    }

    private suspend fun myAppendBoard(
        difficultyModifier: Int,
    ) = suspendCoroutine { continuation ->
        val yPos =
            (BOARD_STARTING_Y + ((boards.size - 1) * SQUARE_HEIGHT * (DIMENSIONS)) - (SQUARE_HEIGHT)).toInt()
        val board = VeryEasyBoard1(
            environmentYPos = yPos,
        )
        boards.add(board)
        Gdx.app.log("manager", "a board has been added. (boards.size = ${boards.size})")

        continuation.resume(Unit)
    }

    private fun appendBoard(difficultyModifier: Int) {
        var board: Board = StandardBoard()
        val yPos =
            (BOARD_STARTING_Y + boards.size * SQUARE_HEIGHT * (DIMENSIONS) - (SQUARE_HEIGHT)).toInt()
        KtxAsync.launch {
            when (difficultyModifier) {
                1 -> {
                    // Choose randomly from very easy boards
                    board = VeryEasyBoard1(environmentYPos = yPos)
                    // Spawn enemies based on difficulty modifier
                    EnemyManager.spawnEnemy(
                        PieceTypes.PAWN,
                        (1..8).random(),
                        (1..8).random(),
                        board
                    )
                    EnemyManager.spawnTrap(TrapTypes.SPIKE, (1..8).random(), (1..8).random(), board)
                }

                2 -> {
                    // Choose randomly from easy boards
                }

                3 -> {
                    // Choose randomly from medium boards
                }
                // Populate future boards with pieces, traps etc. according to difficulty modifier
            }
        }
            .invokeOnCompletion {
                boards.add(board)
                Gdx.app.log("manager", "a board has been added. (boards.size = ${boards.size})")
            }
    }

    private fun removeBoard() {
        // If board is entirely off the bottom of the screen, remove from List
        // Remove all board objects from their corresponding lists
        val objectsToRemove: MutableList<BoardObject> = mutableListOf()
        KtxAsync.launch {

            boards[0].squaresList.forEach { square ->
                square.contents.forEach {
                    objectsToRemove.add(it)
                }
            }
        }.invokeOnCompletion {
            objectsToRemove.forEach {
                if (pieces.contains(it)) {
                    pieces.remove(it)
                }
                if (enemyPieces.contains(it)) {
                    enemyPieces.remove(it)
                }
                if (traps.contains(it)) {
                    traps.remove(it)
                }
                if (pickups.contains(it)) {
                    pickups.remove(it)
                }
            }

            // Remove from boards list.
            boards.removeFirst()
            Gdx.app.log("manager", "a board has been dropped. (boards.size = ${boards.size})")
        }

    }

    private fun moveWithinEnvironment() {
        // Each Board, and all its contents, must move down by SQUARE_HEIGHT units, while camera remains fixed
        boards.forEach { board ->
            board.environmentYPos -= SQUARE_HEIGHT.toInt()
        }

        getAllObjects().forEach { obj ->
            obj.updateBoundingBox(
                obj.boundingBox.min.x,
                obj.boundingBox.min.y - SQUARE_HEIGHT.toInt(),
                SQUARE_WIDTH,
                SQUARE_HEIGHT
            )
        }.also {
            activeAnimations.forEach { anim ->
                // Will inactive animations have to be moved too?
                // I don't think so since when activated they are placed at their current bounding box position
                anim.y -= SQUARE_HEIGHT
            }
        }
    }

    fun selectPiece(piece: Piece) {
        firstMoveComplete = true
        if (!piece.hostile && !moveLimitReached) {
            selectedPiece = piece
            piece.highlight = true
            piece.getValidMoves()
            for (board in boards) {
                for (square in board.squaresList) {
                    if (piece.movement.contains(square)) {
                        square.swapToAltHighlight(true)
                        square.highlight = true
                    }
                }
            }
            freezeHighlights = true
        }
    }

    fun deselectPiece() {
        if (selectedPiece != null) {
            selectedPiece!!.highlight = false
            freezeHighlights = false
            selectedPiece!!.movement.forEach {
                it.highlight = false
            }
        }
        selectedPiece = null
    }

    fun updateValidMoves() {
        for (piece in this.pieces) {
            piece.getValidMoves()
        }
    }

    fun getAllObjects(): MutableList<BoardObject> {
        val allObjects: MutableList<BoardObject> = mutableListOf()
        pieces.forEach { allObjects.add(it) }
        traps.forEach { allObjects.add(it) }
        enemyPieces.forEach { allObjects.add(it) }
        pickups.forEach { allObjects.add(it) }
        return allObjects
    }

// ------------------------------SETUP PLACEMENT--------------------------------------------- //

    fun setStartingLayout() {
        // PAWNS
        placeStartingPawns()
        // ROOKS
        placeStartingRooks()
        // OTHERS...
    }

    private fun placeStartingPawns() {
        for (x in 1..8) {
            BlackPawn().also {
                it.associatedBoard = boards[0]
                it.nextBoard = boards[1]
                it.move(x, 2, null)
            }.apply { pieces.add(this) }
        }
    }

    private fun placeStartingRooks() {
        RookDefault().also {
            it.associatedBoard = boards[0]
            it.nextBoard = boards[1]
            it.move(1, 1, null)
        }.apply { pieces.add(this) }

        RookDefault().also {
            it.associatedBoard = boards[0]
            it.nextBoard = boards[1]
            it.move(8, 1, null)
        }.apply { pieces.add(this) }
    }

    fun setEnemyPieces() {
        enemyPieces.forEach {
            it.move(it.boardXpos, it.boardYpos, null)
            it.getValidMoves()
        }
    }

}


