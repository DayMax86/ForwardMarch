package com.daymax86.forwardmarch

import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.managers.GameManager.DIMENSIONS
import com.daymax86.forwardmarch.squares.Square
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktx.async.KtxAsync

class Stage() {
    // A Stage is 3 boards stacked on top of each other, essentially one mega-board.

    val squaresList: MutableList<Square> = mutableListOf()
    var environmentXPos: Int = GameManager.BOARD_STARTING_X
    var environmentYPos: Int = GameManager.BOARD_STARTING_Y

    fun initialise(
        boards: Triple<Board, Board, Board>,
    ) {
        runBlocking {
            boards.toList().forEach { board ->
                board.completeActionQueue()
            }

            val board1 = boards.toList().elementAt(0)
            val board2 = boards.toList().elementAt(1)
            val board3 = boards.toList().elementAt(2)
            // Create the initial stage from the 3 board parameters
            appendBoard(board1, 0)
            appendBoard(board2, 8)
            appendBoard(board3, 16)
        }
    }

    fun appendBoard(
        board: Board,
        startingY: Int
    ) { // StartingY is where the (1,1) of this board should be on the stage
        for (y: Int in 1..DIMENSIONS) {
            for (x: Int in 1..DIMENSIONS) {
                board.getSquare(x, y)?.let { square ->
                    // Change the y pos to be stageYpos (increase it by 8* the number of boards in the stage)
                    square.stageYpos += startingY
                    // Add to the squaresList
                    this.squaresList.add(square)
                }
            }
        }
    }

    fun removeRows(numberOfRows: Int) {
        squaresList.sortBy { square ->
            square.stageYpos
        }
        val squaresToRemove: MutableList<Square> = squaresList.subList(
            fromIndex = squaresList.indexOf(squaresList.first()),
            toIndex = DIMENSIONS * numberOfRows,
        )
        squaresList.removeAll(squaresToRemove)
    }

    fun getSquare(x: Int, y: Int): Square? {
        return squaresList.firstOrNull { square ->
            square.stageXpos == x &&
                square.stageYpos == y
        }
    }

    fun rowsBelow(sq: Square): Int {
//        squaresList.sortBy { square ->
//            square.stageYpos
//        }
//        return squaresList.indexOf(sq) - 1
        var i = 0
        squaresList.forEach { square ->
            if (square.stageYpos < sq.stageYpos) {
                i++
            }
        }
        return i
    }

}
