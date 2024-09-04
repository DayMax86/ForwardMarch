package com.daymax86.forwardmarch.managers

import com.badlogic.gdx.Gdx
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.board_objects.pickups.Pickup
import com.daymax86.forwardmarch.managers.GameManager.BOARD_STARTING_Y
import com.daymax86.forwardmarch.managers.GameManager.DIMENSIONS
import com.daymax86.forwardmarch.managers.GameManager.ENVIRONMENT_HEIGHT
import com.daymax86.forwardmarch.managers.GameManager.SQUARE_HEIGHT
import com.daymax86.forwardmarch.managers.GameManager.difficultyModifier
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import java.nio.file.DirectoryStream
import java.nio.file.FileSystems
import java.nio.file.Path

object BoardManager {

    val boards: MutableList<Board> = mutableListOf()
    var totalBoardIndex: Int = 0

    @Suppress("NewApi")
    fun addStartingBoard(onComplete: () -> Unit) {
        val path: Path = FileSystems.getDefault().getPath("boards/starting_boards")
        val stream: DirectoryStream<Path> = java.nio.file.Files.newDirectoryStream(path)
        val files: MutableList<Path> = mutableListOf()
        stream.forEach { p ->
            files.add(p)
        }
        val (startingBoard, actionQueue) = FileManager.makeBoardFromFile(
            Gdx.files.internal(
                files.random().toString()
            ).file()
        )
        if (startingBoard != null) {
            startingBoard.environmentYPos = BOARD_STARTING_Y
            actionQueue.forEach { it.invoke() }.apply {
                boards.add(startingBoard)
            }
        }
        onComplete.invoke()
    }

    @Suppress("NewApi")
    fun addBoard(difficultyModifier: Int, onComplete: () -> Unit) {
        val dir: String
        when (difficultyModifier.floorDiv(1)) {
            1 -> {
                // Add a very easy board
                dir = "boards/very_easy_boards"
            }

            2 -> {
                // Add an easy board
                dir = "boards/easy_boards"
            }

            3 -> {
                // Add a medium board
                dir = "boards/medium_boards"
            }

            4 -> {
                // Add a hard board
                dir = "boards/hard_boards"
            }

            5 -> {
                // Add a very hard board
                dir = "boards/very_hard_boards"
            }

            else -> {
                // Add the hardest boards possible
                dir = "boards/impossible_boards"
            }
        }.also {
            val path: Path = FileSystems.getDefault().getPath(dir)
            val stream: DirectoryStream<Path> = java.nio.file.Files.newDirectoryStream(path)
            val files: MutableList<Path> = mutableListOf()
            stream.forEach { p ->
                files.add(p)
            }
            val (board, actionQueue) = FileManager.makeBoardFromFile(
                Gdx.files.internal(
                    files.random().toString()
                ).file()
            )
            if (board != null) {
                board.environmentYPos =
                    BOARD_STARTING_Y + ((DIMENSIONS * SQUARE_HEIGHT) * boards.size).toInt()
                actionQueue.forEach { it.invoke() }.apply {
                    // ----------------------------------------------------------------
                    var i = 0
                    board.squaresList.forEach { sq ->
                        i = sq.contents.filterIsInstance<Pickup>().size
                    }
                    boards.add(board)
                }
            }
            onComplete.invoke()
        }
    }

    fun checkBoardsStatus() {
        // See if any boards need to be removed, or any new boards appended
        if (boards.first().environmentYPos <= ENVIRONMENT_HEIGHT / 6) {
            appendBoard(difficultyModifier)
            var lowestIndex: Int =
                totalBoardIndex // Set placeholder value that is larger than possible smallest
            boards.forEach { board ->
                if (board.boardIndex < lowestIndex) {
                    lowestIndex = board.boardIndex
                }
            }.apply {
                removeBoard(lowestIndex)
            }

        }
    }

    private fun appendBoard(difficultyModifier: Int) {
        addBoard(difficultyModifier) {
            Gdx.app.log("manager", "a board has been added. (boards.size = ${boards.size})")
        }
    }

    private fun removeBoard(index: Int) {
        val boardToRemove = boards.first { board ->
            board.boardIndex == index
        }
        boards.remove(boardToRemove)
        boardToRemove.destroy()
        Gdx.app.log("manager", "a board has been dropped. (boards.size = ${boards.size})")
    }

}
