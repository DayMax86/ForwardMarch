package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.daymax86.forwardmarch.animations.SpriteAnimation

enum class ItemTypes {
    MOVEMENT_MODIFIER,
    STATS_MODIFIER,
    DEATH_MODIFIER,
}

enum class ItemPools {
    SHOP,
}

abstract class Item: GameObject() {
    abstract var deathAnimation: SpriteAnimation
    abstract var idleAnimation: SpriteAnimation?
    abstract var currentPosition: Vector2
    abstract var movementTarget: Vector2
    abstract var interpolationType: Interpolation
    abstract var itemType: ItemTypes
    abstract var itemPools: MutableList<ItemPools>

    override fun onShopClick(button: Int) {
        if (Player.canAfford(this)) {
            Player.playerItems.add(this)
            GameManager.currentShop!!.exitShop()
        } else {
            // Feedback to the player that they don't have enough money
            GameManager.toast = Toast(text = "You can't afford this! It costs $shopPrice and you have ${Player.coinTotal}")
        }
    }

    fun getAllAnimations(): MutableList<SpriteAnimation?> {
        return mutableListOf(
            deathAnimation, idleAnimation
        )
    }

    open fun use() {

    }

}
