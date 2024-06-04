package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.ScreenUtils
import com.daymax86.forwardmarch.animations.SpriteAnimation
import kotlinx.coroutines.CoroutineScope
import ktx.graphics.lerpTo

class GameScreen(private val application: MainApplication) : Screen {

    // Hardcoded constants which affect visuals only
    val viewWidth = 2000
    val viewHeight = 2000

    private var camera: OrthographicCamera
    private var hudCamera: OrthographicCamera
    private var cameraTargetInX: Float = 0f
    private var cameraTargetInY: Float = 0f
    private var environmentSprite = Sprite(Texture(Gdx.files.internal("background_500x750.png")))
    var windowWidth: Int = 0
    var windowHeight: Int = 0
    private val gameHUD: GameHUD = GameHUD(this)
    private val hudBatch = SpriteBatch()

    init {
        environmentSprite.setPosition(0f, 0f)
        environmentSprite.setSize(GameManager.ENVIRONMENT_WIDTH, GameManager.ENVIRONMENT_HEIGHT)
        windowWidth = Gdx.graphics.width
        windowHeight = Gdx.graphics.height

        camera = OrthographicCamera(
            (viewWidth).toFloat(), (viewHeight * (viewWidth / viewHeight)).toFloat()
        )
        hudCamera = OrthographicCamera(
            (windowWidth).toFloat(), (windowHeight).toFloat()
        )

        cameraTargetInX = camera.viewportWidth / 2f
        cameraTargetInY = ((windowHeight / 2) + GameManager.SQUARE_WIDTH)
        camera.position.set(cameraTargetInX, cameraTargetInY, 0f)

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val xPos = getMouseEnvironmentPosition()?.x?.toInt()
                val yPos = getMouseEnvironmentPosition()?.y?.toInt()
                if (xPos != null && yPos != null) {
                    if (!GameManager.movementInProgress) {
                        GameManager.boards.forEach { board ->
                            checkSquareCollisions(
                                board.squaresList,
                                xPos,
                                yPos,
                                button
                            )
                        }
                    }
                    checkBoardObjectCollisions(
                        GameManager.pieces,
                        xPos,
                        yPos,
                        button
                    ).apply { GameManager.updateValidMoves() } // Call method only once moves have been made
                }
                return true
            }


            override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
                val xPos = getMouseEnvironmentPosition()?.x?.toInt()
                val yPos = getMouseEnvironmentPosition()?.y?.toInt()
                if (!GameManager.movementInProgress) {
                    GameManager.boards.forEach { board ->
                        getMouseEnvironmentPosition()?.let {
                            checkSquareCollisions(
                                board.squaresList,
                                it.x.toInt(),
                                it.y.toInt()
                            )
                        }
                    }
                }
                if (xPos != null && yPos != null) {
                    checkBoardObjectCollisions(
                        GameManager.pieces,
                        xPos,
                        yPos
                    )
                }
                return true
            }

            override fun keyDown(keycode: Int): Boolean {
                super.keyDown(keycode)
                when (keycode) {
                    (Input.Keys.UP) -> {
                        camera.translate(0f, 50f)
                    }

                    (Input.Keys.LEFT) -> {
                        camera.translate(-50f, 0f)
                    }

                    (Input.Keys.DOWN) -> {
                        camera.translate(0f, -50f)
                    }

                    (Input.Keys.RIGHT) -> {
                        camera.translate(50f, 0f)
                    }

                    // --------- FOR TESTING ONLY ---------- //
                    (Input.Keys.F) -> {
                        GameManager.forwardMarch(1)
                        cameraTargetInY += GameManager.SQUARE_HEIGHT
                    }

                }
                return true
            }
        }

    }


    private fun updateCamera() {
        camera.lerpTo(Vector2(cameraTargetInX, cameraTargetInY), 0.1f)
        camera.update()
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0.8f, 0.8f, 1f)
        updateCamera()
        application.batch.projectionMatrix = camera.combined
        application.batch.begin()

        drawBackground()

        var boardsOnScreen = 0
        GameManager.boards.forEach { board ->
            if (board.onScreen) {
                boardsOnScreen++
            }
            drawBoard(board, board.environmentXPos, board.environmentYPos)
        }

        drawBoardObjects(GameManager.pieces)
        drawBoardObjects(GameManager.traps)
        drawAnimations(GameManager.activeAnimations)

        application.batch.end()

        hudBatch.projectionMatrix = hudCamera.combined
        hudBatch.begin()
        gameHUD.drawHUD(hudBatch)
        hudBatch.end()
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
                anim.x,
                anim.y,
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
            application.batch.draw(
                img,
                obj.boundingBox.min.x,
                obj.boundingBox.min.y,
                GameManager.SQUARE_WIDTH,
                GameManager.SQUARE_HEIGHT
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


    private fun getMouseEnvironmentPosition(): Vector3? {
        return camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
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


    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = viewWidth.toFloat()
        camera.viewportHeight = (viewHeight * windowHeight / windowWidth).toFloat()
        camera.update()
        gameHUD.resize(width.toFloat(), height.toFloat())
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
