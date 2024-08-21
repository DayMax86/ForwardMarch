package com.daymax86.forwardmarch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

data class InfoBox(
    var titleText: String,
    var backgroundImage: Texture = Texture(Gdx.files.internal("hud_elements/infobox_background.png")),
    var thumbnailImage: Texture,
    var x: Float,
    var y: Float,
    var width: Int,
    var height: Int,
    var description: String,
) {


}
