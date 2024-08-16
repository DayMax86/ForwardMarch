package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.ScreenUtils
import com.daymax86.forwardmarch.GameManager.DIMENSIONS
import com.daymax86.forwardmarch.GameManager.ENVIRONMENT_HEIGHT
import com.daymax86.forwardmarch.GameManager.ENVIRONMENT_WIDTH
import com.daymax86.forwardmarch.GameManager.SQUARE_HEIGHT
import com.daymax86.forwardmarch.GameManager.SQUARE_WIDTH
import com.daymax86.forwardmarch.GameManager.boards
import com.daymax86.forwardmarch.GameManager.cameraTargetInX
import com.daymax86.forwardmarch.GameManager.cameraTargetInY
import com.daymax86.forwardmarch.GameManager.currentShop
import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.squares.Square
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

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val xPos = getMouseEnvironmentPosition(gameCamera)?.x?.toInt()
                val yPos = getMouseEnvironmentPosition(gameCamera)?.y?.toInt()
                if (xPos != null && yPos != null) {

                    GameManager.boards.forEach { board ->
                        checkSquareCollisions(
                            board.squaresList,
                            xPos,
                            yPos,
                            button
                        )
                    }

                    checkBoardObjectCollisions(
                        GameManager.getAllObjects(),
                        xPos,
                        yPos,
                        button
                    ).apply { GameManager.updateValidMoves() } // Call method only once moves have been made

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

                return true
            }


            override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
                val xPos = getMouseEnvironmentPosition(gameCamera)?.x?.toInt()
                val yPos = getMouseEnvironmentPosition(gameCamera)?.y?.toInt()
                GameManager.boards.forEach { board ->
                    getMouseEnvironmentPosition(gameCamera)?.let {
                        checkSquareCollisions(
                            board.squaresList,
                            it.x.toInt(),
                            it.y.toInt()
                        )
                    }
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

                    (Input.Keys.Z) -> {
                        if (!GameManager.marchInProgress) {
                            // Undo moves since last forwardMarch
                            GameManager.revertToLastSavedState()
                        }
                    }

//                    (Input.Keys.S) -> {
//                        //Show the shop
//                        GameManager.currentShop!!.displayShopWindow =
//                            !GameManager.currentShop!!.displayShopWindow
//                        if (GameManager.currentShop != null) {
//                            if (GameManager.currentShop!!.displayShopWindow) {
//                                GameManager.currentShop!!.enterShop()
//                            } else {
//                                GameManager.currentShop!!.exitShop()
//                            }
//                        }
//                    }

                }
                return true
            }
        }

    }

    private fun updateCamera() {
        gameCamera.lerpTo(Vector2(cameraTargetInX, cameraTargetInY), 0.1f)
        gameCamera.update()
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0.8f, 0.8f, 1f)
        updateCamera()
        application.batch.projectionMatrix = gameCamera.combined
        application.batch.begin()

        drawBackground()

        boards.forEach { board ->
            drawBoard(board, board.environmentXPos, board.environmentYPos)
        }

        drawBoardObjects(GameManager.getAllObjects())
        drawAnimations(GameManager.activeAnimations)

//        // TESTING --------------------------
//        val shapeRenderer = ShapeRenderer()
//        shapeRenderer.projectionMatrix = gameCamera.combined
//        GameManager.pieces.forEach { piece ->
//                shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
//                shapeRenderer.color = Color.RED
//                shapeRenderer.rect(
//                    piece.boundingBox.min.x,
//                    piece.boundingBox.min.y,
//                    SQUARE_WIDTH,
//                    SQUARE_HEIGHT,
//                )
//                shapeRenderer.end()
//            }
//
//        //----------------------------------


        application.batch.end()

        if (GameManager.currentShop != null) {
            if (GameManager.currentShop!!.displayShopWindow) {
                try {
                    GameManager.currentShop!!.shopWindow.render()
                } catch (e: Exception) {
                    Gdx.app.log("shop", "Problem calling render on shop (is currentShop set to null properly?)")
                }
            }
        }

        hudBatch.projectionMatrix = hudCamera.combined
        hudBatch.begin()
        drawHUD()
        hudBatch.end()
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
            newDisplayText = "${GameManager.coinTotal}"
        )
        gameHUD.hudElements.firstOrNull {
            it.tag == "bombTotal"
        }?.update(
            newDisplayText = "${GameManager.bombTotal}"
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

    private fun drawBoardObjects(objects: List<BoardObject>) {
        objects.forEach { obj ->
            val img = if (obj.highlight) {
                obj.highlightedImage
            } else {
                obj.image
            }
            // If it's not yet in position, keep lerping
            obj.currentPosition.x =
                obj.interpolationType.apply(obj.currentPosition.x, obj.movementTarget.x, 0.25f)
            obj.currentPosition.y =
                obj.interpolationType.apply(obj.currentPosition.y, obj.movementTarget.y, 0.25f)
            //obj.currentPosition.lerp(obj.movementTarget,0.25f)
            obj.updateBoundingBox()
            // Really the above code shouldn't be under the 'drawObjects' title since this isn't drawing!
            application.batch.draw(
                img,
                obj.boundingBox.min.x,
                obj.boundingBox.min.y,
                SQUARE_WIDTH,
                SQUARE_HEIGHT
            )
        }
    }

    private fun drawBoard(board: Board, startingX: Int, startingY: Int) {
        val rect = Rectangle()
        board.squaresList.forEach { square ->
            rect.set(
                startingX + square.boardXpos * GameManager.SQUARE_WIDTH,
                startingY + square.boardYpos * GameManager.SQUARE_HEIGHT,
                square.squareWidth.toFloat(),
                square.squareWidth.toFloat(),
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
                obj.onExitHover()
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
                if (button >= 0) {
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
        if (GameManager.currentShop != null) {
            GameManager.currentShop!!.shopWindow.resize(width, height)
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
    }


}
