package com.daymax86.forwardmarch.squares

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.GameObject
import com.daymax86.forwardmarch.Square
import com.daymax86.forwardmarch.TileColours

class BlackSquareDefault(
    override var tileImage: Texture = Texture(Gdx.files.internal("black_square_1000.png")),
    override var colour: TileColours = TileColours.BLACK,
    override var hostile: Boolean = false,
    override var clickable: Boolean = false,
    override var contents: Array<GameObject> = Array<GameObject>(),
    override var boardXpos: Int,
    override var boardYpos: Int,
    override var tileWidth: Int,
) : Square {

}
