package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.squares.Square
import kotlin.math.abs

enum class MovementTypes {
    ROOK,
    BISHOP,
    KNIGHT,
}

enum class MovementDirections {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    UL,
    UR,
    DL,
    DR,
}

object Movement {

    fun getMovement(
        piece: Piece,
        movementType: MovementTypes,
        range: Int,
        directions: List<MovementDirections>
    ): MutableList<Square> {
        val movementList: MutableList<Square> = mutableListOf()

        when (movementType) {

            MovementTypes.ROOK -> { // --------------------- ROOK ---------------------------------
                // --------------------------------------------------------------------------------
                // Take piece coordinates and check the four directions

                directions.forEach { direction ->
                    when (direction) {
                        // ------------------------- UP -------------------------
                        MovementDirections.UP -> {
                            var upIndex = 1
                            if (piece.associatedBoard != null && piece.nextBoard != null) {
                                while (upIndex <= range) {

                                    // Allow for movement across boards
                                    var board: Board? = piece.associatedBoard
                                    var y = piece.boardYpos + upIndex
                                    if (piece.boardYpos + upIndex > GameManager.DIMENSIONS) {
                                        board = piece.nextBoard
                                        y = piece.boardYpos + upIndex - GameManager.DIMENSIONS
                                    }

                                    board?.squaresList?.firstOrNull { square ->
                                        square.boardXpos == piece.boardXpos
                                            && square.boardYpos == y
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                        } else {
                                            upIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    upIndex++
                                }
                            }
                        }
                        // ------------------------- DOWN -------------------------
                        MovementDirections.DOWN -> {
                            var downIndex = 1
                            if (piece.associatedBoard != null && piece.nextBoard != null) {
                                while (downIndex <= range) {

                                    // Allow for movement across boards
                                    var board: Board? = piece.associatedBoard
                                    var y = piece.boardYpos - downIndex
                                    if (piece.boardYpos - downIndex < 1) {
                                        try {
                                            board =
                                                GameManager.boards[GameManager.boards.indexOf(piece.associatedBoard) - 1] // Board below
                                        } catch (e: IndexOutOfBoundsException) {
                                            // Do something...
                                        }
                                        y =
                                            GameManager.DIMENSIONS - abs(piece.boardYpos - downIndex)
                                    }

                                    board?.squaresList?.firstOrNull { square ->
                                        square.boardXpos == piece.boardXpos
                                            && square.boardYpos == y
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                        } else {
                                            downIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    downIndex++
                                }
                            }
                        }
                        // ------------------------- LEFT -------------------------
                        MovementDirections.LEFT -> {
                            var leftIndex = 1
                            if (piece.associatedBoard != null) {
                                while (leftIndex <= range) {

                                    val board: Board? = piece.associatedBoard
                                    var x = piece.boardXpos - leftIndex
                                    if (piece.boardXpos - leftIndex < 1) {
                                        x = 1
                                        leftIndex = range + 1 // Stop checking this direction
                                        continue
                                    }

                                    board?.squaresList?.firstOrNull { square ->
                                        square.boardXpos == x
                                            && square.boardYpos == piece.boardYpos
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true && !movementList.contains(sq)) {
                                            movementList.add(sq)
                                        } else {
                                            leftIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    leftIndex++
                                }
                            }
                        }
                        // ------------------------- RIGHT -------------------------
                        MovementDirections.RIGHT -> {
                            var rightIndex = 1
                            if (piece.associatedBoard != null) {
                                while (rightIndex <= range) {

                                    val board: Board? = piece.associatedBoard
                                    var x = piece.boardXpos + rightIndex
                                    if (piece.boardXpos + rightIndex > GameManager.DIMENSIONS) {
                                        x = 8
                                        rightIndex = range + 1 // Stop checking this direction
                                        continue
                                    }

                                    board?.squaresList?.firstOrNull { square ->
                                        square.boardXpos == x
                                            && square.boardYpos == piece.boardYpos
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true && !movementList.contains(sq)) {
                                            movementList.add(sq)
                                        } else {
                                            rightIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    rightIndex++
                                }
                            }
                        }
                        else -> {/* Do nothing...*/
                        }
                    }
                }
            }
            MovementTypes.BISHOP -> { // --------------------- BISHOP ------------------------------
                // ---------------------------------------------------------------------------------
                directions.forEach { direction ->
                    when (direction) {
                        // ------------------------- UP-LEFT -------------------------
                        MovementDirections.UL -> {
                            var ulIndex = 1
                            if (piece.associatedBoard != null && piece.nextBoard != null) {
                                while (ulIndex <= range) {

                                    // Allow for movement across boards
                                    var board: Board? = piece.associatedBoard
                                    var x = piece.boardXpos - ulIndex
                                    var y = piece.boardYpos + ulIndex
                                    if (piece.boardYpos + ulIndex > GameManager.DIMENSIONS) {
                                        board = piece.nextBoard
                                        y = piece.boardYpos + ulIndex - GameManager.DIMENSIONS
                                    }
                                    if (piece.boardXpos - ulIndex < 1) {
                                        x = 1
                                        ulIndex = range + 1 // Stop checking this direction
                                        continue
                                    }

                                    board?.squaresList?.firstOrNull { square ->
                                        square.boardXpos == x
                                            && square.boardYpos == y
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                        } else {
                                            ulIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    ulIndex++
                                }
                            }
                        }
                        // ------------------------- UP-RIGHT ------------------------
                        MovementDirections.UR -> {
                            var urIndex = 1
                            if (piece.associatedBoard != null && piece.nextBoard != null) {
                                while (urIndex <= range) {

                                    // Allow for movement across boards
                                    var board: Board? = piece.associatedBoard
                                    var x = piece.boardXpos + urIndex
                                    var y = piece.boardYpos + urIndex
                                    if (piece.boardYpos + urIndex > GameManager.DIMENSIONS) {
                                        board = piece.nextBoard
                                        y = piece.boardYpos + urIndex - GameManager.DIMENSIONS
                                    }
                                    if (piece.boardXpos + urIndex > GameManager.DIMENSIONS) {
                                        x = GameManager.DIMENSIONS
                                        urIndex = range + 1 // Stop checking this direction
                                        continue
                                    }

                                    board?.squaresList?.firstOrNull { square ->
                                        square.boardXpos == x
                                            && square.boardYpos == y
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                        } else {
                                            urIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    urIndex++
                                }
                            }
                        }
                        // ------------------------- DOWN-LEFT -----------------------
                        MovementDirections.DL -> {
                            var dlIndex = 1
                            if (piece.associatedBoard != null && piece.nextBoard != null) {
                                while (dlIndex <= range) {

                                    // Allow for movement across boards
                                    var board: Board? = piece.associatedBoard
                                    var x = piece.boardXpos - dlIndex
                                    var y = piece.boardYpos - dlIndex
                                    if (piece.boardYpos - dlIndex < 1) {
                                        try {
                                            board =
                                                GameManager.boards[GameManager.boards.indexOf(piece.associatedBoard) - 1] // Board below
                                        } catch (e: IndexOutOfBoundsException) {
                                            // Do something...
                                        }
                                        y =
                                            GameManager.DIMENSIONS - abs(piece.boardYpos - dlIndex)
                                    }
                                    if (piece.boardXpos - dlIndex < 1) {
                                        x = 1
                                        dlIndex = range + 1 // Stop checking this direction
                                        continue
                                    }

                                    board?.squaresList?.firstOrNull { square ->
                                        square.boardXpos == x
                                            && square.boardYpos == y
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                        } else {
                                            dlIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    dlIndex++
                                }
                            }
                        }
                        // ------------------------- DOWN-RIGHT ----------------------
                        MovementDirections.DR -> {
                            var drIndex = 1
                            if (piece.associatedBoard != null && piece.nextBoard != null) {
                                while (drIndex <= range) {

                                    // Allow for movement across boards
                                    var board: Board? = piece.associatedBoard
                                    var x = piece.boardXpos + drIndex
                                    var y = piece.boardYpos - drIndex
                                    if (piece.boardYpos - drIndex < 1) {
                                        try {
                                            board =
                                                GameManager.boards[GameManager.boards.indexOf(piece.associatedBoard) - 1] // Board below
                                        } catch (e: IndexOutOfBoundsException) {
                                            // Do something...
                                        }
                                        y =
                                            GameManager.DIMENSIONS - abs(piece.boardYpos - drIndex)
                                    }
                                    if (piece.boardXpos + drIndex > GameManager.DIMENSIONS) {
                                        x = GameManager.DIMENSIONS
                                        drIndex = range + 1 // Stop checking this direction
                                        continue
                                    }

                                    board?.squaresList?.firstOrNull { square ->
                                        square.boardXpos == x
                                            && square.boardYpos == y
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                        } else {
                                            drIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    drIndex++
                                }
                            }
                        }
                        else -> {}
                    }
                }

            }

            else -> {}

        }
        return movementList
    }
}
