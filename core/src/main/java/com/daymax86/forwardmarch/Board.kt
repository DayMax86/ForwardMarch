package com.daymax86.forwardmarch

import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.squares.Square

class Board(
    var dimensions: Int = GameManager.DIMENSIONS,
    var environmentXPos: Int = 0,
    var environmentYPos: Int = 0,
    val squaresList: MutableList<Square> = mutableListOf(),
    var squareWidth: Int = GameManager.SQUARE_WIDTH.toInt(),
    var boardIndex: Int = 0,
    var initialActionQueue: MutableList<() -> Unit> = mutableListOf(),
) {

    fun getSquare(x: Int, y: Int): Square? {
        return this.squaresList.firstOrNull {
            it.stageXpos == x && it.stageYpos == y
        }
    }

    fun completeActionQueue() {
        initialActionQueue.forEach { it.invoke() }
    }

    fun destroy() {
        this.squaresList.forEach { square ->
            square.contents.forEach { content ->
                content.image.dispose()
                content.highlightedImage.dispose()
                // Animations?

            }
        }
    }


}
