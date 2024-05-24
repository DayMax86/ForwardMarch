package com.daymax86.forwardmarch.squares

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.Square
import com.daymax86.forwardmarch.TileColours

class BlackSquareDefault(
    override var tileImage: Texture = Texture(Gdx.files.internal("black_square_1000.png")),
    override var highlightedTileImage: Texture = Texture(Gdx.files.internal("black_square_1000_highlighted.png")),
    override var colour: TileColours = TileColours.BLACK,
    override var clickable: Boolean = false,
    override var contents: Array<BoardObject> = Array<BoardObject>(),
    override var boardXpos: Int,
    override var boardYpos: Int,
    override var squareWidth: Int,
    override var highlight: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    ) : Square {

}
