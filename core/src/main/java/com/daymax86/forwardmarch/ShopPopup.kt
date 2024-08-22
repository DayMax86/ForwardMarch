package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Disposable
import com.daymax86.forwardmarch.GameManager.SQUARE_HEIGHT
import com.daymax86.forwardmarch.GameManager.SQUARE_WIDTH
import com.daymax86.forwardmarch.GameManager.currentShop
import com.daymax86.forwardmarch.GameManager.shops

class ShopPopup : Disposable {

    // Hardcoded constants which affect visuals only // TODO Store these in one place - currently repeated across files!
    val viewWidth = 2000
    val viewHeight = 2000
    var xPos = -350f
    var yPos = -450f

    val font = BitmapFont(Gdx.files.internal("fonts/default.fnt"), Gdx.files.internal("fonts/default.png"), false)
    val batch = SpriteBatch()
    var cam =
        OrthographicCamera((viewWidth).toFloat(), (viewHeight * (viewWidth / viewHeight)).toFloat())
    var backgroundImage: Texture = Texture(Gdx.files.internal("background_500x750.png"))
    // Create button to exit shop
    var exitButton = BoundingBox(
        Vector3(xPos, yPos, 0f), Vector3(xPos + 20f, yPos + 20f, 0f)
    )

    init {
        resize(GameManager.currentScreenWidth.toInt(), GameManager.currentScreenHeight.toInt())
    }

    fun render() {

        cam.viewportWidth = viewWidth.toFloat()
        cam.viewportHeight = (viewHeight * Gdx.graphics.height / Gdx.graphics.width).toFloat()
        cam.update()

        batch.begin()
        batch.draw(
            backgroundImage,
            xPos,
            yPos,
            700f,
            450f
        )
        var i = 0
        GameManager.currentShop?.shopItems?.forEach { item ->
            item.updateBoundingBox(
                x = xPos + 75f + (i * SQUARE_WIDTH),
                y = (yPos + 25f),
                width = SQUARE_WIDTH,
                height = SQUARE_HEIGHT,
            )
            i++
        }.apply { i = 0 }





        GameManager.currentShop?.shopItems?.let { items ->
            GameManager.currentShop?.shopItems?.forEach { item ->
                val img = if (item.highlight) {
                    item.highlightedImage
                } else {
                    item.image
                }

                batch.draw(
                    img,
                    item.boundingBox.min.x,
                    item.boundingBox.min.y,
                    SQUARE_WIDTH,
                    SQUARE_HEIGHT
                )

                // Add a price next to each item
                batch.draw(
                    Texture(Gdx.files.internal("sprites/coin_front.png")),
                    item.boundingBox.max.x - (item.boundingBox.width / 2),
                    item.boundingBox.max.y,
                    (item.image.width / 4).toFloat(),
                    (item.image.height / 4).toFloat(),
                )
                font.draw(
                    batch,
                    "${item.shopPrice}",
                    item.boundingBox.max.x - (item.boundingBox.width / 2) + (GlyphLayout(font, item.shopPrice.toString()).width),
                    item.boundingBox.max.y + (GlyphLayout(font, item.shopPrice.toString()).height * 2),
                )

            }
            val exitImg = checkPopupCollisions(items)
            exitButton = BoundingBox(
                Vector3(xPos + 700f, yPos + 450f, 0f), Vector3(xPos + 700f + 50f, yPos + 450f + 50f, 0f)
            )
            batch.draw(
                exitImg,
                exitButton.min.x,
                exitButton.min.y,
                50f,
                50f,
            )
        }

//        // TESTING ----------------------------------------------------
//        val shapeRenderer = ShapeRenderer()
//        shapeRenderer.projectionMatrix = cam.combined
//
//
//        GameManager.currentShop?.shopItems?.let { items ->
//            items.forEach { item ->
//                shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
//                shapeRenderer.color = Color.RED
//                shapeRenderer.rect(
//                    item.boundingBox.min.x,
//                    item.boundingBox.min.y,
//                    SQUARE_WIDTH,
//                    SQUARE_HEIGHT,
//                )
//                shapeRenderer.end()
//            }
//        }
//        // -----------------------------------------------------------

        batch.end()
    }

    private fun getMouseEnvironmentPosition(cam: OrthographicCamera): Vector3? {
        return cam.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
    }

    fun checkPopupCollisions(
        collection: List<GameObject>,
        button: Int = -1
    ): Texture {
        collection.forEach { obj ->
            if (obj.boundingBox.contains(getMouseEnvironmentPosition(cam))) {
                obj.onHover()
                if (button >= 0) {
                    obj.onShopClick(button)
                }
            } else {
                obj.onExitHover()
            }
        }


        var exitImg: Texture
        if (exitButton.contains(getMouseEnvironmentPosition(cam))) {
            exitImg = Texture(Gdx.files.internal("shop/exit_button_highlighted.png"))
            if (button >= 0 && currentShop != null) {
                currentShop!!.exitShop()
            }
        } else {
            exitImg = Texture(Gdx.files.internal("shop/exit_button.png"))
        }
        return exitImg

    }

    fun resize(screenWidth: Int, screenHeight: Int) {
        cam = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.translate(screenWidth.toFloat() / 2, screenHeight.toFloat() / 2)
        cam.update()
        batch.projectionMatrix = cam.combined
    }

    override fun dispose() {
        font.dispose()
        batch.dispose()
    }
}
