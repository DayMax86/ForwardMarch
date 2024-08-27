package com.daymax86.forwardmarch.files

import com.badlogic.gdx.Gdx
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pickups.Coin
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.squares.BlackSquareDefault
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.squares.TileColours
import com.daymax86.forwardmarch.squares.WhiteSquareDefault
import java.io.File
import java.io.InputStream

object FileManager {

    data class SquareData(
        val xPos: Int,
        val yPos: Int,
        val colour: String,
        val contents: List<String>,
    )

    private fun readBoardFile(inputStream: InputStream): List<SquareData> {
        try {
            val reader = inputStream.bufferedReader()
            return reader.lineSequence()
                .filter { it.isNotBlank() }
                .map {
                    val (xPos, yPos, colour, contents) = it.split(",")
                    SquareData(
                        xPos.toInt(),
                        yPos.toInt(),
                        colour.trim(),
                        contents.trim().split("/")
                    )
                }.toList()
        } catch (e: Exception) {
            Gdx.app.log("files", "Error reading file - $e")
            return emptyList()
        }
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
            when (dataSquare.colour) {
                "black" -> {
                    square = BlackSquareDefault(
                        colour = TileColours.BLACK,
                        associatedBoard = board,
                        boardXpos = dataSquare.xPos,
                        boardYpos = dataSquare.yPos,
                        clickable = true,
                    )
                }

                "white" -> {
                    square = WhiteSquareDefault(
                        colour = TileColours.WHITE,
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
                }
            }
        }
    }

}
