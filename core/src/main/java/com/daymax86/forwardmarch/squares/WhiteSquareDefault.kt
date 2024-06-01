package com.daymax86.forwardmarch.squares

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.Square
import com.daymax86.forwardmarch.TileColours

class WhiteSquareDefault(
    override var tileImage: Texture = Texture(Gdx.files.internal("white_square_256.png")),
    override var highlightedTileImage: Texture = Texture(Gdx.files.internal("white_square_256_highlighted.png")),
    override var colour: TileColours = TileColours.WHITE,
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
                Texture(Gdx.files.internal("white_square_256_highlighted_alt.png"))
            altHighlight = true
        } else {
            highlightedTileImage = Texture(Gdx.files.internal("white_square_256_highlighted.png"))
            altHighlight = false
        }
    }
}
