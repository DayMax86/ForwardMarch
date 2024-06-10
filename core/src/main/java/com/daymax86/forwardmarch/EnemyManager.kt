package com.daymax86.forwardmarch

import com.daymax86.forwardmarch.board_objects.pieces.EnemyPawn
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes

object EnemyManager {

    val enemyPieces: MutableList<Piece> = mutableListOf()

    fun spawnEnemy(type: PieceTypes, x: Int, y: Int, board: Board) {
        when (type) {
            PieceTypes.PAWN -> {
                val pawnToAdd = EnemyPawn()
                pawnToAdd.associatedBoard = board
                pawnToAdd.boardXpos = x
                pawnToAdd.boardYpos = y
                pawnToAdd.move(x, y, null)
                enemyPieces.add(pawnToAdd)
            }

            PieceTypes.KING -> TODO()
            PieceTypes.QUEEN -> TODO()
            PieceTypes.ROOK -> TODO()
            PieceTypes.KNIGHT -> TODO()
            PieceTypes.BISHOP -> TODO()
        }
    }

}
