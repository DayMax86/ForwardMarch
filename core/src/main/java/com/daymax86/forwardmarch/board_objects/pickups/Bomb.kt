package com.daymax86.forwardmarch.board_objects.pickups

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.animations.StickySpriteAnimator
import com.daymax86.forwardmarch.squares.BrokenSquare
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.squares.TileColours

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
    override var associatedBoard: Board? = null,
    override var highlight: Boolean = false,
    override var boardXpos: Int = -1,
    override var boardYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var currentPosition: Vector2 = Vector2(),
    override var movementTarget: Vector2 = Vector2(),
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
    var active: Boolean = false

    init {
        initialise()
    }

    override fun onShopClick(button: Int) {
        GameManager.bombTotal += 1
        GameManager.currentShop!!.exitShop()
    }

    override fun use(xPos: Int, yPos: Int, square: Square?) {
        active = true
        this.move(xPos, yPos, square?.associatedBoard)
        //square?.onEnter(this)
        // It should collide with something and trigger the collision handler
    }

    override fun collide(other: BoardObject) {
        super.collide(other)
        if (active) {
            other.kill()
            this.kill()
        }
    }

    fun explode(targetSquare: Square) {
        var img = targetSquare.tileImage
        when (targetSquare.colour) {
            TileColours.BLACK -> {
                img = Texture(Gdx.files.internal("sprites/black_square_hole_256.png"))
                // Replace the square with a 'hole' square
            }

            TileColours.WHITE -> {
                img = Texture(Gdx.files.internal("sprites/white_square_hole_256.png"))
            }

            TileColours.OTHER -> {}
        }

        GameManager.boards.forEach { board ->
            board.squaresList.firstOrNull { square ->
                square == targetSquare
            }?.let { oldSquare ->
                val oldIndex = board.squaresList.indexOf(oldSquare)
                board.squaresList[oldIndex] =
                    BrokenSquare(
                        tileImage = img,
                        associatedBoard = oldSquare.associatedBoard,
                        boardXpos = oldSquare.boardXpos,
                        boardYpos = oldSquare.boardYpos,
                    )
            }
        }
        GameManager.bombTotal -= 1
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
