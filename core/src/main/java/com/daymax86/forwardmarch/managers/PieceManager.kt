package com.daymax86.forwardmarch.managers

import com.daymax86.forwardmarch.Board
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
import com.daymax86.forwardmarch.managers.BoardManager.boards
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
        } else {
            // Must've been bought in the shop
            selectedPiece = piece
            piece.highlight = true
            // TODO() Player must place their new piece behind or in line with the king
            for (board in boards) {
                for (square in board.squaresList) {
                    square.swapToAltHighlight(true)
                    square.highlight = true
                    selectedPiece!!.movement.add(square)
                }
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


    fun spawnPiece(type: PieceTypes, x: Int, y: Int, board: Board) {
        when (type) {
            PieceTypes.PAWN -> {
                val pawnToAdd = PawnDefault()
                pawnToAdd.associatedBoard = board
                pawnToAdd.boardXpos = x
                pawnToAdd.boardYpos = y
                pawnToAdd.move(x, y, board)
                pieces.add(pawnToAdd)
            }

            PieceTypes.KING -> {
                val kingToAdd = KingDefault()
                kingToAdd.associatedBoard = board
                kingToAdd.boardXpos = x
                kingToAdd.boardYpos = y
                kingToAdd.move(x, y, board)
                pieces.add(kingToAdd)
            }

            PieceTypes.QUEEN -> {
                val queenToAdd = QueenDefault()
                queenToAdd.associatedBoard = board
                queenToAdd.boardXpos = x
                queenToAdd.boardYpos = y
                queenToAdd.move(x, y, board)
                pieces.add(queenToAdd)
            }

            PieceTypes.ROOK -> {
                val rookToAdd = RookDefault()
                rookToAdd.associatedBoard = board
                rookToAdd.boardXpos = x
                rookToAdd.boardYpos = y
                rookToAdd.move(x, y, board)
                pieces.add(rookToAdd)
            }

            PieceTypes.KNIGHT -> {
                val knightToAdd = KnightDefault()
                knightToAdd.associatedBoard = board
                knightToAdd.boardXpos = x
                knightToAdd.boardYpos = y
                knightToAdd.move(x, y, board)
                pieces.add(knightToAdd)
            }

            PieceTypes.BISHOP -> {
                val bishopToAdd = BishopDefault()
                bishopToAdd.associatedBoard = board
                bishopToAdd.boardXpos = x
                bishopToAdd.boardYpos = y
                bishopToAdd.move(x, y, board)
                pieces.add(bishopToAdd)
            }

            PieceTypes.PRINCE -> {
                val princeToAdd = PrinceDefault()
                princeToAdd.associatedBoard = board
                princeToAdd.boardXpos = x
                princeToAdd.boardYpos = y
                princeToAdd.move(x, y, board)
                pieces.add(princeToAdd)
            }

            PieceTypes.MONK -> {
                val monkToAdd = MonkDefault()
                monkToAdd.associatedBoard = board
                monkToAdd.boardXpos = x
                monkToAdd.boardYpos = y
                monkToAdd.move(x, y, board)
                pieces.add(monkToAdd)
            }

            PieceTypes.VILLEIN -> {
                val villeinToAdd = VilleinDefault()
                villeinToAdd.associatedBoard = board
                villeinToAdd.boardXpos = x
                villeinToAdd.boardYpos = y
                villeinToAdd.move(x, y, board)
                pieces.add(villeinToAdd)
            }

            PieceTypes.BARON -> {
                val baronToAdd = BaronDefault()
                baronToAdd.associatedBoard = board
                baronToAdd.boardXpos = x
                baronToAdd.boardYpos = y
                baronToAdd.move(x, y, board)
                pieces.add(baronToAdd)
            }

            PieceTypes.BARONESS -> {
                val baronessToAdd = BaronessDefault()
                baronessToAdd.associatedBoard = board
                baronessToAdd.boardXpos = x
                baronessToAdd.boardYpos = y
                baronessToAdd.move(x, y, board)
                pieces.add(baronessToAdd)
            }
        }
    }

    // ------------------------------SETUP PLACEMENT--------------------------------------------- //

    fun setStartingLayout() {
        spawnPiece(PieceTypes.QUEEN, 4, 1, boards[0])
        spawnPiece(PieceTypes.KING, 5, 1, boards[0])
        spawnPiece(PieceTypes.ROOK, 1, 1, boards[0])
        spawnPiece(PieceTypes.ROOK, 8, 1, boards[0])
        spawnPiece(PieceTypes.BISHOP, 3, 1, boards[0])
        spawnPiece(PieceTypes.BISHOP, 6, 1, boards[0])
        spawnPiece(PieceTypes.KNIGHT, 2, 1, boards[0])
        spawnPiece(PieceTypes.KNIGHT, 7, 1, boards[0])
        for (x in 1..8) {
            spawnPiece(PieceTypes.PAWN, x, 2, boards[0])
        }
    }


}
