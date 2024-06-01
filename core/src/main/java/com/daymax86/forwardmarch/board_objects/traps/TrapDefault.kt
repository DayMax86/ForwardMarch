package com.daymax86.forwardmarch.board_objects.traps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject

open class TrapDefault (
    override var associatedBoard: Board? = null,
    override var image: Texture = Texture(Gdx.files.internal("spike_trap_256.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("spike_trap_256_damage.png")),
    override var highlight: Boolean = false,
    override var boardXpos: Int = -1,
    override var boardYpos: Int = -1,
    override var clickable: Boolean = false,
    override var hostile: Boolean = true,
    override var boundingBox: BoundingBox = BoundingBox(),
    ) : BoardObject() {

}
