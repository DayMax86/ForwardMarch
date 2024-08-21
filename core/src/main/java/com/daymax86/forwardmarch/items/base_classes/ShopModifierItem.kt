package com.daymax86.forwardmarch.items.base_classes

import com.daymax86.forwardmarch.items.Item
import com.daymax86.forwardmarch.items.ItemTypes

abstract class ShopModifierItem(
    override var itemType: ItemTypes = ItemTypes.SHOP_MODIFIER
) : Item() {

    open fun applyShopModifier() {
        // This should be overridden by individual items, changing some shop behaviour
    }

}
