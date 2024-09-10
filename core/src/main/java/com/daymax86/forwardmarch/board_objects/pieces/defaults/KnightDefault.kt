package com.daymax86.forwardmarch.board_objects.pieces.defaults

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.Movement
import com.daymax86.forwardmarch.MovementDirections
import com.daymax86.forwardmarch.MovementTypes
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes

open class KnightDefault(
    override var image: Texture = Texture(Gdx.files.internal("sprites/pieces/black_knight.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/pieces/black_knight_highlighted.png")),
    override var highlight: Boolean = false,
    override var stageXpos: Int = -1,
    override var stageYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var pieceType: PieceTypes = PieceTypes.KNIGHT,
    override val movement: MutableList<Square> = mutableListOf(),
    override val movementTypes: List<MovementTypes> = mutableListOf(MovementTypes.KNIGHT),
    override val movementDirections: MutableList<MovementDirections> = mutableListOf(),
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
    override var shopPrice: Int = 5,
    override var infoBox: InfoBox = InfoBox(
        titleText = "Knight",
        thumbnailImage = Texture(Gdx.files.internal("sprites/pieces/black_knight.png")),
        x = boundingBox.min.x,
        y = boundingBox.min.y,
        width = boundingBox.width.toInt(),
        height = boundingBox.height.toInt(),
        description = "Knights have a unique movement pattern that follow an L shape. They can 'hop over' any pieces.",
    ),
) : Piece(
    image = image,
    highlightedImage = highlightedImage,
    highlight = highlight,
    stageXpos = stageXpos,
    stageYpos = stageYpos,
    clickable = clickable,
    hostile = hostile,
    boundingBox = boundingBox,
) {

    init {
        this.soundSet.move.add(Gdx.audio.newSound(Gdx.files.internal("sound/effects/move_default.ogg")))
        this.soundSet.death.add(Gdx.audio.newSound(Gdx.files.internal("sound/effects/death_default.ogg")))
    }

    override var range: Int = 0 // Set a default value for friendly knight's movement
    // Knight's range works differently so set to 0

    override fun getValidMoves(onComplete: () -> Unit): Boolean {

        this.movement.clear() // Reset movement array

        Movement.getMovement(
            this,
            this.movementTypes,
            range,
            this.movementDirections,
        ).forEach { square ->
            this.movement.add(square)
        }.apply {
            onComplete.invoke()
        }
        return this.movement.isNotEmpty() // No valid moves if array is empty
    }
}
