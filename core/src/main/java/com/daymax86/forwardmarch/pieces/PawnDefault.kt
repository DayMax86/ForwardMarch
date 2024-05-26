package com.daymax86.forwardmarch.pieces

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.GameLogic
import com.daymax86.forwardmarch.Square
import com.daymax86.forwardmarch.inputTypes
import ktx.collections.contains
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
    override var movement: Array<Square> = Array<Square>(),
    override var associatedBoard: Board? = null,
    override var nextBoard: Board? = null,
    override var associatedGame: GameLogic
) : Piece(
    image = image,
    highlightedImage = highlightedImage,
    highlight = highlight,
    boardXpos = boardXpos,
    boardYpos = boardYpos,
    clickable = clickable,
    hostile = hostile,
    boundingBox = boundingBox,
    associatedBoard = associatedBoard,
    associatedGame = associatedGame,
) {

    override fun getValidMoves(): Boolean { // TODO() This needs to be optimised!
        // TODO() Allow for first-move rule where pawn can move 2 spaces forward. En passant too?
        if (this.associatedBoard != null) { // No need to check if piece is not on a board
            // and this allows for safe !! usage
            this.movement.clear() // Reset movement array
            /* Use piece's XY positions on the board
                    * ----------- PAWN --
                    * ----?-0-?----------
                    * ----0-X-0----------
                    * -------------------
                    */
            // Determine if the movement will straddle across 2 (or more?) boards
            // For a pawn this will only be the case if it's in the 8th row
            if (this.boardYpos == 8) {
                // Pawn must be at the very top of one board
                // Left and right as normal
                // LEFT
                this.movement.add(this.associatedBoard!!.squaresArray.firstOrNull {
                    it.boardXpos == this.boardXpos - 1 && it.boardYpos == this.boardYpos
                })
                // RIGHT
                this.movement.add(this.associatedBoard!!.squaresArray.firstOrNull {
                    it.boardXpos == this.boardXpos + 1 && it.boardYpos == this.boardYpos
                })
                // UP
                this.movement.add(this.nextBoard!!.squaresArray.firstOrNull {
                    it.boardXpos == this.boardXpos && it.boardYpos == 1
                })
                var upLeftDiagonal: Square? = null
                var upRightDiagonal: Square? = null
                if (this.nextBoard != null) {
                    // DIAGONALS
                    upLeftDiagonal = this.nextBoard!!.squaresArray.firstOrNull {
                        it.boardXpos == this.boardXpos - 1 && it.boardYpos == 1
                    }
                    upRightDiagonal = this.nextBoard!!.squaresArray.firstOrNull {
                        it.boardXpos == this.boardXpos + 1 && it.boardYpos == 1
                    }
                }
                // TODO() This .first() works only if a square contains one thing only!!
                if (upLeftDiagonal != null) {
                    if (upLeftDiagonal.contents.isNotEmpty()) {
                        if (upLeftDiagonal.contents.first().hostile) { // There's a hostile piece
                            this.movement.add(upLeftDiagonal)
                        }
                    }
                }
                if (upRightDiagonal != null) {
                    if (upRightDiagonal.contents.isNotEmpty()) {
                        if (upRightDiagonal.contents.first().hostile) { // There's a hostile piece
                            this.movement.add(upRightDiagonal)
                        }
                    }
                }

            } else { // Must be contained within one board
                // First check for diagonal spaces to see if there are any hostile pieces
                val upLeftDiagonal: Square? = this.associatedBoard!!.squaresArray.firstOrNull {
                    it.boardXpos == this.boardXpos - 1 && it.boardYpos == this.boardYpos + 1
                }
                val upRightDiagonal: Square? = this.associatedBoard!!.squaresArray.firstOrNull {
                    it.boardXpos == this.boardXpos + 1 && it.boardYpos == this.boardYpos + 1
                }
                if (upLeftDiagonal != null) {
                    if (upLeftDiagonal.contents.isNotEmpty()) {
                        if (upLeftDiagonal.contents.firstOrNull()!!.hostile) { // There's a hostile piece
                            this.movement.add(upLeftDiagonal)
                        }
                    }
                }
                if (upRightDiagonal != null) {
                    if (upRightDiagonal.contents.isNotEmpty()) {
                        if (upRightDiagonal.contents.firstOrNull()!!.hostile) { // There's a hostile piece
                            this.movement.add(upRightDiagonal)
                        }
                    }
                }

                // Add normal non-diagonal squares
                // LEFT
                this.movement.add(this.associatedBoard!!.squaresArray.firstOrNull() {
                    it.boardXpos == this.boardXpos - 1 && it.boardYpos == this.boardYpos
                })
                // RIGHT
                this.movement.add(this.associatedBoard!!.squaresArray.firstOrNull {
                    it.boardXpos == this.boardXpos + 1 && it.boardYpos == this.boardYpos
                })
                // UP
                this.movement.add(this.associatedBoard!!.squaresArray.firstOrNull {
                    it.boardXpos == this.boardXpos && it.boardYpos == this.boardYpos + 1
                })
            }

            // Remove any occupied squares
            val toRemoveArray = Array<Square>()
            for (sq in movement) {
                if (sq != null) {
                    if (sq.contents.isNotEmpty())
                        toRemoveArray.add(sq)
                }
            }
            for (trsq in toRemoveArray) {
                this.movement.minus(trsq)
            }
        }
        return !this.movement.isEmpty // No valid moves if array is empty (if empty = true, valid = false)
    }
}
