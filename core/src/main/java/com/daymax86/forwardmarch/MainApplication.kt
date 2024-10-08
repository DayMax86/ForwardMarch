package com.daymax86.forwardmarch

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.managers.StageManager
import ktx.async.KtxAsync

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class MainApplication : Game() {

    var windowHeight: Int = 0
    var windowWidth: Int = 0
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont
    lateinit var fps: FrameRateCounter
    lateinit var shapeRenderer: ShapeRenderer

    override fun create() {
        windowHeight = Gdx.graphics.height
        windowWidth = Gdx.graphics.width
        batch = SpriteBatch()
        font = BitmapFont()
        font.data.setScale(3f)
        fps = FrameRateCounter()
        shapeRenderer = ShapeRenderer()
        KtxAsync.initiate()
        StageManager.getStartingBoards()
        setScreen(GameScreen(this@MainApplication))
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        fps.resize(width, height)
    }

    override fun render() {
        super.render()
        fps.update()
        fps.render()
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        font.dispose()
        shapeRenderer.dispose()
    }
}
