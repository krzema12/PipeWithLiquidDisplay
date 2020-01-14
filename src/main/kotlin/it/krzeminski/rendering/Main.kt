package it.krzeminski.rendering

import it.krzeminski.examples.piping.parallelLines
import it.krzeminski.examples.piping.spiral
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

//            val piping = parallelLines
//            g2d.render(piping)
            g2d.color = Color.BLUE
            val arc2dOuter = Arc2D.Float(
                200.0f, 200.0f,
                300.0f, 300.0f,
                20.0f, 70.0f,
                Arc2D.PIE)
            val arc2dInner = Arc2D.Float(
                250.0f, 250.0f,
                200.0f, 200.0f,
                20.0f, 70.0f,
                Arc2D.PIE)
            val area = Area(arc2dOuter).apply {
                subtract(Area(arc2dInner))
            }
            g2d.fill(area)
        }
    }
}