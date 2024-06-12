package com.daymax86.forwardmarch.board_objects.pieces

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.AudioManager
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.SoundSet
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.animations.SpriteAnimator
import com.daymax86.forwardmarch.board_objects.pickups.Coin
import com.daymax86.forwardmarch.board_objects.traps.Trap
import com.daymax86.forwardmarch.inputTypes

abstract class Piece(
    override var associatedBoard: Board?,
    override var image: Texture,
    override var highlightedImage: Texture,
    override var highlight: Boolean,
    override var boardXpos: Int,
    override var boardYpos: Int,
    override var clickable: Boolean,
    override var hostile: Boolean,
    override var boundingBox: BoundingBox,
) : BoardObject() {
    open lateinit var pieceType: PieceTypes
    open val movement: MutableList<Square> = mutableListOf()
    open var nextBoard: Board? = null
    var soundSet: SoundSet = SoundSet()

    open fun getValidMoves(onComplete: () -> Unit = {}): Boolean {
        // Return an array of squares into which the piece can move
        // Individual pieces should override this method
        // Update movement variable
        return false // Returns true if valid move(s) available, otherwise false
    }

    override fun onClick(button: Int) {
        if (clickable) {
            when (button) {
                inputTypes["LMB"] -> {
                    if (GameManager.selectedPiece == null) {
                        GameManager.selectPiece(this)
                    } else {
                        GameManager.deselectPiece()
                    }
                }
            }
        }
    }

    override fun onHover() {

    }

    override fun onExitHover() {

    }

    override fun collide(other: BoardObject) {
        super.collide(other)
        if (other.hostile) {
            this.kill()
        }
        Gdx.app.log("collision", "Other in collision is a $other")
        when (other) {
            is Coin -> {
                // TODO Increase player's coin count
                Gdx.app.log("collisions", "Coin!")
                other.kill()
            }

            is Trap -> {
                other.springTrap(this)
            }
        }
    }

    open fun attack() {
        val actionQueue: MutableList<() -> Unit> = mutableListOf()
        var attacked = false
        this.movement.forEach { square ->
            if (!attacked) {
                GameManager.pieces.forEach { piece ->
                    if (square.contents.contains(piece)) {
                        actionQueue.add {
                            this.move(
                                square.boardXpos,
                                square.boardYpos,
                                null
                            ) // What happens across boards?
                            piece.kill()
                        }
                        attacked = true
                    }
                }
            }
        }
        actionQueue.forEach {
            it.invoke()
        }
    }

    override fun move(x: Int, y: Int, newBoard: Board?) {
        super.move(x, y, newBoard)
        if (GameManager.firstMoveComplete) {
            AudioManager.playRandomSound(this.soundSet.move)
            if (!this.hostile) {
                GameManager.moveCounter++
                if (GameManager.moveCounter >= GameManager.moveLimit) {
                    GameManager.moveLimitReached = true
                }
            }
        }
    }

    override fun kill() {
        SpriteAnimator.activateAnimation(
            this.deathAnimation.atlasFilepath,
            this.deathAnimation.frameDuration,
            this.deathAnimation.loop,
            this.boundingBox.min.x,
            this.boundingBox.min.y
        )
        AudioManager.playRandomSound(this.soundSet.death)
        GameManager.pieces.remove(this)
    }

}
