package com.daymax86.forwardmarch.board_objects.traps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

class SpikeTrap(
    override var image: Texture = Texture(Gdx.files.internal("spike_trap_256.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("spike_trap_256_damage.png")),
) : TrapDefault() {

}
