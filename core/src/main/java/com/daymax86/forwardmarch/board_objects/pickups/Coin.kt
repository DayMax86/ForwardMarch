package com.daymax86.forwardmarch.board_objects.pickups

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.animations.SpriteAnimation

class Coin(
    override var image: Texture = Texture(Gdx.files.internal("sprites/alpha.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/alpha.png")),
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
    override var visuallyStatic: Boolean = true,
    override var associatedBoard: Board? = null,
    override var highlight: Boolean = false,
    override var boardXpos: Int = -1,
    override var boardYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var currentPosition: Vector2 = Vector2(),
    override var movementTarget: Vector2 = Vector2(),
    override var shopPrice: Int = 1,
    override var infoBox: InfoBox = InfoBox(
        titleText = "Coin",
        thumbnailImage = Texture(Gdx.files.internal("sprites/coin_front.png")),
        x = boundingBox.min.x,
        y = boundingBox.min.y,
        width = boundingBox.width.toInt(),
        height = boundingBox.height.toInt(),
        description = "For spending in Grumpy Frog's shop",
    ),
) : Pickup(
    associatedBoard = associatedBoard,
    highlight = highlight,
    boardXpos = boardXpos,
    boardYpos = boardYpos,
    clickable = clickable,
    hostile = hostile,
    boundingBox = boundingBox,
    currentPosition = currentPosition,
    movementTarget = movementTarget,
) {

    init {
        initialise()
    }

}
