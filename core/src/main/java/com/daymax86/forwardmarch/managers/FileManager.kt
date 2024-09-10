package com.daymax86.forwardmarch.managers

import com.badlogic.gdx.Gdx
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.board_objects.SacrificeStation
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.board_objects.pickups.Coin
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.board_objects.traps.TrapTypes
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
                .also {
                    reader.close()
                }
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

    fun makeBoardFromFile(file: File): Pair<Board, MutableList<() -> Unit>> {
        val inputStream = file.inputStream()
        val dataSquares = readBoardFile(inputStream)
        val tempSquaresList: MutableList<Square> = mutableListOf()
        val board = Board()
        val actionQueue: MutableList<() -> Unit> = mutableListOf()
        if (dataSquares.isEmpty()) {
            return Pair(board, mutableListOf())
        }
        dataSquares.forEach { dataSquare ->
            lateinit var square: Square
            when (dataSquare.type) {
                "black" -> {
                    square = BlackSquareDefault(
                        colour = SquareTypes.BLACK,
                        stageXpos = dataSquare.xPos,
                        stageYpos = dataSquare.yPos,
                        clickable = true,
                    )
                }

                "white" -> {
                    square = WhiteSquareDefault(
                        colour = SquareTypes.WHITE,
                        stageXpos = dataSquare.xPos,
                        stageYpos = dataSquare.yPos ,
                        clickable = true,
                    )
                }

                "mystery" -> {
                    square = MysterySquare(
                        colour = SquareTypes.MYSTERY,
                        stageXpos = dataSquare.xPos,
                        stageYpos = dataSquare.yPos ,
                        clickable = true,
                    )
                }

                "trapdoor" -> {
                    square = TrapdoorSquare(
                        colour = SquareTypes.TRAPDOOR,
                        stageXpos = dataSquare.xPos,
                        stageYpos = dataSquare.yPos ,
                        clickable = true,
                    )
                }

                else -> {
                    square = BrokenSquare(
                        colour = SquareTypes.BROKEN,
                        stageXpos = dataSquare.xPos,
                        stageYpos = dataSquare.yPos ,
                        clickable = true,
                    )
                }
            }

            dataSquare.contents.forEach { content ->
                when (content) {
                    "coin" -> {
                        square.contents.add(Coin())
                    }

                    "bomb" -> {
                        actionQueue.add {
                            PickupManager.spawnPickup(
                                PickupTypes.BOMB,
                                square.stageXpos,
                                square.stageYpos ,
                            )
                        }
                    }

                    "trap" -> {
                        actionQueue.add {
                            EnemyManager.spawnTrap(
                                type = TrapTypes.SPIKE,
                                x = square.stageXpos,
                                y = square.stageYpos,
                            )
                        }
                    }

                    "shop" -> {
                        actionQueue.add {
                            StageManager.spawnShop(
                                x = square.stageXpos,
                                y = square.stageYpos,
                            )
                        }
                    }

                    "sacrifice_station" -> {
                        actionQueue.add {
                            StageManager.spawnSacrificeStation(
                                x = square.stageXpos,
                                y = square.stageYpos,
                            )
                        }
                    }

                    "item_token" -> {
                        actionQueue.add {
                            PickupManager.spawnPickup(
                                PickupTypes.ITEM_TOKEN,
                                square.stageXpos,
                                square.stageYpos ,
                            )
                        }
                    }

                    "enemy_pawn" -> {
                        actionQueue.add {
                            EnemyManager.spawnEnemy(
                                PieceTypes.PAWN,
                                square.stageXpos,
                                square.stageYpos ,
                            )
                        }
                    }

                    "enemy_knight" -> {
                        actionQueue.add {
                            EnemyManager.spawnEnemy(
                                PieceTypes.KNIGHT,
                                square.stageXpos,
                                square.stageYpos,
                            )
                        }
                    }

                    "enemy_rook" -> {
                        actionQueue.add {
                            EnemyManager.spawnEnemy(
                                PieceTypes.ROOK,
                                square.stageXpos,
                                square.stageYpos,
                            )
                        }
                    }

                    else -> {
                        // add nothing.
                    }

                }
            }
            tempSquaresList.add(square)
        }.apply {
            inputStream.close()
            board.squaresList.clear()
            tempSquaresList.forEach { tempSq ->
                board.squaresList.add(tempSq)
            }
        }.apply {
            tempSquaresList.clear()
            return if (board.squaresList.size == 64) {// Was the operation successful?
                Pair(board, actionQueue)
            } else {
                Pair(board, mutableListOf())
            }
        }
    }
}
