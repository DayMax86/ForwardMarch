package com.daymax86.forwardmarch

import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.board_objects.pickups.Pickup
import com.daymax86.forwardmarch.squares.Square

abstract class Board {

    abstract var dimensions: Int
    abstract var environmentXPos: Int
    abstract var environmentYPos: Int
    abstract val squaresList: MutableList<Square>
    abstract var squareWidth: Int

    fun getSquare(x: Int, y: Int): Square? {
        return this.squaresList.firstOrNull {
            it.boardXpos == x && it.boardYpos == y
        }
    }

    fun initialiseBoardObjects() {
        initialisePickups().forEach { action ->
            action.invoke()
        }
        initialiseShops().forEach { action ->
            action.invoke()
        }
    }

    private fun initialisePickups(): MutableList<() -> Unit> {
        val actionQueue: MutableList<() -> Unit> = mutableListOf()
        this.squaresList.forEach { square ->
            square.contents.forEach { content ->
                if (content is Pickup) {
                    actionQueue.add {
                        content.initialise()
                    }
                }
            }
        }
        return actionQueue
    }

    private fun initialiseShops(): MutableList<() -> Unit> {
        val actionQueue: MutableList<() -> Unit> = mutableListOf()
        this.squaresList.forEach { square ->
            square.contents.forEach { content ->
                if (content is Shop) {
                    actionQueue.add {
                        content.move(content.boardXpos, content.boardYpos, content.associatedBoard)
                    }
                }
            }
        }
        return actionQueue
    }

}
