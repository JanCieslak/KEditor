package jterm.utils

import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.glfwGetKey

object Input {
    private val isJustPressedMap = HashMap<Int, Boolean>()

    fun isKeyPressed(key: Int): Boolean = glfwGetKey(Window.windowID, key) == GLFW_PRESS
    fun isKeyReleased(key: Int): Boolean = glfwGetKey(Window.windowID, key) == GLFW_PRESS

    fun isKeyJustPressed(key: Int): Boolean {
        isJustPressedMap.putIfAbsent(key, false)

        return if (isKeyPressed(key)) {
            if (isJustPressedMap[key] == false) {
                isJustPressedMap[key] = true
                true
            } else {
                false
            }
        } else {
            isJustPressedMap[key] = false
            false
        }
    }
}