package com.daymax86.forwardmarch.pieces

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameScreen
import com.daymax86.forwardmarch.Square
import com.daymax86.forwardmarch.inputTypes
import ktx.collections.contains

interface Piece : BoardObject {
    var pieceType: PieceTypes
    var friendly: Boolean
    var movement: Array<Square>
    var nextBoard: Board?

    fun getValidMoves(): Boolean {
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
                    if (associatedGame.selectedPiece == null) {
                        // No selected piece so assign this one
                        associatedGame.selectedPiece = this
                        highlight = true
                        Gdx.app.log("piece", "associatedGame.selectedPiece = $this")
                    } else if (associatedGame.selectedPiece == this) {
                        // Already selected so deselect
                        associatedGame.selectedPiece = null
                        highlight = false
                        Gdx.app.log("piece", "associatedGame.selectedPiece = null")
                    } else {
                        // Another piece has been clicked on that isn't this one
                        // How do we want this to behave?
                        // For now do nothing until deselected
                    }
                }
            }
        }
    }

    override fun onHover(){
        if (this.associatedBoard != null) { // Null safety check for !! use
            for (square in this.associatedBoard!!.squaresArray) {
                if (this.movement.contains(square)) {
                    square.swapToAltHighlight(true)
                    square.highlight = true
                }
            }
        }
        if (this.nextBoard != null) { // Null safety check for !! use
            for (square in this.nextBoard!!.squaresArray) {
                if (this.movement.contains(square)) {
                    square.swapToAltHighlight(true)
                    square.highlight = true
                }
            }
        }
    }

    override fun onExitHover() {

    }

    override fun move(x: Int, y: Int, newBoard: Board?) {
        this.boardXpos = x
        this.boardYpos = y
        if (newBoard != null) {
            this.associatedBoard = newBoard
        }
    }

    fun updateBoundingBox(x: Float, y: Float, width: Float, height: Float) {
        boundingBox = BoundingBox(Vector3(x, y, 0f), Vector3(x + width, y + height, 0f))
    }

}
