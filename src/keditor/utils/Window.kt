package keditor.utils

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

object Window {
    var windowID: Long
    var title = "KEditor"
    var width = 1200.0f
    var height = 700.0f

    // create main window
    init {
        GLFWErrorCallback.createPrint(System.out).set()
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
        windowID = GLFW.glfwCreateWindow(
            width.toInt(),
            height.toInt(),
            title, MemoryUtil.NULL, MemoryUtil.NULL)

        if (windowID == MemoryUtil.NULL)
            throw RuntimeException("Failed to create the GLFW window")

        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*
            GLFW.glfwGetWindowSize(windowID, pWidth, pHeight)
            val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())

            // Center the window
            GLFW.glfwSetWindowPos(
                windowID,
                (vidmode!!.width() - pWidth[0]) / 2,
                (vidmode.height() - pHeight[0]) / 2
            )
        }

        GLFW.glfwMakeContextCurrent(windowID)
        GLFW.glfwSwapInterval(0)
        GLFW.glfwShowWindow(windowID)
        GL.createCapabilities()
        GLFW.glfwSetInputMode(
            windowID,
            GLFW.GLFW_STICKY_KEYS,
            GLFW.GLFW_TRUE
        )
        setupCallbacks()
        GL11C.glViewport(0, 0,
            width.toInt(),
            height.toInt()
        )
    }

    // in main cycle
    fun shouldClose() = GLFW.glfwWindowShouldClose(windowID)
    fun prepare() = GL11C.glClear(GL11C.GL_COLOR_BUFFER_BIT or GL11C.GL_DEPTH_BUFFER_BIT)
    fun pollEvents() = glfwPollEvents()
    fun swapBuffers() = glfwSwapBuffers(windowID)

    private fun setupCallbacks() {
        GLFWWindowSizeCallback.create { window: Long, width: Int, height: Int ->
            Window.width = width.toFloat()
            Window.height = height.toFloat()
            GL11C.glViewport(0, 0, width, height)
        }.set(windowID)

        GLFWKeyCallback.create { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) GLFW.glfwSetWindowShouldClose(
                windowID,
                true
            )
        }.set(windowID)

    }
}