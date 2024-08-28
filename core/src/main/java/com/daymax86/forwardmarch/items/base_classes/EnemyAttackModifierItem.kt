package com.daymax86.forwardmarch.items.base_classes

import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.items.Item
import com.daymax86.forwardmarch.items.ItemTypes

abstract class EnemyAttackModifierItem(
    override var itemType: ItemTypes = ItemTypes.ENEMY_ATTACK_MODIFIER
) : Item() {

    open fun applyAttackModifier(
        enemyPiece: Piece,
    ): MutableList<() -> Unit>{
        // Items should override this and perform some action
        return mutableListOf()
    }

}
