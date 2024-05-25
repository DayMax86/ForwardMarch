package com.daymax86.forwardmarch

import com.badlogic.gdx.utils.Array

interface Board {

    var dimensions: Int
    var environmentXPos: Int
    var environmentYPos: Int
    var squaresArray: Array<Square>
    var onScreen: Boolean
    var squareWidth: Int
    var associatedGame: GameLogic

}
