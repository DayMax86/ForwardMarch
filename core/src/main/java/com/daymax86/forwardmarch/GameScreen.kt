package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.pieces.BlackPawn
import com.daymax86.forwardmarch.pieces.Piece

class GameScreen(private val application: MainApplication) : Screen {

    // Global variables
    val boards = Array<Board>()
    val pieces = Array<Piece>()

    private val boardDimensions: Int = 8
    private var camera: OrthographicCamera = OrthographicCamera()
    private val edgeBuffer: Float =
        (application.windowWidth / 8).toFloat()
    private val squareVisualWidth: Float = edgeBuffer / 2
    private val squareVisualHeight: Float = edgeBuffer / 2
    private val cameraHeightMultiplier: Float = 1.25f

    init {
        camera.setToOrtho(
            false,
            application.windowWidth.toFloat(),
            application.windowHeight * cameraHeightMultiplier
        )

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val adjustedX = (edgeBuffer + screenX).toInt()
                val adjustedY = application.windowHeight - screenY
                checkSquareCollisions(
                    boards[0].squaresArray, // TODO() Needs to know which board
                    screenX,
                    adjustedY,
                    button
                )
                checkPieceCollisions(
                    pieces,
                    adjustedX,
                    adjustedY,
                    button
                )
                return true
            }

            override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
                val adjustedX = (edgeBuffer + screenX).toInt()
                val adjustedY = application.windowHeight - screenY
                for (board in boards) {
                    checkSquareCollisions(
                        board.squaresArray,
                        screenX,
                        adjustedY
                    )
                }
                checkPieceCollisions(
                    pieces,
                    adjustedX,
                    adjustedY
                )
                return true
            }
        }

        val testBoard = StandardBoard(
            dimensions = boardDimensions,
            tileWidth = application.tileWidth
        )
        testBoard.onScreen = true
        val testBoard2 = StandardBoard(
            dimensions = boardDimensions,
            tileWidth = application.tileWidth
        )
        testBoard2.onScreen = true
        boards.add(testBoard, testBoard2)
        val testPawn = BlackPawn()
        testPawn.activeBoards = boards
        testPawn.boardXpos = 3
        testPawn.boardYpos = 3
        pieces.add(testPawn)
        val testPawn2 = BlackPawn()
        testPawn2.activeBoards = boards
        testPawn2.boardXpos = 5
        testPawn2.boardYpos = 8
        pieces.add(testPawn2)
        val testPawn3 = BlackPawn()
        testPawn3.activeBoards = boards
        testPawn3.boardXpos = 0
        testPawn3.boardYpos = 0
        //pieces.add(testPawn3)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0.8f, 0.8f, 1f)
        camera.update()
        application.batch.projectionMatrix = camera.combined
        application.batch.begin()

        var boardsOnScreen = 0
        var startingY: Int = 0
        for (board in boards) {
            if (board.onScreen) {boardsOnScreen++}
            // Adjust starting Y value of drawn board according to number of boards on screen
            startingY = (squareVisualHeight.toInt() * boardDimensions * boardsOnScreen) - application.windowHeight
            drawBoard(board, startingY)
        }

        drawPieces(pieces)
        application.batch.end()
    }

    private fun drawPieces(pieces: Array<Piece>) {
        for (piece in pieces) {

            val rect = Rectangle()
            rect.set(
                edgeBuffer + piece.boardXpos.toFloat() * squareVisualWidth + squareVisualWidth,
                piece.boardYpos.toFloat() * squareVisualHeight - squareVisualHeight,
                squareVisualWidth,
                squareVisualHeight,
            )
            piece.updateBoundingBox(rect.x + edgeBuffer, rect.y, rect.width, rect.height)

            val img = if (piece.highlight) {
                piece.highlightedImage
            } else {
                piece.image
            }
            application.batch.draw(
                img,
                rect.x,
                rect.y,
                squareVisualWidth,
                squareVisualHeight
            )
        }
    }

    private fun drawBoard(board: Board, startingY: Int) {
        val rect = Rectangle()
        for ((index, square: Square) in board.squaresArray.withIndex()) {
            rect.set(
                edgeBuffer + (index.mod(boardDimensions) * squareVisualWidth),
                (square.boardYpos * squareVisualHeight) + startingY,
                squareVisualWidth,
                squareVisualHeight,
            )
            square.updateBoundingBox(rect.x + edgeBuffer, rect.y, rect.width, rect.height)
            // Check for highlight and use appropriate variable!
            val img = if (square.highlight) {
                square.highlightedTileImage
            } else {
                square.tileImage
            }
            application.batch.draw(
                img, edgeBuffer + rect.x, rect.y, rect.width, rect.height
            )
        }
    }


    private fun getMouseBox(mouseX: Int, mouseY: Int): BoundingBox {
        val adjustedY = mouseY * cameraHeightMultiplier
        val mouseBox = BoundingBox(
            Vector3(mouseX.toFloat(), adjustedY.toFloat(), 0f),
            Vector3(mouseX.toFloat() + 0.1f, adjustedY.toFloat() + 0.1f, 0f)
        )
        return mouseBox
    }

    private fun checkSquareCollisions(
        collection: Array<Square>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        for (square in collection) {
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
        collection: Array<BoardObject>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        for (obj in collection) {
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

    // Specific function for pieces
    private fun checkPieceCollisions(
        collection: Array<Piece>,
        mouseX: Int,
        mouseY: Int,
        button: Int = -1
    ) {
        for (piece in collection) {
            if (piece.boundingBox.contains(getMouseBox(mouseX, mouseY))) {
                piece.onHover()
                if (button >= 0) {
                    piece.onClick(button)
                }
            } else {
                piece.onExitHover()
            }
        }

    }


    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(
            false,
            application.windowWidth.toFloat(),
            application.windowHeight * cameraHeightMultiplier
        )
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
