package com.daymax86.forwardmarch.items

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.ItemPools
import com.daymax86.forwardmarch.Movement
import com.daymax86.forwardmarch.MovementDirections
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.items.base_classes.MovementModifierItem
import com.daymax86.forwardmarch.squares.Square

class ReverseCard(
    override var image: Texture = Texture(Gdx.files.internal("sprites/items/reverse_card.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/items/reverse_card_highlighted.png")),
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
) : MovementModifierItem() {

    override fun applyMovementModifier(piece: Piece): MutableList<Square> {
        val dirs: MutableList<MovementDirections> = mutableListOf()
        piece.movementDirections.forEach { d ->
            dirs.add(d)
        }
        dirs.add(MovementDirections.DOWN)

        return Movement.getMovement(
            piece,
            piece.movementType,
            piece.range,
            dirs,
        )
    }

}
