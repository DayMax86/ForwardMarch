package com.daymax86.forwardmarch.board_objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.GameObject
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.items.ItemPools
import com.daymax86.forwardmarch.Player
import com.daymax86.forwardmarch.ShopPopup
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.board_objects.pieces.defaults.*
import com.daymax86.forwardmarch.items.base_classes.ShopModifierItem
import com.daymax86.forwardmarch.managers.StageManager

class Shop(
    override var image: Texture = Texture(Gdx.files.internal("sprites/shop.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/shop.png")),
    override var highlight: Boolean = false,
    override var stageXpos: Int = -1,
    override var stageYpos: Int = -1,
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
    override var shopPrice: Int = 0,
    override var infoBox: InfoBox = InfoBox(
        titleText = "Shop",
        thumbnailImage = Texture(Gdx.files.internal("sprites/shop.png")),
        x = boundingBox.min.x,
        y = boundingBox.min.y,
        width = boundingBox.width.toInt(),
        height = boundingBox.height.toInt(),
        description = "Grumpy Frog's emporium. Make sure you bring some cash!",
    ),
) : BoardObject() {

    var displayShopWindow: Boolean = false
    val shopWindow = ShopPopup()

    val shopItems: MutableList<GameObject> = mutableListOf()

    // Remember which objects were clickable before opening shop
    private val clickables: MutableList<BoardObject> = mutableListOf()

    init {
        shopWindow.backgroundImage = Texture(Gdx.files.internal("shop/shop_background.png"))
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
        Player.playerItems.filterIsInstance<ShopModifierItem>().forEach { shopItem ->
            shopItem.applyShopModifier()
        }
        shopItems.add(Bomb())
        shopItems.add(PawnDefault())
        shopItems.add(GameManager.allItems.filter { item ->
            item.itemPools.contains(ItemPools.SHOP)
        }.random())

        when (PieceTypes.entries.random()) {
            PieceTypes.KING -> { /* Can only have one king in a game so do nothing */ }
            PieceTypes.QUEEN -> { shopItems.add(QueenDefault()) }
            PieceTypes.ROOK -> { shopItems.add(RookDefault()) }
            PieceTypes.KNIGHT -> { shopItems.add(KnightDefault()) }
            PieceTypes.BISHOP -> { shopItems.add(BishopDefault()) }
            PieceTypes.PAWN -> { shopItems.add(VilleinDefault()) /* Can always buy pawn so swapped to next-weakest piece */ }
            PieceTypes.PRINCE -> { shopItems.add(PrinceDefault()) }
            PieceTypes.MONK -> { shopItems.add(MonkDefault()) }
            PieceTypes.VILLEIN -> { shopItems.add(VilleinDefault()) }
            PieceTypes.BARON -> { shopItems.add(BaronDefault()) }
            PieceTypes.BARONESS -> { shopItems.add(BaronessDefault()) }
        }

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
                shopToRemove = it.first()
            }
        }.also {
            GameManager.shops.remove(shopToRemove)
            StageManager.stage.getSquare(this.stageXpos, this.stageYpos)?.contents?.remove(this)
        }

        GameManager.currentShop = null
        GameManager.getAllObjects().forEach { obj ->
            obj.clickable = clickables.contains(obj)
        }
        shopWindow.dispose()
    }

}
