package com.daymax86.forwardmarch

import com.daymax86.forwardmarch.animations.SpriteAnimation
import com.daymax86.forwardmarch.board_objects.pieces.Piece

class GameState() {
    val state_pieces: MutableList<Piece> = mutableListOf()
    val state_boards: MutableList<Board> = mutableListOf()
    val state_pickups: MutableList<BoardObject> = mutableListOf()
    val state_activeAnimations: MutableList<SpriteAnimation> = mutableListOf()

    var state_selectedPiece: Piece? = null
    var state_freezeHighlights: Boolean = false

    var state_difficultyModifier: Int = 1
    var state_forwardMarchCounter: Int = 0
    var state_marchInProgress = false
    var state_moveCounter: Int = 0
    var state_moveLimit: Int = 3
    var state_moveLimitReached: Boolean = false
    var state_firstMoveComplete: Boolean = false

    fun updateGameState(
        new_pieces: MutableList<Piece>,
        new_boards: MutableList<Board>,
        new_pickups: MutableList<BoardObject>,
        new_activeAnimations: MutableList<SpriteAnimation>,
        new_selectedPiece: Piece?,
        new_freezeHighlights: Boolean,
        new_difficultyModifier: Int,
        new_forwardMarchCounter: Int,
        new_marchInProgress: Boolean,
        new_moveCounter: Int,
        new_moveLimit: Int,
        new_moveLimitReached: Boolean,
        new_firstMoveComplete: Boolean,
    ) {
        state_pieces.clear()
        new_pieces.forEach { state_pieces.add(it) }
        state_boards.clear()
        new_boards.forEach { state_boards.add(it) }
        state_pickups.clear()
        new_pickups.forEach { state_pickups.add(it) }
        state_activeAnimations.clear()
        new_activeAnimations.forEach { state_activeAnimations.add(it) }
        state_selectedPiece = new_selectedPiece
        state_freezeHighlights = new_freezeHighlights
        state_difficultyModifier = new_difficultyModifier
        state_forwardMarchCounter = new_forwardMarchCounter
        state_marchInProgress = new_marchInProgress
        state_moveCounter = new_moveCounter
        state_moveLimit = new_moveLimit
        state_moveLimitReached = new_moveLimitReached
        state_firstMoveComplete = new_firstMoveComplete
    }
}
