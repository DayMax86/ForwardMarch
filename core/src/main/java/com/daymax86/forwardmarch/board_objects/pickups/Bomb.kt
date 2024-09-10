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
    var active: Boolean = false

    init {
        initialise()
    }

    override fun onShopClick(button: Int) {
        super.onShopClick(button)
        Player.changeBombTotal(1)
    }

    override fun onSacrificeClick(button: Int) {
        super.onSacrificeClick(button)
        Player.changeBombTotal(
            (round(GameManager.currentStation?.enteredPiece?.shopPrice?.div(3f) ?: 1f)
                )
        )
        GameManager.currentStation!!.exitStation()
    }

    override fun use(xPos: Int, yPos: Int, square: Square?) {
        active = true
        this.move(xPos, yPos)
    }

    override fun collide(other: BoardObject, friendlyAttack: Boolean) {
        super.collide(other, friendlyAttack)
        if (active) {
            other.kill()
            this.kill()
        }
    }

    fun explode(targetSquare: Square) {
        val actionQueue: MutableList<() -> Unit> = mutableListOf()
        var img = targetSquare.tileImage
        when (targetSquare.colour) {
            SquareTypes.BLACK -> {
                img = Texture(Gdx.files.internal("sprites/black_square_hole_256.png"))
                // Replace the square with a 'hole' square
            }

            SquareTypes.WHITE -> {
                img = Texture(Gdx.files.internal("sprites/white_square_hole_256.png"))
            }

            SquareTypes.MYSTERY -> {}
            SquareTypes.TRAPDOOR -> {}
            SquareTypes.BROKEN -> {}
        }

            StageManager.stage.squaresList.firstOrNull { square ->
                square == targetSquare
            }
        Player.changeBombTotal(-1)
        actionQueue.forEach { it.invoke() }
        }




    override fun kill() {
        super.kill()
        StickySpriteAnimator.activateAnimation(
            atlasFilepath = deathAnimation.atlasFilepath,
            frameDuration = deathAnimation.frameDuration,
            loop = deathAnimation.loop,
            x = deathAnimation.x,
            y = deathAnimation.y,
            width = deathAnimation.width,
            height = deathAnimation.height,
        )
    }
}

