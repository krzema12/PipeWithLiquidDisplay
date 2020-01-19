package it.krzeminski.rendering

import it.krzeminski.examples.piping.parallelLines
import it.krzeminski.model.LiquidStream
import it.krzeminski.model.LiquidStreamSegment
import it.krzeminski.repeat
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

object DrawShapesExample {
    @JvmStatic
    fun main(args: Array<String>) {
        val frame = Frame()
        val customPaintComponent = CustomPaintComponent()
        frame.add(customPaintComponent)

        val frameWidth = 1024
        val frameHeight = 768
        frame.setSize(frameWidth, frameHeight)
        frame.isVisible = true
        frame.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {
            }

            override fun keyPressed(e: KeyEvent?) {
                when (e?.keyChar) {
                    'q' -> customPaintComponent.liquidOffset += 20.0f
                    'a' -> customPaintComponent.liquidOffset -= 20.0f
                    'w' -> customPaintComponent.liquidOffset += 200.0f
                    's' -> customPaintComponent.liquidOffset -= 200.0f
                    'e' -> customPaintComponent.addPieceOfLiquid(200.0f)
                    'd' -> customPaintComponent.addPieceOfAir(200.0f)
                }
                frame.repaint()
            }

            override fun keyReleased(e: KeyEvent?) {
            }
        })
    }

    internal class CustomPaintComponent() : Component() {
        var liquidOffset: Float = 0.0f
        var editableLiquidStream = LiquidStream(
            streamSegment = listOf(LiquidStreamSegment(false, 0.0f)))

        override fun paint(g: Graphics) {
            // Draw to a back-buffer first.
            val backBufferImage = createImage(width, height)
            val backBufferImageGraphics = backBufferImage.graphics

            val g2d = backBufferImageGraphics as Graphics2D
            val renderingHints = RenderingHints(mapOf(
                RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
                RenderingHints.KEY_RENDERING to RenderingHints.VALUE_RENDER_QUALITY))
            g2d.setRenderingHints(renderingHints)

            val piping = parallelLines
//            val liquidStream = LiquidStream(
//                streamSegment = listOf(LiquidStreamSegment(false, liquidOffset)) + listOf(
//                    LiquidStreamSegment(true, 12340.0f),
//                    LiquidStreamSegment(false, 234.0f),
//                    LiquidStreamSegment(true, 345.0f),
//                    LiquidStreamSegment(false, 567.0f)
//                ).repeat(30)
//            )
            g2d.render(piping, editableLiquidStream)

            g.drawImage(backBufferImage, 0, 0, this)
        }

        fun addPieceOfLiquid(volume: Float) {
            with (editableLiquidStream) {
                editableLiquidStream = if (streamSegment.last().liquidPresent) {
                    val lastSegment = streamSegment.last()
                    copy(
                        streamSegment = streamSegment.dropLast(1) +
                                LiquidStreamSegment(true, lastSegment.volume + volume))
                } else {
                    copy(
                        streamSegment = streamSegment +
                                LiquidStreamSegment(true, volume))
                }
            }
        }

        fun addPieceOfAir(volume: Float) {
            with (editableLiquidStream) {
                editableLiquidStream = if (!streamSegment.last().liquidPresent) {
                    val lastSegment = streamSegment.last()
                    copy(
                        streamSegment = streamSegment.dropLast(1) +
                                LiquidStreamSegment(false, lastSegment.volume + volume))
                } else {
                    copy(
                        streamSegment = streamSegment +
                                LiquidStreamSegment(false, volume))
                }
            }
        }
    }
}