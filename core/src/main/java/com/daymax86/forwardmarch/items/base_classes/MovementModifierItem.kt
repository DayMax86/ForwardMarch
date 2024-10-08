package com.daymax86.forwardmarch.items.base_classes

import com.daymax86.forwardmarch.items.Item
import com.daymax86.forwardmarch.items.ItemTypes
import com.daymax86.forwardmarch.Movement
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.squares.Square

abstract class MovementModifierItem(
    override var itemType: ItemTypes = ItemTypes.MOVEMENT_MODIFIER
) : Item() {

    open fun applyMovementModifier(
        piece: Piece,
    ): MutableList<Square> {
        // Items should override this and return the modified list as appropriate
        return Movement.getMovement(
            piece,
            piece.movementTypes,
            piece.range,
            piece.movementDirections,
        )
    }

}
