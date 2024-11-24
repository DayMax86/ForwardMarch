package com.daymax86.forwardmarch.managers

import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pickups.Coin
import com.daymax86.forwardmarch.board_objects.pickups.ItemToken
import com.daymax86.forwardmarch.items.FakeMoustache
import com.daymax86.forwardmarch.items.Item

object PickupManager {

    val pickups: MutableList<BoardObject> = mutableListOf()

    fun spawnPickup(type: PickupTypes, x: Int, y: Int, associatedItem: Item = FakeMoustache()) {
        when (type) {
            PickupTypes.COIN -> {
                val coinToAdd = Coin()
                coinToAdd.stageXpos = x
                coinToAdd.stageYpos = y
                coinToAdd.move(x, y)
                coinToAdd.initialise()
                pickups.add(coinToAdd)
            }

            PickupTypes.BOMB -> {
                val bombToAdd = Bomb()
                bombToAdd.stageXpos = x
                bombToAdd.stageYpos = y
                bombToAdd.move(x, y)
                bombToAdd.initialise()
                pickups.add(bombToAdd)
            }

            PickupTypes.ITEM_TOKEN -> {
                val tokenToAdd = ItemToken(associatedItem = associatedItem)
                tokenToAdd.stageXpos = x
                tokenToAdd.stageYpos = y
                tokenToAdd.move(x, y)
                tokenToAdd.initialise()
                pickups.add(tokenToAdd)}
        }
    }

}

enum class PickupTypes {
    COIN,
    BOMB,
    ITEM_TOKEN,
}
