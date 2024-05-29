package com.daymax86.forwardmarch.board_objects.traps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

class SpikeTrap(
    override var image: Texture = Texture(Gdx.files.internal("spike_trap_1000.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("spike_trap_1000_damage.png")),
) : TrapDefault() {

}
