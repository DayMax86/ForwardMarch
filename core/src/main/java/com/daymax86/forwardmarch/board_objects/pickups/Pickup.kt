package com.daymax86.forwardmarch.board_objects.pickups

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.Player
import com.daymax86.forwardmarch.Toast
import com.daymax86.forwardmarch.managers.PickupManager.pickups
import com.daymax86.forwardmarch.managers.StageManager
import com.daymax86.forwardmarch.squares.Square

abstract class Pickup(
    override var highlight: Boolean,
    override var stageXpos: Int,
    override var stageYpos: Int,
    override var clickable: Boolean,
    override var hostile: Boolean,
    override var boundingBox: BoundingBox,
    override var currentPosition: Vector2,
    override var movementTarget: Vector2,
    override var interpolationType: Interpolation = Interpolation.linear
) : BoardObject() {

    override fun onShopClick(button: Int) {
        super.onShopClick(button)
        if (Player.canAfford(this)) {
            when (this) {
                is Coin -> {
                    Player.changeCoinTotal(1)
                }

                is Bomb -> {
                    Player.changeBombTotal(1)
                }
            }
            // Purchase successful so remove the appropriate amount of money from the player
            Player.changeCoinTotal(-shopPrice)
            if (GameManager.currentShop != null) {
                GameManager.currentShop!!.exitShop()
            }
        } else {
            // Feedback to the player that they don't have enough money
            GameManager.toast =
                Toast(text = "You can't afford this! It costs $shopPrice and you have ${Player.coinTotal}")
        }
    }

    fun initialise() {
        pickups.add(this)
        hideImage = true
        idleAnimation?.source = this
        deathAnimation.source = this
        idleAnimation?.activate()
    }

    open fun use(xPos: Int = stageXpos, yPos: Int = stageYpos, square: Square? = null) {

    }

    override fun kill() {

        pickups.remove(this)

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

        StageManager.stage.getSquare(this.stageXpos, this.stageYpos)?.contents?.remove(this)
    }

}
