package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox


class GameHUD(gameScreen: GameScreen) {
    // HUD elements use the OpenGL coordinate system where the origin is in the centre of the screen
    // Draw everything square to have it display correctly
    val hudElements: MutableList<HUDElement> = mutableListOf()
    private val hudCamera = OrthographicCamera(
        gameScreen.windowWidth.toFloat(),
        gameScreen.windowHeight.toFloat()
    )

    init {
        val testHUDElement =
            HUDElement(
                "hud_elements/forward_march_button.png",
                1080 / 2f,
                1920 / 4f,
                150f,
                150f,
                visible = true
            )
        testHUDElement.highlightImage =
            Texture(Gdx.files.internal("hud_elements/forward_march_button_highlighted.png"))
        hudElements.add(testHUDElement)
    }

    // Must be called within a batch's begin and end methods!
    fun drawHUD(batch: SpriteBatch) {
        hudElements.forEach { element ->
            if (element.visible) {
                try {
                    val img = if (element.highlight) element.highlightImage else element.image
                    batch.draw(
                        img, element.x, element.y,
                        element.width * GameManager.aspectRatio,
                        element.height * GameManager.aspectRatio
                    )
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
        var highlightImage = Texture(Gdx.files.internal(texturePath))
        var boundingBox: BoundingBox = BoundingBox()
        var highlight: Boolean = false

        init {
            boundingBox = BoundingBox(
                Vector3(x, y, 0f),
                Vector3(
                    x + width * GameManager.aspectRatio,
                    y + height * GameManager.aspectRatio,
                    0f
                )
            )
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
                    if (!GameManager.marchInProgress) {
                        GameManager.forwardMarch(1)
                    }
                }
            }
        }
    }
}

