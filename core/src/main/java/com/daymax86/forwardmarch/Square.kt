package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array

enum class TileColours{
    BLACK,
    WHITE,
}

interface Square {
    var tileImage: Texture
    var highlightedTileImage: Texture
    var colour: TileColours
    var clickable: Boolean
    var contents: Array<BoardObject>
    var boardXpos: Int
    var boardYpos: Int
    var tileWidth: Int
    var highlight: Boolean
    var boundingBox: BoundingBox

    /*
    * Mouse buttons:
    * 0 - LMB
    * 1 - RMB
    * 2 - SCROLL_WHEEL_CLICK
    * 3 - MOUSE3
    * 4 - MOUSE4
    */
    fun onClick(button: Int) {
        if (clickable) {
            when (button) {
                0 -> {// LMB
                    //Gdx.app.log("square", "OnClick event for square and LMB")
                    Gdx.app.log("square", "$boardXpos, $boardYpos")
                }
                1 -> {// LMB
                    Gdx.app.log("square", "OnClick event for square and RMB")
                }
                2 -> {// LMB
                    Gdx.app.log("square", "OnClick event for square and SCROLL_WHEEL_CLICK")
                }
                3 -> {// LMB
                    Gdx.app.log("square", "OnClick event for square and MOUSE3")
                }
                4 -> {// LMB
                    Gdx.app.log("square", "OnClick event for square and MOUSE4")
                }
            }
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
