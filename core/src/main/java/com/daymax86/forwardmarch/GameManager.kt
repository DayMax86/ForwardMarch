package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.BlackPawn
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.defaults.RookDefault
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.boards.VeryEasyBoard1
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
    val traps: MutableList<BoardObject> = mutableListOf()
    val pickups: MutableList<BoardObject> = mutableListOf()
    val activeAnimations: MutableList<SpriteAnimation> = mutableListOf()
    val enemyPieces: MutableList<Piece> = mutableListOf()

    var selectedPiece: Piece? = null
    var freezeHighlights: Boolean = false

    var difficultyModifier: Int = 1
    var forwardMarchCounter: Int = 0

    var cameraTargetInX: Float = 0f
    var cameraTargetInY: Float = 0f

    init {
        val testBoard = StandardBoard(environmentYPos = BOARD_STARTING_Y)
        val testBoard2 =
            VeryEasyBoard1(environmentYPos = BOARD_STARTING_Y + (DIMENSIONS * SQUARE_HEIGHT).toInt())
        val testBoard3 =
            VeryEasyBoard1(environmentYPos = BOARD_STARTING_Y + ((DIMENSIONS * SQUARE_HEIGHT) * 2).toInt())
        this.boards.add(testBoard)
        this.boards.add(testBoard2)
        this.boards.add(testBoard3)

        setStartingLayout()
        //placeTraps()
        setEnemyPieces()

//        SpikeTrap().also {
//            it.associatedBoard = boards[0]
//            it.move(5, 4, null)
//        }.apply { traps.add(this) }


    }

    fun checkBoardsStatus() {
        // See if any boards need to be removed, or any new boards appended
        if (boards[0].environmentYPos <= 500) {
            removeBoard()
            appendBoard(difficultyModifier)
        }
    }

    fun appendBoard(difficultyModifier: Int) = runBlocking {
        launch {
            when (difficultyModifier) {
                1 -> {
                    // Choose randomly from very easy boards
                    boards.add(
                        VeryEasyBoard1(
                            environmentYPos = (BOARD_STARTING_Y + boards.size * SQUARE_HEIGHT * (DIMENSIONS) - SQUARE_HEIGHT).toInt()
                        )
                    )
                }

                2 -> {
                    // Choose randomly from easy boards
                }

                3 -> {
                    // Choose randomly from medium boards
                }
                // Populate future boards with pieces, traps etc. according to difficulty modifier
            }
            Gdx.app.log("manager", "a board has been added. (boards.size = ${boards.size})")
        }
    }

    fun removeBoard() = runBlocking {
        // If board is entirely off the bottom of the screen, remove from List
        // Remove all board objects from their corresponding lists
        val objectsToRemove: MutableList<BoardObject> = mutableListOf()
        launch {
            boards[0].squaresList.forEach { square ->
                square.contents.forEach {
                    objectsToRemove.add(it)
                }
            }
        }

        val objectRemovalUnits: MutableList<() -> Unit> = mutableListOf()
        objectsToRemove.forEach {
            if (pieces.contains(it)) {
                objectRemovalUnits.add { pieces.remove(it) }
            }
            if (enemyPieces.contains(it)) {
                objectRemovalUnits.add { enemyPieces.remove(it) }
            }
            if (traps.contains(it)) {
                objectRemovalUnits.add { traps.remove(it) }
            }
            if (pickups.contains(it)) {
                objectRemovalUnits.add { pickups.remove(it) }
            }
        }

        objectRemovalUnits.forEach { it.invoke() }

        // Remove from boards list.
        boards.removeFirst()
        Gdx.app.log("manager", "a board has been dropped. (boards.size = ${boards.size})")
    }


    fun forwardMarch(distance: Int) {
        forwardMarchCounter++
        val actionQueue: MutableList<() -> Unit> = mutableListOf()
        // Move all pieces up by one square
        pieces.forEach { piece ->
            val yMovement =
                if (piece.boardYpos + distance > 8) piece.boardYpos + distance - 8 else piece.boardYpos + distance
            val newBoard =
                if (piece.boardYpos + distance > 8) boards[boards.indexOf(piece.associatedBoard) + 1] else null
            actionQueue.add {
                piece.move(
                    piece.boardXpos,
                    yMovement,
                    newBoard
                )
            } // Add to queue to invoke after movement is fully resolved
        }

        actionQueue.forEach {
            it.invoke()
        }

        moveWithinEnvironment { enemyAttacks() }
        checkBoardsStatus()
    }

    private fun enemyAttacks() {
        val attackQueue: MutableList<() -> Unit> = mutableListOf()
        enemyPieces.forEach { piece ->
            attackQueue.add {
                piece.attack()
            }
        }

        attackQueue.forEach {
            it.invoke()
        }
    }

    private fun moveWithinEnvironment(onComplete: () -> Unit) {
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
            onComplete()
        }
    }

    fun selectPiece(piece: Piece) {
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
