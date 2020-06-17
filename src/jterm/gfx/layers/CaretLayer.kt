package jterm.gfx.layers

import KTerm.CELL_HEIGHT
import KTerm.CELL_WIDTH
import jterm.gfx.shaders.CaretShader
import jterm.utils.Input
import jterm.utils.Time
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30C.*

class CaretLayer : AbstractLayer() {
    private val modelMatrix = Matrix4f()

    private val caretColor = Vector4f(1.0f, 0.0f, 1.0f, 1.0f)
    private val caretShader = CaretShader()

    private val caretWidth = CELL_WIDTH.toFloat()
    private val caretHeight = CELL_HEIGHT.toFloat()

    private val vertices = floatArrayOf(
        // first triangle
            0.0f, caretHeight,
            caretWidth, 0.0f,
            0.0f, 0.0f,
        // second triangle
            0.0f, caretHeight,
            caretWidth, 0.0f,
            caretWidth, caretHeight)

    private val vao = genVao()

    private var alpha = 1.0f
    private var blinkingIncrease = false
    private val BLINKING_SPEED = 2.0f

    override fun update() {
        // todo blinking
        caretShader.bind()

        // blinking
        if (alpha >= 1.0f) {
            blinkingIncrease = false
            caretColor.w = 1.0f
        }
        else if (alpha <= 0.0f) {
            blinkingIncrease = true
            caretColor.w = 0.0f
        }

        if (blinkingIncrease)
            alpha += BLINKING_SPEED * Time.deltaTime.toFloat()
        else
            alpha -= BLINKING_SPEED * Time.deltaTime.toFloat()


        caretShader.loadVector4f("color", caretColor)

        if (Input.isKeyJustPressed(GLFW_KEY_RIGHT))
            moveCaret(caretWidth, 0.0f)
        if (Input.isKeyJustPressed(GLFW_KEY_LEFT))
            moveCaret(-caretWidth, 0.0f)
        if (Input.isKeyJustPressed(GLFW_KEY_UP))
            moveCaret(0.0f, -caretHeight)
        if (Input.isKeyJustPressed(GLFW_KEY_DOWN))
            moveCaret(0.0f, caretHeight)

        caretShader.unbind()
    }

    override fun render() {
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        caretShader.bind()

        glBindVertexArray(vao)
        glEnableVertexAttribArray(0)

        glDrawArrays(GL_TRIANGLES, 0, 6)

        glDisableVertexAttribArray(0)
        glBindVertexArray(0)

        caretShader.unbind()
        glDisable(GL_BLEND)
    }

    override fun dispose() {
        caretShader.dispose()
    }

    private fun genVao(): Int {
        val vaoID = glGenVertexArrays()
        glBindVertexArray(vaoID)
        storeDataInAttribList(0, 2, vertices)
        glBindVertexArray(0)
        return vaoID
    }

    private fun moveCaret(xOffset: Float, yOffset: Float) {
        caretShader.loadMatrix("model", modelMatrix.translate(xOffset, yOffset, 0.0f))
        alpha = 1.0f
        caretColor.w = 1.0f
        blinkingIncrease = false
    }
}