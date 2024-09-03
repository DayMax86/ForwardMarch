package com.daymax86.forwardmarch

import com.daymax86.forwardmarch.board_objects.SacrificeStation
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.board_objects.pickups.Pickup
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.squares.Square

class Board(
    var dimensions: Int = GameManager.DIMENSIONS,
    var environmentXPos: Int = 0,
    var environmentYPos: Int = 0,
    val squaresList: MutableList<Square> = mutableListOf(),
    var squareWidth: Int = GameManager.SQUARE_WIDTH.toInt(),
    var boardIndex: Int = 0
) {

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
        initialiseStations().forEach { action ->
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

    private fun initialiseStations(): MutableList<() -> Unit> {
        val actionQueue: MutableList<() -> Unit> = mutableListOf()
        this.squaresList.forEach { square ->
            square.contents.forEach { content ->
                if (content is SacrificeStation) {
                    actionQueue.add {
                        content.move(content.boardXpos, content.boardYpos, content.associatedBoard)
                    }
                }
            }
        }
        return actionQueue
    }

}
