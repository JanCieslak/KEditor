package jterm.gfx.shaders

import jterm.utils.Window
import org.joml.Matrix4f
import org.joml.Vector3f

class CaretShader : AbstractShader("caret/vertex", "caret/fragment") {
    init {
        bind()
        loadMatrix("projection", Matrix4f().ortho(0.0f, Window.width, Window.height, 0.0f, -1.0f, 1.0f))
        loadMatrix("model", Matrix4f())
        loadVector3f("color", Vector3f(0.0f, 1.0f, 0.0f))
        unbind()
    }
}