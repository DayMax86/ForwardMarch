package com.daymax86.forwardmarch

import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.pieces.BlackPawn
import com.daymax86.forwardmarch.pieces.Piece

class GameLogic(
    squareWidth: Float,
    squareHeight: Float,
    edgeBuffer: Float
) {

    val pieces = Array<Piece>()
    val boards = Array<Board>()
    private val boardDimensions: Int = 8
    var selectedPiece: Piece? = null

    init {
        val testBoard = StandardBoard(
            dimensions = boardDimensions,
            environmentXPos = edgeBuffer.toInt(),
            environmentYPos = 0,
            squareWidth = squareWidth.toInt(),
            associatedGame = this,
        )
        testBoard.onScreen = true
        val testBoard2 = StandardBoard(
            dimensions = boardDimensions,
            environmentXPos = edgeBuffer.toInt(),
            environmentYPos = (squareHeight * boardDimensions).toInt(),
            squareWidth = squareWidth.toInt(),
            associatedGame = this,
        )
        testBoard2.onScreen = true
        this.boards.add(testBoard, testBoard2)

        val testPawn = BlackPawn(
            associatedGame = this
        )
        testPawn.associatedBoard = this.boards[0]
        testPawn.nextBoard = this.boards[1]
        testPawn.move(3,3)
        this.pieces.add(testPawn)
        val testPawn2 = BlackPawn(
            associatedGame = this
        )
        testPawn2.associatedBoard = this.boards[0]
        testPawn2.nextBoard = this.boards[1]
        testPawn2.move(5,8)
        this.pieces.add(testPawn2)

    }

    fun updateValidMoves() {
        for (piece in this.pieces) {
            piece.getValidMoves()
        }
    }

}
