package it.krzeminski.rendering

import it.krzeminski.examples.piping.parallelLines
import it.krzeminski.model.LiquidStream
import it.krzeminski.model.LiquidStreamSegment
import it.krzeminski.repeat
import java.awt.*
import java.lang.System.currentTimeMillis
import javax.swing.Timer
import kotlin.properties.Delegates

object DrawShapesExample {
    @JvmStatic
    fun main(args: Array<String>) {
        val frame = Frame()

        frame.add(CustomPaintComponent())

        val frameWidth = 400
        val frameHeight = 768
        frame.setSize(frameWidth, frameHeight)
        frame.isVisible = true

        val t = Timer(50) {
            frame.repaint()
        }
        t.start()
    }

    internal class CustomPaintComponent() : Component() {
        private var startTime by Delegates.notNull<Long>()

        init {
            startTime = currentTimeMillis()
        }

        override fun paint(g: Graphics) {
            // Draw to a back-buffer first.
            val backBufferImage = createImage(width, height)
            val backBufferImageGraphics = backBufferImage.graphics

            val g2d = backBufferImageGraphics as Graphics2D
            val renderingHints = RenderingHints(mapOf(
                RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
                RenderingHints.KEY_RENDERING to RenderingHints.VALUE_RENDER_QUALITY))
            g2d.setRenderingHints(renderingHints)

            val offset = (currentTimeMillis() - startTime).toFloat() * 0.1f

            val piping = parallelLines
            val liquidStream = LiquidStream(
                streamSegment = listOf(LiquidStreamSegment(false, offset)) + listOf(
                    LiquidStreamSegment(true, 12340.0f),
                    LiquidStreamSegment(false, 234.0f),
                    LiquidStreamSegment(true, 345.0f),
                    LiquidStreamSegment(false, 567.0f)
                ).repeat(30)
            )
            g2d.render(piping, liquidStream)

            g.drawImage(backBufferImage, 0, 0, this)
        }
    }
}