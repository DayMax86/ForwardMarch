package com.daymax86.forwardmarch.items

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.ItemPools
import com.daymax86.forwardmarch.Player
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.items.base_classes.StatsModifierItem

class Knightshoe(
    override var image: Texture = Texture(Gdx.files.internal("sprites/items/knightshoe.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/items/knightshoe_highlighted.png")),
    override var highlight: Boolean = false,
    override var clickable: Boolean = false,
    override var boundingBox: BoundingBox = BoundingBox(),
    override var currentPosition: Vector2 = Vector2(),
    override var movementTarget: Vector2 = Vector2(),
    override var interpolationType: Interpolation = Interpolation.linear,
    override var deathAnimation: SpriteAnimation = SpriteAnimation(
        atlasFilepath = "atlases/black_pawn_death_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = false,
    ),
    override var idleAnimation: SpriteAnimation? = SpriteAnimation(
        atlasFilepath = "atlases/coin_spin_animation.atlas",
        frameDuration = GameManager.DEFAULT_ANIMATION_DURATION,
        loop = true,
    ),
    override var itemPools: MutableList<ItemPools> = mutableListOf(ItemPools.SHOP),
    override var shopPrice: Int = 5,
) : StatsModifierItem() {

    override fun applyStatsModifier() {
        Player.changeLuck(15)
    }

}
