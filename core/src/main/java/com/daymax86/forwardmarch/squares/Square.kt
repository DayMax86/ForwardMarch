package com.daymax86.forwardmarch.squares

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.Player
import com.daymax86.forwardmarch.Toast
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pickups.Pickup
import com.daymax86.forwardmarch.board_objects.traps.Trap
import com.daymax86.forwardmarch.inputTypes

enum class SquareTypes {
    BLACK,
    WHITE,
    MYSTERY,
    TRAPDOOR,
    BROKEN,
}

abstract class Square {
    abstract var tileImage: Texture
    abstract var highlightedTileImage: Texture
    abstract var colour: SquareTypes
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
                    Gdx.app.log("square", "$boardXpos, $boardYpos, contents = $contents")
                    if (GameManager.selectedPiece != null) { // Null safety check for !! use
                        if (this.canBeEntered() &&
                            GameManager.selectedPiece!!.movement.contains(this)
                        ) {
                            GameManager.selectedPiece!!.move(
                                this.boardXpos,
                                this.boardYpos,
                                this.associatedBoard,
                            )
                        } else {
                            if (GameManager.selectedPiece != null) {
                                if (!this.contents.contains(GameManager.selectedPiece!!)) {
                                    GameManager.toast = Toast("Invalid move!")
                                }
                            }
                        }
                    }
                }

                inputTypes["RMB"] -> {
                    Gdx.app.log("square", "OnClick event for square and RMB")
                    if (this.contents.isNotEmpty()) {
                        GameManager.currentInfoBox = this.contents[0].infoBox
                    }
                }

                inputTypes["SCROLL_WHEEL_CLICK"] -> {
                    Gdx.app.log("square", "OnClick event for square and SCROLL_WHEEL_CLICK")
                    //------------------------FOR TESTING--------------------//
                    if (Player.bombTotal > 0) {
                        val bomb = Bomb()
                        bomb.move(this.boardXpos, this.boardYpos, this.associatedBoard)
                        bomb.use()
                    }
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

    fun canBeEntered(): Boolean {
        for (bo in this.contents) {
            if (bo !is Pickup &&
                bo !is Shop
            ) {
                return bo.hostile
            }
        }
        return true
    }

    fun containsEnemy(): Boolean {
        for (bo in this.contents) {
            if (this.contents.any { obj ->
                    obj.hostile
                }) {
                // The square contains a hostile piece, so it can be entered to attack, but can't move through
                return true
            }
        }
        return false
    }

    fun containsTrap(): Boolean {
        for (bo in this.contents) {
            if (this.contents.any { obj ->
                    obj is Trap
                }) {
                // The square contains a trap
                return true
            }
        }
        return false
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
