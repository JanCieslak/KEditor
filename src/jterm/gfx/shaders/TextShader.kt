package jterm.gfx.shaders

import jterm.utils.Window
import org.joml.Matrix4f

class TextShader : AbstractShader("text/vertex", "text/fragment") {
    init {
        bind()
        loadMatrix("projection", Matrix4f().ortho(0.0f, Window.width, Window.height, 0.0f, -1.0f, 1.0f))
        loadMatrix("model", Matrix4f())
        unbind()
    }
}