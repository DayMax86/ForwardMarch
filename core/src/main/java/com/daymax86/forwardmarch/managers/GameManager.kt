package com.daymax86.forwardmarch.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.daymax86.forwardmarch.BoardObject
import com.daymax86.forwardmarch.GameState
import com.daymax86.forwardmarch.InfoBox
import com.daymax86.forwardmarch.Toast
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.SacrificeStation
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.items.FakeMoustache
import com.daymax86.forwardmarch.items.Item
import com.daymax86.forwardmarch.items.Knightshoe
import com.daymax86.forwardmarch.items.ReverseCard
import com.daymax86.forwardmarch.items.VoodooTotem
import com.daymax86.forwardmarch.managers.BoardManager.addBoard
import com.daymax86.forwardmarch.managers.BoardManager.addStartingBoard
import com.daymax86.forwardmarch.managers.BoardManager.boards
import com.daymax86.forwardmarch.managers.BoardManager.checkBoardsStatus
import com.daymax86.forwardmarch.managers.EnemyManager.enemyPieces
import com.daymax86.forwardmarch.managers.EnemyManager.traps
import com.daymax86.forwardmarch.managers.PickupManager.pickups
import com.daymax86.forwardmarch.managers.PieceManager.pieces
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.async.KtxAsync


object GameManager {

    const val ENVIRONMENT_WIDTH = 2000f
    const val ENVIRONMENT_HEIGHT = 3000f
    const val SQUARE_WIDTH = 120f
    const val SQUARE_HEIGHT = 120f
    const val DIMENSIONS: Int = 8
    const val DEFAULT_ANIMATION_DURATION: Float = 0.033f
    const val BOARD_STARTING_Y = ((ENVIRONMENT_HEIGHT / 2)).toInt()
    var aspectRatio = 1920 / 1080f

    var currentScreenWidth = 0f
    var currentScreenHeight = 0f

    var gameOver: Boolean = false

    val activeAnimations: MutableList<SpriteAnimation> = mutableListOf()

    val shops: MutableList<Shop> = mutableListOf()
    val stations: MutableList<SacrificeStation> = mutableListOf()
    var currentShop: Shop? = null
    var currentStation: SacrificeStation? = null

    val allItems: MutableList<Item> = mutableListOf()

    var toast: Toast? = null
    var currentInfoBox: InfoBox? = null

    var freezeHighlights: Boolean = false

    var difficultyModifier: Int = 1
    var forwardMarchCounter: Int = 0
    var marchInProgress = false
    var moveCounter: Int = 0
    var moveLimit: Int = 3

    var moveLimitReached: Boolean = false
    var firstMoveComplete: Boolean = false
    private var gameState = GameState()

    var cameraTargetInX: Float = 0f
    var cameraTargetInY: Float = 0f

    init {
        loadAllItems()

        // TESTING ----------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------------------

        addStartingBoard {
            addBoard(difficultyModifier) {
                addBoard(difficultyModifier) {
                    PieceManager.setStartingLayout()
                    getAllObjects()
                }
            }
        }.apply {
            boards.forEach { board -> board.initialiseBoardObjects() }
        }

//        saveGameState()
    }

    fun triggerGameOver() {
        gameOver = true
        getAllObjects().forEach { obj ->
            obj.clickable = false
        }
    }

    private fun loadAllItems() { // TODO This should be read from a file instead of manually listed here
        allItems.add(Knightshoe())
        allItems.add(ReverseCard())
        allItems.add(VoodooTotem())
        allItems.add(FakeMoustache())
    }

//    private fun saveGameState() {
//        gameState.updateGameState(
//            new_pieces = pieces,
//            new_boards = boards,
//            new_pickups = PickupManager.pickups,
//            new_activeAnimations = activeAnimations,
//            new_selectedPiece = selectedPiece,
//            new_freezeHighlights = freezeHighlights,
//            new_difficultyModifier = difficultyModifier,
//            new_forwardMarchCounter = forwardMarchCounter,
//            new_marchInProgress = marchInProgress,
//            new_moveCounter = moveCounter,
//            new_moveLimit = moveLimit,
//            new_moveLimitReached = moveLimitReached,
//            new_firstMoveComplete = firstMoveComplete,
//        )
//    }

    fun forwardMarch(distance: Int) {
        KtxAsync.launch {
            marchInProgress = true
            forwardMarchCounter++
            val movementQueue: MutableList<() -> Unit> = mutableListOf()
            // Make the player pieces move forward one at a time
            // -> Resolve collisions in each of the squares they now occupy
            // Once all movements have been resolved...
            // -> Get valid moves for the enemy pieces
            // -> -> Resolve attacks for the pieces in their new positions.

            // Move all pieces up by one square
            pieces.forEach { piece ->
                val yMovement =
                    if (piece.boardYpos + distance > 8) piece.boardYpos + distance - 8 else piece.boardYpos + distance
                val newBoard =
                    if (piece.boardYpos + distance > 8) boards[boards.indexOf(piece.associatedBoard) + 1] else null
                movementQueue.add {
                    piece.move(
                        piece.boardXpos,
                        yMovement,
                        newBoard
                    )
                }
            }

            movementQueue.forEach {
                it.invoke()
            }

            moveWithinEnvironment()

            checkBoardsStatus()

            moveLimitReached = false
            moveCounter = 0
            marchInProgress = false

            Gdx.app.log("collections", "All objects size: ${getAllObjects().size}")

            delay(500) // Delaying stops pieces like rooks appearing to move diagonally

        }.invokeOnCompletion {
            val actionQueue: MutableList<() -> Unit> = mutableListOf()
            enemyPieces.forEach { enemy ->
                enemy.getValidMoves { actionQueue.add { enemy.enemyAttack() } }
            }.also { // Fulfil the attacks afterwards to avoid concurrent modification
                actionQueue.forEach {
                    it.invoke()
                }
            }

            difficultyModifier = forwardMarchCounter / DIMENSIONS
            Gdx.app.log("forward_march", "Difficulty modifier = $difficultyModifier")
//            saveGameState()
        }
    }

//    fun revertToLastSavedState() { // TODO elements in the lists are pass by reference? We need value
//        pieces.clear()
//        gameState.state_pieces.forEach { pieces.add(it) }
//        boards.clear()
//        gameState.state_boards.forEach { boards.add(it) }
//        PickupManager.pickups.clear()
//        gameState.state_pickups.forEach { PickupManager.pickups.add(it) }
//        activeAnimations.clear()
//        gameState.state_activeAnimations.forEach { activeAnimations.add(it) }
//        selectedPiece = gameState.state_selectedPiece
//        freezeHighlights = gameState.state_freezeHighlights
//        difficultyModifier = gameState.state_difficultyModifier
//        forwardMarchCounter = gameState.state_forwardMarchCounter
//        marchInProgress = gameState.state_marchInProgress
//        moveCounter = gameState.state_moveCounter
//        moveLimit = gameState.state_moveLimit
//        moveLimitReached = gameState.state_moveLimitReached
//        firstMoveComplete = gameState.state_firstMoveComplete
//        saveGameState()
//    }


//    private suspend fun myAppendBoard(
//        difficultyModifier: Int,
//    ) = suspendCoroutine { continuation ->
//        val yPos =
//            (BOARD_STARTING_Y + ((boards.size - 1) * SQUARE_HEIGHT * (DIMENSIONS)) - (SQUARE_HEIGHT)).toInt()
//        val board = VeryEasyBoard1(
//            environmentYPos = yPos,
//        )
//        boards.add(board)
//        Gdx.app.log("manager", "a board has been added. (boards.size = ${boards.size})")
//
//        continuation.resume(Unit)
//    }


    private fun moveWithinEnvironment() {
        // Each Board, and all its contents, must move down by SQUARE_HEIGHT units, while camera remains fixed
        boards.forEach { board ->
            board.environmentYPos -= SQUARE_HEIGHT.toInt()
        }

        getAllObjects().forEach { obj ->
            obj.movementTarget = Vector2(
                obj.currentPosition.x,
                if (obj.visuallyStatic) {
                    obj.currentPosition.y - SQUARE_HEIGHT
                } else {
                    obj.currentPosition.y
                }
            )
            obj.updateBoundingBox()
        }
    }


    fun updateValidMoves() {
        for (piece in pieces) {
            piece.getValidMoves()
        }
        for (enemy in enemyPieces) {
            enemy.getValidMoves()
        }
    }

    fun getAllObjects(): MutableList<BoardObject> {
        val allObjects: MutableList<BoardObject> = mutableListOf()
        pieces.forEach { allObjects.add(it) }
        traps.forEach { allObjects.add(it) }
        enemyPieces.forEach { allObjects.add(it) }
        pickups.forEach { allObjects.add(it) }
        shops.forEach { allObjects.add(it) }
        stations.forEach { allObjects.add(it) }
        return allObjects
    }


}


