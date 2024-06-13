package com.daymax86.forwardmarch

import com.daymax86.forwardmarch.board_objects.pieces.EnemyPawn
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.board_objects.traps.SpikeTrap
import com.daymax86.forwardmarch.board_objects.traps.TrapTypes

object EnemyManager {

    val enemyPieces: MutableList<Piece> = mutableListOf()
    val traps: MutableList<BoardObject> = mutableListOf()

    fun spawnEnemy(type: PieceTypes, x: Int, y: Int, board: Board) {
        when (type) {
            PieceTypes.PAWN -> {
                val pawnToAdd = EnemyPawn()
                pawnToAdd.associatedBoard = board
                pawnToAdd.boardXpos = x
                pawnToAdd.boardYpos = y
                pawnToAdd.move(x, y, board)
                enemyPieces.add(pawnToAdd)
            }

            PieceTypes.KING -> TODO()
            PieceTypes.QUEEN -> TODO()
            PieceTypes.ROOK -> TODO()
            PieceTypes.KNIGHT -> TODO()
            PieceTypes.BISHOP -> TODO()
        }
    }

    fun spawnTrap(type: TrapTypes, x: Int, y: Int, board: Board) {
        when (type) {
            TrapTypes.SPIKE -> {
                val spikesToAdd = SpikeTrap()
                spikesToAdd.associatedBoard = board
                spikesToAdd.boardXpos = x
                spikesToAdd.boardYpos = y
                spikesToAdd.move(x, y, null)
                traps.add(spikesToAdd)
            }
        }
    }

}
