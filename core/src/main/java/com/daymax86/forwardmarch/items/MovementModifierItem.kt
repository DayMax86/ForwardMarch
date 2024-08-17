package com.daymax86.forwardmarch.items

import com.daymax86.forwardmarch.Item
import com.daymax86.forwardmarch.ItemTypes
import com.daymax86.forwardmarch.Movement
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.squares.Square

abstract class MovementModifierItem(
    override var itemType: ItemTypes = ItemTypes.MOVEMENT_MODIFIER
) : Item() {

    open val piecesAffected: MutableList<PieceTypes> = mutableListOf()

    open fun applyMovementModifier(
        piece: Piece,
    ): MutableList<Square> {
        // Items should override this and return the modified list as appropriate
        return Movement.getMovement(
            piece,
            piece.movementType,
            piece.range,
            piece.movementDirections,
        )
    }

}
