package com.daymax86.forwardmarch

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.ScreenUtils
import com.daymax86.forwardmarch.boards.StandardBoard

class GameScreen(private val application: MainApplication) : Screen {

    private val boardDimensions: Int = 8
    private var camera: OrthographicCamera = OrthographicCamera()
    private val testBoard = StandardBoard(
        dimensions = boardDimensions,
        tileWidth = application.tileWidth
    )

    init {
        camera.setToOrtho(false, application.windowWidth.toFloat(), application.windowHeight.toFloat())
        application.batch.begin()
        application.batch.end()
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
        val quarterScreen: Float = (application.windowWidth / 8).toFloat() // Why is this 8 instead of 4?
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
            application.batch.draw(
                square.tileImage, quarterScreen + rect.x, rect.y, rect.width, rect.height
            )
        }
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, application.windowWidth.toFloat(), application.windowHeight.toFloat())
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
