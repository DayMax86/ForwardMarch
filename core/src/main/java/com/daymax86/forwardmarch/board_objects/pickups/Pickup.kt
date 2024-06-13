package com.daymax86.forwardmarch.board_objects.pickups

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.squares.Square

abstract class Pickup(
    override var associatedBoard: Board?,
    override var highlight: Boolean,
    override var boardXpos: Int,
    override var boardYpos: Int,
    override var clickable: Boolean,
    override var hostile: Boolean,
    override var boundingBox: BoundingBox,
    override var currentPosition: Vector2,
    override var movementTarget: Vector2,
    override var interpolationType: Interpolation = Interpolation.circle
) : BoardObject() {

    open fun initialise() {
        associatedBoard?.let {
            this.updateBoundingBox(
                it.environmentXPos + (boardXpos * GameManager.SQUARE_WIDTH),
                it.environmentYPos + (boardYpos * GameManager.SQUARE_HEIGHT),
                GameManager.SQUARE_WIDTH,
                GameManager.SQUARE_HEIGHT,
            )
        }

        idleAnimation?.source = this
        deathAnimation.source = this
        idleAnimation?.activate()
    }

    open fun use(xPos: Int = boardXpos , yPos: Int = boardYpos, square: Square? = null) {

    }

    override fun kill() {

        GameManager.pickups.remove(this)

        val toRemove: MutableList<() -> Unit> = mutableListOf()
        GameManager.activeAnimations.forEach {
            if (it.source == this) {
                toRemove.add {
                    GameManager.activeAnimations.remove(it)
                }
            }
        }.apply {
            toRemove.forEach { it.invoke() }
        }

        this.associatedBoard.let { board ->
            board?.squaresList?.forEach { square ->
                if (square.contents.contains(this)) {
                    square.contents.remove(this)
                }
            }
        }


    }

}
