package com.daymax86.forwardmarch.managers

import com.badlogic.gdx.Gdx
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.Stage
import com.daymax86.forwardmarch.board_objects.SacrificeStation
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.managers.GameManager.BOARD_STARTING_X
import com.daymax86.forwardmarch.managers.GameManager.BOARD_STARTING_Y
import com.daymax86.forwardmarch.managers.GameManager.DIMENSIONS
import com.daymax86.forwardmarch.managers.GameManager.SQUARE_HEIGHT
import com.daymax86.forwardmarch.managers.GameManager.SQUARE_WIDTH
import com.daymax86.forwardmarch.managers.GameManager.forwardMarchCounter
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.DirectoryStream
import java.nio.file.FileSystems
import java.nio.file.Path

object StageManager {

    val stage: Stage = Stage()

    fun load() = runBlocking {
        stage.initialise(
            getStartingBoards()
        )
    }

    @Suppress("NewApi")
    private fun getStartingBoards(): Pair<Triple<Board, Board, Board>, Triple<File, File, File>> {

        // ------------------ Board 1 ------------------
        var path: Path = FileSystems.getDefault().getPath("boards/starting_boards")
        var stream: DirectoryStream<Path> = java.nio.file.Files.newDirectoryStream(path)
        val files: MutableList<Path> = mutableListOf()
        stream.forEach { p ->
            files.add(p)
        }
        val file1 = Gdx.files.internal(
            files.random().toString()
        ).file()
        val board1 = FileManager.makeBoardFromFile(
            file1
        )
        board1.environmentXPos = BOARD_STARTING_X
        board1.environmentYPos = BOARD_STARTING_Y

        // ------------------ Boards 2 & 3 ----------------
        path = FileSystems.getDefault().getPath("boards/easy_boards")
        stream = java.nio.file.Files.newDirectoryStream(path)
        files.clear()
        stream.forEach { p ->
            files.add(p)
        }
        val source2 = files.random().toString()
        var source3 = files.random().toString()
        while (source3 == source2) {
            source3 = files.random().toString()
        }
        val file2 = Gdx.files.internal(
            source2
        ).file()
        val board2 = FileManager.makeBoardFromFile(
            file2
        )
        board2.environmentXPos = BOARD_STARTING_X + DIMENSIONS * SQUARE_WIDTH.toInt()
        board2.environmentYPos = BOARD_STARTING_Y + DIMENSIONS * SQUARE_HEIGHT.toInt()

        val file3 = Gdx.files.internal(
            source3
        ).file()
        val board3 = FileManager.makeBoardFromFile(
            file3
        )
        board3.environmentXPos = BOARD_STARTING_X + (DIMENSIONS * 2) * SQUARE_WIDTH.toInt()
        board3.environmentYPos = BOARD_STARTING_Y + (DIMENSIONS * 2) * SQUARE_HEIGHT.toInt()

        return Pair(Triple(board1, board2, board3), Triple(file1, file2, file3))
    }

    private fun resolveActionQueues(queues: List<MutableList<() -> Unit>>) {
        queues.forEach { q ->
            q.forEach { it.invoke() }
        }
    }

    @Suppress("NewApi")
    fun addBoard(difficultyModifier: Int) {
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
            val file = Gdx.files.internal(
                files.random().toString()
            ).file()
            val board = FileManager.makeBoardFromFile(
                file
            )
            val numberOfRows = DIMENSIONS * 2 + forwardMarchCounter
            board.environmentXPos = BOARD_STARTING_X
            board.environmentYPos = BOARD_STARTING_Y + (SQUARE_HEIGHT * numberOfRows).toInt()
            stage.appendBoard(board, numberOfRows)
            FileManager.populateBoardFromFile(file, numberOfRows)
        }
    }

    fun removeBoard() {
        stage.removeRows(DIMENSIONS)
    }

    fun spawnShop(x: Int, y: Int) {
        val shopToAdd = Shop()
        shopToAdd.stageXpos = x
        shopToAdd.stageYpos = y
        shopToAdd.move(x, y)
        GameManager.shops.add(shopToAdd)
    }

    fun spawnSacrificeStation(x: Int, y: Int) {
        val stationToAdd = SacrificeStation()
        stationToAdd.stageXpos = x
        stationToAdd.stageYpos = y
        stationToAdd.move(x, y)
        GameManager.stations.add(stationToAdd)
    }

}
