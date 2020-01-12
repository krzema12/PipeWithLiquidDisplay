package it.krzeminski.rendering

import it.krzeminski.model.*
import java.awt.Graphics2D

fun Graphics2D.render(pipe: Pipe) {
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
    val smallRadius = (pipeSegment.radius - pipeRadius).toInt()
    val smallDiameter = 2*smallRadius
    val largeRadius = (pipeSegment.radius + pipeRadius).toInt()
    val largeDiameter = 2*largeRadius
    drawArc(
        -smallRadius, pipeRadius.toInt(),
        smallDiameter, smallDiameter,
        90, pipeSegment.angle.angle.toInt() * pipeSegment.direction.asAngleSign)
    drawArc(
        -largeRadius, -pipeRadius.toInt(),
        largeDiameter, largeDiameter,
        90, pipeSegment.angle.angle.toInt() * pipeSegment.direction.asAngleSign)
    // TODO implement left direction and translation/rotation afterwards
}

val Direction.asAngleSign get() =
    when (this) {
        Direction.LEFT -> 1
        Direction.RIGHT -> -1
    }
