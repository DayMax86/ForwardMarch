package com.daymax86.forwardmarch.pieces

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.Square
import ktx.collections.isNotEmpty
import ktx.collections.minus

open class PawnDefault( // TODO() Provide placeholder image for default pieces
    override var image: Texture = Texture(Gdx.files.internal("black_pawn_1000.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("black_pawn_1000_highlighted.png")),
    override var highlight: Boolean = false,
    override var boardXpos: Int = -1,
    override var boardYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var pieceType: PieceTypes = PieceTypes.PAWN,
    override var friendly: Boolean = true,
    override var movement: Array<Square> = Array<Square>()
) : Piece {

    override fun getValidMoves(boards: Array<Board>): Boolean {
        // TODO() Allow for first-move rule where pawn can move 2 spaces forward. En passant too?
        this.movement.clear() // Reset movement array
        /* Use piece's XY positions on the board
        * ----------- PAWN --
        * ----?-0-?----------
        * ----0-X-0----------
        * -------------------
        */
        // TODO() Check if the filter method works when piece is at the very edge of a board
        // Determine if the movement will straddle across 2 (or more?) boards
        // For a pawn this will only be the case if it's in the 8th row
        if (this.boardXpos == 8) {
            // Pawn must be at the very top of one board
            // Left and right as normal
            // LEFT
            this.movement.add(boards[0].squaresArray.first {
                it.boardXpos == this.boardXpos - 1 && it.boardYpos == this.boardYpos
            })
            // RIGHT
            this.movement.add(boards[0].squaresArray.first {
                it.boardXpos == this.boardXpos + 1 && it.boardYpos == this.boardYpos
            })
            // UP
            this.movement.add(boards[1].squaresArray.first {
                it.boardXpos == this.boardXpos && it.boardYpos == 1
            })
            // DIAGONALS
            val upLeftDiagonal: Square? = boards[1].squaresArray.first {
                it.boardXpos == this.boardXpos - 1 && it.boardYpos == 1
            }
            val upRightDiagonal: Square? = boards[1].squaresArray.first {
                it.boardXpos == this.boardXpos + 1 && it.boardYpos == 1
            }
            // TODO() This .first() works only if a square contains one thing only!!
            if (upLeftDiagonal != null) {
                if (upLeftDiagonal.contents.first().hostile) { // There's a hostile piece
                    this.movement.add(upLeftDiagonal)
                }
            }
            if (upRightDiagonal != null) {
                if (upRightDiagonal.contents.first().hostile) { // There's a hostile piece
                    this.movement.add(upRightDiagonal)
                }
            }

        } else { // Must be contained within one board
            // First check for diagonal spaces to see if there are any hostile pieces
            val upLeftDiagonal: Square? = boards[0].squaresArray.first {
                it.boardXpos == this.boardXpos - 1 && it.boardYpos == this.boardYpos + 1
            }
            val upRightDiagonal: Square? = boards[0].squaresArray.first {
                it.boardXpos == this.boardXpos + 1 && it.boardYpos == this.boardYpos + 1
            }
            if (upLeftDiagonal != null) {
                if (upLeftDiagonal.contents.first().hostile) { // There's a hostile piece
                    this.movement.add(upLeftDiagonal)
                }
            }
            if (upRightDiagonal != null) {
                if (upRightDiagonal.contents.first().hostile) { // There's a hostile piece
                    this.movement.add(upRightDiagonal)
                }
            }

            // Add normal non-diagonal squares
            // LEFT
            this.movement.add(boards[0].squaresArray.first {
                it.boardXpos == this.boardXpos - 1 && it.boardYpos == this.boardYpos
            })
            // RIGHT
            this.movement.add(boards[0].squaresArray.first {
                it.boardXpos == this.boardXpos + 1 && it.boardYpos == this.boardYpos
            })
            // UP
            this.movement.add(boards[0].squaresArray.first {
                it.boardXpos == this.boardXpos && it.boardYpos == this.boardYpos + 1
            })
        }

        // Remove any occupied squares
        val toRemoveArray= Array<Square>()
        for (sq in movement) {
            if (sq.contents.isNotEmpty())
                toRemoveArray.add(sq)
        }
        for (trsq in toRemoveArray) {
            this.movement.minus(trsq)
        }
        return !this.movement.isEmpty // No valid moves if array is empty (if empty = true, valid = false)
    }
}
