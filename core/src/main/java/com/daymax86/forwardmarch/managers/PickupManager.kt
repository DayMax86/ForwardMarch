package com.daymax86.forwardmarch.managers

import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.board_objects.pickups.Coin

object PickupManager {

    val pickups: MutableList<BoardObject> = mutableListOf()

    fun spawnPickup(type: PickupTypes, x: Int, y: Int, board: Board) {
        when (type) {
            PickupTypes.COIN -> {
                val coinToAdd = Coin()
                coinToAdd.associatedBoard = board
                coinToAdd.boardXpos = x
                coinToAdd.boardYpos = y
                coinToAdd.move(x, y, board)
                pickups.add(coinToAdd)
            }

            PickupTypes.BOMB -> {}
            PickupTypes.ITEM_TOKEN -> {}
        }
    }

}

enum class PickupTypes {
    COIN,
    BOMB,
    ITEM_TOKEN,
}
