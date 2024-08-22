package com.daymax86.forwardmarch.items

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.BlackPawn
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.defaults.KnightDefault
import com.daymax86.forwardmarch.items.base_classes.DeathModifierItem
import com.daymax86.forwardmarch.squares.Square

class VoodooTotem(
    override var image: Texture = Texture(Gdx.files.internal("sprites/items/voodoo_totem.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/items/voodoo_totem_highlighted.png")),
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
    override var infoBox: InfoBox = InfoBox(
        titleText = "Voodoo totem",
        thumbnailImage = Texture(Gdx.files.internal("sprites/items/voodoo_totem.png")),
        x = boundingBox.min.x,
        y = boundingBox.min.y,
        width = boundingBox.width.toInt(),
        height = boundingBox.height.toInt(),
        description = "A strange totem with a mysterious glow, filled with the souls of captured pawns." +
            "\n\nTwo pawns will spawn in the squares to the left and right of a knight when it is destroyed (only if there is space).",
    ),
) : DeathModifierItem() {

    override fun applyDeathModifier(piece: Piece) {
        if (piece is KnightDefault) {
            if (piece.associatedBoard != null) {
                var squareLeft: Square? = null
                var squareRight: Square? = null
                piece.associatedBoard!!.squaresList.filter { sq ->
                    sq.boardYpos == piece.boardYpos
                }.let { filteredList ->
                    squareLeft = filteredList.firstOrNull { l ->
                        l.boardXpos == piece.boardXpos - 1
                    }
                    squareRight = filteredList.firstOrNull { r ->
                        r.boardXpos == piece.boardXpos + 1
                    }
                }.also {
                    squareLeft.let { sqL ->
                        if (sqL != null) {
                            val pawnLeft = BlackPawn()
                            pawnLeft.move(
                                sqL.boardXpos,
                                sqL.boardYpos,
                                sqL.associatedBoard
                            )
                            sqL.contents.add(pawnLeft)
                            GameManager.pieces.add(pawnLeft)
                        }
                    }
                    squareRight.let { sqR ->
                        if (sqR != null) {
                            val pawnRight = BlackPawn()
                            pawnRight.move(
                                sqR.boardXpos,
                                sqR.boardYpos,
                                sqR.associatedBoard
                            )
                            sqR.contents.add(pawnRight)
                            GameManager.pieces.add(pawnRight)
                        }
                    }
                }
            }
        }
    }

}
