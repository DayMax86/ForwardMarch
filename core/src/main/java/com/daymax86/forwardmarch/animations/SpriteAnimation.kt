package com.daymax86.forwardmarch.animations

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager

open class SpriteAnimation(
    val atlasFilepath: String,
    val frameDuration: Float,
    var loop: Boolean,
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = GameManager.SQUARE_WIDTH,
    var height: Float = GameManager.SQUARE_HEIGHT,
    var elapsedTime: Float = 0f,
    var anim: Animation<TextureRegion> = Animation<TextureRegion>(0f),
    var source: BoardObject? = null,
) {

    init {
        val atlas = TextureAtlas(Gdx.files.internal(this.atlasFilepath))
        anim = Animation<TextureRegion>(this.frameDuration, atlas.regions)
    }

    fun isFinished(stateTime: Float): Boolean {
        if (!loop) {
            return (this.anim.isAnimationFinished(stateTime))
        } else {
            if (this.elapsedTime >= this.frameDuration * anim.keyFrames.size) {
                this.elapsedTime = 0f // Reset the elapsed time for looping animations
            }
            return false
        }
    }

    fun activate() {
        GameManager.activeAnimations.add(this)
    }

}

object StickySpriteAnimator {
    // For when an animation needs to be run in place and not follow the source
    // e.g. when something is killed and the death animation needs to be played
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
