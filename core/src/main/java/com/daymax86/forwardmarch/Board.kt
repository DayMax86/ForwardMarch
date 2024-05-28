package com.daymax86.forwardmarch

import com.badlogic.gdx.utils.Array

abstract class Board {

    abstract var dimensions: Int
    abstract var environmentXPos: Int
    abstract var environmentYPos: Int
    abstract var squaresArray: Array<Square>
    abstract var onScreen: Boolean
    abstract var squareWidth: Int

    fun getSquare(x: Int, y: Int): Square? {
        return this.squaresArray.first{
            it.boardXpos == x && it.boardYpos == y
        }
    }

}
