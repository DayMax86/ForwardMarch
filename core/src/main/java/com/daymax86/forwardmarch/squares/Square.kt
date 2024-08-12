package com.daymax86.forwardmarch.squares

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pickups.Pickup
import com.daymax86.forwardmarch.inputTypes

enum class TileColours {
    BLACK,
    WHITE,
    OTHER,
}

abstract class Square {
    abstract var tileImage: Texture
    abstract var highlightedTileImage: Texture
    abstract var colour: TileColours
    abstract var clickable: Boolean
    abstract val contents: MutableList<BoardObject>
    abstract var boardXpos: Int
    abstract var boardYpos: Int
    abstract var squareWidth: Int
    abstract var highlight: Boolean
    abstract var altHighlight: Boolean
    abstract var boundingBox: BoundingBox
    abstract var associatedBoard: Board

    open fun onClick(button: Int) {
        if (clickable) {
            when (button) {
                inputTypes["LMB"] -> {
                    Gdx.app.log("square", "$boardXpos, $boardYpos")
                    if (GameManager.selectedPiece != null) { // Null safety check for !! use
                        if (this.canBeEntered()) { // Make sure the square isn't occupied (assuming pieces can't share a square with anything else - will need updating if not)
                            if (GameManager.selectedPiece!!.movement.contains(this)) {
                                GameManager.selectedPiece!!.move(
                                    this.boardXpos,
                                    this.boardYpos,
                                    this.associatedBoard,
                                )
                            } else {
                                // TODO() Feedback to user that this is an invalid move
                            }
                        }
                    }
                }

                inputTypes["RMB"] -> {
                    Gdx.app.log("square", "OnClick event for square and RMB")
                    //------------------------FOR TESTING--------------------//
                    if (GameManager.bombTotal > 0) {
                        val bomb = Bomb()
                        bomb.move(this.boardXpos, this.boardYpos, this.associatedBoard)
                        bomb.use()
                    }
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

    fun canBeEntered() : Boolean {
        for (bo in this.contents) {
            if (bo !is Pickup) {
                return false
            }
        }
        return true
    }

    fun onHover() {
        if (!GameManager.freezeHighlights) {
            swapToAltHighlight(false)
            highlight = true
        }
    }

    fun onExitHover() {
        if (!GameManager.freezeHighlights) {
            highlight = false
        }
    }

    fun updateBoundingBox(x: Float, y: Float, width: Float, height: Float) {
        boundingBox = BoundingBox(Vector3(x, y, 0f), Vector3(x + width, y + height, 0f))
    }

    open fun swapToAltHighlight(swap: Boolean) {

    }

    open fun onEnter(obj: BoardObject) {
        if (obj is Bomb && obj.active) {
            obj.explode(this)
        }
    }


}
