package com.daymax86.forwardmarch

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array

enum class TileColours{
    BLACK,
    WHITE,
    READ
}

interface Square {
    var tileImage: Texture
    var colour: TileColours
    var hostile: Boolean
    var clickable: Boolean
    var contents: Array<GameObject>
    var boardXpos: Int
    var boardYpos: Int

    fun updateTileImage(){

    }

}
