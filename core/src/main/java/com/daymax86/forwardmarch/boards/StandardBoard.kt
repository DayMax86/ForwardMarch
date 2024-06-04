package com.daymax86.forwardmarch.boards

import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.squares.BlackSquareDefault
import com.daymax86.forwardmarch.squares.WhiteSquareDefault

class StandardBoard(
    override var dimensions: Int,
    override var onScreen: Boolean = false,
    override val squaresList: MutableList<Square> = mutableListOf(),
    override var environmentXPos: Int,
    override var environmentYPos: Int,
    override var squareWidth: Int,
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
