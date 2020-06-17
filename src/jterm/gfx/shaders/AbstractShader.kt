package jterm.gfx.shaders

import jterm.utils.Window
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20C.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

abstract class AbstractShader constructor(vertex: String, fragment: String) {
    private var programID  = 0
    private var vertexID   = 0
    private var fragmentID = 0

    private val locations = HashMap<String, Int>()
    private val matrixBuffer = BufferUtils.createFloatBuffer(16)

    init {
        vertexID = loadShader("resources/shaders/$vertex.vert", GL_VERTEX_SHADER)
        fragmentID = loadShader("resources/shaders/$fragment.frag", GL_FRAGMENT_SHADER)
        programID = glCreateProgram()
        glAttachShader(programID, vertexID);
        glAttachShader(programID, fragmentID);
        glLinkProgram(programID)
        glValidateProgram(programID)
    }

    fun bind() = glUseProgram(programID)
    fun unbind() = glUseProgram(0)

    fun loadInt(name: String, value: Int) = glUniform1i(getLocation(name), value)
    fun loadFloat(name: String, value: Float) = glUniform1f(getLocation(name), value)
    fun loadVector2f(name: String, vec: Vector2f) = glUniform2f(getLocation(name), vec.x, vec.y)
    fun loadVector3f(name: String, vec: Vector3f) = glUniform3f(getLocation(name), vec.x, vec.y, vec.z)
    fun loadVector4f(name: String, vec: Vector4f) = glUniform4f(getLocation(name), vec.x, vec.y, vec.z, vec.w)
    fun loadBoolean(name: String, bool: Boolean) = glUniform1f(getLocation(name), if (bool) 1.0f else 0.0f)
    fun loadMatrix(name: String, matrix: Matrix4f) {
        matrix.get(matrixBuffer)
        glUniformMatrix4fv(getLocation(name), false, matrixBuffer)
    }

    fun dispose() {
        glDetachShader(programID, vertexID)
        glDetachShader(programID, fragmentID)
        glDeleteShader(vertexID)
        glDeleteShader(fragmentID)
        glDeleteProgram(programID)
    }

    private fun getLocation(name: String): Int {
        locations.putIfAbsent(name, glGetUniformLocation(programID, name))
        return locations[name]!!
    }

    private fun loadShader(filePath: String, type: Int): Int {
        val shaderSource = String(Files.readAllBytes(Path.of(filePath)))
        val shaderID = glCreateShader(type)
        glShaderSource(shaderID, shaderSource)
        glCompileShader(shaderID)
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            println(glGetShaderInfoLog(shaderID))
            println("Couldn't compile $filePath shader")
            exitProcess(-1)
        }

        return shaderID
    }
}