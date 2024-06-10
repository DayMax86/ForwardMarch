package com.daymax86.forwardmarch.board_objects.traps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.animations.SpriteAnimation

class SpikeTrap(
    override var image: Texture = Texture(Gdx.files.internal("sprites/spike_trap_256.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/spike_trap_256.png")),
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
) : Trap() {

    override fun springTrap(sprungBy: BoardObject) {
        this.image = Texture(Gdx.files.internal("sprites/spike_trap_256_damage.png"))
        this.highlightedImage = Texture(Gdx.files.internal("sprites/spike_trap_256_damage.png"))
    }

}
