package com.daymax86.forwardmarch

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array

enum class TileColours{
    BLACK,
    WHITE,
    RED
}

interface Square {
    var tileImage: Texture
    var highlightedTileImage: Texture
    var colour: TileColours
    var hostile: Boolean
    var clickable: Boolean
    var contents: Array<GameObject>
    var boardXpos: Int
    var boardYpos: Int
    var tileWidth: Int
    var highlight: Boolean
    var boundingBox: BoundingBox

    fun onClick() {
        if (clickable) {

        }
    }

    fun onHover() {
        highlight = true
    }

    fun onExitHover() {
        highlight = false
    }

    fun updateBoundingBox(x: Float, y: Float, width: Float, height: Float) {
        boundingBox = BoundingBox(Vector3(x,y,0f),Vector3(x + width,y + height,0f))
    }


}
