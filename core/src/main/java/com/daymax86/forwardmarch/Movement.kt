package com.daymax86.forwardmarch

import com.daymax86.forwardmarch.board_objects.pieces.Piece
import com.daymax86.forwardmarch.managers.GameManager
import com.daymax86.forwardmarch.managers.StageManager
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
        movementTypes: List<MovementTypes>,
        range: Int,
        directions: List<MovementDirections>
    ): MutableList<Square> {
        val movementList: MutableList<Square> = mutableListOf()

        movementTypes.forEach { movementType ->
            when (movementType) {

                MovementTypes.ROOK -> { // --------------------- ROOK ---------------------------------
                    // --------------------------------------------------------------------------------
                    // Take piece coordinates and check the four directions

                    directions.forEach { direction ->
                        when (direction) {
                            // ------------------------- UP -------------------------
                            MovementDirections.UP -> {
                                var upIndex = 1
                                while (upIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == piece.stageXpos
                                            && square.stageYpos == (piece.stageYpos + upIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                upIndex = range + 1
                                            }
                                        } else {
                                            upIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    upIndex++
                                }
                            }
                            // ------------------------- DOWN -------------------------
                            MovementDirections.DOWN -> {
                                var downIndex = 1
                                while (downIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == piece.stageXpos
                                            && square.stageYpos == (piece.stageYpos - downIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                downIndex = range + 1
                                            }
                                        } else {
                                            downIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    downIndex++
                                }
                            }
                            // ------------------------- LEFT -------------------------
                            MovementDirections.LEFT -> {
                                var leftIndex = 1
                                while (leftIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos - leftIndex)
                                            && square.stageYpos == piece.stageYpos
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                leftIndex = range + 1
                                            }
                                        } else {
                                            leftIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    leftIndex++
                                }
                            }
                            // ------------------------- RIGHT -------------------------
                            MovementDirections.RIGHT -> {
                                var rightIndex = 1
                                while (rightIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos + rightIndex)
                                            && square.stageYpos == piece.stageYpos
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                rightIndex = range + 1
                                            }
                                        } else {
                                            rightIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    rightIndex++
                                }
                            }

                            else -> {
                                /* Do nothing...*/
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
                                while (ulIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos - ulIndex)
                                            && square.stageYpos == (piece.stageYpos + ulIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                ulIndex = range + 1
                                            }
                                        } else {
                                            ulIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    ulIndex++
                                }
                            }
                            // ------------------------- UP-RIGHT ------------------------
                            MovementDirections.UR -> {
                                var urIndex = 1
                                while (urIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos + urIndex)
                                            && square.stageYpos == (piece.stageYpos + urIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                urIndex = range + 1
                                            }
                                        } else {
                                            urIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    urIndex++
                                }

                            }
                            // ------------------------- DOWN-LEFT -----------------------
                            MovementDirections.DL -> {
                                var dlIndex = 1
                                while (dlIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos - dlIndex)
                                            && square.stageYpos == (piece.stageYpos - dlIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                dlIndex = range + 1
                                            }
                                        } else {
                                            dlIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    dlIndex++
                                }
                            }
                            // ------------------------- DOWN-RIGHT ----------------------
                            MovementDirections.DR -> {
                                var drIndex = 1
                                while (drIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos + drIndex)
                                            && square.stageYpos == (piece.stageYpos - drIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                drIndex = range + 1
                                            }
                                        } else {
                                            drIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    drIndex++
                                }
                            }

                            else -> {}
                        }
                    }

                }

                MovementTypes.KNIGHT -> {
                    // --------------------(-1 , +2)---------------------------------
                    // Check the 8 possible locations the knight can move to
                    var x: Int = piece.stageXpos - 1
                    var y = piece.stageYpos + 2
                    StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }

                    // --------------------(+1 , +2)---------------------------------
                    x = piece.stageXpos + 1
                    y = piece.stageYpos + 2
                        StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(+2 , +1)---------------------------------
                    x = piece.stageXpos + 2
                    y = piece.stageYpos + 1
                        StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(+2 , -1)---------------------------------
                    x = piece.stageXpos + 2
                    y = piece.stageYpos - 1
                        StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(+1 , -2)---------------------------------
                    x = piece.stageXpos + 1
                    y = piece.stageYpos - 2
                        StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(-1 , -2)---------------------------------
                    x = piece.stageXpos - 1
                    y = piece.stageYpos - 2
                        StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(-2 , -1)---------------------------------
                    x = piece.stageXpos - 2
                    y = piece.stageYpos - 1
                        StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(-2 , +1)---------------------------------
                    x = piece.stageXpos - 2
                    y = piece.stageYpos + 1
                        StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                }
            }
        }
        return movementList
    }

    /*
    fun getEnemyMovement(
        piece: Piece,
        movementTypes: List<MovementTypes>,
        range: Int,
        directions: List<MovementDirections>
    ): MutableList<Square> {
        val movementList: MutableList<Square> = mutableListOf()

        movementTypes.forEach { movementType ->
            when (movementType) {

                MovementTypes.ROOK -> { // --------------------- ROOK ---------------------------------
                    // --------------------------------------------------------------------------------
                    // Take piece coordinates and check the four directions

                    directions.forEach { direction ->
                        when (direction) {
                            // ------------------------- UP -------------------------
                            MovementDirections.UP -> {
                                var upIndex = 1
                                while (upIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == piece.stageXpos
                                            && square.stageYpos == (piece.stageYpos + upIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                upIndex = range + 1
                                            }
                                        } else {
                                            upIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    upIndex++
                                }
                            }
                            // ------------------------- DOWN -------------------------
                            MovementDirections.DOWN -> {
                                var downIndex = 1
                                while (downIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == piece.stageXpos
                                            && square.stageYpos == (piece.stageYpos - downIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                downIndex = range + 1
                                            }
                                        } else {
                                            downIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    downIndex++
                                }
                            }
                            // ------------------------- LEFT -------------------------
                            MovementDirections.LEFT -> {
                                var leftIndex = 1
                                while (leftIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos - leftIndex)
                                            && square.stageYpos == piece.stageYpos
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                leftIndex = range + 1
                                            }
                                        } else {
                                            leftIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    leftIndex++
                                }
                            }
                            // ------------------------- RIGHT -------------------------
                            MovementDirections.RIGHT -> {
                                var rightIndex = 1
                                while (rightIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos + rightIndex)
                                            && square.stageYpos == piece.stageYpos
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                rightIndex = range + 1
                                            }
                                        } else {
                                            rightIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    rightIndex++
                                }
                            }

                            else -> {
                                /* Do nothing...*/
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
                                while (ulIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos - ulIndex)
                                            && square.stageYpos == (piece.stageYpos + ulIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                ulIndex = range + 1
                                            }
                                        } else {
                                            ulIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    ulIndex++
                                }
                            }
                            // ------------------------- UP-RIGHT ------------------------
                            MovementDirections.UR -> {
                                var urIndex = 1
                                while (urIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos + urIndex)
                                            && square.stageYpos == (piece.stageYpos + urIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                urIndex = range + 1
                                            }
                                        } else {
                                            urIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    urIndex++
                                }

                            }
                            // ------------------------- DOWN-LEFT -----------------------
                            MovementDirections.DL -> {
                                var dlIndex = 1
                                while (dlIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos - dlIndex)
                                            && square.stageYpos == (piece.stageYpos - dlIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                dlIndex = range + 1
                                            }
                                        } else {
                                            dlIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    dlIndex++
                                }
                            }
                            // ------------------------- DOWN-RIGHT ----------------------
                            MovementDirections.DR -> {
                                var drIndex = 1
                                while (drIndex <= range) {
                                    StageManager.stage.squaresList.firstOrNull { square ->
                                        square.stageXpos == (piece.stageXpos + drIndex)
                                            && square.stageYpos == (piece.stageYpos - drIndex)
                                    }.let { sq ->
                                        if (sq?.canBeEntered() == true) {
                                            movementList.add(sq)
                                            if (sq.containsEnemy()) {
                                                // Square contains an enemy, so it can be entered, but not skipped over
                                                drIndex = range + 1
                                            }
                                        } else {
                                            drIndex = range + 1 // Stop checking this direction
                                        }
                                    }
                                    drIndex++
                                }
                            }

                            else -> {}
                        }
                    }

                }

                MovementTypes.KNIGHT -> {
                    // --------------------(-1 , +2)---------------------------------
                    // Check the 8 possible locations the knight can move to
                    var x: Int = piece.stageXpos - 1
                    var y = piece.stageYpos + 2
                    StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }

                    // --------------------(+1 , +2)---------------------------------
                    x = piece.stageXpos + 1
                    y = piece.stageYpos + 2
                    StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(+2 , +1)---------------------------------
                    x = piece.stageXpos + 2
                    y = piece.stageYpos + 1
                    StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(+2 , -1)---------------------------------
                    x = piece.stageXpos + 2
                    y = piece.stageYpos - 1
                    StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(+1 , -2)---------------------------------
                    x = piece.stageXpos + 1
                    y = piece.stageYpos - 2
                    StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(-1 , -2)---------------------------------
                    x = piece.stageXpos - 1
                    y = piece.stageYpos - 2
                    StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(-2 , -1)---------------------------------
                    x = piece.stageXpos - 2
                    y = piece.stageYpos - 1
                    StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                    // --------------------(-2 , +1)---------------------------------
                    x = piece.stageXpos - 2
                    y = piece.stageYpos + 1
                    StageManager.stage.squaresList.firstOrNull { square ->
                        square.stageXpos == x
                            && square.stageYpos == y
                    }.let { sq ->
                        if (sq?.canBeEntered() == true) {
                            movementList.add(sq)
                        }
                    }
                }
            }
        }
        return movementList
    } */

    /*
    fun getSurroundingSquares(
        piece: Piece,
        movementTypes: List<MovementTypes>,
        range: Int,
        directions: List<MovementDirections>
    ): MutableList<Square> {
        val surroundingSquares: MutableList<Square> = mutableListOf()


        movementTypes.forEach { movementType ->
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
                                        var y = piece.stageYpos + upIndex
                                        if (piece.stageYpos + upIndex > GameManager.DIMENSIONS) {
                                            board = piece.nextBoard
                                            y = piece.stageYpos + upIndex - GameManager.DIMENSIONS
                                        }

                                        board?.squaresList?.firstOrNull { square ->
                                            square.stageXpos == piece.stageXpos
                                                && square.stageYpos == y
                                        }.let { sq ->
                                            if (sq != null) {
                                                surroundingSquares.add(sq)
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
                                        var y = piece.stageYpos - downIndex
                                        if (piece.stageYpos - downIndex < 1) {
                                            board = try {
                                                boards[piece.associatedBoard!!.boardIndex - 1] // Board below
                                            } catch (e: IndexOutOfBoundsException) {
                                                null
                                            }
                                            y =
                                                GameManager.DIMENSIONS - abs(piece.stageYpos - downIndex)
                                        }

                                        board?.squaresList?.firstOrNull { square ->
                                            square.stageXpos == piece.stageXpos
                                                && square.stageYpos == y
                                        }.let { sq ->
                                            if (sq != null) {
                                                surroundingSquares.add(sq)
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
                                        var x = piece.stageXpos - leftIndex
                                        if (piece.stageXpos - leftIndex < 1) {
                                            x = 1
                                            leftIndex = range + 1 // Stop checking this direction
                                            continue
                                        }

                                        board?.squaresList?.firstOrNull { square ->
                                            square.stageXpos == x
                                                && square.stageYpos == piece.stageYpos
                                        }.let { sq ->
                                            if (sq != null) {
                                                surroundingSquares.add(sq)
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
                                        var x = piece.stageXpos + rightIndex
                                        if (piece.stageXpos + rightIndex > GameManager.DIMENSIONS) {
                                            x = 8
                                            rightIndex = range + 1 // Stop checking this direction
                                            continue
                                        }

                                        board?.squaresList?.firstOrNull { square ->
                                            square.stageXpos == x
                                                && square.stageYpos == piece.stageYpos
                                        }.let { sq ->
                                            if (sq != null) {
                                                surroundingSquares.add(sq)
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
                                        var x = piece.stageXpos - ulIndex
                                        var y = piece.stageYpos + ulIndex
                                        if (piece.stageYpos + ulIndex > GameManager.DIMENSIONS) {
                                            board = piece.nextBoard
                                            y = piece.stageYpos + ulIndex - GameManager.DIMENSIONS
                                        }
                                        if (piece.stageXpos - ulIndex < 1) {
                                            x = 1
                                            ulIndex = range + 1 // Stop checking this direction
                                            continue
                                        }

                                        board?.squaresList?.firstOrNull { square ->
                                            square.stageXpos == x
                                                && square.stageYpos == y
                                        }.let { sq ->
                                            if (sq != null) {
                                                surroundingSquares.add(sq)
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
                                        var x = piece.stageXpos + urIndex
                                        var y = piece.stageYpos + urIndex
                                        if (piece.stageYpos + urIndex > GameManager.DIMENSIONS) {
                                            board = piece.nextBoard
                                            y = piece.stageYpos + urIndex - GameManager.DIMENSIONS
                                        }
                                        if (piece.stageXpos + urIndex > GameManager.DIMENSIONS) {
                                            x = GameManager.DIMENSIONS
                                            urIndex = range + 1 // Stop checking this direction
                                            continue
                                        }

                                        board?.squaresList?.firstOrNull { square ->
                                            square.stageXpos == x
                                                && square.stageYpos == y
                                        }.let { sq ->
                                            if (sq != null) {
                                                surroundingSquares.add(sq)
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
                                        var x = piece.stageXpos - dlIndex
                                        var y = piece.stageYpos - dlIndex
                                        if (piece.stageYpos - dlIndex < 1) {
                                            board = try {
                                                boards[piece.associatedBoard!!.boardIndex - 1] // Board below
                                            } catch (e: IndexOutOfBoundsException) {
                                                null
                                            }
                                            y =
                                                GameManager.DIMENSIONS - abs(piece.stageYpos - dlIndex)
                                        }
                                        if (piece.stageXpos - dlIndex < 1) {
                                            x = 1
                                            dlIndex = range + 1 // Stop checking this direction
                                            continue
                                        }

                                        board?.squaresList?.firstOrNull { square ->
                                            square.stageXpos == x
                                                && square.stageYpos == y
                                        }.let { sq ->
                                            if (sq != null) {
                                                surroundingSquares.add(sq)
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
                                        var x = piece.stageXpos + drIndex
                                        var y = piece.stageYpos - drIndex
                                        if (piece.stageYpos - drIndex < 1) {
                                            board = try {
                                                boards[piece.associatedBoard!!.boardIndex - 1] // Board below
                                            } catch (e: IndexOutOfBoundsException) {
                                                null
                                            }
                                            y =
                                                GameManager.DIMENSIONS - abs(piece.stageYpos - drIndex)
                                        }
                                        if (piece.stageXpos + drIndex > GameManager.DIMENSIONS) {
                                            x = GameManager.DIMENSIONS
                                            drIndex = range + 1 // Stop checking this direction
                                            continue
                                        }

                                        board?.squaresList?.firstOrNull { square ->
                                            square.stageXpos == x
                                                && square.stageYpos == y
                                        }.let { sq ->
                                            if (sq != null) {
                                                surroundingSquares.add(sq)
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

                MovementTypes.KNIGHT -> {
                    // --------------------(-1 , +2)---------------------------------
                    // Check the 8 possible locations the knight can move to
                    var x: Int = piece.stageXpos - 1
                    var y = piece.stageYpos + 2
                    var board: Board? = piece.associatedBoard
                    if (x > 0) {
                        // The square isn't off the edge of the board
                        if (y > GameManager.DIMENSIONS) {
                            // Must be onto the next board
                            board = piece.nextBoard
                            y -= GameManager.DIMENSIONS
                        }
                        board?.squaresList?.firstOrNull { square ->
                            square.stageXpos == x
                                && square.stageYpos == y
                        }.let { sq ->
                            if (sq != null) {
                                surroundingSquares.add(sq)
                            }
                        }
                    }
                    // --------------------(+1 , +2)---------------------------------
                    x = piece.stageXpos + 1
                    y = piece.stageYpos + 2
                    board = piece.associatedBoard
                    if (x <= GameManager.DIMENSIONS) {
                        // The square isn't off the edge of the board
                        if (y > GameManager.DIMENSIONS) {
                            // Must be onto the next board
                            board = piece.nextBoard
                            y -= GameManager.DIMENSIONS
                        }
                        board?.squaresList?.firstOrNull { square ->
                            square.stageXpos == x
                                && square.stageYpos == y
                        }.let { sq ->
                            if (sq != null) {
                                surroundingSquares.add(sq)
                            }
                        }
                    }
                    // --------------------(+2 , +1)---------------------------------
                    x = piece.stageXpos + 2
                    y = piece.stageYpos + 1
                    board = piece.associatedBoard
                    if (x <= GameManager.DIMENSIONS) {
                        // The square isn't off the edge of the board
                        if (y > GameManager.DIMENSIONS) {
                            // Must be onto the next board
                            board = piece.nextBoard
                            y -= GameManager.DIMENSIONS
                        }
                        board?.squaresList?.firstOrNull { square ->
                            square.stageXpos == x
                                && square.stageYpos == y
                        }.let { sq ->
                            if (sq != null) {
                                surroundingSquares.add(sq)
                            }
                        }
                    }
                    // --------------------(+2 , -1)---------------------------------
                    x = piece.stageXpos + 2
                    y = piece.stageYpos - 1
                    board = piece.associatedBoard
                    if (x <= GameManager.DIMENSIONS) {
                        // The square isn't off the edge of the board
                        if (y < 1) {
                            // Must be onto the previous board
                            board = try {
                                boards[piece.associatedBoard!!.boardIndex - 1] // Board below
                            } catch (e: IndexOutOfBoundsException) {
                                null
                            }
                            y = GameManager.DIMENSIONS - abs(y)
                        }
                        board?.squaresList?.firstOrNull { square ->
                            square.stageXpos == x
                                && square.stageYpos == y
                        }.let { sq ->
                            if (sq != null) {
                                surroundingSquares.add(sq)
                            }
                        }
                    }
                    // --------------------(+1 , -2)---------------------------------
                    x = piece.stageXpos + 1
                    y = piece.stageYpos - 2
                    board = piece.associatedBoard
                    if (x <= GameManager.DIMENSIONS) {
                        // The square isn't off the edge of the board
                        if (y < 1) {
                            // Must be onto the previous board
                            board = try {
                                boards[piece.associatedBoard!!.boardIndex - 1] // Board below
                            } catch (e: IndexOutOfBoundsException) {
                                null
                            }
                            y = GameManager.DIMENSIONS - abs(y)
                        }
                        board?.squaresList?.firstOrNull { square ->
                            square.stageXpos == x
                                && square.stageYpos == y
                        }.let { sq ->
                            if (sq != null) {
                                surroundingSquares.add(sq)
                            }
                        }
                    }
                    // --------------------(-1 , -2)---------------------------------
                    x = piece.stageXpos - 1
                    y = piece.stageYpos - 2
                    board = piece.associatedBoard
                    if (x > 0) {
                        // The square isn't off the edge of the board
                        if (y < 1) {
                            // Must be onto the previous board
                            board = try {
                                boards[piece.associatedBoard!!.boardIndex - 1] // Board below
                            } catch (e: IndexOutOfBoundsException) {
                                null
                            }
                            y = GameManager.DIMENSIONS - abs(y)
                        }
                        board?.squaresList?.firstOrNull { square ->
                            square.stageXpos == x
                                && square.stageYpos == y
                        }.let { sq ->
                            if (sq != null) {
                                surroundingSquares.add(sq)
                            }
                        }
                    }
                    // --------------------(-2 , -1)---------------------------------
                    x = piece.stageXpos - 2
                    y = piece.stageYpos - 1
                    board = piece.associatedBoard
                    if (x > 0) {
                        // The square isn't off the edge of the board
                        if (y < 1) {
                            // Must be onto the previous board
                            board = try {
                                boards[piece.associatedBoard!!.boardIndex - 1] // Board below
                            } catch (e: IndexOutOfBoundsException) {
                                null
                            }
                            y = GameManager.DIMENSIONS - abs(y)
                        }
                        board?.squaresList?.firstOrNull { square ->
                            square.stageXpos == x
                                && square.stageYpos == y
                        }.let { sq ->
                            if (sq != null) {
                                surroundingSquares.add(sq)
                            }
                        }
                    }
                    // --------------------(-2 , +1)---------------------------------
                    x = piece.stageXpos - 2
                    y = piece.stageYpos + 1
                    board = piece.associatedBoard
                    if (x > 0) {
                        // The square isn't off the edge of the board
                        if (y > GameManager.DIMENSIONS) {
                            // Must be onto the next board
                            board = piece.nextBoard
                            y -= GameManager.DIMENSIONS
                        }
                        board?.squaresList?.firstOrNull { square ->
                            square.stageXpos == x
                                && square.stageYpos == y
                        }.let { sq ->
                            if (sq != null) {
                                surroundingSquares.add(sq)
                            }
                        }
                    }
                }

            }
        }


        return surroundingSquares
    } */


}
