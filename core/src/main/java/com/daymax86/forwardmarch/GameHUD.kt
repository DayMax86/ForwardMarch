package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox


class GameHUD(gameScreen: GameScreen) {
    // HUD elements use the OpenGL coordinate system where the origin is in the centre of the screen
    // Draw everything square to have it display correctly
    val hudElements: MutableList<HUDElement> = mutableListOf()
    val font = BitmapFont(Gdx.files.internal("fonts/default.fnt"))
    private val hudCamera = OrthographicCamera(
        gameScreen.windowWidth.toFloat(),
        gameScreen.windowHeight.toFloat()
    )

    init {


        val testHUDElement =
            HUDElement(
                ElementTypes.IMAGE,
                texturePath = "hud_elements/forward_march_button.png",
                x = 1080 / 2f,
                y = 1920 / 4f,
                width = 150f,
                height = 150f,
                visible = true,
            ) {
                if (!GameManager.marchInProgress) {
                    GameManager.forwardMarch(1)
                }
            }
        testHUDElement.highlightImage =
            Texture(Gdx.files.internal("hud_elements/forward_march_button_highlighted.png"))
        hudElements.add(testHUDElement)

        val marchCountdownHUDElement =
            HUDElement(
                ElementTypes.TEXT,
                displayText = "Example text",
                x = 1080 / 2f,
                y = 1920 / 5f,
                visible = true,
            ) {
                Gdx.app.log("HUD", "HUD example text clicked")
            }
        hudElements.add(marchCountdownHUDElement)
    }

    // Must be called within a batch's begin and end methods!
    fun drawHUD(batch: SpriteBatch) {
        hudElements.forEach { element ->
            if (element.visible) {
                try {
                    when (element.type) {
                        ElementTypes.IMAGE -> {
                            val img =
                                if (element.highlight) element.highlightImage else element.image
                            batch.draw(
                                img, element.x, element.y,
                                element.width * GameManager.aspectRatio,
                                element.height * GameManager.aspectRatio
                            )
                        }

                        ElementTypes.TEXT -> {
                            font.draw(
                                batch,
                                element.text,
                                element.x,
                                element.y,
                            )
                        }
                    }
                } catch (e: Exception) {
                    Gdx.app.log(
                        "HUD",
                        "Error drawing HUD: " +
                            "Did you try and use the drawHUD method outside of a batch's begin and end methods?" +
                            "Or did you perhaps set the HUD element to the wrong type?"
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

    enum class ElementTypes {
        IMAGE,
        TEXT,
    }

    inner class HUDElement(
        elementType: ElementTypes,
        texturePath: String = "",
        displayText: String = "",
        var x: Float,
        var y: Float,
        var width: Float = 0f,
        var height: Float = 0f,
        var visible: Boolean = false,
        private var onClickBehaviour: () -> Unit,
    ) {
        val type = elementType
        lateinit var image: Texture
        lateinit var highlightImage: Texture
        var text = displayText
        var boundingBox: BoundingBox = BoundingBox()
        var highlight: Boolean = false

        init {
            if (elementType == ElementTypes.TEXT) {
                width = GlyphLayout(font, displayText).width
                height = GlyphLayout(font, displayText).height
                Gdx.app.log("HUD", "HUD example text width = $width, height = $height")
            } else {
                image = Texture(Gdx.files.internal(texturePath))
                highlightImage = Texture(Gdx.files.internal(texturePath))
            }

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

        fun onClick() {
            onClickBehaviour.invoke()
        }
    }
}

