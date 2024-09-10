package com.daymax86.forwardmarch.items

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.BlackPawn
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.defaults.KnightDefault
import com.daymax86.forwardmarch.items.base_classes.DeathModifierItem
import com.daymax86.forwardmarch.managers.PieceManager.pieces
import com.daymax86.forwardmarch.managers.StageManager
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
            var squareLeft: Square? = null
            var squareRight: Square? = null
            StageManager.stage.squaresList.filter { sq ->
                sq.stageYpos == piece.stageYpos
            }.let { filteredList ->
                squareLeft = filteredList.firstOrNull { l ->
                    l.stageXpos == piece.stageXpos - 1
                }
                squareRight = filteredList.firstOrNull { r ->
                    r.stageXpos == piece.stageXpos + 1
                }
            }.also {
                squareLeft.let { sqL ->
                    if (sqL != null) {
                        if (sqL.contents.isEmpty()) {
                            val pawnLeft = BlackPawn()
                            pawnLeft.move(
                                sqL.stageXpos,
                                sqL.stageYpos,
                            )
                            sqL.contents.add(pawnLeft)
                            pieces.add(pawnLeft)
                        }
                    }
                }
                squareRight.let { sqR ->
                    if (sqR != null) {
                        if (sqR.contents.isEmpty()) {
                            val pawnRight = BlackPawn()
                            pawnRight.move(
                                sqR.stageXpos,
                                sqR.stageYpos,
                            )
                            sqR.contents.add(pawnRight)
                            pieces.add(pawnRight)
                        }
                    }
                }
            }
        }
    }
}
