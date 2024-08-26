package com.daymax86.forwardmarch.board_objects.pieces.enemies

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.Movement
import com.daymax86.forwardmarch.MovementDirections
import com.daymax86.forwardmarch.MovementTypes
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.defaults.PawnDefault

class EnemyPawn(
    override var image: Texture = Texture(Gdx.files.internal("sprites/enemies/enemy_pawn.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/enemies/enemy_pawn_highlighted.png")),
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
        MovementTypes.BISHOP
    ),
    override val movementDirections: MutableList<MovementDirections> = mutableListOf(
        MovementDirections.DL,
        MovementDirections.DR,
    ),
) : PawnDefault() {
    override var hostile: Boolean = true
    override var visuallyStatic: Boolean = true

    override fun onHover() {
        super.onHover()
        this.highlight = true
        this.getValidMoves().also { exists ->
            if (exists) {
                movement.forEach {
                    it.swapToAltHighlight(true)
                    it.highlight = true
                }
            }
        }
    }

    override fun onExitHover() {
        super.onExitHover()
        this.highlight = false
        this.getValidMoves().also { exists ->
            if (exists) {
                movement.forEach {
                    it.swapToAltHighlight(false)
                    it.highlight = false
                }
            }
        }
    }

    override var range: Int = 1

    override fun getValidMoves(onComplete: () -> Unit): Boolean {
        if (this.associatedBoard != null) { // No need to check if piece is not on a board
            // and this allows for safe !! usage
            this.movement.clear() // Reset movement array

                Movement.getEnemyMovement(
                    this,
                    this.movementTypes,
                    range,
                    this.movementDirections
                ).forEach { square ->
                    this.movement.add(square)
                }
            }

        onComplete.invoke()
        return this.movement.isNotEmpty() // No valid moves if array is empty
    }


}

