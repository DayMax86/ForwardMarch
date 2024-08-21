package com.daymax86.forwardmarch.items.base_classes

import com.daymax86.forwardmarch.items.Item
import com.daymax86.forwardmarch.items.ItemTypes
import com.daymax86.forwardmarch.board_objects.pieces.Piece

abstract class DeathModifierItem(
    override var itemType: ItemTypes = ItemTypes.DEATH_MODIFIER
) : Item() {

    open fun applyDeathModifier(
        piece: Piece,
    ){
        // Items should override this and perform some action
    }

}
