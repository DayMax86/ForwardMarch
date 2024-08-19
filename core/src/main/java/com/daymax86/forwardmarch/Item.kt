package com.daymax86.forwardmarch

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.daymax86.forwardmarch.animations.SpriteAnimation

enum class ItemTypes {
    MOVEMENT_MODIFIER,
    STATS_MODIFIER,
    DEATH_MODIFIER,
}

abstract class Item(): GameObject() {
    abstract var deathAnimation: SpriteAnimation
    abstract var idleAnimation: SpriteAnimation?
    abstract var currentPosition: Vector2
    abstract var movementTarget: Vector2
    abstract var interpolationType: Interpolation
    abstract var itemType: ItemTypes

    override fun onShopClick(button: Int) {
        Player.playerItems.add(this)
        GameManager.currentShop!!.exitShop()
    }

    fun getAllAnimations(): MutableList<SpriteAnimation?> {
        return mutableListOf(
            deathAnimation, idleAnimation
        )
    }

    open fun use() {

    }

}
