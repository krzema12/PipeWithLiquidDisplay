package it.krzeminski.rendering

import it.krzeminski.model.*
import java.awt.Graphics2D
import kotlin.math.cos
import kotlin.math.sin
import java.awt.RenderingHints

fun Graphics2D.render(pipe: Pipe) {
    val renderingHints = RenderingHints(mapOf(
        RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
        RenderingHints.KEY_RENDERING to RenderingHints.VALUE_RENDER_QUALITY))
    setRenderingHints(renderingHints)

    translate(pipe.initialPosition.x.toInt(), pipe.initialPosition.y.toInt())
    rotate(pipe.initialOrientation.radians)

    pipe.pipeSegments.forEach {
        render(it, pipe.radius)
    }
}

fun Graphics2D.render(pipeSegment: PipeSegment, radius: Float) =
    when (pipeSegment) {
        is PipeSegment.Straight -> renderStraight(pipeSegment, radius)
        is PipeSegment.Arc -> renderArc(pipeSegment, radius)
    }

fun Graphics2D.renderStraight(pipeSegment: PipeSegment.Straight, pipeRadius: Float) {
    drawLine(0, -pipeRadius.toInt(), pipeSegment.length.toInt(), -pipeRadius.toInt())
    drawLine(0, pipeRadius.toInt(), pipeSegment.length.toInt(), +pipeRadius.toInt())
    translate(pipeSegment.length.toInt(), 0)
}

fun Graphics2D.renderArc(pipeSegment: PipeSegment.Arc, pipeRadius: Float) {
    if (pipeSegment.direction == Direction.LEFT) {
        scale(1.0, -1.0)
    }

    val smallRadius = (pipeSegment.radius - pipeRadius).toInt()
    val smallDiameter = 2 * smallRadius
    val largeRadius = (pipeSegment.radius + pipeRadius).toInt()
    val largeDiameter = 2 * largeRadius
    drawArc(
        -smallRadius, pipeRadius.toInt(),
        smallDiameter, smallDiameter,
        90, -pipeSegment.angle.angle.toInt()
    )
    drawArc(
        -largeRadius, -pipeRadius.toInt(),
        largeDiameter, largeDiameter,
        90, -pipeSegment.angle.angle.toInt()
    )
    translate(
        pipeSegment.radius * sin(pipeSegment.angle.radians),
        pipeSegment.radius - pipeSegment.radius * cos(pipeSegment.angle.radians)
    )
    rotate(pipeSegment.angle.radians)
    if (pipeSegment.direction == Direction.LEFT) {
        scale(1.0, -1.0)
    }
}
