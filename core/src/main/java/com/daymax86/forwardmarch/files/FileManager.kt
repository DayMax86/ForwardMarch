package com.daymax86.forwardmarch.files

import com.badlogic.gdx.Gdx
import com.daymax86.forwardmarch.Board
import com.daymax86.forwardmarch.EnemyManager
import com.daymax86.forwardmarch.GameManager
import com.daymax86.forwardmarch.board_objects.Shop
import com.daymax86.forwardmarch.board_objects.pickups.Bomb
import com.daymax86.forwardmarch.board_objects.pickups.Coin
import com.daymax86.forwardmarch.board_objects.pickups.ItemToken
import com.daymax86.forwardmarch.board_objects.pieces.PieceTypes
import com.daymax86.forwardmarch.boards.StandardBoard
import com.daymax86.forwardmarch.squares.BlackSquareDefault
import com.daymax86.forwardmarch.squares.MysterySquare
import com.daymax86.forwardmarch.squares.Square
import com.daymax86.forwardmarch.squares.TileColours
import com.daymax86.forwardmarch.squares.TrapdoorSquare
import com.daymax86.forwardmarch.squares.WhiteSquareDefault
import java.io.File
import java.io.InputStream

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

                "mystery" -> {
                    square = MysterySquare(
                        colour = TileColours.OTHER,
                        associatedBoard = board,
                        boardXpos = dataSquare.xPos,
                        boardYpos = dataSquare.yPos,
                        clickable = true,
                    )
                }

                "trapdoor" -> {
                    square = TrapdoorSquare(
                        colour = TileColours.OTHER,
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
                            EnemyManager.spawnEnemy(PieceTypes.PAWN, square.boardXpos, square.boardYpos, board)
                        }
                    }

                    "enemy_knight" -> {
                        actionQueue.add {
                            EnemyManager.spawnEnemy(PieceTypes.KNIGHT, square.boardXpos, square.boardYpos, board)
                        }
                    }

                    "enemy_rook" -> {
                        actionQueue.add {
                            EnemyManager.spawnEnemy(PieceTypes.ROOK, square.boardXpos, square.boardYpos, board)
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
