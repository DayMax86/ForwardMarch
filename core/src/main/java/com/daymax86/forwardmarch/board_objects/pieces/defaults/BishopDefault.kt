package com.daymax86.forwardmarch.board_objects.pieces.defaults

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.GameManager.DIMENSIONS
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import kotlin.math.abs

open class BishopDefault(
    override var image: Texture = Texture(Gdx.files.internal("sprites/black_bishop_256.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/black_bishop_256_highlighted.png")),
    override var highlight: Boolean = false,
    override var boardXpos: Int = -1,
    override var boardYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var pieceType: PieceTypes = PieceTypes.ROOK,
    override val movement: MutableList<Square> = mutableListOf(),
    override var associatedBoard: Board? = null,
    override var nextBoard: Board? = null,
    override var deathAnimation: SpriteAnimation = SpriteAnimation(
        atlasFilepath = "atlases/black_pawn_death_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = false,
    ),
    override var idleAnimation: SpriteAnimation? = SpriteAnimation(
        atlasFilepath = "atlases/coin_spin_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = true,
    ),
    override var visuallyStatic: Boolean = false,
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

    init {
        this.soundSet.move.add(Gdx.audio.newSound(Gdx.files.internal("sound/effects/move_default.ogg")))
        this.soundSet.death.add(Gdx.audio.newSound(Gdx.files.internal("sound/effects/death_default.ogg")))
    }

    open var range: Int = 4 // Set a default value for friendly bishop's movement

    override fun getValidMoves(onComplete: () -> Unit): Boolean {
        if (this.associatedBoard != null) { // No need to check if piece is not on a board
            // and this allows for safe !! usage
            this.movement.clear() // Reset movement array
            /* Use piece's XY positions on the board

                    * --------- BISHOP --
                    * ------0-----0------
                    * -------0---0-------
                    * --------0-0--------
                    * ---------X---------
                    * --------0-0--------
                    * -------0---0-------
                    * ------0-----0------
                    */

            // Start from piece's location and go outwards in the diagonals directions,
            // this way we can stop in that direction if their path is blocked

            var upLeftChecked = false
            var upRightChecked = false
            var downLeftChecked = false
            var downRightChecked = false

            var yPos = this.boardYpos
            var xPos = this.boardXpos
            var b = this.associatedBoard

            // UP-LEFT
            for (ulIndex in 1..range) { // Check n times, where n is the piece's movement range
                if (!upLeftChecked) {
                    b = this.associatedBoard
                    yPos = this.boardYpos + ulIndex
                    if (yPos > DIMENSIONS) {
                        yPos = this.boardYpos + ulIndex - DIMENSIONS
                        b = this.nextBoard
                    }

                    xPos = this.boardXpos - ulIndex
                    if (xPos < 1) {
                        upLeftChecked = true
                        continue
                    }

                    // Get the square at the correct coordinates
                    b?.squaresList?.first { square ->
                        square.boardXpos == xPos && square.boardYpos == yPos
                    }?.let {
                        if (it.contents.isEmpty() && !this.movement.contains(it)) {
                            this.movement.add(it)
                        } else {
                            upLeftChecked = true
                        }
                    }
                }
            }

            // UP-RIGHT
            for (urIndex in 1..range) { // Check n times, where n is the piece's movement range
                if (!upRightChecked) {
                    b = this.associatedBoard
                    yPos = this.boardYpos + urIndex
                    if (yPos > DIMENSIONS) {
                        yPos = this.boardYpos + urIndex - DIMENSIONS
                        b = this.nextBoard
                    }

                    xPos = this.boardXpos + urIndex
                    if (xPos > DIMENSIONS) {
                        upRightChecked = true
                        continue
                    }

                    // Get the square at the correct coordinates
                    b?.squaresList?.first { square ->
                        square.boardXpos == xPos && square.boardYpos == yPos
                    }?.let {
                        if (it.contents.isEmpty() && !this.movement.contains(it)) {
                            this.movement.add(it)
                        } else {
                            upRightChecked = true
                        }
                    }
                }
            }

            // DOWN-LEFT
            for (dlIndex in 1..range) { // Check n times, where n is the piece's movement range
                if (!downLeftChecked) {
                    b = this.associatedBoard
                    yPos = this.boardYpos - dlIndex
                    if (yPos < 1) {
                        yPos = DIMENSIONS - abs(yPos)
                        b = GameManager.boards[0] // Boards offscreen will be removed from stack
                    }

                    xPos = this.boardXpos - dlIndex
                    if (xPos < 1) {
                        downLeftChecked = true
                        continue
                    }

                    // Get the square at the correct coordinates
                    b?.squaresList?.first { square ->
                        square.boardXpos == xPos && square.boardYpos == yPos
                    }?.let {
                        if (it.contents.isEmpty() && !this.movement.contains(it)) {
                            this.movement.add(it)
                        } else {
                            upLeftChecked = true
                        }
                    }
                }

            }

            // DOWN-RIGHT
            for (drIndex in 1..range) { // Check n times, where n is the piece's movement range
                if (!downRightChecked) {
                    b = this.associatedBoard
                    yPos = this.boardYpos - drIndex
                    if (yPos < 1) {
                        yPos = DIMENSIONS - abs(yPos)
                        b = GameManager.boards[0] // Boards offscreen will be removed from stack
                    }

                    xPos = this.boardXpos + drIndex
                    if (xPos > DIMENSIONS) {
                        downRightChecked = true
                        continue
                    }

                    // Get the square at the correct coordinates
                    b?.squaresList?.first { square ->
                        square.boardXpos == xPos && square.boardYpos == yPos
                    }?.let {
                        if (it.contents.isEmpty() && !this.movement.contains(it)) {
                            this.movement.add(it)
                        } else {
                            upLeftChecked = true
                        }
                    }
                }


            }
        }
        onComplete.invoke()
        return this.movement.isNotEmpty() // No valid moves if array is empty
    }
}
