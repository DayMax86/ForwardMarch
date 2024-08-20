package com.daymax86.forwardmarch.board_objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.GameObject
import com.daymax86.forwardmarch.ItemPools
import com.daymax86.forwardmarch.ShopPopup
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.defaults.BishopDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.PawnDefault
import com.daymax86.forwardmarch.items.Knightshoe

class Shop(
    override var associatedBoard: Board?,
    override var image: Texture = Texture(Gdx.files.internal("sprites/shop.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/shop.png")),
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

    val shopItems: MutableList<GameObject> = mutableListOf()

    // Remember which objects were clickable before opening shop
    private val clickables: MutableList<BoardObject> = mutableListOf()

    init {
        shopWindow.backgroundImage = Texture(Gdx.files.internal("shop/shop_background.png"))
        this.move(boardXpos, boardYpos, GameManager.boards.elementAt(0)) // For TESTING ------
    }

    fun enterShop() {
        // Load a new screen within the game screen that can be interacted with
        stockShop()
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
        shopItems.add(PawnDefault())
        shopItems.add(GameManager.allItems.filter { item ->
            item.itemPools.contains(ItemPools.SHOP)
        }.random())
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

        var shopToRemove: Shop? = null
        GameManager.shops.filter { shop ->
            shop == this
        }.let {
            if (it.isNotEmpty()) {
                shopToRemove = it[0]
            }
        }.also {
            GameManager.shops.remove(shopToRemove)
            if (this.associatedBoard != null) {
                this.associatedBoard!!.squaresList.firstOrNull { square ->
                    square.boardXpos == shopToRemove!!.boardXpos && square.boardYpos == shopToRemove!!.boardYpos
                }?.contents?.remove(this)
            }
        }

        GameManager.currentShop = null
        GameManager.getAllObjects().forEach { obj ->
            obj.clickable = clickables.contains(obj)
        }
        shopWindow.dispose()
    }

}
