package jterm.gfx.layers

import jterm.gfx.shaders.TextShader
import jterm.gfx.textures.TextTexture
import jterm.utils.Window
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL11C.glDrawArrays
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL30C.glBindVertexArray
import org.lwjgl.opengl.GL30C.glGenVertexArrays

class TextLayer(private val caret: CaretLayer, private val textTexture: TextTexture) : AbstractLayer() {
    private val textShader = TextShader()
    private val vao = glGenVertexArrays()
    private val posVbo = glGenBuffers()
    private val texVbo = glGenBuffers()

    private val positions = ArrayList<Float>()
    private val texCoords = ArrayList<Float>()

    private var changed = true
    private var lines = ArrayList<String>()

    init {
        val initialLines = Window.height / KTerm.CELL_HEIGHT
        for (i in 0..initialLines.toInt())
            lines.add("")


        GLFWKeyCallback.create { window, key, scancode, action, mods ->
            val caretX = (caret.modelMatrix.m30() / KTerm.CELL_WIDTH).toInt()
            val caretY = (caret.modelMatrix.m31() / KTerm.CELL_HEIGHT).toInt()

            fun insert(letter: String) {
                while (caretY >= lines.size)
                    lines.add("")

                if (GLFW_MOD_SHIFT and mods > 0)
                    lines[caretY] += letter.toUpperCase()
                else
                    lines[caretY] += letter
            }

            if (action == GLFW_PRESS) {
                when (key) {
                    GLFW_KEY_A -> insert("a")
                    GLFW_KEY_B -> insert("b")
                    GLFW_KEY_C -> insert("c")
                    GLFW_KEY_D -> insert("d")
                    GLFW_KEY_E -> insert("e")
                    GLFW_KEY_F -> insert("f")
                    GLFW_KEY_G -> insert("g")
                    GLFW_KEY_H -> insert("h")
                    GLFW_KEY_I -> insert("i")
                    GLFW_KEY_J -> insert("j")
                    GLFW_KEY_K -> insert("k")
                    GLFW_KEY_L -> insert("l")
                    GLFW_KEY_M -> insert("m")
                    GLFW_KEY_N -> insert("n")
                    GLFW_KEY_O -> insert("o")
                    GLFW_KEY_P -> insert("p")
                    GLFW_KEY_Q -> insert("q")
                    GLFW_KEY_R -> insert("r")
                    GLFW_KEY_S -> insert("s")
                    GLFW_KEY_T -> insert("t")
                    GLFW_KEY_U -> insert("u")
                    GLFW_KEY_V -> insert("v")
                    GLFW_KEY_W -> insert("w")
                    GLFW_KEY_X -> insert("x")
                    GLFW_KEY_Y -> insert("y")
                    GLFW_KEY_Z -> insert("z")
                    GLFW_KEY_SPACE -> insert(" ")
                    GLFW_KEY_BACKSPACE -> if (lines.isNotEmpty()) {
                        lines[caretY] = lines[caretY].substring(0, lines[caretY].length - 1)
                    }
                }

                changed = true
            }
        }.set(Window.windowID)
    }

    override fun update() {
        if (changed) {
            positions.clear()
            texCoords.clear()

            val caretX = caret.modelMatrix.m30() / KTerm.CELL_WIDTH
            val caretY = caret.modelMatrix.m31() / KTerm.CELL_HEIGHT

            println("Caret position x:${caretX} y:${caretY}")
            
            // set positions / texture coords
            var i = 0.0f
            for (line in lines) {
                var xOffset = 0

                for (c in line.toCharArray()) {
                    val charInfo = textTexture.charMap[c]!!
                    val charWidth = charInfo.width
                    val charStartX = charInfo.startX

                    // left-top
                    positions.add(0.0f + xOffset)
                    positions.add((i * KTerm.CELL_HEIGHT))
                    texCoords.add(charStartX / textTexture.width.toFloat())
                    texCoords.add(0.0f)

                    // right-bottom
                    positions.add(0.0f + xOffset + charWidth)
                    positions.add((i * KTerm.CELL_HEIGHT) + textTexture.height)
                    texCoords.add((charStartX + charWidth) / textTexture.width.toFloat())
                    texCoords.add(1.0f)

                    // left-bottom
                    positions.add(0.0f + xOffset)
                    positions.add((i * KTerm.CELL_HEIGHT) + textTexture.height)
                    texCoords.add(charStartX / textTexture.width.toFloat())
                    texCoords.add(1.0f)

                    // left-top
                    positions.add(0.0f + xOffset)
                    positions.add((i * KTerm.CELL_HEIGHT))
                    texCoords.add(charStartX / textTexture.width.toFloat())
                    texCoords.add(0.0f)

                    // right-bottom
                    positions.add(0.0f + xOffset + charWidth)
                    positions.add((i * KTerm.CELL_HEIGHT) + textTexture.height)
                    texCoords.add((charStartX + charWidth) / textTexture.width.toFloat())
                    texCoords.add(1.0f)

                    // right-top
                    positions.add(0.0f + xOffset + charWidth)
                    positions.add((i * KTerm.CELL_HEIGHT))
                    texCoords.add((charStartX + charWidth) / textTexture.width.toFloat())
                    texCoords.add(0.0f)

                    xOffset += charWidth
                }

                i += 1
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
        glDrawArrays(GL_TRIANGLES, 0, lines.map { it.length }.reduce { acc, i -> acc + i } * 6)

        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)

        glDisable(GL_BLEND)

        textShader.unbind()
    }

    override fun dispose() {

    }
}