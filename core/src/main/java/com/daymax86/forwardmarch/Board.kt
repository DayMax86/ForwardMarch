package com.daymax86.forwardmarch

import com.daymax86.forwardmarch.squares.Square

abstract class Board {

    abstract var dimensions: Int
    abstract var environmentXPos: Int
    abstract var environmentYPos: Int
    abstract val squaresList: MutableList<Square>
    abstract var onScreen: Boolean
    abstract var squareWidth: Int

    fun getSquare(x: Int, y: Int): Square? {
        return this.squaresList.firstOrNull {
            it.boardXpos == x && it.boardYpos == y
        }
    }

}
