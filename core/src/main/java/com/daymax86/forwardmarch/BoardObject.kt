package com.daymax86.forwardmarch

import com.badlogic.gdx.graphics.Texture
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

    }

}
