package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.daymax86.forwardmarch.managers.GameManager

class Toast(
    var text: String = "",
    var backgroundImage: Texture = Texture(Gdx.files.internal("hud_elements/toastbar.png")),
    var duration: Float = 300f,
    var timeElapsed: Float = 0f,
) {

    fun isFinished(): Boolean {
        return if (timeElapsed >= duration) true else false
    }

    fun tick() {
        timeElapsed += 1f
        if (isFinished()) {
            GameManager.toast = null
        }
    }

}
