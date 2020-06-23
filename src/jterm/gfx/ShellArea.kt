package jterm.gfx

import jterm.gfx.layers.AbstractLayer
import jterm.gfx.layers.CaretLayer
import jterm.gfx.layers.TextLayer
import jterm.gfx.textures.TextTexture

class ShellArea(textTexture: TextTexture) {
    private val caretLayer = CaretLayer()
    private val textLayer = TextLayer(textTexture)
    private val layers: List<AbstractLayer> = listOf(textLayer, caretLayer)

    fun update() {

    }

    fun render() {
        for (layer in layers) {
            layer.update()
            layer.render()
        }
    }

    fun dispose() {
        for (layer in layers)
            layer.dispose()
    }
}