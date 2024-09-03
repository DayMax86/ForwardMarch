package com.daymax86.forwardmarch.managers

import com.badlogic.gdx.audio.Sound

object AudioManager {

    var effectsVolume: Float = 0.1f

    fun playSound(sound: Sound?) {
        sound?.play(effectsVolume)
    }

    fun playRandomSound(sounds: MutableList<Sound?>) {
        if (sounds.isNotEmpty()) {
            sounds.random()?.play(effectsVolume)
        }
    }

}
