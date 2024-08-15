package com.daymax86.forwardmarch.board_objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.ShopPopup
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.defaults.BishopDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.PawnDefault

class Shop(
    override var associatedBoard: Board?,
    override var image: Texture = Texture(Gdx.files.internal("sprites/spike_trap_256.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/spike_trap_256.png")),
    override var highlight: Boolean = false,
    override var boardXpos: Int = -1,
    override var boardYpos: Int = -1,
    override var clickable: Boolean = true,
    override var hostile: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var deathAnimation: SpriteAnimation = SpriteAnimation(
        atlasFilepath = "atlases/black_pawn_death_animation.atlas",
        frameDuration = 0.1f,
        loop = false,
    ),
    override var idleAnimation: SpriteAnimation? = SpriteAnimation(
        atlasFilepath = "atlases/coin_spin_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = true,
    ),
    override var currentPosition: Vector2 = Vector2(),
    override var movementTarget: Vector2 = Vector2(),
    override var visuallyStatic: Boolean = true,
    override var interpolationType: Interpolation = Interpolation.linear,
) : BoardObject() {

    var displayShopWindow: Boolean = false
    val shopWindow = ShopPopup()

    val shopItems: MutableList<BoardObject> =
        mutableListOf() // If non-board items can be bought this will need updating!

    // Remember which objects were clickable before opening shop
    private val clickables: MutableList<BoardObject> = mutableListOf()

    init {
        shopWindow.backgroundImage = Texture(Gdx.files.internal("shop/shop_background.png"))
    }

    fun enterShop() {
        // Load a new screen within the game screen that can be interacted with
        stockShop()
        GameManager.currentShop = this
        displayShopWindow = true
        clickables.clear()
        GameManager.getAllObjects().forEach { obj ->
            if (obj.clickable) {
                clickables.add(obj)
            }
            obj.clickable = false
        }
    }

    private fun stockShop() {
        shopItems.add(Bomb())
        shopItems.add(BishopDefault())
        shopItems.forEach { item ->
            item.clickable = true
        }
    }

    fun exitShop() {
        displayShopWindow = false
        this.shopItems.forEach { item ->
            if (item is Piece) {
                clickables.add(item)
            }
        }
        this.shopItems.clear()
        GameManager.currentShop = null
        GameManager.getAllObjects().forEach { obj ->
            obj.clickable = clickables.contains(obj)
        }
        shopWindow.dispose()
    }

}
