package keditor.gfx.layers

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15C.*
import org.lwjgl.opengl.GL20C.glVertexAttribPointer

abstract class AbstractLayer {
    abstract fun update()
    abstract fun render()
    abstract fun dispose()

    protected fun storeDataInAttribList(attribNumber: Int, attribSize: Int, data: FloatArray) {
        val vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        val buffer = BufferUtils.createFloatBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)
        glVertexAttribPointer(attribNumber, attribSize, GL_FLOAT, false, 0, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }
}