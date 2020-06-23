package jterm.gfx.layers

import jterm.gfx.shaders.TextShader
import jterm.gfx.textures.TextTexture
import jterm.utils.Input
import jterm.utils.Window
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL11C.glDrawArrays
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL30C
import org.lwjgl.opengl.GL30C.glBindVertexArray
import org.lwjgl.opengl.GL30C.glGenVertexArrays

class TextLayer(private val textTexture: TextTexture) : AbstractLayer() {
    private val textShader = TextShader()
    private val vao = glGenVertexArrays()
    private val posVbo = glGenBuffers()
    private val texVbo = glGenBuffers()

    private val positions = ArrayList<Float>()
    private val texCoords = ArrayList<Float>()

    private var changed = true
    private var text = "abc"

    init {
        GLFWKeyCallback.create { window, key, scancode, action, mods ->
            fun insert(letter: String): String {
                return if (GLFW_MOD_SHIFT and mods > 0)
                    letter.toUpperCase()
                else
                    letter
            }

            if (action == GLFW_PRESS) {
                when (key) {
                    GLFW_KEY_A -> text += insert("a")
                    GLFW_KEY_B -> text += insert("b")
                    GLFW_KEY_C -> text += insert("c")
                    GLFW_KEY_D -> text += insert("d")
                    GLFW_KEY_E -> text += insert("e")
                    GLFW_KEY_F -> text += insert("f")
                    GLFW_KEY_G -> text += insert("g")
                    GLFW_KEY_H -> text += insert("h")
                    GLFW_KEY_I -> text += insert("i")
                    GLFW_KEY_J -> text += insert("j")
                    GLFW_KEY_K -> text += insert("k")
                    GLFW_KEY_L -> text += insert("l")
                    GLFW_KEY_M -> text += insert("m")
                    GLFW_KEY_N -> text += insert("n")
                    GLFW_KEY_O -> text += insert("o")
                    GLFW_KEY_P -> text += insert("p")
                    GLFW_KEY_Q -> text += insert("q")
                    GLFW_KEY_R -> text += insert("r")
                    GLFW_KEY_S -> text += insert("s")
                    GLFW_KEY_T -> text += insert("t")
                    GLFW_KEY_U -> text += insert("u")
                    GLFW_KEY_V -> text += insert("v")
                    GLFW_KEY_W -> text += insert("w")
                    GLFW_KEY_X -> text += insert("x")
                    GLFW_KEY_Y -> text += insert("y")
                    GLFW_KEY_Z -> text += insert("z")
                    GLFW_KEY_SPACE -> text += " "
                    GLFW_KEY_BACKSPACE -> if (text.isNotEmpty()) text = text.substring(0, text.length - 1)
                }

                changed = true
            }
        }.set(Window.windowID)
    }

    override fun update() {
        // input
//        if (Input.isKeyJustPressed(GLFW_KEY_BACKSPACE) && text.isNotEmpty()) {
//            text = text.substring(0, text.length - 1)
//            changed = true
//        }

        if (changed) {
            positions.clear()
            texCoords.clear()

            var xOffset = 0

            // set positions / texture coords
            for (c in text.toCharArray()) {
                val charInfo = textTexture.charMap[c]!!
                val charWidth = charInfo.width
                val charStartX = charInfo.startX

                // left-top
                positions.add(0.0f + xOffset)
                positions.add(0.0f)
                texCoords.add(charStartX / textTexture.width.toFloat())
                texCoords.add(0.0f)

                // right-bottom
                positions.add(0.0f + xOffset + charWidth)
                positions.add(0.0f + textTexture.height)
                texCoords.add((charStartX + charWidth) / textTexture.width.toFloat())
                texCoords.add(1.0f)

                // left-bottom
                positions.add(0.0f + xOffset)
                positions.add(0.0f + textTexture.height)
                texCoords.add(charStartX / textTexture.width.toFloat())
                texCoords.add(1.0f)

                // left-top
                positions.add(0.0f + xOffset)
                positions.add(0.0f)
                texCoords.add(charStartX / textTexture.width.toFloat())
                texCoords.add(0.0f)

                // right-bottom
                positions.add(0.0f + xOffset + charWidth)
                positions.add(0.0f + textTexture.height)
                texCoords.add((charStartX + charWidth) / textTexture.width.toFloat())
                texCoords.add(1.0f)

                // right-top
                positions.add(0.0f + xOffset + charWidth)
                positions.add(0.0f)
                texCoords.add((charStartX + charWidth) / textTexture.width.toFloat())
                texCoords.add(0.0f)

                xOffset += charWidth
            }

            // generate mesh
            glBindVertexArray(vao)

            // pos vbo
            glBindBuffer(GL_ARRAY_BUFFER, posVbo)
            val posBuffer = BufferUtils.createFloatBuffer(positions.size)
            posBuffer.put(positions.toFloatArray())
            posBuffer.flip()
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_DYNAMIC_DRAW)
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0)
            glBindBuffer(GL_ARRAY_BUFFER, 0)

            // tex vbo
            glBindBuffer(GL_ARRAY_BUFFER, texVbo)
            val texBuffer = BufferUtils.createFloatBuffer(texCoords.size)
            texBuffer.put(texCoords.toFloatArray())
            texBuffer.flip()
            glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_DYNAMIC_DRAW)
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0)
            glBindBuffer(GL_ARRAY_BUFFER, 0)

            glBindVertexArray(0)

            changed = false
        }
    }

    override fun render() {
        textShader.bind()

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glBindVertexArray(vao)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textTexture.textureID)
        glDrawArrays(GL_TRIANGLES, 0, text.length * 6)

        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)

        glDisable(GL_BLEND)

        textShader.unbind()
    }

    override fun dispose() {

    }
}