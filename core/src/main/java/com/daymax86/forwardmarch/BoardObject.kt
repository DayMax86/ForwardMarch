package com.daymax86.forwardmarch

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox

interface BoardObject {

    // All items to appear on the board should be children of this class,
    // including pieces, traps, pickups, interactibles etc.

    var image: Texture
    var highlightedImage: Texture
    var highlight: Boolean
    var boardXpos: Int
    var boardYpos: Int
    var clickable: Boolean
    var hostile: Boolean
    var boundingBox: BoundingBox

    fun onHover() {
        highlight = true
    }

    fun onExitHover() {
        highlight = false
    }

}
