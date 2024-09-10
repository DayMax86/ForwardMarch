package com.daymax86.forwardmarch.managers

import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.board_objects.pickups.Coin

object PickupManager {

    val pickups: MutableList<BoardObject> = mutableListOf()

    fun spawnPickup(type: PickupTypes, x: Int, y: Int) {
        when (type) {
            PickupTypes.COIN -> {
                val coinToAdd = Coin()
                coinToAdd.stageXpos = x
                coinToAdd.stageYpos = y
                coinToAdd.move(x, y)
                coinToAdd.initialise()
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
