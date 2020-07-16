package keditor.gfx.layers

import keditor.gfx.shaders.TextShader
import keditor.gfx.textures.TextTexture
import keditor.utils.Window
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
        lines.add("")
//        val initialLines = Window.height / KTerm.CELL_HEIGHT
//        for (i in 0..initialLines.toInt())
//            lines.add("")

        GLFWKeyCallback.create { window, key, scancode, action, mods ->
            val caretX = (caret.modelMatrix.m30() / KEditor.CELL_WIDTH).toInt()
            val caretY = (caret.modelMatrix.m31() / KEditor.CELL_HEIGHT).toInt()

            fun insert(letter: String) {
                // add lines
                while (caretY >= lines.size)
                    lines.add("")

                // add spaces
                while (caretX >= lines[caretY].length)
                    lines[caretY] += " "

                if (GLFW_MOD_SHIFT and mods > 0)
                    lines[caretY] = lines[caretY].substring(0, caretX) + letter.toUpperCase() + lines[caretY].substring(caretX, lines[caretY].length)
                else
                    lines[caretY] = lines[caretY].substring(0, caretX) + letter + lines[caretY].substring(caretX, lines[caretY].length)

                caret.caretShader.bind()
                caret.moveCaret(caret.caretWidth, 0.0f)
                caret.caretShader.unbind()
            }

            if (action == GLFW_PRESS) {
                when (key) {
                    GLFW_KEY_0 -> if (shiftPressed(mods)) insert(")")
                    GLFW_KEY_1 -> if (shiftPressed(mods)) insert("!") else insert("1")
                    GLFW_KEY_9 -> if (shiftPressed(mods)) insert("(")
                    GLFW_KEY_LEFT_BRACKET -> if (shiftPressed(mods)) insert("{") else insert("[")
                    GLFW_KEY_RIGHT_BRACKET -> if (shiftPressed(mods)) insert("}") else insert("]")
                    GLFW_KEY_APOSTROPHE -> if (shiftPressed(mods)) insert("\"") else insert("\'")
                    GLFW_KEY_COMMA -> insert(",")
                    GLFW_KEY_PERIOD -> insert(".")
                    GLFW_KEY_SEMICOLON -> if (shiftPressed(mods)) insert(":") else insert(";")
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
                    GLFW_KEY_ENTER -> {
                        lines.add(caretY + 1, "")

                        caret.caretShader.bind()
                        caret.moveCaretTo(0.0f, (caretY + 1) * caret.caretHeight)
                        caret.caretShader.unbind()
                    }
                    GLFW_KEY_BACKSPACE ->
                        if (lines[caretY].isNotEmpty() && caretX != 0) {
                            lines[caretY] = lines[caretY].removeRange(caretX - 1, caretX)
                            caret.caretShader.bind()
                            caret.moveCaret(-caret.caretWidth, 0.0f)
                            caret.caretShader.unbind()
                        }
                }

                // could implement observable property
                changed = true
            }
        }.set(Window.windowID)
    }

    override fun update() {
        if (changed) {
            positions.clear()
            texCoords.clear()

            // set positions / texture coords
            var yOffset = 0
            for (line in lines) {
                var xOffset = 0

                for (c in line.toCharArray()) {
                    val charInfo = textTexture.charMap[c]!!
                    val charWidth = charInfo.width
                    val charStartX = charInfo.startX

                    // left-top
                    positions.add(0.0f + xOffset)
                    positions.add(0.0f + (yOffset * KEditor.CELL_HEIGHT))
                    texCoords.add(charStartX / textTexture.width.toFloat())
                    texCoords.add(0.0f)

                    // right-bottom
                    positions.add(0.0f + xOffset + charWidth)
                    positions.add(0.0f + (yOffset * KEditor.CELL_HEIGHT) + textTexture.height)
                    texCoords.add((charStartX + charWidth) / textTexture.width.toFloat())
                    texCoords.add(1.0f)

                    // left-bottom
                    positions.add(0.0f + xOffset)
                    positions.add(0.0f + (yOffset * KEditor.CELL_HEIGHT) + textTexture.height)
                    texCoords.add(charStartX / textTexture.width.toFloat())
                    texCoords.add(1.0f)

                    // left-top
                    positions.add(0.0f + xOffset)
                    positions.add(0.0f + (yOffset * KEditor.CELL_HEIGHT))
                    texCoords.add(charStartX / textTexture.width.toFloat())
                    texCoords.add(0.0f)

                    // right-bottom
                    positions.add(0.0f + xOffset + charWidth)
                    positions.add(0.0f + (yOffset * KEditor.CELL_HEIGHT) + textTexture.height)
                    texCoords.add((charStartX + charWidth) / textTexture.width.toFloat())
                    texCoords.add(1.0f)

                    // right-top
                    positions.add(0.0f + xOffset + charWidth)
                    positions.add(0.0f + (yOffset * KEditor.CELL_HEIGHT))
                    texCoords.add((charStartX + charWidth) / textTexture.width.toFloat())
                    texCoords.add(0.0f)

                    xOffset += charWidth
                }

                yOffset += 1
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

    private fun shiftPressed(mods: Int): Boolean = ((mods and GLFW_MOD_SHIFT) > 0)
}