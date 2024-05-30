package com.daymax86.forwardmarch.board_objects.pieces.defaults

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.Square
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import kotlin.math.abs

open class RookDefault(
    override var image: Texture = Texture(Gdx.files.internal("black_rook_1000.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("black_rook_1000_highlighted.png")),
    override var highlight: Boolean = false,
    override var boardXpos: Int = -1,
    override var boardYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var pieceType: PieceTypes = PieceTypes.ROOK,
    override var friendly: Boolean = true,
    override val movement: MutableList<Square> = mutableListOf(),
    override var associatedBoard: Board? = null,
    override var nextBoard: Board? = null,
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
) {

    open var range: Int = 4 // Set a default value for friendly rook's movement
    // Can be overridden by individual pieces

    override fun getValidMoves(): Boolean {
        if (this.associatedBoard != null) { // No need to check if piece is not on a board
            // and this allows for safe !! usage
            this.movement.clear() // Reset movement array
            /* Use piece's XY positions on the board

                    * ----------- ROOK --
                    * ------0------------
                    * ------0------------
                    * ------0------------
                    * 0-0-0-X-0-0-0------
                    * ------0------------
                    * ------0------------
                    */

            // Start from piece's location and go outwards in the cardinal directions,
            // this way we can stop in that direction if their path is blocked

            var leftChecked = false
            var rightChecked = false
            var upChecked = false
            var downChecked = false

            // LEFT
            val leftLimit = if (this.boardXpos < range) 0 else (this.boardXpos - range)
            for (leftIndex in this.boardXpos - 1 downTo leftLimit) {
                if (!leftChecked) {
                    // For each xPos to the left of the piece, check the square for content,
                    // if square is empty, add to the movement array,
                    // otherwise stop looking in this direction
                    this.associatedBoard!!.squaresList.first { square ->
                        square.boardXpos - 1 == leftIndex && square.boardYpos == this.boardYpos // -1 to not include piece's square!
                    }.let {
                        if (it.contents.isEmpty()) {
                            this.movement.add(it)
                        } else {
                            leftChecked = true
                        }
                    }
                }
            }

            // RIGHT
            val rightLimit =
                if (this.boardXpos + 1 > range) GameManager.DIMENSIONS + 1 else (this.boardXpos + range)
            for (rightIndex in this.boardXpos + 1..rightLimit) {
                if (!rightChecked) {
                    // For each xPos to the right of the piece, check the square for content,
                    // if square is empty, add to the movement array,
                    // otherwise stop looking in this direction
                    this.associatedBoard!!.squaresList.first { square ->
                        square.boardXpos == rightIndex && square.boardYpos == this.boardYpos // +1 to not include piece's square!
                    }.let {
                        if (it.contents.isEmpty()) {
                            this.movement.add(it)
                        } else {
                            rightChecked = true
                        }
                    }
                }
            }

            // UP
            // Manage movement across boards
            var board: Board
            if (this.nextBoard != null) {
                for (upIndex in this.boardYpos + 1..this.boardYpos + range) {
                    board =
                        if (boardYpos - GameManager.DIMENSIONS < 1) this.associatedBoard!! else this.nextBoard!!
                    board.squaresList.first { square ->
                        square.boardXpos == this.boardXpos && (abs(square.boardYpos - GameManager.DIMENSIONS) == upIndex)
                    }.let {
                        if (it.contents.isEmpty()) {
                            this.movement.add(it)
                        } else {
                            upChecked = true
                        }
                    }
                }
            }

        }



        return this.movement.isNotEmpty() // No valid moves if array is empty
    }

}
