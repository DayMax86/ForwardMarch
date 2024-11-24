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
import com.daymax86.forwardmarch.Player
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.items.base_classes.MovementModifierItem
import com.daymax86.forwardmarch.managers.StageManager

open class PawnDefault(
    // TODO() Provide placeholder image for default pieces
    override var image: Texture = Texture(Gdx.files.internal("sprites/pieces/black_pawn.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/pieces/black_pawn_highlighted.png")),
    override var highlight: Boolean = false,
    override var stageXpos: Int = -1,
    override var stageYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var pieceType: PieceTypes = PieceTypes.PAWN,
    override val movement: MutableList<Square> = mutableListOf(),
    override val movementTypes: List<MovementTypes> = mutableListOf(
        MovementTypes.ROOK,
        MovementTypes.BISHOP
    ),
    override val movementDirections: MutableList<MovementDirections> = mutableListOf(
        MovementDirections.UP,
        MovementDirections.UL,
        MovementDirections.UR,
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
    override var shopPrice: Int = 2,
    override var infoBox: InfoBox = InfoBox(
        titleText = "Pawn",
        thumbnailImage = Texture(Gdx.files.internal("sprites/pieces/black_pawn.png")),
        x = boundingBox.min.x,
        y = boundingBox.min.y,
        width = boundingBox.width.toInt(),
        height = boundingBox.height.toInt(),
        description = "A lowly pawn. By default it can move one space forward and attack one space on the diagonal.",
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

    override var range: Int = 1 // Set a default value for friendly pawn's movement
    // Can be overridden by individual pieces

    override fun getValidMoves(onComplete: () -> Unit): Boolean {
        // TODO() Allow for first-move rule where pawn can move 2 spaces forward. En passant too?
        this.movement.clear() // Reset movement array

        var movementModified: Boolean = false

        Player.playerItems.filterIsInstance<MovementModifierItem>().forEach { movementItem ->
            movementItem.applyMovementModifier(this).forEach { square ->
                this.movement.add(square)
            }
            movementModified = true
        }

        if (!movementModified) { // Default if no movement modifications on this piece
            Movement.getMovement(
                this,
                this.movementTypes,
                range,
                this.movementDirections
            ).forEach { square ->
                this.movement.add(square)
            }
        }

        val squaresToRemove: MutableList<Square> = mutableListOf()
        // Filter out the diagonal squares without enemy pieces in them
        // and the square directly above if it contains an enemy piece
        this.movement.firstOrNull { square ->
            square.stageXpos == this.stageXpos - 1 && square.stageYpos == this.stageYpos + 1
        }.let { sq ->
            if (sq != null) {
                if (!sq.containsEnemy()) {
                    squaresToRemove.add(sq)
                }
            }
        }
        this.movement.firstOrNull { square ->
            square.stageXpos == this.stageXpos + 1 && square.stageYpos == this.stageYpos + 1
        }.let { sq ->
            if (sq != null) {
                if (!sq.containsEnemy()) {
                    squaresToRemove.add(sq)
                }
            }
        }
        this.movement.firstOrNull { square ->
            square.stageXpos == this.stageXpos && square.stageYpos == this.stageYpos + 1
        }.let { sq ->
            if (sq != null) {
                if (sq.containsEnemy()) {
                    squaresToRemove.add(sq)
                }
            }
        }

        squaresToRemove.forEach { square ->
            this.movement.remove(square)
        }

        // Check for baron in this row
        var baronInRow = false
        StageManager.stage.squaresList.forEach { square ->
            if (square.stageYpos == this.stageYpos) {
                // In the same row - is there a baron?
                if (square.contents.filterIsInstance<BaronDefault>().isNotEmpty()) {
                    baronInRow = true
                }
            }
        }
        if (baronInRow) {
            val extraMovement: MutableList<Square> = Movement.getMovement(
                this,
                mutableListOf(MovementTypes.ROOK),
                1,
                mutableListOf(MovementDirections.LEFT, MovementDirections.RIGHT)
            )
            extraMovement.forEach { square ->
                if (!this.movement.contains(square)) {
                    this.movement.add(square)
                }
            }
        }

        onComplete.invoke()
        return this.movement.isNotEmpty() // No valid moves if array is empty
    }
}
