package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.ScreenUtils
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.animations.StickySpriteAnimator
import com.daymax86.forwardmarch.managers.AudioManager
import com.daymax86.forwardmarch.managers.EnemyManager
import com.daymax86.forwardmarch.managers.FileManager
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.managers.GameManager.ENVIRONMENT_HEIGHT
import com.daymax86.forwardmarch.managers.GameManager.ENVIRONMENT_WIDTH
import com.daymax86.forwardmarch.managers.GameManager.SQUARE_HEIGHT
import com.daymax86.forwardmarch.managers.GameManager.SQUARE_WIDTH
import com.daymax86.forwardmarch.managers.GameManager.cameraTargetInX
import com.daymax86.forwardmarch.managers.GameManager.cameraTargetInY
import com.daymax86.forwardmarch.managers.GameManager.currentShop
import com.daymax86.forwardmarch.managers.GameManager.currentStation
import com.daymax86.forwardmarch.managers.PieceManager.pieces
import com.daymax86.forwardmarch.managers.StageManager
import com.daymax86.forwardmarch.managers.StageManager.stage
import com.daymax86.forwardmarch.squares.Square
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.graphics.lerpTo

class GameScreen(private val application: MainApplication) : Screen {

    // Hardcoded constants which affect visuals only
    val viewWidth = 2000
    val viewHeight = 2000

    private var environmentSprite = Sprite(Texture(Gdx.files.internal("background_500x750.png")))
    var windowWidth: Int = 0
    var windowHeight: Int = 0
    private val gameHUD: GameHUD = GameHUD(this)
    private val hudBatch = SpriteBatch()
    val gameCamera = OrthographicCamera(
        (viewWidth).toFloat(), (viewHeight * (viewWidth / viewHeight)).toFloat()
    )
    val hudCamera = OrthographicCamera(
        (viewWidth).toFloat(), (viewHeight * (viewWidth / viewHeight)).toFloat()
    )

    init {

        environmentSprite.setPosition(0f, 0f)
        environmentSprite.setSize(ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT)
        windowWidth = Gdx.graphics.width
        windowHeight = Gdx.graphics.height

        cameraTargetInX = gameCamera.viewportWidth / 2f
        cameraTargetInY = ((ENVIRONMENT_HEIGHT / 2) + (windowHeight / 2) + SQUARE_HEIGHT)
        gameCamera.position.set(cameraTargetInX, cameraTargetInY, 0f)

        if (!GameManager.isLoading) {

            Gdx.input.inputProcessor = object : InputAdapter() {
                override fun touchDown(
                    screenX: Int,
                    screenY: Int,
                    pointer: Int,
                    button: Int
                ): Boolean {
                    val xPos = getMouseEnvironmentPosition(gameCamera)?.x?.toInt()
                    val yPos = getMouseEnvironmentPosition(gameCamera)?.y?.toInt()
                    if (xPos != null && yPos != null) {

                        if (GameManager.currentInfoBox != null) {
                            // Showing an info box so disable it when any mouse button pressed
                            GameManager.currentInfoBox = null
                        }

                        checkSquareCollisions(
                            StageManager.stage.squaresList,
                            xPos,
                            yPos,
                            button
                        )

                        checkBoardObjectCollisions(
                            GameManager.getAllObjects(),
                            xPos,
                            yPos,
                            button
                        )

                        val hudXPos = getMouseEnvironmentPosition(hudCamera)?.x?.toInt()
                        val hudYPos = getMouseEnvironmentPosition(hudCamera)?.y?.toInt()
                        checkHUDCollisions(
                            gameHUD.hudElements,
                            hudXPos ?: 0,
                            hudYPos ?: 0,
                            button,
                        )

                    }

                    if (GameManager.currentShop != null) {
                        currentShop!!.shopItems.let { items ->
                            currentShop!!.shopWindow.checkPopupCollisions(items, button)
                        }
                    }

                    if (GameManager.currentStation != null) {
                        currentStation!!.choices.let { choices ->
                            currentStation!!.choiceWindow.checkPopupCollisions(choices, button)
                        }
                    }

                    return true
                }

                override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
                    val xPos = getMouseEnvironmentPosition(gameCamera)?.x?.toInt()
                    val yPos = getMouseEnvironmentPosition(gameCamera)?.y?.toInt()

                    getMouseEnvironmentPosition(gameCamera)?.let {
                        checkSquareCollisions(
                            StageManager.stage.squaresList,
                            it.x.toInt(),
                            it.y.toInt()
                        )
                    }

                    if (xPos != null && yPos != null) {
                        checkBoardObjectCollisions(
                            GameManager.getAllObjects(),
                            xPos,
                            yPos,
                        )
                    }

                    val hudXPos = getMouseEnvironmentPosition(hudCamera)?.x?.toInt()
                    val hudYPos = getMouseEnvironmentPosition(hudCamera)?.y?.toInt()
                    checkHUDCollisions(
                        gameHUD.hudElements,
                        hudXPos ?: 0,
                        hudYPos ?: 0,
                    )

                    return true
                }

                override fun keyDown(keycode: Int): Boolean {
                    super.keyDown(keycode)
                    when (keycode) {
                        // --------- FOR TESTING ONLY ---------- //
                        (Input.Keys.F) -> {
                            if (!GameManager.marchInProgress) {
                                GameManager.forwardMarch(1)
                            }
                        }

                        (Input.Keys.T) -> {
                            // TESTING ----------------
                            FileManager.generateBoardFile(1)
                            // ------------------------
                        }

                    }
                    return true
                }
            }
        }
    }

    private fun updateCamera() {
        gameCamera.lerpTo(Vector2(cameraTargetInX, cameraTargetInY), 0.1f)
        gameCamera.update()
    }

    private fun showLoadingAnimation(show: Boolean) {
        application.loading = show
    }

    private fun loading(): Boolean {
        return GameManager.isLoading
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0.8f, 0.8f, 1f)
        updateCamera()
        application.batch.projectionMatrix = gameCamera.combined
        application.batch.begin()

        if (!loading()) {
            showLoadingAnimation(false)

            drawBackground()

            drawStageAndContents()

            drawAnimations(GameManager.activeAnimations)

            checkShopRender()
            checkStationRender()

            hudBatch.projectionMatrix = hudCamera.combined
            hudBatch.begin()
            drawHUD()
            hudBatch.end()

        } else {
            // Show a loading screen/animation instead
            showLoadingAnimation(true)
        }
        application.batch.end()
    }


    private fun checkShopRender() {
        if (currentShop != null) {
            if (currentShop!!.displayShopWindow) {
                try {
                    currentShop!!.shopWindow.render()
                } catch (e: Exception) {
                    Gdx.app.log(
                        "shop",
                        "Problem calling render on shop (is currentShop set to null properly?)"
                    )
                }
            }
        }
    }

    private fun checkStationRender() {
        if (currentStation != null) {
            if (currentStation!!.displayChoiceWindow) {
                try {
                    currentStation!!.choiceWindow.render()
                } catch (e: Exception) {
                    Gdx.app.log(
                        "station",
                        "Problem calling render on station (is currentStation set to null properly?)"
                    )
                }
            }
        }
    }

    private fun drawHUD() {
        // Update any HUD elements here
        gameHUD.hudElements.firstOrNull {
            it.tag == "marchCountdown"
        }?.update(
            newDisplayText = "Moves used: ${GameManager.moveCounter}/${GameManager.moveLimit}"
        )
        gameHUD.hudElements.firstOrNull {
            it.tag == "marchTotal"
        }?.update(
            newDisplayText = "Forward marches: ${GameManager.forwardMarchCounter}"
        )
        gameHUD.hudElements.firstOrNull {
            it.tag == "coinTotal"
        }?.update(
            newDisplayText = "${Player.coinTotal}"
        )
        gameHUD.hudElements.firstOrNull {
            it.tag == "bombTotal"
        }?.update(
            newDisplayText = "${Player.bombTotal}"
        )
        //---------------------//
        gameHUD.drawHUD(hudBatch)
    }

    private fun drawBackground() {
        application.batch.draw(
            environmentSprite, 0f, 0f, GameManager.ENVIRONMENT_WIDTH, GameManager.ENVIRONMENT_HEIGHT
        )
    }

    private fun drawAnimations(anims: List<SpriteAnimation>) {
        val animsToRemove = mutableListOf<SpriteAnimation>()
        anims.forEach { anim ->
            application.batch.draw(
                anim.anim.getKeyFrame(anim.elapsedTime, anim.loop),
                anim.source?.boundingBox?.min?.x ?: anim.x,
                anim.source?.boundingBox?.min?.y ?: anim.y,
                anim.width,
                anim.height,
            )
            anim.elapsedTime += Gdx.graphics.deltaTime
            if (anim.isFinished(anim.elapsedTime)) {
                animsToRemove.add(anim)
            }
        }
        animsToRemove.forEach {
            GameManager.activeAnimations.remove(it)
        }
    }

    private fun drawStageAndContents() {
        val rect = Rectangle()
        stage.squaresList.forEach { square ->
            val (x, y) = square.getEnvironmentPosition()
            rect.set(
                x,
                y,
                SQUARE_WIDTH,
                SQUARE_HEIGHT,
            ).apply {
                square.updateBoundingBox(x, y, width, height)
            }

            // Check for highlight and use appropriate variable!
            val img = if (square.highlight) {
                square.highlightedTileImage
            } else {
                square.tileImage
            }

            application.batch.draw(
                img, rect.x, rect.y, rect.width, rect.height
            )

            // Draw the square's contents too
            drawSquareContents(square)
        }

    }

    private fun drawSquareContents(square: Square) {
        square.contents.forEach { content ->
            var contentImg = content.image
            if (content.hideImage) {
                contentImg = Texture(Gdx.files.internal("sprites/alpha.png"))
            } else if (content.highlight) {
                contentImg = content.highlightedImage
            }
            val currentX = content.interpolationType.apply(
                content.currentPosition.x,
                content.movementTarget.x,
                0.25f
            )
            val currentY = content.interpolationType.apply(
                content.currentPosition.y,
                content.movementTarget.y,
                0.25f
            )
            application.batch.draw(
                contentImg,
                currentX,
                currentY,
                SQUARE_WIDTH,
                SQUARE_HEIGHT,
            )
            content.setCurrentPosition(currentX, currentY)
        }
    }


    private fun getMouseEnvironmentPosition(cam: OrthographicCamera): Vector3? {
        return cam.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
    }

    private fun getMouseBox(mouseX: Int, mouseY: Int): BoundingBox {
        val mouseBox = BoundingBox(
            Vector3(mouseX.toFloat(), mouseY.toFloat(), 0f),
            Vector3(mouseX.toFloat() + 0.1f, mouseY.toFloat() + 0.1f, 0f)
        )
        return mouseBox
    }

    private fun checkSquareCollisions(
        collection: List<Square>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        collection.forEach { square ->
            if (square.boundingBox.contains(getMouseBox(mouseX, mouseY))) {
                square.onHover()
                if (button >= 0) {
                    square.onClick(button)
                }
            } else {
                square.onExitHover()
            }
        }
    }

    // Generic function for any board object
    private fun checkBoardObjectCollisions(
        collection: List<BoardObject>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        collection.forEach { obj ->
            if (obj.boundingBox.contains(getMouseBox(mouseX, mouseY))) {
                obj.onHover()
                if (button >= 0) {
                    obj.onClick(button)
                }
            } else {
                if (obj.hovered) {
                    obj.onExitHover()
                }
            }
        }
    }

    private fun checkHUDCollisions(
        collection: List<GameHUD.HUDElement>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        collection.forEach { element ->
            if (element.boundingBox.contains(getMouseBox(mouseX, mouseY))) {
                element.onHover()
                if (button == 0) {
                    element.onClick()
                }
            } else {
                element.onExitHover()
            }
        }
    }


    override fun resize(width: Int, height: Int) {
        gameCamera.viewportWidth = viewWidth.toFloat()
        gameCamera.viewportHeight = (viewHeight * windowHeight / windowWidth).toFloat()
        gameCamera.update()
        gameHUD.resize(hudCamera.viewportWidth, hudCamera.viewportHeight)
        GameManager.currentScreenWidth = Gdx.graphics.width.toFloat()
        GameManager.currentScreenHeight = Gdx.graphics.height.toFloat()
        if (currentShop != null) {
            currentShop!!.shopWindow.resize(width, height)
        }
        if (currentStation != null) {
            currentStation!!.choiceWindow.resize(width, height)
        }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun show() {
    }

    override fun hide() {
    }

    override fun dispose() {
        application.batch.dispose()
        hudBatch.dispose()
    }


}
