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
    var squareWidth: Int
    var highlight: Boolean
    var boundingBox: BoundingBox


    fun onClick(button: Int) {
        if (clickable) {
            when (button) {
                inputTypes["LMB"] -> {
                    //Gdx.app.log("square", "OnClick event for square and LMB")
                    Gdx.app.log("square", "$boardXpos, $boardYpos")
                }
                inputTypes["RMB"] -> {
                    Gdx.app.log("square", "OnClick event for square and RMB")
                }
                inputTypes["SCROLL_WHEEL_CLICK"] -> {
                    Gdx.app.log("square", "OnClick event for square and SCROLL_WHEEL_CLICK")
                }
                inputTypes["MOUSE3"] -> {
                    Gdx.app.log("square", "OnClick event for square and MOUSE3")
                }
                inputTypes["MOUSE4"] -> {
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
