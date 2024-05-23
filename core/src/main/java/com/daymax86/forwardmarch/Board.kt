package com.daymax86.forwardmarch

import com.badlogic.gdx.utils.Array

interface Board {

    var dimensions: Int
    var squaresArray: Array<Square>
    var tileWidth: Int
    var onScreen: Boolean

}
