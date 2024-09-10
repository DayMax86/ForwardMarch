package com.daymax86.forwardmarch.board_objects.pieces.enemies

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.Movement
import com.daymax86.forwardmarch.MovementDirections
import com.daymax86.forwardmarch.MovementTypes
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.defaults.RookDefault

class EnemyRook(
    override var image: Texture = Texture(Gdx.files.internal("sprites/enemies/enemy_rook.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/enemies/enemy_rook_highlighted.png")),
    override var deathAnimation: SpriteAnimation = SpriteAnimation(
        atlasFilepath = "atlases/black_pawn_death_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = false,
    ),
    override var idleAnimation: SpriteAnimation? = SpriteAnimation(
        atlasFilepath = "atlases/coin_spin_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = true,
    ),
    override val movementTypes: List<MovementTypes> = mutableListOf(
        MovementTypes.ROOK
    ),
    override val movementDirections: MutableList<MovementDirections> = mutableListOf(
        MovementDirections.UP,
        MovementDirections.DOWN,
        MovementDirections.LEFT,
        MovementDirections.RIGHT,
    ),
) : RookDefault() {
    override var hostile: Boolean = true
    override var visuallyStatic: Boolean = true

    override var range: Int = 3

    override fun getValidMoves(onComplete: () -> Unit): Boolean {
            this.movement.clear() // Reset movement array

            Movement.getMovement(
                this,
                this.movementTypes,
                range,
                this.movementDirections
            ).forEach { square ->
                this.movement.add(square)
            }

        onComplete.invoke()
        return this.movement.isNotEmpty() // No valid moves if array is empty
    }


}
