package com.daymax86.forwardmarch.managers

import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.board_objects.pieces.enemies.EnemyKnight
import com.daymax86.forwardmarch.board_objects.pieces.enemies.EnemyPawn
import com.daymax86.forwardmarch.board_objects.pieces.enemies.EnemyRook
import com.daymax86.forwardmarch.board_objects.traps.SpikeTrap
import com.daymax86.forwardmarch.board_objects.traps.TrapTypes

object EnemyManager {

    val enemyPieces: MutableList<Piece> = mutableListOf()
    val traps: MutableList<BoardObject> = mutableListOf()

    fun spawnEnemy(type: PieceTypes, x: Int, y: Int) {
        when (type) {
            PieceTypes.PAWN -> {
                val pawnToAdd = EnemyPawn()
                pawnToAdd.stageXpos = x
                pawnToAdd.stageYpos = y
                pawnToAdd.move(x, y)
                pawnToAdd.getValidMoves()
                enemyPieces.add(pawnToAdd)
            }

            PieceTypes.KING -> TODO()
            PieceTypes.QUEEN -> TODO()

            PieceTypes.ROOK -> {
                val rookToAdd = EnemyRook()
                rookToAdd.stageXpos = x
                rookToAdd.stageYpos = y
                rookToAdd.move(x, y)
                enemyPieces.add(rookToAdd)
            }

            PieceTypes.KNIGHT -> {
                val knightToAdd = EnemyKnight()
                knightToAdd.stageXpos = x
                knightToAdd.stageYpos = y
                knightToAdd.move(x, y)
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

    fun spawnTrap(type: TrapTypes, x: Int, y: Int) {
        when (type) {
            TrapTypes.SPIKE -> {
                val spikesToAdd = SpikeTrap()
                spikesToAdd.stageXpos = x
                spikesToAdd.stageYpos = y
                spikesToAdd.move(x, y)
                traps.add(spikesToAdd)
            }
        }
    }

}
