package com.daymax86.forwardmarch.board_objects.pieces.defaults

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.Movement
import com.daymax86.forwardmarch.MovementDirections
import com.daymax86.forwardmarch.MovementTypes
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.board_objects.traps.Trap
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.managers.StageManager
import com.daymax86.forwardmarch.squares.Square

class BaronessDefault(
    override var image: Texture = Texture(Gdx.files.internal("sprites/pieces/black_baroness.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/pieces/black_baroness_highlighted.png")),
    override var highlight: Boolean = false,
    override var stageXpos: Int = -1,
    override var stageYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var pieceType: PieceTypes = PieceTypes.BARONESS,
    override val movement: MutableList<Square> = mutableListOf(),
    override val movementTypes: List<MovementTypes> = mutableListOf(
        MovementTypes.ROOK
    ),
    override val movementDirections: MutableList<MovementDirections> = mutableListOf(
        MovementDirections.UP,
        MovementDirections.DOWN,
        MovementDirections.LEFT,
        MovementDirections.RIGHT,
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
    override var shopPrice: Int = 3,
    override var infoBox: InfoBox = InfoBox(
        titleText = "Baroness",
        thumbnailImage = Texture(Gdx.files.internal("sprites/pieces/black_baroness.png")),
        x = boundingBox.min.x,
        y = boundingBox.min.y,
        width = boundingBox.width.toInt(),
        height = boundingBox.height.toInt(),
        description = "The baroness looks after those around her.\n\n" +
            "Any traps within 1 square of the baroness are disarmed. Moves like a rook but with shorter range.",
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

    override var range: Int = 2 // Set a default value for friendly baroness's movement

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

        checkTraps()

        return this.movement.isNotEmpty() // No valid moves if array is empty
    }

    private fun checkTraps() {
        StageManager.stage.squaresList.filter {
            it.contents.filterIsInstance<Trap>().isNotEmpty()
        } // Left with a list of squares containing traps
            .forEach { square ->
                square.contents.filterIsInstance<Trap>().forEach { it.checkNearbyBaroness() }
            }
    }

}

