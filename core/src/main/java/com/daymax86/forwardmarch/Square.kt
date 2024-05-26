package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array

enum class TileColours {
    BLACK,
    WHITE,
}

abstract class Square {
    abstract var tileImage: Texture
    abstract var highlightedTileImage: Texture
    abstract var colour: TileColours
    abstract var clickable: Boolean
    abstract var contents: Array<BoardObject>
    abstract var boardXpos: Int
    abstract var boardYpos: Int
    abstract var squareWidth: Int
    abstract var highlight: Boolean
    abstract var boundingBox: BoundingBox
    abstract var associatedBoard: Board
    abstract var associatedGame: GameLogic


    open fun onClick(button: Int) {
        if (clickable) {
            when (button) {
                inputTypes["LMB"] -> {
                    Gdx.app.log("square", "$boardXpos, $boardYpos")
                    if (associatedBoard.associatedGame.selectedPiece != null) { // Null safety check for !! use
                        associatedBoard.associatedGame.selectedPiece!!.move(
                            this.boardXpos,
                            this.boardYpos,
                            this.associatedBoard
                        )
                    }
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
        swapToAltHighlight(false)
        highlight = true
    }

    fun onExitHover() { // TODO() Squares which are part of movement shouldn't be un-highlighted when piece is selected
        highlight = false
    }

    fun updateBoundingBox(x: Float, y: Float, width: Float, height: Float) {
        boundingBox = BoundingBox(Vector3(x, y, 0f), Vector3(x + width, y + height, 0f))
    }

    open fun swapToAltHighlight(swap: Boolean) {

    }


}
