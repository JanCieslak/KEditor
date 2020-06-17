import jterm.gfx.ShellArea
import jterm.utils.Window
import jterm.utils.Time
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL11C.glClearColor
import java.awt.Canvas
import java.awt.Font

object KTerm {
    private val DEFAULT_FONT = Font("Roboto Mono", Font.BOLD, 20)
    var CELL_WIDTH = 0
        var CELL_HEIGHT = 0

    init {
        val canvas = Canvas()
        val fontMetrics = canvas.getFontMetrics(DEFAULT_FONT)
        CELL_WIDTH = fontMetrics.charWidth('a')
        CELL_HEIGHT = fontMetrics.height
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val window = Window
        val time = Time
        println(String.format("Cell size (%d, %d)", CELL_WIDTH, CELL_HEIGHT))
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        val shellArea = ShellArea()

        while (!window.shouldClose()) {
            time.begin()
            window.prepare()
            window.pollEvents()

            shellArea.render()

            window.swapBuffers()
            time.end()
        }

        // free resources
        shellArea.dispose()
    }
}