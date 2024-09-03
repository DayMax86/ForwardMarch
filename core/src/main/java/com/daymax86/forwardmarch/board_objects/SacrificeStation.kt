package com.daymax86.forwardmarch.board_objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.ChoicePopup
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.GameObject
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pickups.Coin
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.board_objects.pieces.defaults.BaronDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.BaronessDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.BishopDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.KnightDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.MonkDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.PawnDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.PrinceDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.QueenDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.RookDefault
import com.daymax86.forwardmarch.board_objects.pieces.defaults.VilleinDefault

class SacrificeStation(
    override var associatedBoard: Board?,
    override var image: Texture = Texture(Gdx.files.internal("sprites/sacrifice_station.png")),
    override var highlightedImage: Texture = Texture(Gdx.files.internal("sprites/sacrifice_station.png")),
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
    override var shopPrice: Int = 0,
    override var infoBox: InfoBox = InfoBox(
        titleText = "Sacrifice station",
        thumbnailImage = Texture(Gdx.files.internal("sprites/sacrifice_station.png")),
        x = boundingBox.min.x,
        y = boundingBox.min.y,
        width = boundingBox.width.toInt(),
        height = boundingBox.height.toInt(),
        description = "One piece enters... who knows what will come out?",
    ),
) : BoardObject() {

    var displayChoiceWindow: Boolean = false
    val choiceWindow = ChoicePopup()

    val choices: MutableList<GameObject> = mutableListOf()
    var enteredPiece: Piece? = null

    // Remember which objects were clickable before opening window
    private val clickables: MutableList<BoardObject> = mutableListOf()

    init {
        choiceWindow.backgroundImage =
            Texture(Gdx.files.internal("sacrifice_station_background.png"))
    }

    fun enterStation(piece: Piece) {
        // Load a new screen within the game screen that can be interacted with
        enteredPiece = piece
        loadChoices()
        displayChoiceWindow = true
        clickables.clear()
        GameManager.getAllObjects().forEach { obj ->
            if (obj.clickable) {
                clickables.add(obj)
            }
            obj.clickable = false
        }
    }

    private fun loadChoices() {
//        Player.playerItems.filterIsInstance<SacrificeModifierItem>().forEach { sacrificeItem ->
//            sacrificeItem.applySacrificeModifier()
//        }

//        choices.add(GameManager.allItems.filter { item ->
//            item.itemPools.contains(ItemPools.SACRIFICE)
//        }.random())

        choices.add(Coin())
        choices.add(Bomb())
        if (enteredPiece != null) {
            var noneSelected = true
            while (noneSelected) {
                val randomPieceType = PieceTypes.entries.random()
                when (randomPieceType) {
                    PieceTypes.KING -> { /* Can only have one king in a game so do nothing */
                    }

                    PieceTypes.QUEEN -> {
                        if (enteredPiece!!.shopPrice >= QueenDefault().shopPrice && enteredPiece!!.pieceType != PieceTypes.QUEEN) { // Does this make an instance of QueenDefault? If so this could sneakily fill up memory.
                            choices.add(QueenDefault())
                            noneSelected = false
                        }
                    }

                    PieceTypes.ROOK -> {
                        if (enteredPiece!!.shopPrice >= RookDefault().shopPrice && enteredPiece!!.pieceType != PieceTypes.ROOK) {
                            choices.add(RookDefault())
                            noneSelected = false
                        }
                    }

                    PieceTypes.KNIGHT -> {
                        if (enteredPiece!!.shopPrice >= KnightDefault().shopPrice && enteredPiece!!.pieceType != PieceTypes.KNIGHT) {
                            choices.add(KnightDefault())
                            noneSelected = false
                        }
                    }

                    PieceTypes.BISHOP -> {
                        if (enteredPiece!!.shopPrice >= BishopDefault().shopPrice && enteredPiece!!.pieceType != PieceTypes.BISHOP) {
                            choices.add(BishopDefault())
                            noneSelected = false
                        }
                    }

                    PieceTypes.PAWN -> {
                        if (enteredPiece!!.shopPrice >= PawnDefault().shopPrice && enteredPiece!!.pieceType != PieceTypes.PAWN) {
                            choices.add(PawnDefault())
                            noneSelected = false
                        }
                    }

                    PieceTypes.PRINCE -> {
                        if (enteredPiece!!.shopPrice >= PrinceDefault().shopPrice && enteredPiece!!.pieceType != PieceTypes.PRINCE) {
                            choices.add(PrinceDefault())
                            noneSelected = false
                        }
                    }

                    PieceTypes.MONK -> {
                        if (enteredPiece!!.shopPrice >= MonkDefault().shopPrice && enteredPiece!!.pieceType != PieceTypes.MONK) {
                            choices.add(MonkDefault())
                            noneSelected = false
                        }
                    }

                    PieceTypes.VILLEIN -> {
                        if (enteredPiece!!.shopPrice >= VilleinDefault().shopPrice && enteredPiece!!.pieceType != PieceTypes.VILLEIN) {
                            choices.add(VilleinDefault())
                            noneSelected = false
                        }
                    }

                    PieceTypes.BARON -> {
                        if (enteredPiece!!.shopPrice >= BaronDefault().shopPrice && enteredPiece!!.pieceType != PieceTypes.BARON) {
                            choices.add(BaronDefault())
                            noneSelected = false
                        }
                    }

                    PieceTypes.BARONESS -> {
                        if (enteredPiece!!.shopPrice >= BaronessDefault().shopPrice && enteredPiece!!.pieceType != PieceTypes.BARONESS) {
                            choices.add(BaronessDefault())
                            noneSelected = false
                        }
                    }
                }

                choices.forEach { item ->
                    item.clickable = true
                }
            }
        }
    }

    fun exitStation() {
        displayChoiceWindow = false
        this.choices.forEach { item ->
            if (item is Piece) {
                clickables.add(item)
            }
        }
        if (GameManager.currentStation != null && enteredPiece!= null) {
            GameManager.currentStation!!.enteredPiece!!.kill()
        }

        var stationToRemove: SacrificeStation? = null
        GameManager.stations.filter { station ->
            station == this
        }.let {
            if (it.isNotEmpty()) {
                stationToRemove = it[0]
            }
        }.also {
            GameManager.stations.remove(stationToRemove)
            if (this.associatedBoard != null) {
                this.associatedBoard!!.squaresList.firstOrNull { square ->
                    square.boardXpos == stationToRemove!!.boardXpos && square.boardYpos == stationToRemove!!.boardYpos
                }?.contents?.remove(this)
            }
        }

        GameManager.currentStation = null
        GameManager.getAllObjects().forEach { obj ->
            obj.clickable = clickables.contains(obj)
        }
        choiceWindow.dispose()
    }

}
