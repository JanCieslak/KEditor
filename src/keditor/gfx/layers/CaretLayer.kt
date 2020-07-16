package keditor.gfx.layers

import KEditor.CELL_HEIGHT
import KEditor.CELL_WIDTH
import keditor.gfx.shaders.CaretShader
import keditor.utils.Input
import keditor.utils.Time
import keditor.utils.Window
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWWindowFocusCallback
import org.lwjgl.opengl.GL30C.*

class CaretLayer : AbstractLayer() {
    val modelMatrix = Matrix4f()

    private val caretMaxAlpha = 0.6f
    private val caretColor = Vector4f(1.0f, 0.79f, 0.54f, caretMaxAlpha)
    val caretShader = CaretShader()

    val caretWidth = CELL_WIDTH.toFloat()
    val caretHeight = CELL_HEIGHT.toFloat()

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
    private val blinkingSpeed = 2.0f
    private var shouldBlink = true;

    init {
        GLFWWindowFocusCallback.create { _, focused ->
            shouldBlink = focused
        }.set(Window.windowID)
    }

    override fun update() {
        caretShader.bind()

        if (shouldBlink) {
            // blinking
            if (alpha >= 1.0f) {
                blinkingIncrease = false
                caretColor.w = caretMaxAlpha
            } else if (alpha <= 0.0f) {
                blinkingIncrease = true
                caretColor.w = 0.0f
            }

            if (blinkingIncrease)
                alpha += blinkingSpeed * Time.deltaTime.toFloat()
            else
                alpha -= blinkingSpeed * Time.deltaTime.toFloat()

            caretShader.loadVector4f("color", caretColor)
        }

        if (Input.isKeyJustPressed(GLFW_KEY_RIGHT))
            moveCaret(caretWidth, 0.0f)
        if (Input.isKeyJustPressed(GLFW_KEY_LEFT))
            moveCaret(-caretWidth, 0.0f)
        if (Input.isKeyJustPressed(GLFW_KEY_UP))
            moveCaret(0.0f, -caretHeight)
        if (Input.isKeyJustPressed(GLFW_KEY_DOWN))
            moveCaret(0.0f, caretHeight)
//        if (Input.isKeyPressed(GLFW_KEY_RIGHT))
//            moveCaret(caretWidth, 0.0f)
//        if (Input.isKeyPressed(GLFW_KEY_LEFT))
//            moveCaret(-caretWidth, 0.0f)
//        if (Input.isKeyPressed(GLFW_KEY_UP))
//            moveCaret(0.0f, -caretHeight)
//        if (Input.isKeyPressed(GLFW_KEY_DOWN))
//            moveCaret(0.0f, caretHeight)

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

    fun moveCaretTo(xPos: Float, yPos: Float) {
        modelMatrix.identity()
        caretShader.loadMatrix("model", modelMatrix.translate(xPos, yPos, 0.0f))
        alpha = 1.0f
        caretColor.w = 1.0f
        blinkingIncrease = false
    }

    fun moveCaret(xOffset: Float, yOffset: Float) {
        caretShader.loadMatrix("model", modelMatrix.translate(xOffset, yOffset, 0.0f))
        alpha = 1.0f
        caretColor.w = 1.0f
        blinkingIncrease = false
    }
}