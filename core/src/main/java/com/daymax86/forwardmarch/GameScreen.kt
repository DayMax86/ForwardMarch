package com.daymax86.forwardmarch

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils

class GameScreen(private val application: MainApplication): Screen {

    private var camera: OrthographicCamera = OrthographicCamera()

    init {
        camera.setToOrtho(false, 1920f, 1080f)
    }

    override fun show() {
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0.8f, 0.8f, 1f)
        camera.update()
        application.batch.projectionMatrix = camera.combined

        application.batch.begin();
        application.font.draw(application.batch, "This is the main game screen", 100f, 150f);
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
