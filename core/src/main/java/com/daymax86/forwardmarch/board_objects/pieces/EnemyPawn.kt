package com.daymax86.forwardmarch.board_objects.pieces

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.defaults.PawnDefault

class EnemyPawn(
    override var image: Texture = Texture(Gdx.files.internal("sprites/enemy_pawn_256.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/enemy_pawn_256_highlighted.png")),
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
) : PawnDefault() {
    override var hostile: Boolean = true

    override fun onHover() {
        super.onHover()
        this.highlight = true
        this.getValidMoves().also { exists ->
            if (exists) {
                movement.forEach {
                    it.swapToAltHighlight(true)
                    it.highlight = true
                }
            }
        }
    }

    override fun onExitHover() {
        super.onExitHover()
        this.highlight = false
        this.getValidMoves().also { exists ->
            if (exists) {
                movement.forEach {
                    it.swapToAltHighlight(false)
                    it.highlight = false
                }
            }
        }
    }

    override fun getValidMoves(onComplete: () -> Unit): Boolean {
        if (this.associatedBoard != null) { // No need to check if piece is not on a board
            // and this allows for safe !! usage
            this.movement.clear() // Reset movement array
            /* Use piece's XY positions on the board

                    * ----- ENEMY PAWN --
                    * ----X---X----------
                    * ------0------------
                    * -------------------
                    */

            // DIAGONALS
            if (this.boardYpos == 1) {
                // Pawn will have to move across boards
                try {
                    val previousBoard: Board =
                        GameManager.boards[GameManager.boards.indexOf(this.associatedBoard) - 1]

                    previousBoard.squaresList.firstOrNull {
                        it.boardXpos == this.boardXpos - 1 && it.boardYpos == 8
                    }.let {
                        if (it != null) {
                            this.movement.add(it)
                        }
                    }
                    previousBoard.squaresList.firstOrNull {
                        it.boardXpos == this.boardXpos + 1 && it.boardYpos == 8
                    }.let {
                        if (it != null) {
                            this.movement.add(it)
                        }
                    }

                } catch (e: IndexOutOfBoundsException) {
                    Gdx.app.log("enemies", "There is no previous board!")
                    Gdx.app.log("enemies", "$e")
                }

            } else {
                // Pawn movement contained within one board
                this.associatedBoard!!.squaresList.firstOrNull {
                    it.boardXpos == this.boardXpos - 1 && it.boardYpos == this.boardYpos - 1
                }.let {
                    if (it != null) {
                        this.movement.add(it)
                    }
                }
                this.associatedBoard!!.squaresList.firstOrNull {
                    it.boardXpos == this.boardXpos + 1 && it.boardYpos == this.boardYpos - 1
                }.let {
                    if (it != null) {
                        this.movement.add(it)
                    }
                }
            }
        }
        onComplete.invoke()
        return this.movement.isNotEmpty() // No valid moves if array is empty
    }


}

