package com.daymax86.forwardmarch.pieces

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array
import com.daymax86.forwardmarch.Board

class BlackPawn(
    override var image: Texture = Texture(Gdx.files.internal("black_pawn_1000.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("black_pawn_1000_highlighted.png")),
) : PawnDefault() {


}
