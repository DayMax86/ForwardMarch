package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox

val font = BitmapFont(Gdx.files.internal("fonts/default.fnt"))

class GameHUD(gameScreen: GameScreen) {
    // HUD elements use the OpenGL coordinate system where the origin is in the centre of the screen
    // Draw everything square to have it display correctly
    val hudElements: MutableList<HUDElement> = mutableListOf()
    private val hudCamera = OrthographicCamera(
        gameScreen.windowWidth.toFloat(),
        gameScreen.windowHeight.toFloat(),
    )

    init {
        // March button + info
        addMarchElements()
        // Pickup info
        addPickupElements()
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

    private fun addMarchElements() {
        val totalMarchesCounterHUDElement =
            HUDElement(
                ElementTypes.TEXT,
                displayText = GameManager.forwardMarchCounter.toString(),
                x = 1080 / 2f,
                y = 1920 / 5f,
                visible = true,
                tag = "marchTotal"
            ) {
                Gdx.app.log("HUD", "HUD marchTotal text clicked")
            }
        hudElements.add(totalMarchesCounterHUDElement)
        val marchCountdownHUDElement =
            HUDElement(
                ElementTypes.TEXT,
                displayText = "Moves used: ${GameManager.moveCounter}/${GameManager.moveLimit}",
                x = 1080 / 2f,
                y = 1920 / 6f,
                visible = true,
                tag = "marchCountdown"
            ) {
                Gdx.app.log("HUD", "HUD marchCountdown text clicked")
            }
        hudElements.add(marchCountdownHUDElement)
        val forwardMarchButtonHUDElement =
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
        forwardMarchButtonHUDElement.highlightImage =
            Texture(Gdx.files.internal("hud_elements/forward_march_button_highlighted.png"))
        hudElements.add(forwardMarchButtonHUDElement)
    }
    private fun addPickupElements() {
        // COINS
        val coinTotalHUDElement =
            HUDElement(
                ElementTypes.IMAGE,
                texturePath = "sprites/coin_front.png",
                x = 1080 / 2f,
                y = (1920 / 6) - 200f,
                width = 50f,
                height = 50f,
                visible = true,
            ) { }
        coinTotalHUDElement.highlightImage =
            Texture(Gdx.files.internal("sprites/coin_back.png"))
        hudElements.add(coinTotalHUDElement)
        val coinTotalTextHUDElement =
            HUDElement(
                ElementTypes.TEXT,
                displayText = "${GameManager.coinTotal}",
                x = (1080 / 2) + 100f,
                y = (1920 / 6) - 150f,
                visible = true,
                tag = "coinTotal",
            ) { }
        hudElements.add(coinTotalTextHUDElement)
        // BOMBS
        val bombTotalHUDElement =
            HUDElement(
                ElementTypes.IMAGE,
                texturePath = "sprites/bomb.png",
                x = 1080 / 2f,
                y = (1920 / 6) - 300f,
                width = 50f,
                height = 50f,
                visible = true,
            ) { }
        bombTotalHUDElement.highlightImage =
            Texture(Gdx.files.internal("sprites/bomb.png"))
        hudElements.add(bombTotalHUDElement)
        val bombTotalTextHUDElement =
            HUDElement(
                ElementTypes.TEXT,
                displayText = "${GameManager.bombTotal}",
                x = (1080 / 2) + 100f,
                y = (1920 / 6) - 250f,
                visible = true,
                tag = "bombTotal",
            ) { }
        hudElements.add(bombTotalTextHUDElement)
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

    class HUDElement(
        elementType: ElementTypes,
        var texturePath: String = "",
        displayText: String = "",
        var tag: String = "", // Used as an identifier for HUD updates
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

        fun update(
            newTexturePath: String = texturePath,
            newDisplayText: String = text,
            newX: Float = x,
            newY: Float = y,
            newWidth: Float = width,
            newHeight: Float = height,
            newVisible: Boolean = visible,
            newOnClickBehaviour: () -> Unit = onClickBehaviour,
        ) {
            texturePath = newTexturePath
            text = newDisplayText
            x = newX
            y = newY
            width = newWidth
            height = newHeight
            visible = newVisible
            onClickBehaviour = newOnClickBehaviour
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

