package keditor.utils

import org.lwjgl.glfw.GLFW
import java.util.*

object Time {
    private var previousTime = GLFW.glfwGetTime()
    private var lastTime = GLFW.glfwGetTime()
    private var currentTime = 0.0
    private var frameCount = 0
    private var fpsCap = -1.0
    private var frameTime = -1.0
    var deltaTime = 0.0
    var fps = 0

    init {
        // get monitor refresh rate
        val refreshRate: Int = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()))!!.refreshRate()
        println("Fps capped at $refreshRate")

        fpsCap = refreshRate.toDouble()
        frameTime = 1.0 / fpsCap
    }

    fun begin() {
        currentTime = GLFW.glfwGetTime()
        this.deltaTime = currentTime - lastTime
        lastTime = currentTime
    }

    fun end() {
        ++frameCount

        // fps cap
        if (frameTime != -1.0)
            while (GLFW.glfwGetTime() - lastTime < frameTime);

        // set current fps
        if (currentTime - previousTime >= 1.0) {
            previousTime = currentTime
            fps = frameCount
            frameCount = 0
        }
    }
}