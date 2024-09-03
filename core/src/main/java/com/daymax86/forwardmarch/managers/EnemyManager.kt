package com.daymax86.forwardmarch.managers

import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.board_objects.pieces.enemies.EnemyPawn
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.board_objects.pieces.enemies.EnemyKnight
import com.daymax86.forwardmarch.board_objects.pieces.enemies.EnemyRook
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
                pawnToAdd.getValidMoves()
                enemyPieces.add(pawnToAdd)
            }

            PieceTypes.KING -> TODO()
            PieceTypes.QUEEN -> TODO()

            PieceTypes.ROOK -> {
                val rookToAdd = EnemyRook()
                rookToAdd.associatedBoard = board
                rookToAdd.boardXpos = x
                rookToAdd.boardYpos = y
                rookToAdd.move(x, y, board)
                enemyPieces.add(rookToAdd)
            }

            PieceTypes.KNIGHT -> {
                val knightToAdd = EnemyKnight()
                knightToAdd.associatedBoard = board
                knightToAdd.boardXpos = x
                knightToAdd.boardYpos = y
                knightToAdd.move(x, y, board)
                enemyPieces.add(knightToAdd)
            }

            PieceTypes.BISHOP -> TODO()
            PieceTypes.PRINCE -> TODO()
            PieceTypes.MONK -> TODO()
            PieceTypes.VILLEIN -> TODO()
            PieceTypes.BARON -> TODO()
            PieceTypes.BARONESS -> TODO()
        }
    }

    fun spawnTrap(type: TrapTypes, x: Int, y: Int, board: Board) {
        when (type) {
            TrapTypes.SPIKE -> {
                val spikesToAdd = SpikeTrap()
                spikesToAdd.associatedBoard = board
                spikesToAdd.boardXpos = x
                spikesToAdd.boardYpos = y
                spikesToAdd.move(x, y, board)
                traps.add(spikesToAdd)
            }
        }
    }

}
