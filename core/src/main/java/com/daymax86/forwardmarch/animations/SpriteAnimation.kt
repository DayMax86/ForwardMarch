package com.daymax86.forwardmarch.animations

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.GameManager

class SpriteAnimation(
    val atlasFilepath: String,
    val frameDuration: Float,
    var loop: Boolean,
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = GameManager.SQUARE_WIDTH,
    var height: Float = GameManager.SQUARE_HEIGHT,
    var elapsedTime: Float = 0f
) {

    fun getAnim(): Animation<TextureRegion> {
        val atlas = TextureAtlas(Gdx.files.internal(this.atlasFilepath))
        return Animation<TextureRegion>(this.frameDuration, atlas.regions)
    }

}

object SpriteAnimator{

    fun activateAnimation(
        atlasFilepath: String,
        frameDuration: Float,
        loop: Boolean,
        x: Float = 0f,
        y: Float = 0f,
        width: Float = GameManager.SQUARE_WIDTH,
        height: Float = GameManager.SQUARE_HEIGHT,
    ) {
        GameManager.activeAnimations.add(
            SpriteAnimation(
                atlasFilepath = atlasFilepath,
                frameDuration = frameDuration,
                loop = loop,
                x = x,
                y = y,
                width = width,
                height = height,
            )
        )
    }

}
