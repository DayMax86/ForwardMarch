package com.daymax86.forwardmarch.animations

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.GameManager

class SpriteAnimation(
    val atlasFilepath: String,
    val frameDuration: Float,
    var loop: Boolean,
) {
    var boundingBox: BoundingBox = BoundingBox()

    fun activate(box: BoundingBox) {
        this.boundingBox.min.x = box.min.x
        this.boundingBox.min.y = box.min.y
        GameManager.activeAnimations.add(this)
    }

    fun getAnim(): Animation<TextureRegion> {
        val atlas = TextureAtlas(Gdx.files.internal(this.atlasFilepath))
        return Animation<TextureRegion>(this.frameDuration, atlas.regions)
    }

}
