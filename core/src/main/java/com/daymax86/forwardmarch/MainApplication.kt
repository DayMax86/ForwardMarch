package com.daymax86.forwardmarch

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.daymax86.forwardmarch.managers.GameManager
import ktx.async.KtxAsync

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class MainApplication : Game() {

    var windowHeight: Int = 0
    var windowWidth: Int = 0
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont
    lateinit var fps: FrameRateCounter
    lateinit var shapeRenderer: ShapeRenderer
    lateinit var loadingAnimation: LoadingAnimation
    var loading = true

    override fun create() {
        windowHeight = Gdx.graphics.height
        windowWidth = Gdx.graphics.width
        batch = SpriteBatch()
        font = BitmapFont()
        font.data.setScale(3f)
        fps = FrameRateCounter()
        loadingAnimation = LoadingAnimation()
        shapeRenderer = ShapeRenderer()
        KtxAsync.initiate()
        setScreen(GameScreen(this@MainApplication))
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        fps.resize(width, height)
        loadingAnimation.resize(width, height)
    }

    override fun render() {
        super.render()
//        ScreenUtils.clear(0.5f, 0.2f, 0.2f, 1f)
        fps.update()
        fps.render()
        if (loading) { showLoadingAnim() }
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        font.dispose()
        shapeRenderer.dispose()
        loadingAnimation.dispose()
    }

    private fun showLoadingAnim() {
        loadingAnimation.render()
    }

    class LoadingAnimation : Disposable {
        private val font = BitmapFont()
        private val batch = SpriteBatch()
        private var cam = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        fun resize(screenWidth: Int, screenHeight: Int) {
            cam = OrthographicCamera(screenWidth.toFloat(), screenHeight.toFloat())
            cam.translate(screenWidth.toFloat() / 2, screenHeight.toFloat() / 2)
            cam.update()
            batch.projectionMatrix = cam.combined
        }

        fun render() {
            batch.begin()
            font.draw(batch, "Loading...............", 200f, 200f)
            batch.end()
        }

        override fun dispose() {
            font.dispose()
            batch.dispose()
        }

    }

}
