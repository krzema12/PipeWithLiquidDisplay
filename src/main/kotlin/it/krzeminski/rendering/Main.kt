package it.krzeminski.rendering

import it.krzeminski.examples.piping.parallelLines
import it.krzeminski.examples.piping.spiral
import it.krzeminski.model.LiquidStream
import it.krzeminski.model.LiquidStreamSegment
import java.awt.*
import java.awt.geom.Arc2D
import java.awt.geom.Area

object DrawShapesExample {
    @JvmStatic
    fun main(args: Array<String>) {
        val frame = Frame()

        frame.add(CustomPaintComponent())

        val frameWidth = 1024
        val frameHeight = 768
        frame.setSize(frameWidth, frameHeight)
        frame.isVisible = true
    }

    internal class CustomPaintComponent : Component() {
        override fun paint(g: Graphics) {
            val g2d = g as Graphics2D
            val renderingHints = RenderingHints(mapOf(
                RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
                RenderingHints.KEY_RENDERING to RenderingHints.VALUE_RENDER_QUALITY))
            g2d.setRenderingHints(renderingHints)

            val piping = parallelLines
            val liquidStream = LiquidStream(
                streamSegment = listOf(
                    LiquidStreamSegment(true, 200.0f),
                    LiquidStreamSegment(false, 100.0f),
                    LiquidStreamSegment(true, 400.0f)
                )
            )
            g2d.render(piping, liquidStream)
        }
    }
}