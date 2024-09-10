package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.managers.PieceManager.deselectPiece
import com.daymax86.forwardmarch.managers.StageManager
import com.daymax86.forwardmarch.squares.Square

abstract class BoardObject() : GameObject() {
    // All items to appear on the board should be children of this class,
    // including pieces, traps, pickups, interactibles etc.
    abstract var stageXpos: Int
    abstract var stageYpos: Int
    abstract var hostile: Boolean
    abstract var deathAnimation: SpriteAnimation
    abstract var idleAnimation: SpriteAnimation?
    abstract var currentPosition: Vector2
    abstract var movementTarget: Vector2
    abstract var visuallyStatic: Boolean
    abstract var interpolationType: Interpolation
    override var hideImage: Boolean = false

    fun getAllAnimations(): MutableList<SpriteAnimation?> {
        return mutableListOf(
            deathAnimation, idleAnimation
        )
    }

    fun setCurrentPosition(x: Float, y: Float) {
        currentPosition.set(x, y)
        updateBoundingBox(x, y, GameManager.SQUARE_WIDTH, GameManager.SQUARE_HEIGHT)
    }

    open fun move(x: Int, y: Int) {
        // Remove the object from the old square's 'contents' list
        val oldSquare: Square? =
            StageManager.stage.getSquare(this.stageXpos, this.stageYpos)
        oldSquare?.contents?.remove(this)

        // New coordinates
        this.stageXpos = x
        this.stageYpos = y

        val destinationSquare = StageManager.stage.getSquare(x, y)

        // Set environment coordinates to aim for (so it can be seen to move)
        if (destinationSquare != null) {
            val (squareX, squareY) = destinationSquare.getEnvironmentPosition()

            movementTarget = Vector2(squareX, squareY)
            // Trigger onEnter effects
            destinationSquare.onEnter(this)
            // Add to the square's content
            destinationSquare.addToContents(this)
            if (this is Shop) {
                Gdx.app.log("move", "/ ${this.stageYpos} / ")
            }
        }

        deselectPiece()
    }


//    open fun updateBoundingBox() {
//        boundingBox = BoundingBox(
//            Vector3(currentPosition.x, currentPosition.y, 0f),
//            Vector3(
//                currentPosition.x + GameManager.SQUARE_WIDTH,
//                currentPosition.y + GameManager.SQUARE_HEIGHT,
//                0f
//            )
//        )
//    }

    open fun collide(other: BoardObject, friendlyAttack: Boolean = false) {
        Gdx.app.log(
            "collisions", "A collision has happened! (between $this and $other)." +
                "Board position = ${this.stageXpos}, ${this.stageYpos}"
        )
    }

    open fun kill() {
        // Dispose of the piece, remove from all lists etc.
    }

}
