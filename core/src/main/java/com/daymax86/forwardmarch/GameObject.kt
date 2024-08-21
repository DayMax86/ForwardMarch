package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox

abstract class GameObject {
    // Anything common to BoardObjects and Items should be in here (e.g. image)
    abstract var image: Texture
    abstract var highlightedImage: Texture
    abstract var clickable: Boolean
    abstract var boundingBox: BoundingBox
    abstract var highlight: Boolean
    abstract var shopPrice: Int
    abstract var infoBox: InfoBox

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

    open fun updateBoundingBox(x: Float, y: Float, width: Float, height: Float) {
        boundingBox = BoundingBox(Vector3(x, y, 0f), Vector3(x + width, y + height, 0f))
    }

}
