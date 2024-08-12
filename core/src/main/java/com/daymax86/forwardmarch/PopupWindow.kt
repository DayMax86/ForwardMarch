package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable

class PopupWindow : Disposable {

    val font = BitmapFont()
    val batch = SpriteBatch()
    var cam = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    var backgroundImage: Texture = Texture(Gdx.files.internal("background_500x750.png"))

    fun resize(screenWidth: Int, screenHeight: Int) {
        cam = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.translate(screenWidth.toFloat() / 2, screenHeight.toFloat() / 2)
        cam.update()
        batch.projectionMatrix = cam.combined
    }

    fun render() {
        batch.begin()
        batch.draw(backgroundImage,100f,100f)
        font.draw(batch, "Test text!!", 120f, 120f)
        batch.end()
    }

    override fun dispose() {
        font.dispose()
        batch.dispose()
    }
}
