package jterm.gfx

import jterm.gfx.layers.AbstractLayer
import jterm.gfx.layers.CaretLayer

class ShellArea {
    private val layers: List<AbstractLayer> = listOf(CaretLayer())

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