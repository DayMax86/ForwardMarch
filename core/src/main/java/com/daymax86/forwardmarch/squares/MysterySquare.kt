package com.daymax86.forwardmarch.squares

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.Player
import com.daymax86.forwardmarch.Toast
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import kotlinx.coroutines.launch
import ktx.async.KtxAsync

class MysterySquare(
    override var tileImage: Texture = Texture(Gdx.files.internal("sprites/mystery_square_256.png")),
    override var highlightedTileImage: Texture = Texture(Gdx.files.internal("sprites/mystery_square_256_highlighted.png")),
    override var colour: SquareTypes = SquareTypes.MYSTERY,
    override var clickable: Boolean = false,
    override val contents: MutableList<BoardObject> = mutableListOf(),
    override var stageXpos: Int,
    override var stageYpos: Int,
    override var squareWidth: Int = GameManager.SQUARE_WIDTH.toInt(),
    override var highlight: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var altHighlight: Boolean = false,
) : Square() {

    override fun swapToAltHighlight(swap: Boolean) {
        if (swap) {
            highlightedTileImage =
                Texture(Gdx.files.internal("sprites/mystery_square_256_highlighted.png"))
            altHighlight = true
        } else {
            highlightedTileImage =
                Texture(Gdx.files.internal("sprites/mystery_square_256_highlighted.png"))
            altHighlight = false
        }
    }

    override fun onEnter(obj: BoardObject) {
        super.onEnter(obj)
        when (obj) {
            is Piece -> {
                val rnd = (1..100).random()
                rnd.let { percent ->
                    if (percent in 1..Player.luckStat) { // Positive effects
                        val goodRnd = (1..100).random()
                        goodRnd.let {
                            when (it) {
                                in 1..50 -> {
                                    Player.changeCoinTotal(1)
                                    GameManager.toast = Toast("You gained a coin!")
                                }

                                in 51..75 -> {
                                    Player.changeBombTotal(1)
                                    GameManager.toast = Toast("You gained a bomb!")
                                }

                                else -> {
                                    Player.changeLuck(1)
                                    GameManager.toast = Toast("Luck stat increased!")
                                }

                            }
                        }
                    } else { // Negative effects
                        val badRnd = (1..100).random()
                        badRnd.let {
                            when (it) {
                                in 1..50 -> {
                                    Player.changeCoinTotal(-1)
                                    GameManager.toast = Toast("You lost a coin!")
                                }

                                in 51..75 -> {
                                    Player.changeBombTotal(-1)
                                    GameManager.toast = Toast("You lost a coin!")
                                }

                                else -> {
                                    KtxAsync.launch {
                                        obj.kill()
                                    }
                                    GameManager.toast = Toast("Your piece was destroyed!")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}


