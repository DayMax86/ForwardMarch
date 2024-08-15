package com.daymax86.forwardmarch

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.squares.Square

abstract class BoardObject() {

    // All items to appear on the board should be children of this class,
    // including pieces, traps, pickups, interactibles etc.
    abstract var associatedBoard: Board?
    abstract var image: Texture
    abstract var highlightedImage: Texture
    abstract var highlight: Boolean
    abstract var boardXpos: Int
    abstract var boardYpos: Int
    abstract var clickable: Boolean
    abstract var hostile: Boolean
    abstract var boundingBox: BoundingBox
    abstract var deathAnimation: SpriteAnimation
    abstract var idleAnimation: SpriteAnimation?
    abstract var currentPosition: Vector2
    abstract var movementTarget: Vector2
    abstract var visuallyStatic: Boolean
    abstract var interpolationType: Interpolation

    open fun onHover() {
        highlight = true
    }

    open fun onExitHover() {
        highlight = false
    }

    open fun onClick(button: Int) {
        highlight = !highlight
    }

    open fun onShopClick(button: Int) {

    }

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

        // Update the bounding box
        this.associatedBoard.let {
            if (it != null) {
                movementTarget = Vector2(
                    it.environmentXPos + (this.boardXpos * GameManager.SQUARE_WIDTH),
                    it.environmentYPos + (this.boardYpos * GameManager.SQUARE_HEIGHT)
                )
                this.updateBoundingBox(
                    currentPosition.x,
                    currentPosition.y,
                    GameManager.SQUARE_WIDTH,
                    GameManager.SQUARE_HEIGHT,
                )
            }
        }

        val collisionQueue: MutableList<() -> Unit> = mutableListOf()
        // Update the 'contents' property of the appropriate square
        if (this.associatedBoard != null) {
            this.associatedBoard!!.getSquare(x, y)?.let { sq ->
                sq.onEnter(this)
                // Before adding to new square's contents, resolve collisions
                sq.contents.forEach { other ->
                    if (other != this) {
                        collisionQueue.add {
                            this.collide(other)
                        }
                    }
                }
                sq.contents.add(this)
            }
        }
        collisionQueue.forEach { it.invoke() }

        GameManager.deselectPiece()
    }

    open fun updateBoundingBox(x: Float, y: Float, width: Float, height: Float) {
        boundingBox = BoundingBox(Vector3(x, y, 0f), Vector3(x + width, y + height, 0f))
    }

    open fun updateBoundingBox() {
        boundingBox = BoundingBox(
            Vector3(currentPosition.x, currentPosition.y, 0f),
            Vector3(
                currentPosition.x + GameManager.SQUARE_WIDTH,
                currentPosition.y + GameManager.SQUARE_HEIGHT,
                0f
            )
        )
    }

    open fun collide(other: BoardObject) {

    }

    open fun kill() {
        // Dispose of the piece, remove from all lists etc.
    }

}
