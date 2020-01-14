package it.krzeminski.rendering

import it.krzeminski.model.*
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.cos
import kotlin.math.sin
import java.awt.geom.Arc2D
import java.awt.geom.Area

fun Graphics2D.render(pipe: Pipe, liquidStream: LiquidStream) {
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
    // Liquid.
    color = Color.CYAN
    fillRect(0, -pipeRadius.toInt(), pipeSegment.length.toInt(), 2*pipeRadius.toInt())

    // Pipe boundaries.
    color = Color.BLACK
    drawLine(0, -pipeRadius.toInt(), pipeSegment.length.toInt(), -pipeRadius.toInt())
    drawLine(0, pipeRadius.toInt(), pipeSegment.length.toInt(), +pipeRadius.toInt())

    translate(pipeSegment.length.toInt(), 0)
}

fun Graphics2D.renderArc(pipeSegment: PipeSegment.Arc, pipeRadius: Float) {
    if (pipeSegment.direction == Direction.LEFT) {
        scale(1.0, -1.0)
    }

    val smallRadius = (pipeSegment.radius - pipeRadius)
    val smallDiameter = 2 * smallRadius
    val largeRadius = (pipeSegment.radius + pipeRadius)
    val largeDiameter = 2 * largeRadius

    // Liquid.
    color = Color.CYAN
    val arcOuter = Arc2D.Float(
        -largeRadius, -pipeRadius,
        largeDiameter, largeDiameter,
        90.0f, -pipeSegment.angle.angle,
        Arc2D.PIE)
    val arcInner = Arc2D.Float(
        -smallRadius, pipeRadius,
        smallDiameter, smallDiameter,
        90.0f, -pipeSegment.angle.angle,
        Arc2D.PIE)
    val area = Area(arcOuter).apply {
        subtract(Area(arcInner))
    }
    fill(area)

    // Pipe boundaries.
    color = Color.BLACK
    drawArc(
        -smallRadius.toInt(), pipeRadius.toInt(),
        smallDiameter.toInt(), smallDiameter.toInt(),
        90, -pipeSegment.angle.angle.toInt()
    )
    drawArc(
        -largeRadius.toInt(), -pipeRadius.toInt(),
        largeDiameter.toInt(), largeDiameter.toInt(),
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
