package com.daymax86.forwardmarch

import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.board_objects.pieces.BlackPawn
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.defaults.RookDefault
import com.daymax86.forwardmarch.board_objects.traps.SpikeTrap

object GameManager {

    const val ENVIRONMENT_WIDTH = 2000f
    const val ENVIRONMENT_HEIGHT = 3000f
    const val SQUARE_WIDTH = 120f
    const val SQUARE_HEIGHT = 120f
    private const val EDGE_BUFFER: Float = (ENVIRONMENT_WIDTH / 20)
    const val DIMENSIONS: Int = 8
    // Collections
    val pieces: MutableList<Piece> = mutableListOf()
    val boards: MutableList<Board> = mutableListOf()
    val traps: MutableList<BoardObject> = mutableListOf()

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
        val testBoard2 = StandardBoard(
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
            it.move (3, 4, null)
        }. apply {
            pieces.add(this)
        }

        val testPawn2 = BlackPawn()
        testPawn2.associatedBoard = boards[0]
        testPawn2.nextBoard = boards[1]
        testPawn2.move(5, 8, null)
        pieces.add(testPawn2)

        val testTrap = SpikeTrap()
        testTrap.associatedBoard = boards[0]
        testTrap.move(5, 4, null)
        traps.add(testTrap)

    }

    fun forwardMarch(distance: Int) {
        // Move all pieces up by one square
        pieces.forEach { piece ->
            if (piece.boardYpos + distance > 8) {
                // Must be moving onto the next board
                val boardIndex = boards.indexOf(piece.associatedBoard)
                piece.move(piece.boardXpos, (piece.boardYpos + distance - 8), boards[boardIndex + 1])
            } else { // Movement contained within one board
                piece.move(piece.boardXpos, piece.boardYpos + distance, null)
            }
        }

        // Move camera up accordingly (a smooth movement ideally to show what's happened)
    }

    fun selectPiece(piece: Piece) {
        selectedPiece = piece
        piece.highlight = true
        piece.getValidMoves()
        for (board in boards) { // TODO Iterating through nested array may be too intensive
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
            selectedPiece = null
        }
    }

    fun updateValidMoves() {
        for (piece in this.pieces) {
            piece.getValidMoves()
        }
    }

}
