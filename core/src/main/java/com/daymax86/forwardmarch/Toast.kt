package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

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
//        Gdx.app.log("toast", "timeElapsed for toast = $timeElapsed")
        if (isFinished()) {
            GameManager.toast = null
            Gdx.app.log("toast", "toast finished!")
        }
    }

}
