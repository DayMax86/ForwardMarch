package com.daymax86.forwardmarch.managers

import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.board_objects.pieces.defaults.BaronDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.BaronessDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.BishopDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.KingDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.KnightDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.MonkDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.PawnDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.PrinceDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.QueenDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.RookDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.VilleinDefault
import com.daymax86.forwardmarch.managers.GameManager.currentShop
import com.daymax86.forwardmarch.managers.GameManager.firstMoveComplete
import com.daymax86.forwardmarch.managers.GameManager.freezeHighlights
import com.daymax86.forwardmarch.managers.GameManager.moveLimitReached
import com.daymax86.forwardmarch.managers.GameManager.updateValidMoves

object PieceManager {
    val pieces: MutableList<Piece> = mutableListOf()
    var selectedPiece: Piece? = null

    fun selectPiece(piece: Piece, shopPurchase: Boolean) {
        if (!shopPurchase) {
            firstMoveComplete = true
            if (!piece.hostile && !moveLimitReached) {
                selectedPiece = piece
                piece.highlight = true
                updateValidMoves()
                for (square in StageManager.stage.squaresList) {
                    if (piece.movement.contains(square)) {
                        square.swapToAltHighlight(true)
                        square.highlight = true
                    }
                }
                freezeHighlights = true
            }
        } else {
            // Must've been bought in the shop
            selectedPiece = piece
            piece.highlight = true
            // TODO() Player must place their new piece behind or in line with the king
            for (square in StageManager.stage.squaresList) {
                square.swapToAltHighlight(true)
                square.highlight = true
                selectedPiece!!.movement.add(square)
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
            if (currentShop != null) {
                currentShop!!.shopItems.filter { p ->
                    p == selectedPiece
                }.let {
                    if (it.isNotEmpty()) {
                        currentShop!!.exitShop()
                    }
                }
            }
            selectedPiece = null
        }
    }


    fun spawnPiece(type: PieceTypes, x: Int, y: Int) {
        when (type) {
            PieceTypes.PAWN -> {
                val pawnToAdd = PawnDefault()
                pawnToAdd.stageXpos = x
                pawnToAdd.stageYpos = y
                pawnToAdd.move(x, y)
                pieces.add(pawnToAdd)
            }

            PieceTypes.KING -> {
                val kingToAdd = KingDefault()
                kingToAdd.stageXpos = x
                kingToAdd.stageYpos = y
                kingToAdd.move(x, y)
                pieces.add(kingToAdd)
            }

            PieceTypes.QUEEN -> {
                val queenToAdd = QueenDefault()
                queenToAdd.stageXpos = x
                queenToAdd.stageYpos = y
                queenToAdd.move(x, y)
                pieces.add(queenToAdd)
            }

            PieceTypes.ROOK -> {
                val rookToAdd = RookDefault()
                rookToAdd.stageXpos = x
                rookToAdd.stageYpos = y
                rookToAdd.move(x, y)
                pieces.add(rookToAdd)
            }

            PieceTypes.KNIGHT -> {
                val knightToAdd = KnightDefault()
                knightToAdd.stageXpos = x
                knightToAdd.stageYpos = y
                knightToAdd.move(x, y)
                pieces.add(knightToAdd)
            }

            PieceTypes.BISHOP -> {
                val bishopToAdd = BishopDefault()
                bishopToAdd.stageXpos = x
                bishopToAdd.stageYpos = y
                bishopToAdd.move(x, y)
                pieces.add(bishopToAdd)
            }

            PieceTypes.PRINCE -> {
                val princeToAdd = PrinceDefault()
                princeToAdd.stageXpos = x
                princeToAdd.stageYpos = y
                princeToAdd.move(x, y)
                pieces.add(princeToAdd)
            }

            PieceTypes.MONK -> {
                val monkToAdd = MonkDefault()
                monkToAdd.stageXpos = x
                monkToAdd.stageYpos = y
                monkToAdd.move(x, y)
                pieces.add(monkToAdd)
            }

            PieceTypes.VILLEIN -> {
                val villeinToAdd = VilleinDefault()
                villeinToAdd.stageXpos = x
                villeinToAdd.stageYpos = y
                villeinToAdd.move(x, y)
                pieces.add(villeinToAdd)
            }

            PieceTypes.BARON -> {
                val baronToAdd = BaronDefault()
                baronToAdd.stageXpos = x
                baronToAdd.stageYpos = y
                baronToAdd.move(x, y)
                pieces.add(baronToAdd)
            }

            PieceTypes.BARONESS -> {
                val baronessToAdd = BaronessDefault()
                baronessToAdd.stageXpos = x
                baronessToAdd.stageYpos = y
                baronessToAdd.move(x, y)
                pieces.add(baronessToAdd)
            }
        }
    }

    // ------------------------------SETUP PLACEMENT--------------------------------------------- //

    fun setStartingLayout() {
        spawnPiece(PieceTypes.QUEEN, 4, 1)
        spawnPiece(PieceTypes.KING, 5, 1)
        spawnPiece(PieceTypes.ROOK, 1, 1)
        spawnPiece(PieceTypes.ROOK, 8, 1)
        spawnPiece(PieceTypes.BISHOP, 3, 1)
        spawnPiece(PieceTypes.BISHOP, 6, 1)
        spawnPiece(PieceTypes.KNIGHT, 2, 1)
        spawnPiece(PieceTypes.KNIGHT, 7, 1)
        for (x in 1..8) {
            spawnPiece(PieceTypes.PAWN, x, 2)
        }
        pieces.forEach { piece ->
            StageManager.stage.squaresList.firstOrNull { square ->
                square.stageXpos == piece.stageXpos &&
                    square.stageYpos == piece.stageYpos
            }.let { s ->
                if (s != null) {
                    piece.move(s.stageXpos, s.stageYpos)
                }
            }
        }
    }


}
