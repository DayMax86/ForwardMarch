package com.daymax86.forwardmarch.pieces

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.Square
import com.daymax86.forwardmarch.inputTypes

abstract class Piece(
    override var associatedBoard: Board?,
    override var image: Texture,
    override var highlightedImage: Texture,
    override var highlight: Boolean,
    override var boardXpos: Int,
    override var boardYpos: Int,
    override var clickable: Boolean,
    override var hostile: Boolean,
    override var boundingBox: BoundingBox,
) : BoardObject() {
    open lateinit var pieceType: PieceTypes
    open var friendly: Boolean = false
    open lateinit var movement: Array<Square>
    open var nextBoard: Board? = null

    open fun getValidMoves(): Boolean {
        // Return an array of squares into which the piece can move
        // Individual pieces should override this method
        // Update movement variable
        return false // Returns true if valid move(s) available, otherwise false
    }

    override fun onClick(button: Int) {
        if (clickable) {
            when (button) {
                inputTypes["LMB"] -> {
                    Gdx.app.log("piece", "LMB pressed on $pieceType")
                    if (GameManager.selectedPiece == null) {
                        GameManager.selectPiece(this)
                    } else {
                        GameManager.deselectPiece(this)
                    }
                }
            }
        }
    }

    override fun onHover() {

    }

    override fun onExitHover() {

    }

    override fun move(x: Int, y: Int, newBoard: Board?) {
        // New piece coordinates
        this.boardXpos = x
        this.boardYpos = y

        if (newBoard != null) {
            this.associatedBoard = newBoard
        }
        GameManager.deselectPiece(this)
    }

    fun updateBoundingBox(x: Float, y: Float, width: Float, height: Float) {
        boundingBox = BoundingBox(Vector3(x, y, 0f), Vector3(x + width, y + height, 0f))
    }

}
