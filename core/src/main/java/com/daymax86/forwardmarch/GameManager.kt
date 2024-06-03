package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.BlackPawn
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.defaults.RookDefault
import com.daymax86.forwardmarch.board_objects.traps.SpikeTrap
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.boards.VeryEasyBoard1

object GameManager {

    const val ENVIRONMENT_WIDTH = 2000f
    const val ENVIRONMENT_HEIGHT = 3000f
    const val SQUARE_WIDTH = 120f
    const val SQUARE_HEIGHT = 120f
    private const val EDGE_BUFFER: Float = (ENVIRONMENT_WIDTH / 20)
    const val DIMENSIONS: Int = 8
    const val DEFAULT_ANIMATION_DURATION: Float = 0.033f

    // Collections
    val pieces: MutableList<Piece> = mutableListOf()
    val boards: MutableList<Board> = mutableListOf()
    val traps: MutableList<BoardObject> = mutableListOf()
    val activeAnimations: MutableList<SpriteAnimation> = mutableListOf()

    var selectedPiece: Piece? = null
    var freezeHighlights: Boolean = false
    var movementInProgress: Boolean = false

    init {
        val testBoard = StandardBoard(
            dimensions = DIMENSIONS,
            environmentXPos = EDGE_BUFFER.toInt(),
            environmentYPos = 0,
            squareWidth = SQUARE_WIDTH.toInt(),
        )
        testBoard.onScreen = true
        val testBoard2 = VeryEasyBoard1(
            dimensions = DIMENSIONS,
            environmentXPos = EDGE_BUFFER.toInt(),
            environmentYPos = (SQUARE_HEIGHT * DIMENSIONS).toInt(),
            squareWidth = SQUARE_WIDTH.toInt(),
        )
        testBoard2.onScreen = true
        this.boards.add(testBoard)
        this.boards.add(testBoard2)

        BlackPawn().also {
            it.associatedBoard = boards[0]
            it.nextBoard = boards[1]
            it.move(1, 2, null)
        }.apply {
            pieces.add(this)
        }

        RookDefault().also {
            it.associatedBoard = boards[0]
            it.nextBoard = boards[1]
            it.move(3, 4, null)
        }.apply {
            pieces.add(this)
        }

        RookDefault().also {
            it.associatedBoard = boards[0]
            it.nextBoard = boards[1]
            it.move(1, 7, null)
        }.apply {
            pieces.add(this)
        }

        val testPawn2 = BlackPawn()
        testPawn2.associatedBoard = boards[0]
        testPawn2.nextBoard = boards[1]
        testPawn2.move(6, 1, null)
        pieces.add(testPawn2)

        val testPawn3 = BlackPawn()
        testPawn3.associatedBoard = boards[0]
        testPawn3.nextBoard = boards[1]
        testPawn3.move(7, 1, null)
        pieces.add(testPawn3)

        val testPawn4 = BlackPawn()
        testPawn4.associatedBoard = boards[0]
        testPawn4.nextBoard = boards[1]
        testPawn4.move(8, 2, null)
        pieces.add(testPawn4)

        val testTrap = SpikeTrap()
        testTrap.associatedBoard = boards[0]
        testTrap.move(5, 4, null)
        traps.add(testTrap)

    }

    fun forwardMarch(distance: Int) {
        val movementQueue: MutableList<() -> Unit> = mutableListOf()
        // Move all pieces up by one square
        pieces.forEach { piece ->
            val yMovement =
                if (piece.boardYpos + distance > 8) piece.boardYpos + distance - 8 else piece.boardYpos + distance
            val newBoard =
                if (piece.boardYpos + distance > 8) boards[boards.indexOf(piece.associatedBoard) + 1] else null
            movementQueue.add { piece.move(piece.boardXpos, yMovement, newBoard) } // Add to queue to invoke after movement is fully resolved
        }
        movementQueue.forEach {
            it.invoke()
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

}
