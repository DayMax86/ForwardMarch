package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox

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

    open fun onHover() {
        //highlight = true
    }

    open fun onExitHover() {

    }

    open fun onClick(button: Int) {
        highlight = !highlight
    }

    open fun move(x: Int, y: Int, newBoard: Board?) {
        GameManager.movementInProgress = true

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
        // Update the 'contents' property of the appropriate square
        if (this.associatedBoard != null) {
            this.associatedBoard!!.getSquare(x, y).let { sq ->
                sq?.contents?.add(this)
            }
        }
        GameManager.movementInProgress = false
    }

    open fun updateBoundingBox(x: Float, y: Float, width: Float, height: Float) {
        boundingBox = BoundingBox(Vector3(x, y, 0f), Vector3(x + width, y + height, 0f))
    }

}
