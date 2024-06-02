package com.daymax86.forwardmarch.board_objects.pieces.defaults

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.Square
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import java.lang.Math.clamp
import kotlin.math.abs

open class RookDefault(
    override var image: Texture = Texture(Gdx.files.internal("sprites/black_rook_256.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/black_rook_256_highlighted.png")),
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
    override var deathAnimation: SpriteAnimation = SpriteAnimation(
        atlasFilepath = "atlases/black_pawn_death_animation.atlas",
        frameDuration = 0.1f,
        loop = false,
    )
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
            val leftLimit =
                if (this.boardXpos < range) 1 else (this.boardXpos + 1 - range)
            for (leftIndex in this.boardXpos - 1 downTo leftLimit) {
                if (!leftChecked) {
                    // For each xPos to the left of the piece, check the square for content,
                    // if square is empty, add to the movement array,
                    // otherwise stop looking in this direction
                    this.associatedBoard!!.squaresList.first { square ->
                        square.boardXpos == leftIndex && square.boardYpos == this.boardYpos
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
                if (this.boardXpos + range > GameManager.DIMENSIONS) GameManager.DIMENSIONS else (this.boardXpos + range)
            for (rightIndex in this.boardXpos + 1..rightLimit) {
                if (!rightChecked) {
                    // For each xPos to the right of the piece, check the square for content,
                    // if square is empty, add to the movement array,
                    // otherwise stop looking in this direction
                    this.associatedBoard!!.squaresList.first { square ->
                        square.boardXpos == rightIndex && square.boardYpos == this.boardYpos
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
            var boardUp: Board
            var acrossBoardsUp = false
            if (this.nextBoard != null) {
                for (upIndex in this.boardYpos + 1..this.boardYpos + range) {
                    if (!upChecked) {
                        // For i in range ( pieceY +1 <= pieceY + 4)
                        boardUp =
                            if (upIndex - GameManager.DIMENSIONS < 1) this.associatedBoard!! else this.nextBoard!!.also {
                                acrossBoardsUp = true
                            }
                        boardUp.squaresList.first { square ->
                            // Find the square that is pieceY + 1 in y-axis, and no change in x-axis
                            square.boardXpos == this.boardXpos && square.boardYpos == if (acrossBoardsUp) abs(
                                upIndex - GameManager.DIMENSIONS
                            ) else upIndex
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

            // DOWN
            // Manage movement across boards
            var boardDown: Board
            // Search GameManager Boards collection to see if there is a board below to be able to move to
            val boardBelow: Boolean = GameManager.boards.indexOf(this.associatedBoard) > 0
            var downLimit: Int
            if (boardBelow && this.boardYpos - range < 1) {
                // Split across two boards
                downLimit = 1
                for (downIndex1 in this.boardYpos - 1 downTo downLimit) {
                    if (!downChecked) {
                        // Movement on upper board
                        boardDown = this.associatedBoard!!
                        boardDown.squaresList.first { square ->
                            square.boardXpos == this.boardXpos && square.boardYpos == downIndex1
                        }.let {
                            if (it.contents.isEmpty()) {
                                this.movement.add(it)
                            } else {
                                downChecked = true
                            }
                        }
                    }
                }
                downLimit = GameManager.DIMENSIONS - abs(this.boardYpos - range)
                for (downIndex2 in GameManager.DIMENSIONS downTo downLimit)
                    if (!downChecked) {
                        boardDown =
                            GameManager.boards[0] // Boards offscreen will be removed from stack
                        boardDown.squaresList.first { square ->
                            square.boardXpos == this.boardXpos && square.boardYpos == downIndex2
                        }.let {
                            if (it.contents.isEmpty()) {
                                this.movement.add(it)
                            } else {
                                downChecked = true
                            }
                        }
                    }
            } else {
                // Must be contained within one board
                downLimit = MathUtils.clamp(this.boardYpos - range, 1, 8)
                for (downIndex3 in this.boardYpos - 1 downTo downLimit) {
                    if (!downChecked) {
                        // Movement on upper board
                        boardDown = this.associatedBoard!!
                        boardDown.squaresList.first { square ->
                            square.boardXpos == this.boardXpos && square.boardYpos == downIndex3
                        }.let {
                            if (it.contents.isEmpty()) {
                                this.movement.add(it)
                            } else {
                                downChecked = true
                            }
                        }
                    }
                }
            }

        }
        return this.movement.isNotEmpty() // No valid moves if array is empty
    }
}
