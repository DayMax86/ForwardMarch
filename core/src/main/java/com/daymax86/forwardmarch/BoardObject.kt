package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.managers.PieceManager.deselectPiece
import com.daymax86.forwardmarch.managers.PieceManager.selectedPiece

abstract class BoardObject() : GameObject() {
    // All items to appear on the board should be children of this class,
    // including pieces, traps, pickups, interactibles etc.
    abstract var associatedBoard: Board?
    abstract var boardXpos: Int
    abstract var boardYpos: Int
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

    open fun move(x: Int, y: Int, newBoard: Board?) {
        // Remove the object from the old square's 'contents' list
        if (this.associatedBoard != null) {
            this.associatedBoard!!.squaresList.firstOrNull {
                it.boardXpos == this.boardXpos && it.boardYpos == this.boardYpos
            }.let { sq ->
                sq?.contents?.remove(this)
            }
        }

        // New coordinates
        this.boardXpos = x
        this.boardYpos = y

        if (newBoard != null) {
            this.associatedBoard = newBoard
        }

        this.associatedBoard.let { board ->
            if (board != null) {
                val square = board.getSquare(x, y)
                if (square != null) {
                    // Set environment coordinates to aim for (so it can be seen to move)
                    val (squareX, squareY) = square.getEnvironmentPosition()
                    movementTarget = Vector2(squareX, squareY)
                    // Trigger onEnter effects
                    square.onEnter(this)
                    // Add to the square's content
                    square.addToContents(this)
                }
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
                "Board position = ${this.boardXpos}, ${this.boardYpos}"
        )
    }

    open fun kill() {
        // Dispose of the piece, remove from all lists etc.
    }

}
