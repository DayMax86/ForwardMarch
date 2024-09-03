package com.daymax86.forwardmarch.squares

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.managers.GameManager

class WhiteSquareDefault(
    override var tileImage: Texture = Texture(Gdx.files.internal("sprites/white_square_256.png")),
    override var highlightedTileImage: Texture = Texture(Gdx.files.internal("sprites/white_square_256_highlighted.png")),
    override var colour: SquareTypes = SquareTypes.WHITE,
    override var clickable: Boolean = false,
    override val contents: MutableList<BoardObject> = mutableListOf(),
    override var boardXpos: Int,
    override var boardYpos: Int,
    override var squareWidth: Int = GameManager.SQUARE_WIDTH.toInt(),
    override var highlight: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var associatedBoard: Board,
    override var altHighlight: Boolean = false,
) : Square() {

    override fun swapToAltHighlight(swap: Boolean) {
        if (swap) {
            highlightedTileImage =
                Texture(Gdx.files.internal("sprites/white_square_256_highlighted_alt.png"))
            altHighlight = true
        } else {
            highlightedTileImage = Texture(Gdx.files.internal("sprites/white_square_256_highlighted.png"))
            altHighlight = false
        }
    }
}
