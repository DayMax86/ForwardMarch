package com.daymax86.forwardmarch

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array

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
    // Needs to know the boards it can interact with
    var activeBoards: Array<Board>

    fun onHover() {
        //highlight = true
    }

    fun onExitHover() {
        //highlight = false
    }

    fun onClick(button: Int) {
        highlight = !highlight
    }

}
