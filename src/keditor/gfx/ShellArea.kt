package keditor.gfx

import keditor.gfx.layers.AbstractLayer
import keditor.gfx.layers.CaretLayer
import keditor.gfx.layers.TextLayer
import keditor.gfx.textures.TextTexture

class ShellArea(textTexture: TextTexture) {
    private val caretLayer = CaretLayer()
    private val textLayer = TextLayer(caretLayer, textTexture)
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