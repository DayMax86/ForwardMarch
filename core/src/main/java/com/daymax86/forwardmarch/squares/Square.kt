package com.daymax86.forwardmarch.squares

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.Player
import com.daymax86.forwardmarch.Toast
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pickups.Pickup
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.traps.Trap
import com.daymax86.forwardmarch.inputTypes
import com.daymax86.forwardmarch.managers.PickupManager
import com.daymax86.forwardmarch.managers.PieceManager.selectedPiece
import com.daymax86.forwardmarch.managers.StageManager

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
    abstract var stageXpos: Int
    abstract var stageYpos: Int
    abstract var squareWidth: Int
    abstract var highlight: Boolean
    abstract var altHighlight: Boolean
    abstract var boundingBox: BoundingBox

    open fun onClick(button: Int) {
        if (clickable) {
            when (button) {
                inputTypes["LMB"] -> {
                    Gdx.app.log("square", "$stageXpos, $stageYpos, contents = $contents")
                    if (selectedPiece != null) { // Null safety check for !! use
                        if (this.canBeEntered() &&
                            selectedPiece!!.movement.contains(this)
                        ) {
                            selectedPiece!!.move(
                                this.stageXpos,
                                this.stageYpos,
                            )
                        } else {
                            if (selectedPiece != null) {
                                if (!this.contents.contains(selectedPiece!!)) {
                                    GameManager.toast = Toast("Invalid move!")
                                }
                            }
                        }
                    }
                }

                inputTypes["RMB"] -> {
                    Gdx.app.log("square", "OnClick event for square and RMB")
                    if (this.contents.isNotEmpty()) {
                        GameManager.currentInfoBox = this.contents.last().infoBox
                    }
                }

                inputTypes["SCROLL_WHEEL_CLICK"] -> {
                    Gdx.app.log("square", "OnClick event for square and SCROLL_WHEEL_CLICK")
                    if (Player.bombTotal > 0) {
                        val bomb = Bomb()
                        val targetSquare = StageManager.stage.squaresList.firstOrNull { square ->
                            square.stageXpos == this.stageXpos &&
                                square.stageYpos == this.stageYpos
                        }
                        if (targetSquare != null) {
                            bomb.explode(targetSquare)
                        }
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

    fun getEnvironmentPosition(): Pair<Float, Float> {
        val x = StageManager.stage.environmentXPos + this.stageXpos * GameManager.SQUARE_WIDTH
        val y = StageManager.stage.environmentYPos + this.stageYpos * GameManager.SQUARE_HEIGHT
        return Pair(x, y)
    }

    fun addToContents(objToAdd: BoardObject) {
        // Set the new object's bounding box to match that of the square it's in
        objToAdd.boundingBox = this.boundingBox
        val collisionQueue: MutableList<() -> Unit> = mutableListOf()
        // Resolve collisions between existing contents and new object
        contents.add(objToAdd)
        this.contents.forEach { content ->
            collisionQueue.add {
                if (content != objToAdd) {
                    // If a piece is selected it's a friendly attack, otherwise friendly comes off worse
                    if (objToAdd is Piece) {
                        objToAdd.collide(content, selectedPiece == null)
                    } else {
                        // Generic collision handling for non-piece-based interactions
                        objToAdd.collide(content)
                    }
                }
            }
        }
        collisionQueue.forEach { it.invoke() }
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
        this.boundingBox = BoundingBox(Vector3(x, y, 0f), Vector3(x + width, y + height, 0f))
    }

    open fun swapToAltHighlight(swap: Boolean) {

    }

    open fun onEnter(obj: BoardObject) {
//        if (obj is Bomb && obj.active) {
//            obj.explode(this)
//        }
    }


}
