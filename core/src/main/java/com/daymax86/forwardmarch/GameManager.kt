package com.daymax86.forwardmarch

import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.pieces.BlackPawn
import com.daymax86.forwardmarch.pieces.Piece

object GameManager {

    const val ENVIRONMENT_WIDTH = 2000f
    const val ENVIRONMENT_HEIGHT = 3000f
    const val SQUARE_WIDTH = 120f
    const val SQUARE_HEIGHT = 120f
    const val EDGE_BUFFER: Float = (ENVIRONMENT_WIDTH / 20)


    private const val DIMENSIONS: Int = 8
    val pieces = Array<Piece>()
    val boards = Array<Board>()
    var selectedPiece: Piece? = null
    var freezeHighlights: Boolean = false

    init {
        val testBoard = StandardBoard(
            dimensions = DIMENSIONS,
            environmentXPos = EDGE_BUFFER.toInt(),
            environmentYPos = 0,
            squareWidth = SQUARE_WIDTH.toInt(),
        )
        testBoard.onScreen = true
        val testBoard2 = StandardBoard(
            dimensions = DIMENSIONS,
            environmentXPos = EDGE_BUFFER.toInt(),
            environmentYPos = (SQUARE_HEIGHT * DIMENSIONS).toInt(),
            squareWidth = SQUARE_WIDTH.toInt(),
        )
        testBoard2.onScreen = true
        this.boards.add(testBoard, testBoard2)

        val testPawn = BlackPawn()
        testPawn.associatedBoard = this.boards[0]
        testPawn.nextBoard = this.boards[1]
        testPawn.move(3, 3, null)
        this.pieces.add(testPawn)
        val testPawn2 = BlackPawn()
        testPawn2.associatedBoard = this.boards[0]
        testPawn2.nextBoard = this.boards[1]
        testPawn2.move(5, 8, null)
        this.pieces.add(testPawn2)

        updateValidMoves()

    }

    fun selectPiece(piece: Piece) {
        this.selectedPiece = piece
        piece.highlight = true
        piece.getValidMoves()
        for (board in this.boards) { // TODO Iterating through nested array may be too intensive
            for (square in board.squaresArray) {
                if (piece.movement.contains(square)) {
                    square.swapToAltHighlight(true)
                    square.highlight = true
                }
            }
        }
        this.freezeHighlights = true
    }

    fun deselectPiece(piece: Piece) {
        piece.highlight = false
        this.freezeHighlights = false
        this.selectedPiece = null
        for (square in piece.movement) {
            square.highlight = false
        }
    }

    fun updateValidMoves() {
        for (piece in this.pieces) {
            piece.getValidMoves()
        }
    }

}
