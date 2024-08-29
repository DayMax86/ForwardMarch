package com.daymax86.forwardmarch.board_objects.traps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.Movement
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.defaults.BaronessDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.KingDefault

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
    override var currentPosition: Vector2 = Vector2(),
    override var movementTarget: Vector2 = Vector2(),
    override var visuallyStatic: Boolean = true,
    override var interpolationType: Interpolation = Interpolation.linear,
    override var shopPrice: Int = 1,
    override var infoBox: InfoBox = InfoBox(
        titleText = "Trap",
        thumbnailImage = Texture(Gdx.files.internal("sprites/spike_trap_256.png")),
        x = boundingBox.min.x,
        y = boundingBox.min.y,
        width = boundingBox.width.toInt(),
        height = boundingBox.height.toInt(),
        description = "Traps will destroy any piece - both allied and enemy - which move onto them.",
    ),
    var armed: Boolean = true
) : BoardObject() {

    open fun springTrap(sprungBy: BoardObject) {
        // Individual behaviour of traps goes here
    }

    open fun arm() {
        armed = true
    }

    open fun disarm() {
        armed = false
    }

    fun checkNearbyBaroness() {
        val dummyKing = KingDefault(boardXpos = this.boardXpos, boardYpos = this.boardYpos)
        dummyKing.associatedBoard = this.associatedBoard
        dummyKing.nextBoard =
            GameManager.boards[GameManager.boards.indexOf(this.associatedBoard) + 1]
        val surroundingSquares = Movement.getSurroundingSquares(
            dummyKing,
            dummyKing.movementTypes,
            1,
            dummyKing.movementDirections
        )
        if (surroundingSquares.any {
                it.contents.filterIsInstance<BaronessDefault>().isNotEmpty()
            }) {
            this.disarm()
        } else {
            this.arm()
        }

    }

}
