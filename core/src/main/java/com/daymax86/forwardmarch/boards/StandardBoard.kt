package com.daymax86.forwardmarch.boards

import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.squares.BlackSquareDefault
import com.daymax86.forwardmarch.squares.WhiteSquareDefault

class StandardBoard(
    override var dimensions: Int = GameManager.DIMENSIONS,
    override val squaresList: MutableList<Square> = mutableListOf(),
    override var environmentXPos: Int = GameManager.EDGE_BUFFER.toInt(),
    override var environmentYPos: Int = 0,
    override var squareWidth: Int = GameManager.SQUARE_WIDTH.toInt(),
) : Board() {

    init {
        var lastWasBlack = false
        for (y: Int in 1..dimensions) {
            for (x: Int in 1..dimensions) {
                if (lastWasBlack) {
                    WhiteSquareDefault(
                        boardXpos = x, boardYpos = y,
                        clickable = true,
                        squareWidth = squareWidth,
                        associatedBoard = this,
                    )
                } else {
                    BlackSquareDefault(
                        boardXpos = x, boardYpos = y,
                        clickable = true,
                        squareWidth = squareWidth,
                        associatedBoard = this,
                    )
                }.let {
                    this.squaresList.add(it)
                }
                if (x.mod(dimensions) != 0) {
                    lastWasBlack = !lastWasBlack
                }
            }
        }
    }

}
