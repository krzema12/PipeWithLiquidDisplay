package it.krzeminski.rendering

import com.beust.klaxon.Klaxon
import it.krzeminski.examples.piping.parallelLines
import it.krzeminski.examples.piping.spiral
import it.krzeminski.model.LiquidStream
import it.krzeminski.model.LiquidStreamSegment
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object DrawShapesExample {
    @JvmStatic
    fun main(args: Array<String>) {
        val frame = Frame()
        val pipeWithLiquidDisplayComponent = PipeWithLiquidDisplayComponent()
        frame.add(pipeWithLiquidDisplayComponent)

        val frameWidth = 1024
        val frameHeight = 768
        frame.setSize(frameWidth, frameHeight)
        frame.background = Color.WHITE
        frame.isVisible = true
        frame.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {
            }

            override fun keyPressed(e: KeyEvent?) {
                when (e?.keyChar) {
                    'q' -> pipeWithLiquidDisplayComponent.changeLiquidOffset(20.0f)
                    'a' -> pipeWithLiquidDisplayComponent.changeLiquidOffset(-20.0f)
                    'w' -> pipeWithLiquidDisplayComponent.changeLiquidOffset(200.0f)
                    's' -> pipeWithLiquidDisplayComponent.changeLiquidOffset(-200.0f)
                    'e' -> pipeWithLiquidDisplayComponent.addPieceOfLiquid(200.0f)
                    'd' -> pipeWithLiquidDisplayComponent.addPieceOfAir(200.0f)
                    'c' -> pipeWithLiquidDisplayComponent.clearLiquidStream()
                    'i' -> pipeWithLiquidDisplayComponent.toggleImage()
                    'r' -> pipeWithLiquidDisplayComponent.toggleImageStoring()
                }
                e?.let {
                    if (e.keyCode in 48..58) {
                        val slotId = e.keyCode - 48

                        if (e.isShiftDown) { // Store liquid stream.
                            pipeWithLiquidDisplayComponent.storeLiquidStream(slotId)
                        } else if (e.isControlDown) { // Load piping.
                            pipeWithLiquidDisplayComponent.loadPiping(slotId)
                        } else { // Load liquid stream.
                            pipeWithLiquidDisplayComponent.loadLiquidStream(slotId)
                        }
                    }
                }
                frame.repaint()
            }

            override fun keyReleased(e: KeyEvent?) {
            }
        })
    }

    internal class PipeWithLiquidDisplayComponent() : Component() {
        var liquidOffset: Float = 0.0f
        var piping = parallelLines
        var displayImage = true
        var shouldStoreImage = false
        var editableLiquidStream = LiquidStream(
            streamSegment = listOf(LiquidStreamSegment(false, 0.0f)))
        var lastSavedImageId = 0

        override fun paint(g: Graphics) {
            // Draw to a back-buffer first.
            val backBufferImage = createImage(width, height)
            val backBufferImageGraphics = backBufferImage.graphics

            val g2d = backBufferImageGraphics as Graphics2D
            val renderingHints = RenderingHints(mapOf(
                RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
                RenderingHints.KEY_RENDERING to RenderingHints.VALUE_RENDER_QUALITY))
            g2d.setRenderingHints(renderingHints)

            if (displayImage) {
                g2d.drawImage(
                    ImageIO.read(this.javaClass.getResource("/images/heart.jpg")),
                    140, 50, 236*3, 218*3, this)
            }
            g2d.render(piping, editableLiquidStream)

            if (shouldStoreImage) {
                storeImage(backBufferImage)
            }
            g.drawImage(backBufferImage, 0, 0, this)
        }

        private fun storeImage(image: Image) {
            val width = image.getWidth(this)
            val height = image.getHeight(this)
            val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
            bufferedImage.graphics.drawImage(image, 0, 0, this)
            with (File("Image${lastSavedImageId.toString().padStart(5, '0')}.png")) {
                ImageIO.write(bufferedImage, "png", this)
                println("Storing image to $this")
            }
            lastSavedImageId++
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

        fun changeLiquidOffset(airVolume: Float) {
            val first = editableLiquidStream.streamSegment.first()
            editableLiquidStream = editableLiquidStream.copy(streamSegment =
                listOf(first.copy(liquidPresent = false, volume = first.volume + airVolume))
                        + editableLiquidStream.streamSegment.drop(1))
        }

        fun storeLiquidStream(slotId: Int) {
            with (File("LiquidStream$slotId.json")) {
                writeText(Klaxon().toJsonString(editableLiquidStream))
                println("Stored in ${this.absolutePath}")
            }
        }

        fun loadLiquidStream(slotId: Int) {
            with (File("LiquidStream$slotId.json")) {
                println("Loading from ${this.absolutePath}")
                editableLiquidStream = Klaxon().parse(readText())
                    ?: throw IllegalArgumentException("Invalid JSON")
            }
        }

        fun clearLiquidStream() {
            editableLiquidStream = LiquidStream(
                streamSegment = listOf(LiquidStreamSegment(false, 0.0f)))
        }

        fun loadPiping(slotId: Int) {
            piping = when (slotId) {
                1 -> parallelLines
                2 -> spiral
                else -> throw IllegalArgumentException("No piping under slot ID $slotId")
            }
        }

        fun toggleImage() {
            displayImage = !displayImage
        }

        fun toggleImageStoring() {
            shouldStoreImage = !shouldStoreImage
        }
    }
}