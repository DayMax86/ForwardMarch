package com.daymax86.forwardmarch.boards

import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.squares.BlackSquareDefault
import com.daymax86.forwardmarch.squares.MysterySquare
import com.daymax86.forwardmarch.squares.TrapdoorSquare
import com.daymax86.forwardmarch.squares.WhiteSquareDefault

class VeryEasyBoard1(
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

        // Add a trapdoor square and a mystery square
        val randX1 = (1..8).random()
        val randY1 = (1..8).random()
        this.squaresList.firstOrNull {
            it.boardXpos == randX1 && it.boardYpos == randY1
        }.apply {
            this@VeryEasyBoard1.squaresList.remove(this)
            this@VeryEasyBoard1.squaresList.add(
                TrapdoorSquare(
                    boardXpos = randX1, boardYpos = randY1,
                    clickable = true,
                    squareWidth = squareWidth,
                    associatedBoard = this@VeryEasyBoard1,
                )
            )
        }

        val randX2 = (1..8).random()
        val randY2 = (1..8).random()
        this.squaresList.firstOrNull {
            it.boardXpos == randX2 && it.boardYpos == randY2
        }.apply {
            this@VeryEasyBoard1.squaresList.remove(this)
            this@VeryEasyBoard1.squaresList.add(
                MysterySquare(
                    boardXpos = randX2, boardYpos = randY2,
                    clickable = true,
                    squareWidth = squareWidth,
                    associatedBoard = this@VeryEasyBoard1,
                )
            )
        }

    }
}
