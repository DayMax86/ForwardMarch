package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable
import com.daymax86.forwardmarch.GameManager.SQUARE_HEIGHT
import com.daymax86.forwardmarch.GameManager.SQUARE_WIDTH

class ShopPopup : Disposable {

    // Hardcoded constants which affect visuals only // TODO Store these in one place - currently repeated across files!
    val viewWidth = 2000
    val viewHeight = 2000
    var xPos = (1080 * 1.1).toFloat()
    var yPos = ((1920 / 6) - 280).toFloat()

    val font = BitmapFont()
    val batch = SpriteBatch()
    var cam =
        OrthographicCamera((viewWidth).toFloat(), (viewHeight * (viewWidth / viewHeight)).toFloat())
    var backgroundImage: Texture = Texture(Gdx.files.internal("background_500x750.png"))

    init {
        resize(1920, 1080)
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
                width = GameManager.SQUARE_WIDTH,
                height = GameManager.SQUARE_HEIGHT,
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
            }
            checkPopupCollisions(items)
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
        collection: List<BoardObject>,
        button: Int = -1
    ) {
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
