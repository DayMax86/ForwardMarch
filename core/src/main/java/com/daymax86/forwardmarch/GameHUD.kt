package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch


class GameHUD(gameScreen: GameScreen) {
// HUD elements use the OpenGL coordinate system where the origin is in the centre of the screen
    private val hudElements: MutableList<HUDElement> = mutableListOf()
    private val hudCamera = OrthographicCamera(
        gameScreen.windowWidth.toFloat(),
        gameScreen.windowHeight.toFloat()
    )

    init {
        hudElements.add(
            HUDElement(
                "sprites/mystery_square_256.png",
                ((Gdx.graphics.width/3)).toFloat(),
                ((Gdx.graphics.width/4.5)).toFloat(),
                100f,
                100f,
                visible = true
            )
        )
    }

    // Must be called within a batch's begin and end methods!
    fun drawHUD(batch: SpriteBatch) {
        hudElements.forEach { element ->
            if (element.visible) {
                try {
                    batch.draw(element.image, element.x, element.y, element.width, element.height)
                } catch (e: Exception) {
                    Gdx.app.log(
                        "HUD",
                        "Error drawing HUD: " +
                            "Did you try and use the drawHUD method outside of a batch's begin and end methods?"
                    )
                }
            }
        }
    }

    fun resize(screenWidth: Float, screenHeight: Float) {
        hudCamera.translate(screenWidth, screenHeight)
        hudCamera.update()
    }

    inner class HUDElement(
        texturePath: String,
        var x: Float,
        var y: Float,
        var width: Float,
        var height: Float,
        var visible: Boolean = false,
    ) {
        var image = Texture(Gdx.files.internal(texturePath))
    }
}

