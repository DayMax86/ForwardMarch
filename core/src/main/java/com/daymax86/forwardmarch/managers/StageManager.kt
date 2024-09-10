package com.daymax86.forwardmarch.managers

import com.badlogic.gdx.Gdx
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.Stage
import com.daymax86.forwardmarch.board_objects.SacrificeStation
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.board_objects.pickups.Coin
import com.daymax86.forwardmarch.managers.GameManager.DIMENSIONS
import com.daymax86.forwardmarch.managers.GameManager.SQUARE_HEIGHT
import java.nio.file.DirectoryStream
import java.nio.file.FileSystems
import java.nio.file.Path

object StageManager {

    val stage: Stage = Stage()

    init {
        stage.initialise(
            getStartingBoards()
        )
    }

    @Suppress("NewApi")
    fun getStartingBoards(): Triple<Board, Board, Board> {

        // ------------------ Board 1 ------------------
        var path: Path = FileSystems.getDefault().getPath("boards/starting_boards")
        var stream: DirectoryStream<Path> = java.nio.file.Files.newDirectoryStream(path)
        val files: MutableList<Path> = mutableListOf()
        stream.forEach { p ->
            files.add(p)
        }
        val (board1, actionQueue1) = FileManager.makeBoardFromFile(
            Gdx.files.internal(
                files.random().toString()
            ).file(),
        )
        // ------------------ Boards 2 & 3 ----------------
        path = FileSystems.getDefault().getPath("boards/very_easy_boards")
        stream = java.nio.file.Files.newDirectoryStream(path)
        files.clear()
        stream.forEach { p ->
            files.add(p)
        }
        val source1 = files.random().toString()
        var source2 = files.random().toString()
        while (source2 == source1) {
            source2 = files.random().toString()
        }
        val (board2, actionQueue2) = FileManager.makeBoardFromFile(
            Gdx.files.internal(
                source1
            ).file(),
        )


        // TESTING --------
        board2.getSquare(1,1)?.contents?.add(Coin())
        // ----------------

        val (board3, actionQueue3) = FileManager.makeBoardFromFile(
            Gdx.files.internal(
                source2
            ).file(),
        )

        actionQueue1.forEach { it.invoke() }
        actionQueue2.forEach { it.invoke() }
        actionQueue3.forEach { it.invoke() }

        return Triple(board1, board2, board3)
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
                ).file(),
            )
            if (board != null) {
                actionQueue.forEach { it.invoke() }.apply {
                    stage.appendBoard(board,stage.squaresList.size / (DIMENSIONS * DIMENSIONS))
                }
            }
            onComplete.invoke()
        }
    }

    fun checkStageStatus() {
        // See if any boards need to be removed, or any new boards appended
        // Get the y coordinate of the lowest allied piece
        var lowestPieceY = stage.squaresList.size
        PieceManager.pieces.forEach { piece ->
            if (piece.stageYpos < lowestPieceY) {
                lowestPieceY = piece.stageYpos
            }
        }
        // If the lowest y is more than 10 (?) rows above the bottom row, remove a board
        stage.squaresList.sortBy { square ->
            square.stageYpos
        }
        val firstRow = stage.squaresList.first().stageYpos
        if (lowestPieceY > firstRow) {
            stage.removeRows(DIMENSIONS)
        }
    }

    fun spawnShop(x: Int, y: Int) {
        val shopToAdd = Shop()
        shopToAdd.stageXpos = x
        shopToAdd.stageYpos = y
        shopToAdd.move(x,y)
        GameManager.shops.add(shopToAdd)
    }

    fun spawnSacrificeStation(x: Int, y: Int) {
        val stationToAdd = SacrificeStation()
        stationToAdd.stageXpos = x
        stationToAdd.stageYpos = y
        stationToAdd.move(x,y)
        GameManager.stations.add(stationToAdd)
    }

}
