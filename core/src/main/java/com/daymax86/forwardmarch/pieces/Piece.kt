package com.daymax86.forwardmarch.pieces

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

}
