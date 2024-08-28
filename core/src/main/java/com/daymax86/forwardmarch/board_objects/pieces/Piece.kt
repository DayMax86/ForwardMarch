package com.daymax86.forwardmarch.board_objects.pieces

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.AudioManager
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.EnemyManager
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.MovementDirections
import com.daymax86.forwardmarch.MovementTypes
import com.daymax86.forwardmarch.Player
import com.daymax86.forwardmarch.SoundSet
import com.daymax86.forwardmarch.Toast
import com.daymax86.forwardmarch.animations.StickySpriteAnimator
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.board_objects.pickups.Coin
import com.daymax86.forwardmarch.board_objects.pickups.ItemToken
import com.daymax86.forwardmarch.board_objects.pickups.Pickup
import com.daymax86.forwardmarch.board_objects.traps.Trap
import com.daymax86.forwardmarch.inputTypes
import com.daymax86.forwardmarch.items.base_classes.DeathModifierItem
import kotlinx.coroutines.launch
import ktx.async.KtxAsync

abstract class Piece(
    override var associatedBoard: Board?,
    override var image: Texture,
    override var highlightedImage: Texture,
    override var highlight: Boolean,
    override var boardXpos: Int,
    override var boardYpos: Int,
    override var clickable: Boolean,
    override var hostile: Boolean,
    override var boundingBox: BoundingBox,
    override var interpolationType: Interpolation = Interpolation.linear
) : BoardObject() {
    open val movementTypes: List<MovementTypes> = mutableListOf()
    open val movementDirections: MutableList<MovementDirections> = mutableListOf()
    open var range: Int = 0
    override var currentPosition: Vector2 = Vector2()
    override var movementTarget: Vector2 = Vector2()
    open lateinit var pieceType: PieceTypes
    open val movement: MutableList<Square> = mutableListOf()
    open var nextBoard: Board? = null
    var soundSet: SoundSet = SoundSet()

    open fun getValidMoves(onComplete: () -> Unit = {}): Boolean {
        // Return an array of squares into which the piece can move
        // Individual pieces should override this method
        // Update movement variable
        return false // Returns true if valid move(s) available, otherwise false
    }

    override fun onClick(button: Int) {
        if (this.clickable) {
            when (button) {
                inputTypes["LMB"] -> {
                    if (GameManager.selectedPiece == null) {
                        GameManager.selectPiece(this, false)
                    } else {
                        GameManager.deselectPiece()
                    }
                }
            }
        }
    }

    override fun onShopClick(button: Int) {
        if (Player.canAfford(this)) {
            GameManager.pieces.add(this)
            GameManager.selectPiece(this, true)
            GameManager.currentShop!!.exitShop()
        } else {
            // Feedback to the player that they don't have enough money
            GameManager.toast =
                Toast(text = "You can't afford this! It costs $shopPrice and you have ${Player.coinTotal}")
        }
    }

    override fun collide(other: BoardObject, friendlyAttack: Boolean) {
        super.collide(other, friendlyAttack)
        Gdx.app.log("collision", "Other in collision is a $other")
        when (other) {
            is Pickup -> {
                if (other is Coin) {
                    Player.changeCoinTotal(1)
                }
                if (other is Bomb) {
                    if (!other.active) {
                        Player.changeBombTotal(1)
                    }
                }
                if (other is ItemToken) {
                    other.giveItemToPlayer()
                }
                other.kill()
            }

            is Trap -> {
                other.springTrap(this)
            }

            is Shop -> {
                if (!this.hostile) {
                    GameManager.currentShop = other
                    GameManager.currentShop!!.enterShop()
                }
            }

            is Piece -> {
                // When pieces collide, enemies always come out on top unless it's a player attacking on their turn
                // this and other must be of differing hostilities
                if (other.hostile && GameManager.selectedPiece == null) {
                    this.kill()
                } else if (other.hostile && GameManager.selectedPiece != null) {
                    other.kill()
                }
            }
        }
    }

    open fun friendlyAttack(hostileObject: BoardObject) {
        hostileObject.kill()
    }

    open fun enemyAttack() {
        val actionQueue: MutableList<() -> Unit> = mutableListOf()
        this.movement.forEach { square ->
            square.contents.forEach { obj ->
                if (obj is Piece && obj.hostile != this.hostile) {
                    actionQueue.add {
                        this.move(
                            square.boardXpos,
                            square.boardYpos,
                            null
                        ) // What happens across boards?
                        obj.kill()
                    }
                }
            }
        }
        if (actionQueue.isNotEmpty()) {
            actionQueue.random().invoke()
        }
    }

    override fun move(x: Int, y: Int, newBoard: Board?) {
        this.nextBoard = try {
            GameManager.boards[GameManager.boards.indexOf(this.associatedBoard) + 1]
        } catch (e: Exception) {
            null
        }
        super.move(x, y, newBoard)
        if (GameManager.firstMoveComplete) {
            AudioManager.playRandomSound(this.soundSet.move)
            if (!this.hostile) {
                GameManager.moveCounter++
                if (GameManager.moveCounter >= GameManager.moveLimit) {
                    GameManager.moveLimitReached = true
                }
            }
        }
    }

    override fun kill() {

        Player.playerItems.filterIsInstance<DeathModifierItem>().forEach { deathItem ->
            deathItem.applyDeathModifier(this)
        }

        KtxAsync.launch {
            StickySpriteAnimator.activateAnimation(
                deathAnimation.atlasFilepath,
                deathAnimation.frameDuration,
                deathAnimation.loop,
                x = this@Piece.boundingBox.min.x,
                y = this@Piece.boundingBox.min.y,
            )
            AudioManager.playRandomSound(soundSet.death)
        }.invokeOnCompletion {
            if (this.hostile) {
                EnemyManager.enemyPieces.remove(this)
            } else {
                GameManager.pieces.remove(this)
            }
            if (this.associatedBoard != null) {
                this.associatedBoard!!.squaresList.firstOrNull { square ->
                    square.contents.contains(this)
                }.let { sq ->
                    sq?.contents?.remove(this)
                }
            }
        }
    }

}


