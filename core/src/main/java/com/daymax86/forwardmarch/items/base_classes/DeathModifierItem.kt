package com.daymax86.forwardmarch.items.base_classes

import com.daymax86.forwardmarch.Item
import com.daymax86.forwardmarch.ItemTypes
import com.daymax86.forwardmarch.Movement
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.squares.Square

abstract class DeathModifierItem(
    override var itemType: ItemTypes = ItemTypes.DEATH_MODIFIER
) : Item() {

    open fun applyDeathModifier(
        piece: Piece,
    ){
        // Items should override this and perform some action
    }

}
