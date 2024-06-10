package com.daymax86.forwardmarch.board_objects.traps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.animations.SpriteAnimation

open class Trap(
    override var associatedBoard: Board? = null,
    override var image: Texture = Texture(Gdx.files.internal("sprites/spike_trap_256.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/spike_trap_256_damage.png")),
    override var highlight: Boolean = false,
    override var boardXpos: Int = -1,
    override var boardYpos: Int = -1,
    override var clickable: Boolean = false,
    override var hostile: Boolean = true,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var deathAnimation: SpriteAnimation = SpriteAnimation(
        atlasFilepath = "atlases/black_pawn_death_animation.atlas",
        frameDuration = 0.1f,
        loop = false,
    ),
    override var idleAnimation: SpriteAnimation? = SpriteAnimation(
        atlasFilepath = "atlases/coin_spin_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = true,
    ),
) : BoardObject() {

    open fun springTrap(sprungBy: BoardObject) {
        // Individual behaviour of traps goes here
    }

}
