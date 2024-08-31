package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.daymax86.forwardmarch.items.Item

private val font = BitmapFont(
    Gdx.files.internal("fonts/default.fnt"),
    Gdx.files.internal("fonts/default.png"),
    false
)

class GameHUD(gameScreen: GameScreen) {
    // HUD elements use the OpenGL coordinate system where the origin is in the centre of the screen
    // Draw everything square to have it display correctly
    val hudElements: MutableList<HUDElement> = mutableListOf()
    private val hudItemElements: MutableList<HUDElement> = mutableListOf()
    private val hudCamera = OrthographicCamera(
        gameScreen.windowWidth.toFloat(),
        gameScreen.windowHeight.toFloat(),
    )

    init {
        // March button + info
        addMarchElements()
        // Pickup info
        addPickupElements()
        // Player items
        checkForItemChanges()
    }

    private fun checkForItemChanges() {
        if (Player.playerItems.size != hudItemElements.size) {
            hudElements.clear()
            hudItemElements.clear()
            Player.playerItems.forEach { item ->
                addItemElement(item)
            }.also {
                addMarchElements()
                addPickupElements()
                hudItemElements.forEach { hi ->
                    hudElements.add(hi)
                }
            }
        }
    }

    private fun checkForGameOver() {
        if (GameManager.gameOver) {
            val gameOverHUDElement =
                HUDElement(
                    ElementTypes.IMAGE,
                    image = Texture(Gdx.files.internal("hud_elements/game_over.png")),
                    x = -500f,
                    y = -250f,
                    width = 500f,
                    height = 500f,
                    visible = true,
                ) {
                    // Could have a restart game button here perhaps.
                }
            hudElements.add(gameOverHUDElement)
        }
    }

    private fun checkForToast() {
        val toast = GameManager.toast
        if (toast != null) {
            toast.tick()
            hudElements.removeAll { element ->
                element.tag == "toast"
            }
            if (!toast.isFinished()) {
                val toastHUDElement =
                    HUDElement(
                        ElementTypes.TOAST,
                        image = toast.backgroundImage,
                        text = toast.text,
                        x = -575f,
                        y = -900f,
                        width = GlyphLayout(font, toast.text).width,
                        height = GlyphLayout(font, toast.text).height * 2,
                        visible = true,
                        tag = "toast",
                    ) {
                        // On click toast should do nothing
                    }
                hudElements.add(toastHUDElement)
            }
        }
    }


    private fun checkForInfoBox() {
        val infoBox = GameManager.currentInfoBox
        hudElements.removeAll { element ->
            element.tag == "info"
        }
        if (infoBox != null) {
            val hudElement = HUDElement(
                ElementTypes.INFO,
                titleText = GameManager.currentInfoBox!!.titleText,
                description = GameManager.currentInfoBox!!.description,
                image = GameManager.currentInfoBox!!.backgroundImage,
                thumbnail = GameManager.currentInfoBox!!.thumbnailImage,
                x = GameManager.currentInfoBox!!.x,
                y = GameManager.currentInfoBox!!.y,
                width = GameManager.currentInfoBox!!.width.toFloat(),
                height = GameManager.currentInfoBox!!.height.toFloat(),
                visible = true,
                tag = "info",
            ) {
                // OnClick behaviour goes here.
            }
            hudElements.add(hudElement)
        }
    }

    // Must be called within a batch's begin and end methods!
    fun drawHUD(batch: SpriteBatch) {

        checkForGameOver()
        checkForItemChanges()
        checkForToast()
        checkForInfoBox()

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

                        ElementTypes.TOAST -> {
                            val img =
                                if (element.highlight) element.highlightImage else element.image
                            if (GameManager.toast != null) {
                                // Set alpha value to fade message out over time
                                batch.setColor(
                                    batch.color.r,
                                    batch.color.g,
                                    batch.color.b,
                                    1 - (GameManager.toast!!.timeElapsed / GameManager.toast!!.duration) * 1f
                                )
                            }
                            batch.draw(
                                img, element.x, element.y,
                                element.width * GameManager.aspectRatio,
                                element.height * GameManager.aspectRatio,
                            )
                            // Return alpha value to full
                            batch.setColor(
                                batch.color.r,
                                batch.color.g,
                                batch.color.b,
                                1f
                            )

                            font.setColor(
                                255f,
                                255f,
                                255f,
                                1 - (GameManager.toast!!.timeElapsed / GameManager.toast!!.duration) * 1f
                            )
                            font.draw(
                                batch,
                                element.text,
                                element.x,
                                element.y + 2 * GlyphLayout(font, element.text).height,
                            )
                            font.setColor(0f, 0f, 0f, 1f)
                        }

                        ElementTypes.INFO -> {

                            val boxWidth = 400f
                            val boxHeight = 800f
                            //TODO Find some height value for the above which adjusts dynamically according to text content.

                            var mouseX = getMouseEnvironmentPosition(hudCamera)?.x ?: 0f
                            var mouseY = getMouseEnvironmentPosition(hudCamera)?.y ?: 0f

                            var boxX: Float = mouseX - boxWidth
                            var boxY: Float = mouseY

                            if (!element.isInBounds()) {
                                // Is it off the edge of the screen?
                                if (Gdx.input.x - boxWidth < 0) {
                                    // Off the left-hand edge
                                    Gdx.app.log("HUD", "Off the left")
                                    boxX = 0f - GameManager.currentScreenWidth / 2
                                }
                                if (Gdx.input.x > GameManager.currentScreenWidth) {
                                    // Off the right-hand edge
                                    Gdx.app.log("HUD", "Off the right")
                                    boxX = GameManager.currentScreenWidth - boxWidth
                                }
                                if (Gdx.input.y - boxHeight < 0 - boxHeight / 2) {
                                    // Off the top edge
                                    Gdx.app.log("HUD", "Off the top")
                                    boxY = GameManager.currentScreenHeight - boxHeight - 50f
                                }
                            }

                            // Draw background box
                            batch.draw(
                                element.image,
                                boxX,
                                boxY,
                                boxWidth,
                                boxHeight,
                            )
                            // Draw thumbnail image
                            batch.draw(
                                element.thumbnail,
                                boxX + (boxWidth * 0.1f),
                                (boxY + (boxHeight * 0.9) - (element.thumbnail!!.height / 2)).toFloat(),
                                (boxWidth * 0.1 * GameManager.aspectRatio).toFloat(),
                                (boxHeight * 0.1 * GameManager.aspectRatio).toFloat(),
                            )
                            // Write title text
                            font.draw(
                                batch,
                                element.titleText,
                                boxX + (boxWidth * 0.33f),
                                (boxY + (boxHeight) - ((boxHeight * 0.1f * GameManager.aspectRatio) / 2)),
                                0,
                                element.titleText.length,
                                (boxWidth * 0.9f),
                                -1,
                                true,
                            )
                            // Write description
                            font.draw(
                                batch,
                                element.description,
                                boxX + (boxWidth * 0.1f),
                                (boxY + (boxHeight * 0.85f) - (element.thumbnail!!.height / 2)),
                                0,
                                element.description.length,
                                (boxWidth * 0.9).toFloat(),
                                -1,
                                true,
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

    private fun getMouseEnvironmentPosition(cam: OrthographicCamera): Vector3? {
        return cam.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
    }

    private fun addItemElement(item: Item) {
        val itemContainerHUDElement =
            HUDElement(
                ElementTypes.IMAGE,
                image = item.image,
                x = 1080 - 180f,
                y = 120f * Player.playerItems.indexOf(item),
                width = 50f,
                height = 50 * GameManager.aspectRatio,
                visible = true,
                tag = "item",
                associatedObject = item,
            ) {
                Gdx.app.log("HUD", "HUD item ($item) clicked")
            }
        hudItemElements.add(itemContainerHUDElement)
    }

    private fun addMarchElements() {
        val totalMarchesCounterHUDElement =
            HUDElement(
                ElementTypes.TEXT,
                text = GameManager.forwardMarchCounter.toString(),
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
                text = "Moves used: ${GameManager.moveCounter}/${GameManager.moveLimit}",
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
                text = "${Player.coinTotal}",
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
                text = "${Player.bombTotal}",
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
        TOAST,
        INFO,
    }

    class HUDElement(
        elementType: ElementTypes,
        var texturePath: String = "",
        var image: Texture = Texture(Gdx.files.internal("sprites/bomb.png")),
        var thumbnail: Texture? = null,
        var titleText: String = "",
        var description: String = "",
        var text: String = "",
        var tag: String = "", // Used as an identifier for HUD updates
        var x: Float,
        var y: Float,
        var width: Float = 0f,
        var height: Float = 0f,
        var visible: Boolean = false,
        var associatedObject: GameObject? = null,
        private var onClickBehaviour: () -> Unit,
    ) {
        val type = elementType
        var highlightImage: Texture =
            if (texturePath == "") image else Texture(Gdx.files.internal(texturePath))
        var boundingBox: BoundingBox = BoundingBox()
        var highlight: Boolean = false

        init {
            if (elementType == ElementTypes.TEXT) {
                width = GlyphLayout(font, text).width
                height = GlyphLayout(font, text).height
                Gdx.app.log("HUD", "HUD example text width = $width, height = $height")
            } else {
                image = if (texturePath == "") image else Texture(Gdx.files.internal(texturePath))
                highlightImage = if (texturePath == "") highlightImage else image
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
            if (associatedObject != null) {
                associatedObject!!.onHover()
            }
        }

        fun onExitHover() {
            highlight = false
            if (associatedObject != null) {
                associatedObject!!.onExitHover()
            }
        }

        fun onClick() {
            onClickBehaviour.invoke()
        }

        fun isInBounds(): Boolean {
            val box = this.boundingBox
            return !(box.max.x < 1 || box.max.y > GameManager.currentScreenHeight)
        }

    }
}

