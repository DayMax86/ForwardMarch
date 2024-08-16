package com.daymax86.forwardmarch.board_objects.pieces.defaults

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pickups.Coin
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import kotlin.math.abs

open class PawnDefault(
    // TODO() Provide placeholder image for default pieces
    override var image: Texture = Texture(Gdx.files.internal("sprites/black_pawn_256.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/black_pawn_256_highlighted.png")),
    override var highlight: Boolean = false,
    override var boardXpos: Int = -1,
    override var boardYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var pieceType: PieceTypes = PieceTypes.PAWN,
    override val movement: MutableList<Square> = mutableListOf(),
    override var associatedBoard: Board? = null,
    override var nextBoard: Board? = null,
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

    open var range: Int = 1 // Set a default value for friendly pawn's movement
    // Can be overridden by individual pieces

    override fun getValidMoves(onComplete: () -> Unit): Boolean {
        // TODO() Allow for first-move rule where pawn can move 2 spaces forward. En passant too?
        if (this.associatedBoard != null) { // No need to check if piece is not on a board
            // and this allows for safe !! usage
            this.movement.clear() // Reset movement array
            /* Use piece's XY positions on the board

                    * ----------- PAWN --
                    * ----?-0-?----------
                    * ------X------------
                    * -------------------
                    */

            var upChecked = false

            // UP
            // Manage movement across boards
            var boardUp: Board
            var acrossBoardsUp = false
            if (this.nextBoard != null) {
                for (upIndex in this.boardYpos + 1..this.boardYpos + range) {
                    if (!upChecked) {
                        // For i in range ( pieceY +1 <= pieceY + 4)
                        boardUp =
                            if (upIndex - GameManager.DIMENSIONS < 1) this.associatedBoard!! else this.nextBoard!!.also {
                                acrossBoardsUp = true
                            }
                        boardUp.squaresList.first { square ->
                            // Find the square that is pieceY + 1 in y-axis, and no change in x-axis
                            square.boardXpos == this.boardXpos && square.boardYpos == if (acrossBoardsUp) abs(
                                upIndex - GameManager.DIMENSIONS
                            ) else upIndex
                        }.let {
                            if (it.canBeEntered()) {
                                this.movement.add(it)
                            } else {
                                upChecked = true
                            }
                        }
                    }
                }
            }

            // Determine if the movement will straddle across 2 (or more?) boards
            // For a pawn this will only be the case if it's in the 8th row
            if (this.boardYpos == 8) {
                // Pawn must be at the very top of one board
                if (this.nextBoard != null) {

                    // DIAGONALS //TODO These will need changing if a square's contents can contain more than one thing
                    this.nextBoard!!.squaresList.firstOrNull {
                        it.boardXpos == this.boardXpos - 1 && it.boardYpos == 1
                    }.let { sq ->
                        if (sq?.contents?.isEmpty() == false) {
                            this@PawnDefault.movement.add(sq)
                        }
                    }
                    this.nextBoard!!.squaresList.firstOrNull {
                        it.boardXpos == this.boardXpos + 1 && it.boardYpos == 1
                    }.let { sq ->
                        if (sq?.contents?.isEmpty() == false) {
                            this@PawnDefault.movement.add(sq)
                        }
                    }
                }

            } else { // Must be contained within one board

                // First check for diagonal spaces to see if there are any hostile pieces
                // DIAGONALS //TODO These will need changing if a square's contents can contain more than one thing
                this.associatedBoard!!.squaresList.firstOrNull {
                    it.boardXpos == this.boardXpos - 1 && it.boardYpos == this.boardYpos + 1
                }.let { sq ->
                    if (sq?.contents?.isEmpty() == false) {
                        this@PawnDefault.movement.add(sq)
                    }
                }
                this.associatedBoard!!.squaresList.firstOrNull {
                    it.boardXpos == this.boardXpos + 1 && it.boardYpos == this.boardYpos + 1
                }.let { sq ->
                    if (sq?.contents?.isEmpty() == false) {
                        this@PawnDefault.movement.add(sq)
                    }
                }
            }

            // Remove any occupied squares //TODO Account for pickups
            val toRemoveList: MutableList<Square> = mutableListOf()
            this.movement.forEach { sq ->
                if (this.movement.contains(sq) && !sq.canBeEntered()) {
                    toRemoveList.add(sq)
                }
            }.also {
                toRemoveList.forEach { trsq ->
                    this.movement.remove(trsq)
                }
            }

        }
        onComplete.invoke()
        return this.movement.isNotEmpty() // No valid moves if array is empty
    }
}
