import keditor.gfx.ShellArea
import keditor.gfx.textures.TextTexture
import keditor.utils.Window
import keditor.utils.Time
import org.lwjgl.opengl.GL11C.glClearColor
import java.awt.Canvas
import java.awt.Font

object KEditor {
    private val DEFAULT_FONT = Font("Roboto Mono", Font.BOLD, 24)
    var CELL_WIDTH = 0
    var CELL_HEIGHT = 0

    init {
        val canvas = Canvas()
        val fontMetrics = canvas.getFontMetrics(DEFAULT_FONT)
        // todo this only works for mono fonts
        CELL_WIDTH = fontMetrics.charWidth('a')
        CELL_HEIGHT = fontMetrics.height
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val window = Window
        val time = Time
        println(String.format("Cell size (%d, %d)", CELL_WIDTH, CELL_HEIGHT))
        glClearColor(0.0f, 0.08f, 0.15f, 1.0f)

        val textTexture = TextTexture(DEFAULT_FONT)
        val shellArea = ShellArea(textTexture)

        while (!window.shouldClose()) {
            time.begin()
            window.prepare()
            window.pollEvents()

            shellArea.update()
            shellArea.render()

            window.swapBuffers()
            time.end()
        }

        // free resources
        shellArea.dispose()
    }
}