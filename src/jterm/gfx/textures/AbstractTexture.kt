package jterm.gfx.textures

import org.lwjgl.opengl.GL11C.*
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load
import org.lwjgl.system.MemoryStack
import java.lang.RuntimeException

abstract class AbstractTexture {
    var textureID = -1
    var width = -1
    var height = -1

    protected fun loadTexture(textureFilePath: String) {
        textureID = glGenTextures()

        glBindTexture(GL_TEXTURE_2D, textureID)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        val stack = MemoryStack.stackPush()
        stack.use {
            val w = it.mallocInt(1)
            val h = it.mallocInt(1)
            val components = it.mallocInt(1)

            stbi_set_flip_vertically_on_load(false)
            val image = stbi_load("resources/$textureFilePath", w, h, components, 4)
                ?: throw RuntimeException("Failed to load a texture $textureFilePath")

            width = w.get()
            height = h.get()
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
            glBindTexture(GL_TEXTURE_2D, 0)
        }
    }
}