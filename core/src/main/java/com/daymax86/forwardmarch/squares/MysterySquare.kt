package com.daymax86.forwardmarch.squares

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import kotlinx.coroutines.launch
import ktx.async.KtxAsync

class MysterySquare(
    override var tileImage: Texture = Texture(Gdx.files.internal("sprites/mystery_square_256.png")),
    override var highlightedTileImage: Texture = Texture(Gdx.files.internal("sprites/mystery_square_256_highlighted.png")),
    override var colour: TileColours = TileColours.OTHER,
    override var clickable: Boolean = false,
    override val contents: MutableList<BoardObject> = mutableListOf(),
    override var boardXpos: Int,
    override var boardYpos: Int,
    override var squareWidth: Int,
    override var highlight: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var associatedBoard: Board,
    override var altHighlight: Boolean = false,
) : Square() {

    override fun swapToAltHighlight(swap: Boolean) {
        if (swap) {
            highlightedTileImage =
                Texture(Gdx.files.internal("sprites/mystery_square_256_highlighted.png"))
            altHighlight = true
        } else {
            highlightedTileImage = Texture(Gdx.files.internal("sprites/mystery_square_256_highlighted.png"))
            altHighlight = false
        }
    }

    override fun onEnter(obj: BoardObject) {
        super.onEnter(obj)
        when (obj) {
            is Piece -> {
                Gdx.app.log("square", "Trapdoor entered by instance of Piece")
                val rnd = (1..10).random()
                rnd.let {
                    when (it) {
                        in 1..3 -> { // Unlucky!
                            Gdx.app.log("square", "Unlucky my friend!")
                            KtxAsync.launch {
                                obj.kill()
                            }
                        }
                        in 4..6 -> {
                            // Do nothing
                            Gdx.app.log("square", "Nothing seems to have happened.?.?.?.")
                        }
                        else -> {
                            // Do something good!
                            Gdx.app.log("square", "You found a coin. Lucky you!")
                            GameManager.coinTotal++
                        }
                    }
                }
            }
        }
    }

}
