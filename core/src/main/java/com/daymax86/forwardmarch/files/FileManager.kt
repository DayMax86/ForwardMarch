package com.daymax86.forwardmarch.files

import com.badlogic.gdx.Gdx
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.EnemyManager
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.board_objects.SacrificeStation
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pickups.Coin
import com.daymax86.forwardmarch.board_objects.pickups.ItemToken
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.board_objects.traps.SpikeTrap
import com.daymax86.forwardmarch.board_objects.traps.TrapTypes
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.squares.BlackSquareDefault
import com.daymax86.forwardmarch.squares.BrokenSquare
import com.daymax86.forwardmarch.squares.MysterySquare
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.squares.SquareTypes
import com.daymax86.forwardmarch.squares.TrapdoorSquare
import com.daymax86.forwardmarch.squares.WhiteSquareDefault
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object FileManager {

    data class SquareData(
        val xPos: Int,
        val yPos: Int,
        val type: String,
        val contents: List<String>,
    )

    private fun readBoardFile(inputStream: InputStream): List<SquareData> {
        try {
            val reader = inputStream.bufferedReader()
            return reader.lineSequence()
                .filter { it.isNotBlank() }
                .map {
                    val (xPos, yPos, type, contents) = it.split(",")
                    SquareData(
                        xPos.toInt(),
                        yPos.toInt(),
                        type.trim(),
                        contents.trim().split("/")
                    )
                }.toList()
        } catch (e: Exception) {
            Gdx.app.log("files", "Error reading file - $e")
            return emptyList()
        }
    }

    fun generateBoardFile(difficultyModifier: Int) {
        val newFile = File("boards/test.csv")
        newFile.getParentFile().mkdirs()
        newFile.createNewFile()
        FileOutputStream(newFile).apply { writeToCSV(difficultyModifier) }
    }

    private fun OutputStream.writeToCSV(difficultyModifier: Int) {
//        Create a board file that is randomly generated
//        according to the difficulty modifier.
        val writer = bufferedWriter()

        var lastWasBlack = false
        for (y: Int in 1..GameManager.DIMENSIONS) {
            for (x: Int in 1..GameManager.DIMENSIONS) {
                writer.write("$x,$y,")
                if (lastWasBlack) {
                    // White
                } else {
                    // Black
                }
                if (x.mod(GameManager.DIMENSIONS) != 0) {
                    lastWasBlack = !lastWasBlack
                }
                //writer.write(squareType.toString().lowercase() + ",")
                writer.write("null" + ",")
                writer.newLine()
            }
        }


            // intensity of obstacles depends on difficulty modifier.

                val squareType = SquareTypes.entries.random()


        writer.flush()
    }

    fun makeBoardFromFile(file: File): Board? {
        val inputStream = file.inputStream()
        val dataSquares = readBoardFile(inputStream)
        val squaresList: MutableList<Square> = mutableListOf()
        val board: Board = StandardBoard()
        val actionQueue: MutableList<() -> Unit> = mutableListOf()
        if (dataSquares.isEmpty()) {
            return null
        }
        dataSquares.forEach { dataSquare ->
            lateinit var square: Square
            when (dataSquare.type) {
                "black" -> {
                    square = BlackSquareDefault(
                        colour = SquareTypes.BLACK,
                        associatedBoard = board,
                        boardXpos = dataSquare.xPos,
                        boardYpos = dataSquare.yPos,
                        clickable = true,
                    )
                }

                "white" -> {
                    square = WhiteSquareDefault(
                        colour = SquareTypes.WHITE,
                        associatedBoard = board,
                        boardXpos = dataSquare.xPos,
                        boardYpos = dataSquare.yPos,
                        clickable = true,
                    )
                }

                "mystery" -> {
                    square = MysterySquare(
                        colour = SquareTypes.MYSTERY,
                        associatedBoard = board,
                        boardXpos = dataSquare.xPos,
                        boardYpos = dataSquare.yPos,
                        clickable = true,
                    )
                }

                "trapdoor" -> {
                    square = TrapdoorSquare(
                        colour = SquareTypes.TRAPDOOR,
                        associatedBoard = board,
                        boardXpos = dataSquare.xPos,
                        boardYpos = dataSquare.yPos,
                        clickable = true,
                    )
                }
                else -> {
                    square = BrokenSquare(
                        colour = SquareTypes.BROKEN,
                        associatedBoard = board,
                        boardXpos = dataSquare.xPos,
                        boardYpos = dataSquare.yPos,
                        clickable = true,
                    )
                }

            }

            dataSquare.contents.forEach { content ->
                when (content) {
                    "coin" -> {
                        actionQueue.add {
                            square.contents.add(
                                Coin(
                                    associatedBoard = board,
                                    boardXpos = square.boardXpos,
                                    boardYpos = square.boardYpos,
                                )
                            )
                        }
                    }

                    "bomb" -> {
                        actionQueue.add {
                            square.contents.add(
                                Bomb(
                                    associatedBoard = board,
                                    boardXpos = square.boardXpos,
                                    boardYpos = square.boardYpos,
                                )
                            )
                        }
                    }

                    "trap" -> {
                        actionQueue.add {
                            EnemyManager.spawnTrap(
                                type = TrapTypes.SPIKE,
                                x = square.boardXpos,
                                y = square.boardYpos,
                                board = board,
                            )
                        }
                    }

                    "shop" -> {
                        actionQueue.add {
                            square.contents.add(
                                Shop(
                                    associatedBoard = board,
                                    boardXpos = square.boardXpos,
                                    boardYpos = square.boardYpos,
                                ).also { shop ->
                                    shop.move(square.boardXpos, square.boardYpos, board)
                                    GameManager.shops.add(shop)
                                }
                            )
                        }
                    }

                    "sacrifice_station" -> {
                        actionQueue.add {
                            square.contents.add(
                                SacrificeStation(
                                    associatedBoard = board,
                                    boardXpos = square.boardXpos,
                                    boardYpos = square.boardYpos,
                                ).also { station ->
                                    station.move(square.boardXpos, square.boardYpos, board)
                                    GameManager.stations.add(station)
                                }
                            )
                        }
                    }

                    "item_token" -> {
                        actionQueue.add {
                            square.contents.add(
                                ItemToken(
                                    associatedBoard = board,
                                    boardXpos = square.boardXpos,
                                    boardYpos = square.boardYpos,
                                    associatedItem = GameManager.allItems.random()
                                )
                            )
                        }
                    }

                    "enemy_pawn" -> {
                        actionQueue.add {
                            EnemyManager.spawnEnemy(
                                PieceTypes.PAWN,
                                square.boardXpos,
                                square.boardYpos,
                                board
                            )
                        }
                    }

                    "enemy_knight" -> {
                        actionQueue.add {
                            EnemyManager.spawnEnemy(
                                PieceTypes.KNIGHT,
                                square.boardXpos,
                                square.boardYpos,
                                board
                            )
                        }
                    }

                    "enemy_rook" -> {
                        actionQueue.add {
                            EnemyManager.spawnEnemy(
                                PieceTypes.ROOK,
                                square.boardXpos,
                                square.boardYpos,
                                board
                            )
                        }
                    }

                    else -> {
                        // add nothing.
                    }

                }
            }
            squaresList.add(square)
        }

        actionQueue.forEach { it.invoke() }.also {
            board.squaresList.clear()
            squaresList.forEach { sq ->
                board.squaresList.add(sq)
            }.apply {
                return if (squaresList.size == 64) {// Was the operation successful?
                    board
                } else {
                    null
                }.also {
                    Gdx.app.log("files", "Error creating board file!")
                }
            }
        }
    }

}
