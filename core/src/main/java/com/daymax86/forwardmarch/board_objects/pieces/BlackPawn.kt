package com.daymax86.forwardmarch.board_objects.pieces

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.daymax86.forwardmarch.board_objects.pieces.defaults.PawnDefault

class BlackPawn(
    override var image: Texture = Texture(Gdx.files.internal("black_pawn_256.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("black_pawn_256_highlighted.png")),
) : PawnDefault() {


}
