package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox


class GameHUD(gameScreen: GameScreen) {
    // HUD elements use the OpenGL coordinate system where the origin is in the centre of the screen
    val hudElements: MutableList<HUDElement> = mutableListOf()
    private val hudCamera = OrthographicCamera(
        gameScreen.windowWidth.toFloat(),
        gameScreen.windowHeight.toFloat()
    )

    init {
        hudElements.add(
            HUDElement(
                "sprites/mystery_square_256.png",
                1080/2f,
                1920/4f,
                GameManager.SQUARE_WIDTH,
                GameManager.SQUARE_HEIGHT,
                visible = true
            )
        )
    }

    // Must be called within a batch's begin and end methods!
    fun drawHUD(batch: SpriteBatch) {
        hudElements.forEach { element ->
            if (element.visible) {
                try {
                    batch.draw(element.image, element.x, element.y, element.width,
                        element.height * GameManager.aspectRatio)
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
        hudCamera.viewportWidth = screenWidth
        hudCamera.viewportHeight = (screenWidth * screenHeight / screenWidth)
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
        var boundingBox: BoundingBox = BoundingBox()
        var highlight: Boolean = false

        init {
            boundingBox = BoundingBox(Vector3(x, y, 0f), Vector3(x + width, y + height, 0f))
        }

        fun onHover() {
            highlight = true
        }

        fun onExitHover() {
            highlight = false
        }

        fun onClick(button: Int) {
            when (button) {
                inputTypes["LMB"] -> {
                    Gdx.app.log("HUD", "HUD element has been clicked on")
                }
            }
        }
    }
}

