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
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.pieces.BlackPawn
import com.daymax86.forwardmarch.pieces.Piece

class GameScreen(private val application: MainApplication) : Screen {

    // Hardcoded constants which affect visuals only
    private val environmentWidth = 2000f
    private val environmentHeight = 3000f
    private val squareEnvironmentWidth = 120f
    private val squareEnvironmentHeight = 120f
    private val edgeBuffer: Float = (environmentWidth / 20)
    private val viewWidth = 2000
    private val viewHeight = 2000

    private val game: GameLogic = GameLogic(
        squareWidth = squareEnvironmentWidth,
        squareHeight = squareEnvironmentHeight,
        edgeBuffer = edgeBuffer,
    )

    private var camera: OrthographicCamera
    private var environmentSprite = Sprite(Texture(Gdx.files.internal("background_2000x3000.png")))
    private var windowWidth: Int = 0
    private var windowHeight: Int = 0

    init {
        environmentSprite.setPosition(0f, 0f)
        environmentSprite.setSize(environmentWidth, environmentHeight)
        windowWidth = Gdx.graphics.width
        windowHeight = Gdx.graphics.height

        camera = OrthographicCamera(
            (viewWidth).toFloat(), (viewHeight * (viewWidth / viewHeight)).toFloat()
        )
        camera.position.set(camera.viewportWidth / 2f, ((windowHeight/2) + squareEnvironmentHeight).toFloat(), 0f)
        camera.update()

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val xPos = getMouseEnvironmentPosition()?.x?.toInt()
                val yPos = getMouseEnvironmentPosition()?.y?.toInt()
                if (xPos != null && yPos != null) {
                    for (board in game.boards) {
                        checkSquareCollisions(
                            board.squaresArray,
                            xPos,
                            yPos,
                            button
                        )
                    }
                    checkPieceCollisions(
                        game.pieces,
                        xPos,
                        yPos,
                        button
                    ).apply { game.updateValidMoves() } // Call method only once moves have been made
                }
                return true
            }


            override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
                val xPos = getMouseEnvironmentPosition()?.x?.toInt()
                val yPos = getMouseEnvironmentPosition()?.y?.toInt()
                for (board in game.boards) {
                    getMouseEnvironmentPosition()?.let {
                        checkSquareCollisions(
                            board.squaresArray,
                            it.x.toInt(),
                            it.y.toInt()
                        )
                    }
                }
                if (xPos != null && yPos != null) {
                    checkPieceCollisions(
                        game.pieces,
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
        for (board in game.boards) {
            if (board.onScreen) {
                boardsOnScreen++
            }
            drawBoard(board, board.environmentXPos, board.environmentYPos)
        }

        drawPieces(game.pieces)
        application.batch.end()
    }

    private fun drawBackground() {
        application.batch.draw(
            environmentSprite, 0f, 0f, environmentWidth, environmentHeight
        )
    }

    private fun drawPieces(pieces: Array<Piece>) {
        for (piece in pieces) {
            val rect = Rectangle()
            rect.set(
                (piece.associatedBoard?.environmentXPos ?: 0) + (piece.boardXpos * squareEnvironmentWidth),
                (piece.associatedBoard?.environmentYPos ?: 0) + (piece.boardYpos * squareEnvironmentHeight),
                squareEnvironmentWidth,
                squareEnvironmentHeight,
            )
            piece.updateBoundingBox(rect.x, rect.y, rect.width, rect.height)

            val img = if (piece.highlight) {
                piece.highlightedImage
            } else {
                piece.image
            }
            application.batch.draw(
                img,
                rect.x,
                rect.y,
                squareEnvironmentWidth,
                squareEnvironmentHeight
            )
        }
    }

    private fun drawBoard(board: Board, startingX: Int, startingY: Int) {
        val rect = Rectangle()
        for (square in board.squaresArray) {
            rect.set(
                startingX + square.boardXpos * squareEnvironmentWidth,
                startingY + square.boardYpos * squareEnvironmentHeight,
                square.squareWidth.toFloat(),
                square.squareWidth.toFloat(),
            )
            square.updateBoundingBox(rect.x, rect.y, rect.width, rect.height)
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
        collection: Array<Square>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        for (square in collection) {
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
        collection: Array<BoardObject>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        for (obj in collection) {
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
    private fun checkPieceCollisions(
        collection: Array<Piece>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        for (piece in collection) {
            if (piece.boundingBox.contains(getMouseBox(mouseX, mouseY))) {
                piece.onHover()
                if (button >= 0) {
                    piece.onClick(button)
                }
            } else {
                piece.onExitHover()
            }
        }

    }


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
