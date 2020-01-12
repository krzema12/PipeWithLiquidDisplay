package it.krzeminski.rendering

import it.krzeminski.examples.piping.parallelLines
import java.awt.*

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

            val piping = parallelLines

            g2d.render(piping)
        }
    }
}