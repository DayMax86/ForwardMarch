package com.daymax86.forwardmarch.pieces

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.Square

interface Piece: BoardObject {
    var pieceType: PieceTypes
    var friendly: Boolean
    var movement: Array<Square>

    fun getValidMoves(boards: Array<Board>): Boolean{
        // Return an array of squares into which the piece can move
        // Individual pieces should override this method
        // Update movement variable
        return false // Returns true if valid move(s) available, otherwise false
    }

    override fun onClick(button: Int) {
        if (clickable) {
            when (button) {
                0 -> {// LMB
                    Gdx.app.log("piece", "LMB pressed on $pieceType")
                    highlight = true
                }
                1 -> {// LMB

                }
                2 -> {// LMB

                }
                3 -> {// LMB

                }
                4 -> {// LMB

                }
            }
        }
    }

    fun updateBoundingBox(x: Float, y: Float, width: Float, height: Float) {
        boundingBox = BoundingBox(Vector3(x,y,0f), Vector3(x + width,y + height,0f))
    }

}
