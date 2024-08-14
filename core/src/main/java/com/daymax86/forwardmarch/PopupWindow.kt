package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Disposable
import com.daymax86.forwardmarch.GameManager.SQUARE_HEIGHT
import com.daymax86.forwardmarch.GameManager.SQUARE_WIDTH

class PopupWindow : Disposable {

    // Hardcoded constants which affect visuals only // TODO Store these in one place - currently repeated across files!
    val viewWidth = 2000
    val viewHeight = 2000

    val font = BitmapFont()
    val batch = SpriteBatch()
    var cam =
        OrthographicCamera((viewWidth).toFloat(), (viewHeight * (viewWidth / viewHeight)).toFloat())
    var backgroundImage: Texture = Texture(Gdx.files.internal("background_500x750.png"))

    fun render() {

        cam.viewportWidth = viewWidth.toFloat()
        cam.viewportHeight = (viewHeight * Gdx.graphics.height / Gdx.graphics.width).toFloat()
        cam.update()

        batch.begin()
        batch.draw(
            backgroundImage,
            GameManager.ENVIRONMENT_WIDTH / 4,
            GameManager.ENVIRONMENT_HEIGHT / 6,
            800f,
            500f
        )
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



        GameManager.currentShop?.shopItems?.let { items ->
            getMouseBox(Gdx.input.x,Gdx.input.y).let { mouseBox ->
                checkPopupCollisions(
                    items,
                    (mouseBox.min.x).toInt(),
                    (mouseBox.min.y + GameManager.SQUARE_HEIGHT / 4).toInt(),
                )
            }
        }


        batch.end()
    }

    private fun getMouseBox(mouseX: Int, mouseY: Int): BoundingBox {
        val mouseBox = BoundingBox(
            Vector3(mouseX.toFloat(), mouseY.toFloat(), 0f),
            Vector3(mouseX.toFloat() + 0.1f, mouseY.toFloat() + 0.1f, 0f)
        )
        return mouseBox
    }

    private fun checkPopupCollisions(
        collection: List<BoardObject>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        collection.forEach { obj ->
            if (obj.boundingBox.contains(getMouseBox(mouseX, mouseY))) {
//                Gdx.app.log("shop", "$obj has been hovered over. MouseX= $mouseX, MouseY= $mouseY")
//                Gdx.app.log(
//                    "shop",
//                    "$obj's bounding box = (${obj.boundingBox.min.x}-${obj.boundingBox.max.x}," +
//                        " ${obj.boundingBox.min.y}-${obj.boundingBox.max.y})"
//                )
                obj.onHover()
                if (button >= 0) {
                    obj.onClick(button)
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
