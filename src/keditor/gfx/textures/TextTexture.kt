package keditor.gfx.textures

import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO

data class CharInfo(val startX: Int, val width: Int)

class TextTexture(font: Font) : AbstractTexture() {
    val charMap = HashMap<Char, CharInfo>()
    private var ascent = -1

    init {
        // generate a font texture
        // make temporary image to get font metrics
        val tempImg = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val gfx = tempImg.createGraphics()
        gfx.font = font
        val fm = gfx.fontMetrics

        // get all available chars in specified charset
        val charset = StandardCharsets.ISO_8859_1
        val encoder = charset.newEncoder()
        val sb = StringBuilder()
        for (c in Char.MIN_VALUE..Char.MAX_VALUE)
            if (encoder.canEncode(c))
                sb.append(c)

        val availableChars = sb.toString()
        var width = 0
        val height = fm.height
        ascent = fm.ascent

        for (c in availableChars.toCharArray()) {
            val charInfo = CharInfo(width, fm.charWidth(c))
            charMap[c] = charInfo
            width += fm.charWidth(c)
        }
        gfx.dispose()

        // make font texture
        val textureImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val textureGfx = textureImage.createGraphics()
        textureGfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        textureGfx.font = font
        val textureFm = textureGfx.fontMetrics
        textureGfx.color = Color.WHITE
        textureGfx.drawString(availableChars, 0, textureFm.ascent)
        ImageIO.write(textureImage, "png", File("resources/fonts/font.png"))

        loadTexture("fonts/font.png")
    }
}