package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.ScreenUtils

class GameScreen(private val application: MainApplication) : Screen {

    // Hardcoded constants which affect visuals only
    private val viewWidth = 2000
    private val viewHeight = 2000

    private var camera: OrthographicCamera
    private var environmentSprite = Sprite(Texture(Gdx.files.internal("background_500x750.png")))
    private var windowWidth: Int = 0
    private var windowHeight: Int = 0

    init {
        environmentSprite.setPosition(0f, 0f)
        environmentSprite.setSize(GameManager.ENVIRONMENT_WIDTH, GameManager.ENVIRONMENT_HEIGHT)
        windowWidth = Gdx.graphics.width
        windowHeight = Gdx.graphics.height

        camera = OrthographicCamera(
            (viewWidth).toFloat(), (viewHeight * (viewWidth / viewHeight)).toFloat()
        )
        camera.position.set(
            camera.viewportWidth / 2f,
            ((windowHeight / 2) + GameManager.SQUARE_WIDTH),
            0f
        )
        camera.update()

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val xPos = getMouseEnvironmentPosition()?.x?.toInt()
                val yPos = getMouseEnvironmentPosition()?.y?.toInt()
                if (xPos != null && yPos != null) {
                    if (!GameManager.movementInProgress) {
                        GameManager.boards.forEach {board ->
                            checkSquareCollisions(
                                board.squaresList,
                                xPos,
                                yPos,
                                button
                            )
                        }
                    }
                    checkBoardObjectCollisions(
                        GameManager.pieces,
                        xPos,
                        yPos,
                        button
                    ).apply { GameManager.updateValidMoves() } // Call method only once moves have been made
                }
                return true
            }


            override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
                val xPos = getMouseEnvironmentPosition()?.x?.toInt()
                val yPos = getMouseEnvironmentPosition()?.y?.toInt()
                if (!GameManager.movementInProgress) {
                    GameManager.boards.forEach { board ->
                        getMouseEnvironmentPosition()?.let {
                            checkSquareCollisions(
                                board.squaresList,
                                it.x.toInt(),
                                it.y.toInt()
                            )
                        }
                    }
                }
                if (xPos != null && yPos != null) {
                    checkBoardObjectCollisions(
                        GameManager.pieces,
                        xPos,
                        yPos
                    )
                }
                return true
            }

            override fun keyDown(keycode: Int): Boolean {
                super.keyDown(keycode)
                when (keycode) {
                    (Input.Keys.UP) -> {
                        camera.translate(0f, 50f)
                    }

                    (Input.Keys.LEFT) -> {
                        camera.translate(-50f, 0f)
                    }

                    (Input.Keys.DOWN) -> {
                        camera.translate(0f, -50f)
                    }

                    (Input.Keys.RIGHT) -> {
                        camera.translate(50f, 0f)
                    }
                }
                return true
            }
        }

    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0.8f, 0.8f, 1f)
        camera.update()
        application.batch.projectionMatrix = camera.combined
        application.batch.begin()

        drawBackground()

        var boardsOnScreen = 0
        GameManager.boards.forEach {board ->
            if (board.onScreen) {
                boardsOnScreen++
            }
            drawBoard(board, board.environmentXPos, board.environmentYPos)
        }

        //drawStuff(GameManager.traps)
        drawBoardObjects(GameManager.pieces)
        drawBoardObjects(GameManager.traps)

        application.batch.end()
    }

    private fun drawBackground() {
        application.batch.draw(
            environmentSprite, 0f, 0f, GameManager.ENVIRONMENT_WIDTH, GameManager.ENVIRONMENT_HEIGHT
        )
    }

//    private fun drawPieces(pieces: List<BoardObject>) {
//        pieces.forEach { piece ->
//            val rect = Rectangle().apply {
//                set(
//                    (piece.associatedBoard?.environmentXPos
//                        ?: 0) + (piece.boardXpos * GameManager.SQUARE_WIDTH),
//                    (piece.associatedBoard?.environmentYPos
//                        ?: 0) + (piece.boardYpos * GameManager.SQUARE_HEIGHT),
//                    GameManager.SQUARE_WIDTH,
//                    GameManager.SQUARE_HEIGHT,
//                )
//                piece.updateBoundingBox(x, y, width, height)
//            }
//
//            val img = if (piece.highlight) piece.highlightedImage else piece.image
//
//            application.batch.draw(
//                img,
//                rect.x,
//                rect.y,
//                GameManager.SQUARE_WIDTH,
//                GameManager.SQUARE_HEIGHT
//            )
//        }
//    }

//    private fun drawTraps(traps: Array<BoardObject>) {
//        for (trap in traps) {
//            val rect = Rectangle()
//            rect.set(
//                (trap.associatedBoard?.environmentXPos
//                    ?: 0) + (trap.boardXpos * GameManager.SQUARE_WIDTH),
//                (trap.associatedBoard?.environmentYPos
//                    ?: 0) + (trap.boardYpos * GameManager.SQUARE_HEIGHT),
//                GameManager.SQUARE_WIDTH,
//                GameManager.SQUARE_HEIGHT,
//            )
//            trap.updateBoundingBox(rect.x, rect.y, rect.width, rect.height)
//
//            val img = if (trap.highlight) {
//                trap.highlightedImage
//            } else {
//                trap.image
//            }
//            application.batch.draw(
//                img,
//                rect.x,
//                rect.y,
//                GameManager.SQUARE_WIDTH,
//                GameManager.SQUARE_HEIGHT
//            )
//        }
//    }

    private fun drawBoardObjects(objects: List<BoardObject>) {
        objects.forEach {obj ->
            val rect = Rectangle()
            rect.set(
                (obj.associatedBoard?.environmentXPos
                    ?: 0) + (obj.boardXpos * GameManager.SQUARE_WIDTH),
                (obj.associatedBoard?.environmentYPos
                    ?: 0) + (obj.boardYpos * GameManager.SQUARE_HEIGHT),
                GameManager.SQUARE_WIDTH,
                GameManager.SQUARE_HEIGHT,
            ).apply {
                obj.updateBoundingBox(x, y, width, height)
            }

            val img = if (obj.highlight) {
                obj.highlightedImage
            } else {
                obj.image
            }
            application.batch.draw(
                img,
                rect.x,
                rect.y,
                GameManager.SQUARE_WIDTH,
                GameManager.SQUARE_HEIGHT
            )
        }
    }

    private fun drawBoard(board: Board, startingX: Int, startingY: Int) {
        val rect = Rectangle()
        board.squaresList.forEach {square ->
            rect.set(
                startingX + square.boardXpos * GameManager.SQUARE_WIDTH,
                startingY + square.boardYpos * GameManager.SQUARE_HEIGHT,
                square.squareWidth.toFloat(),
                square.squareWidth.toFloat(),
            ).apply {
                square.updateBoundingBox(x, y, width, height)
            }

            // Check for highlight and use appropriate variable!
            val img = if (square.highlight) {
                square.highlightedTileImage
            } else {
                square.tileImage
            }
            application.batch.draw(
                img, rect.x, rect.y, rect.width, rect.height
            )
        }
    }


    private fun getMouseEnvironmentPosition(): Vector3? {
        return camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
    }

    private fun getMouseBox(mouseX: Int, mouseY: Int): BoundingBox {
        val mouseBox = BoundingBox(
            Vector3(mouseX.toFloat(), mouseY.toFloat(), 0f),
            Vector3(mouseX.toFloat() + 0.1f, mouseY.toFloat() + 0.1f, 0f)
        )
        return mouseBox
    }

    private fun checkSquareCollisions(
        collection: List<Square>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        collection.forEach { square ->
            if (square.boundingBox.contains(getMouseBox(mouseX, mouseY))) {
                square.onHover()
                if (button >= 0) {
                    square.onClick(button)
                }
            } else {
                square.onExitHover()
            }
        }
    }

    // Generic function for any board object
    private fun checkBoardObjectCollisions(
        collection: List<BoardObject>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        collection.forEach { obj ->
            if (obj.boundingBox.contains(getMouseBox(mouseX, mouseY))) {
                obj.onHover()
                if (button >= 0) {
                    obj.onClick(button)
                }
            } else {
                obj.onExitHover()
            }
        }
    }

    // Specific function for pieces
//    private fun checkPieceCollisions(
//        collection: List<Piece>,
//        mouseX: Int,
//        mouseY: Int,
//        button: Int = -1
//    ) {
//        collection.forEach {piece ->
//            if (piece.boundingBox.contains(getMouseBox(mouseX, mouseY))) {
//                piece.onHover()
//                if (button >= 0) {
//                    piece.onClick(button)
//                }
//            } else {
//                piece.onExitHover()
//            }
//        }
//
//    }
//

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = viewWidth.toFloat()
        camera.viewportHeight = (viewHeight * windowHeight / windowWidth).toFloat()
        camera.update()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun show() {
    }

    override fun hide() {
    }

    override fun dispose() {
    }


}
