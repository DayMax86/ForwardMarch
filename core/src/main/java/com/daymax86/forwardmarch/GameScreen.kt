package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.daymax86.forwardmarch.boards.StandardBoard
import kotlin.reflect.typeOf

class GameScreen(private val application: MainApplication) : Screen {

    private val boardDimensions: Int = 8
    private var camera: OrthographicCamera = OrthographicCamera()
    private val testBoard = StandardBoard(
        dimensions = boardDimensions,
        tileWidth = application.tileWidth
    )


    init {
        camera.setToOrtho(
            false,
            application.windowWidth.toFloat(),
            application.windowHeight.toFloat()
        )

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
                Gdx.app.log("input", "Test input - $x,$y - $pointer - $button")
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


    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0.8f, 0.8f, 1f)
        camera.update()
        application.batch.projectionMatrix = camera.combined
        application.batch.begin()
        drawBoard(testBoard)
        application.batch.end()
    }

    private fun drawBoard(board: Board) {
        val quarterScreen: Float =
            (application.windowWidth / 8).toFloat() // Why is this 8 instead of 4?
        val squareVisualWidth: Float = quarterScreen / 2
        val squareVisualHeight: Float = quarterScreen / 2
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

    private fun checkMouseCollisions(collection: Array<Square>, mouseX: Int, mouseY: Int) {
        mouseBox = BoundingBox(
            Vector3(mouseX.toFloat(), mouseY.toFloat(), 0f),
            Vector3(mouseX.toFloat() + 0.1f, mouseY.toFloat() + 0.1f, 0f)
        )

        for (square in collection) {
            if (square.boundingBox.contains(mouseBox)) {
                Gdx.app.log(
                    "collisions",
                    "Mouse has collided! -----------------------------------------"
                )
                Gdx.app.log(
                    "collisions",
                    "square: ${square.boundingBox.min.x}-${square.boundingBox.max.x}"
                )
                Gdx.app.log("collisions", "mouse: ${mouseBox.min.x}-${mouseBox.max.x}")
                square.onHover()
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
