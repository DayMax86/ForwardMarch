package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.pieces.BlackPawn
import com.daymax86.forwardmarch.pieces.Piece

class GameScreen(private val application: MainApplication) : Screen {

    private val boardDimensions: Int = 8
    private var camera: OrthographicCamera = OrthographicCamera()
    private val testBoard = StandardBoard(
        dimensions = boardDimensions,
        tileWidth = application.tileWidth
    )
    private val pieces = Array<Piece>()
    private val quarterScreen: Float =
        (application.windowWidth / 8).toFloat() // Why is this 8 instead of 4?
    private val squareVisualWidth: Float = quarterScreen / 2
    private val squareVisualHeight: Float = quarterScreen / 2

    init {
        camera.setToOrtho(
            false,
            application.windowWidth.toFloat(),
            application.windowHeight.toFloat()
        )

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                Gdx.app.log("mouse", "Mouse button = $button")
                checkMouseCollisions(
                    testBoard.squaresArray,
                    screenX,
                    application.windowHeight - screenY,
                    button
                )
                return true
            }

            override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
                checkMouseCollisions(
                    testBoard.squaresArray,
                    screenX,
                    application.windowHeight - screenY
                )
                return true
            }
        }

        val testPawn = BlackPawn()
        testPawn.boardXpos = 3
        testPawn.boardYpos = 3
        pieces.add(testPawn)
        val testPawn2 = BlackPawn()
        testPawn2.boardXpos = 5
        testPawn2.boardYpos = 8
        pieces.add(testPawn2)

    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0.8f, 0.8f, 1f)
        camera.update()
        application.batch.projectionMatrix = camera.combined
        application.batch.begin()
        drawBoard(testBoard)
        drawPieces(pieces)
        application.batch.end()
    }

    private fun drawPieces(pieces: Array<Piece>) {
        // TODO() Adapt this method to allow for multiple boards
        for (piece in pieces) {
            val img = if (piece.highlight) {
                piece.highlightedImage
            } else {
                piece.image
            }
            application.batch.draw(
                img,
                quarterScreen + piece.boardXpos.toFloat() * squareVisualWidth, //TODO() One square over - why?
                piece.boardYpos.toFloat() * squareVisualHeight,
                squareVisualWidth,
                squareVisualHeight
            )
        }
    }

    private fun drawBoard(board: Board) {
        val rect = Rectangle()
        for ((index, square: Square) in board.squaresArray.withIndex()) {
            rect.set(
                quarterScreen + (index.mod(boardDimensions) * squareVisualWidth),
                square.boardYpos * squareVisualHeight,
                squareVisualWidth,
                squareVisualHeight,
            )
            square.updateBoundingBox(rect.x + quarterScreen, rect.y, rect.width, rect.height)
            // Check for highlight and use appropriate variable!
            val img = if (square.highlight) {
                square.highlightedTileImage
            } else {
                square.tileImage
            }
            application.batch.draw(
                img, quarterScreen + rect.x, rect.y, rect.width, rect.height
            )
        }
    }

    private var mouseBox = BoundingBox(Vector3(0f, 0f, 0f), Vector3(0f, 0f, 0f))

    private fun checkMouseCollisions(
        collection: Array<Square>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        mouseBox = BoundingBox(
            Vector3(mouseX.toFloat(), mouseY.toFloat(), 0f),
            Vector3(mouseX.toFloat() + 0.1f, mouseY.toFloat() + 0.1f, 0f)
        )

        for (square in collection) {
            if (square.boundingBox.contains(mouseBox)) {
                square.onHover()
                if (button >= 0) {
                    square.onClick(button)
                }
            } else {
                square.onExitHover()
            }
        }

    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(
            false,
            application.windowWidth.toFloat(),
            application.windowHeight.toFloat()
        )
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

    private fun drawHUD(showDiagnostics: Boolean, x: Int, y: Int) {
        application.batch.begin()
        if (showDiagnostics) {
            application.font.draw(application.batch, "$x,$y", 0f, 0f)
        }
        application.batch.end()
    }


}
