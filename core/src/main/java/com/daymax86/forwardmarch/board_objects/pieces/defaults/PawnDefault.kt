package com.daymax86.forwardmarch.board_objects.pieces.defaults

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.Square
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes

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
    override var friendly: Boolean = true,
    override val movement: MutableList<Square> = mutableListOf(),
    override var associatedBoard: Board? = null,
    override var nextBoard: Board? = null,
    override var deathAnimation: SpriteAnimation = SpriteAnimation(
        atlasFilepath = "atlases/black_pawn_death_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = false,
    )
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

    override fun getValidMoves(): Boolean { // TODO() This needs to be optimised!
        // TODO() Allow for first-move rule where pawn can move 2 spaces forward. En passant too?
        if (this.associatedBoard != null) { // No need to check if piece is not on a board
            // and this allows for safe !! usage
            this.movement.clear() // Reset movement array
            /* Use piece's XY positions on the board

                    * ----------- PAWN --
                    * ----?-0-?----------
                    * ----0-X-0----------
                    * -------------------
                    */

            // Determine if the movement will straddle across 2 (or more?) boards
            // For a pawn this will only be the case if it's in the 8th row
            if (this.boardYpos == 8) {
                // Pawn must be at the very top of one board
                if (this.nextBoard != null) {
                    // Add normal non-diagonal squares
                    // UP
                    this.nextBoard!!.squaresList.firstOrNull {
                        it.boardXpos == this.boardXpos && it.boardYpos == 1
                    }.let {
                        if (it != null) {
                            this@PawnDefault.movement.add(it)
                        }
                    }

                    // DIAGONALS //TODO These will need changing if a square's contents can contain more than one thing
                    this.nextBoard!!.squaresList.firstOrNull {
                        it.boardXpos == this.boardXpos - 1 && it.boardYpos == 1
                    }.let { sq ->
                        if (sq?.contents?.isEmpty() == true) {
                            this@PawnDefault.movement.add(sq)
                        }
                    }
                    this.nextBoard!!.squaresList.firstOrNull {
                        it.boardXpos == this.boardXpos + 1 && it.boardYpos == 1
                    }.let { sq ->
                        if (sq?.contents?.isEmpty() == true) {
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
                    if (sq?.contents?.isEmpty() == true) {
                        this@PawnDefault.movement.add(sq)
                    }
                }
                this.associatedBoard!!.squaresList.firstOrNull {
                    it.boardXpos == this.boardXpos + 1 && it.boardYpos == this.boardYpos + 1
                }.let { sq ->
                    if (sq?.contents?.isEmpty() == true) {
                        this@PawnDefault.movement.add(sq)
                    }
                }
                // Add normal non-diagonal squares
                // UP
                this.associatedBoard!!.squaresList.firstOrNull {
                    it.boardXpos == this.boardXpos && it.boardYpos == this.boardYpos + 1
                }.let {
                    if (it != null) {
                        this@PawnDefault.movement.add(it)
                    }
                }
            }

            // Remove any occupied squares
            val toRemoveList: MutableList<Square> = mutableListOf()
            this.movement.forEach { sq ->
                if (this.movement.contains(sq) && sq.contents.isNotEmpty()) {
                    toRemoveList.add(sq)
                }
            }.also {
                toRemoveList.forEach { trsq ->
                    this.movement.remove(trsq)
                }
            }

        }
        return this.movement.isNotEmpty() // No valid moves if array is empty
    }
}
