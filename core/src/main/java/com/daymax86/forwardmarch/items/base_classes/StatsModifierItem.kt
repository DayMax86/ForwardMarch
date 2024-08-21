package com.daymax86.forwardmarch.items.base_classes

import com.daymax86.forwardmarch.items.Item
import com.daymax86.forwardmarch.items.ItemTypes

abstract class StatsModifierItem(
    override var itemType: ItemTypes = ItemTypes.STATS_MODIFIER
) : Item() {

    open fun applyStatsModifier() {
        // This should be overridden by individual items, changing a stat in the Player object
    }

}
