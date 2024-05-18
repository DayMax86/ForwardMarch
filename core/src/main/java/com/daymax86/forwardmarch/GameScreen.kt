package com.daymax86.forwardmarch

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils
import com.daymax86.forwardmarch.squares.*

class GameScreen(private val application: MainApplication): Screen {

    private var camera: OrthographicCamera = OrthographicCamera()
    private val testBoard: Board = Board()

    init {
        camera.setToOrtho(false, 1920f, 1080f)
        for (x: Int in 1..testBoard.dimensions) {
            for (y: Int in 1..testBoard.dimensions) {
                val square: Square
                if ((x+y).mod(2) != 0) {
                    square = WhiteSquareDefault(boardXpos = x, boardYpos = y)
                } else {
                    square = BlackSquareDefault(boardXpos = x, boardYpos = y)
                }
                testBoard.SquaresArray.add(square)
            }
        }
    }

    override fun show() {
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0.8f, 0.8f, 1f)
        camera.update()
        application.batch.projectionMatrix = camera.combined

        application.batch.begin();
        application.font.draw(application.batch, "This is the main game screen", 100f, 150f);

        for (square : Square in testBoard.SquaresArray) {
            application.batch.draw(square.tileImage,
                (square.boardXpos * 30).toFloat(),
                (square.boardYpos * 30).toFloat()
            )
        }

        application.batch.end();
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
    }
}
