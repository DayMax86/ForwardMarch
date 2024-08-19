package com.daymax86.forwardmarch

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.animations.SpriteAnimation

enum class ItemTypes {
    MOVEMENT_MODIFIER,
    STATS_MODIFIER,
    DEATH_MODIFIER,
}

abstract class Item() {
    abstract var image: Texture
    abstract var highlightedImage: Texture
    abstract var highlight: Boolean
    abstract var clickable: Boolean
    abstract var boundingBox: BoundingBox
    abstract var deathAnimation: SpriteAnimation
    abstract var idleAnimation: SpriteAnimation?
    abstract var currentPosition: Vector2
    abstract var movementTarget: Vector2
    abstract var interpolationType: Interpolation
    abstract var itemType: ItemTypes

    open fun onHover() {
        highlight = true
    }

    open fun onExitHover() {
        highlight = false
    }

    open fun onClick(button: Int) {
        highlight = !highlight
    }

    open fun onShopClick(button: Int) {

    }

    fun getAllAnimations(): MutableList<SpriteAnimation?> {
        return mutableListOf(
            deathAnimation, idleAnimation
        )
    }

    open fun updateBoundingBox(x: Float, y: Float, width: Float, height: Float) {
        boundingBox = BoundingBox(Vector3(x, y, 0f), Vector3(x + width, y + height, 0f))
    }

    open fun use() {

    }

}
