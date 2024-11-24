package com.daymax86.forwardmarch.board_objects.pickups

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.Player
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.animations.StickySpriteAnimator
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.managers.PickupManager
import com.daymax86.forwardmarch.managers.StageManager
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.squares.SquareTypes
import java.lang.Math.round

class Bomb(
    override var image: Texture = Texture(Gdx.files.internal("sprites/bomb.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/bomb_highlighted.png")),
    override var deathAnimation: SpriteAnimation = SpriteAnimation(
        atlasFilepath = "atlases/black_pawn_death_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = false,
    ),
    override var idleAnimation: SpriteAnimation? = SpriteAnimation(
        atlasFilepath = "atlases/bomb_idle_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION * 4f,
        loop = true,
    ),
    override var visuallyStatic: Boolean = true,
    override var highlight: Boolean = false,
    override var stageXpos: Int = -1,
    override var stageYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var currentPosition: Vector2 = Vector2(),
    override var movementTarget: Vector2 = Vector2(),
    override var shopPrice: Int = 1,
    override var infoBox: InfoBox = InfoBox(
        titleText = "Bomb",
        thumbnailImage = Texture(Gdx.files.internal("sprites/bomb.png")),
        x = boundingBox.min.x,
        y = boundingBox.min.y,
        width = boundingBox.width.toInt(),
        height = boundingBox.height.toInt(),
        description = "A handy explosive. Middle-mouse click on a square to bomb it, just make sure you don't fall through the hole it leaves behind!",
    ),
) : Pickup(
    highlight = highlight,
    stageXpos = stageXpos,
    stageYpos = stageYpos,
    clickable = clickable,
    hostile = hostile,
    boundingBox = boundingBox,
    currentPosition = currentPosition,
    movementTarget = movementTarget,
) {

    override fun onSacrificeClick(button: Int) {
        super.onSacrificeClick(button)
        Player.changeBombTotal(
            (round(GameManager.currentStation?.enteredPiece?.shopPrice?.div(3f) ?: 1f)
                )
        )
        GameManager.currentStation!!.exitStation()
    }

    fun explode(targetSquare: Square) {
        val actionQueue: MutableList<() -> Unit> = mutableListOf()
        var img = targetSquare.tileImage
        when (targetSquare.colour) {
            SquareTypes.BLACK -> {
                img = Texture(Gdx.files.internal("sprites/black_square_hole_256.png"))
            }

            SquareTypes.WHITE -> {
                img = Texture(Gdx.files.internal("sprites/white_square_hole_256.png"))
            }

            SquareTypes.MYSTERY -> {}
            SquareTypes.TRAPDOOR -> {}
            SquareTypes.BROKEN -> {}
        }

        targetSquare.contents.forEach { content ->
            actionQueue.add {
                content.kill()
            }
        }

        targetSquare.tileImage =
            img // TODO() This just changes the tile's image, we need a squareTransform method

        Player.changeBombTotal(-1)
        this.kill()
        actionQueue.forEach { it.invoke() }
    }

}

