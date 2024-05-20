package com.daymax86.forwardmarch.boards

import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.Square
import com.daymax86.forwardmarch.squares.BlackSquareDefault
import com.daymax86.forwardmarch.squares.WhiteSquareDefault

class StandardBoard(
    override var dimensions: Int,
    override var squaresArray: Array<Square> = Array<Square>(),
    override var tileWidth: Int,
): Board {
    init {
        var lastWasBlack = false
        for (y: Int in 1..dimensions) {
            for (x: Int in 1..dimensions) {
                val square: Square = if (lastWasBlack) {
                    WhiteSquareDefault(boardXpos = x, boardYpos = y,
                        tileWidth = tileWidth
                    )
                } else {
                    BlackSquareDefault(boardXpos = x, boardYpos = y,
                        tileWidth = tileWidth
                    )
                }
                this.squaresArray.add(square)
                if (x.mod(dimensions)!=0) {
                    lastWasBlack = !lastWasBlack
                }
            }
        }
    }
}
