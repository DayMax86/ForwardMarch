package com.daymax86.forwardmarch.board_objects.pieces.defaults

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.GameManager.DIMENSIONS
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.Movement
import com.daymax86.forwardmarch.MovementDirections
import com.daymax86.forwardmarch.MovementTypes
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import kotlin.math.abs

open class BishopDefault(
    override var image: Texture = Texture(Gdx.files.internal("sprites/pieces/black_bishop.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/pieces/black_bishop_highlighted.png")),
    override var highlight: Boolean = false,
    override var boardXpos: Int = -1,
    override var boardYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var pieceType: PieceTypes = PieceTypes.ROOK,
    override val movement: MutableList<Square> = mutableListOf(),
    override var associatedBoard: Board? = null,
    override var nextBoard: Board? = null,
    override val movementTypes: List<MovementTypes> = mutableListOf(MovementTypes.BISHOP),
    override val movementDirections: MutableList<MovementDirections> = mutableListOf(
        MovementDirections.UL,
        MovementDirections.UR,
        MovementDirections.DL,
        MovementDirections.DR,
    ),
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
    override var visuallyStatic: Boolean = false,
    override var shopPrice: Int = 4,
    override var infoBox: InfoBox = InfoBox(
        titleText = "Bishop",
        thumbnailImage = Texture(Gdx.files.internal("sprites/pieces/black_bishop.png")),
        x = boundingBox.min.x,
        y = boundingBox.min.y,
        width = boundingBox.width.toInt(),
        height = boundingBox.height.toInt(),
        description = "Bishops can move diagonally along squares of the same colour, and will switch colour when marching forward",
    ),
) : Piece(
    image = image,
    highlightedImage = highlightedImage,
    highlight = highlight,
    boardXpos = boardXpos,
    boardYpos = boardYpos,
    clickable = clickable,
    hostile = hostile,
    boundingBox = boundingBox,
    associatedBoard = associatedBoard,
) {

    init {
        this.soundSet.move.add(Gdx.audio.newSound(Gdx.files.internal("sound/effects/move_default.ogg")))
        this.soundSet.death.add(Gdx.audio.newSound(Gdx.files.internal("sound/effects/death_default.ogg")))
    }

    override var range: Int = 3 // Set a default value for friendly bishop's movement

    override fun getValidMoves(onComplete: () -> Unit): Boolean {
        // and this allows for safe !! usage
        this.movement.clear() // Reset movement array

        Movement.getMovement(
            this,
            this.movementTypes,
            range,
            this.movementDirections
        ).forEach { square ->
            this.movement.add(square)
        }.apply {
            onComplete.invoke()
        }
        return this.movement.isNotEmpty() // No valid moves if array is empty

    }
}
