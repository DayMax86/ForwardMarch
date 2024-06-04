package com.daymax86.forwardmarch

import com.badlogic.gdx.audio.Sound

object AudioManager {

    var effectsVolume: Float = 1.0f

    fun playSound(sound: Sound?) {
        sound?.play(effectsVolume)
    }

    fun playRandomSound(sounds: MutableList<Sound?>) {
        if (sounds.isNotEmpty()) {
            sounds.random()?.play()
        }
    }

}
