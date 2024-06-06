package com.daymax86.forwardmarch.board_objects.pickups

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.animations.SpriteAnimator

class Coin(
    override var associatedBoard: Board?,
    override var image: Texture = Texture(Gdx.files.internal("sprites/alpha.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/alpha.png")),
    override var highlight: Boolean = false,
    override var boardXpos: Int,
    override var boardYpos: Int,
    override var clickable: Boolean,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var deathAnimation: SpriteAnimation = SpriteAnimation(
        atlasFilepath = "atlases/black_pawn_death_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = false,
    ),
    override var idleAnimation: SpriteAnimation? = SpriteAnimation(
        atlasFilepath = "atlases/coin_spin_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION * 2f,
        loop = true,
    ),
) : BoardObject() {

    init {

        this.associatedBoard.let {
            if (it != null) {
                this.updateBoundingBox(
                    it.environmentXPos + (this.boardXpos * GameManager.SQUARE_WIDTH),
                    it.environmentYPos + (this.boardYpos * GameManager.SQUARE_HEIGHT),
                    GameManager.SQUARE_WIDTH,
                    GameManager.SQUARE_HEIGHT,
                )
            }
        }

        SpriteAnimator.activateAnimation( // Explicitly declared so safe asserted non-null call
            this.idleAnimation!!.atlasFilepath,
            this.idleAnimation!!.frameDuration,
            this.idleAnimation!!.loop,
            this.boundingBox.min.x,
            this.boundingBox.min.y
        )
    }

    override fun kill() {

        GameManager.pickups.remove(this)

        val toRemove: MutableList<() -> Unit> = mutableListOf()
        GameManager.activeAnimations.forEach {
            if (it.x == this.boundingBox.min.x && it.y == this.boundingBox.min.y) {
                toRemove.add {
                    GameManager.activeAnimations.remove(it)
                }
            }
        }.apply {
            toRemove.forEach { it.invoke() }
        }

        this.associatedBoard.let { board ->
            board?.squaresList?.forEach { square ->
                if (square.contents.contains(this)) {
                    square.contents.remove(this)
                }
            }
        }


    }

}
