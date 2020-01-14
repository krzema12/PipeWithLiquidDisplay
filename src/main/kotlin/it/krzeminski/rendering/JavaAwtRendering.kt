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
        render(it, pipe.radius, liquidStream)
    }
}

fun Graphics2D.render(pipeSegment: PipeSegment, radius: Float, liquidStream: LiquidStream) =
    when (pipeSegment) {
        is PipeSegment.Straight -> renderStraight(pipeSegment, radius, liquidStream)
        is PipeSegment.Arc -> renderArc(pipeSegment, radius, liquidStream)
    }

fun Graphics2D.renderStraight(
    pipeSegment: PipeSegment.Straight,
    pipeRadius: Float,
    liquidStream: LiquidStream
) {
    // Liquid.
    color = Color.CYAN
    // TODO check if the segments don't go beyond pipe's length
    getLiquidSegmentToDraw(pipeSegment, pipeRadius, liquidStream).forEach { (startX, width) ->
        fillRect(startX.toInt(), -pipeRadius.toInt(), width.toInt(), 2 * pipeRadius.toInt())
    }

    // Pipe boundaries.
    color = Color.BLACK
    drawLine(0, -pipeRadius.toInt(), pipeSegment.length.toInt(), -pipeRadius.toInt())
    drawLine(0, pipeRadius.toInt(), pipeSegment.length.toInt(), +pipeRadius.toInt())

    translate(pipeSegment.length.toInt(), 0)
}

data class LiquidSegmentToDrawStraight(
    val startX: Float,
    val width: Float
)

fun getLiquidSegmentToDraw(pipeSegment: PipeSegment.Straight, pipeRadius: Float, liquidStream: LiquidStream): List<LiquidSegmentToDrawStraight> {
    val drawableWidths = liquidStream.streamSegment.map {
        it.volume / (2.0f * pipeRadius)
    }
    val cumulativeDrawableWidths = drawableWidths.fold(listOf(0.0f)) {
        acc, width -> acc + (acc.last() + width)
    }
    val allSegmentsToDraw = (drawableWidths zip cumulativeDrawableWidths).map { (width, startX) ->
        LiquidSegmentToDrawStraight(startX, width)
    }
    return (allSegmentsToDraw zip liquidStream.streamSegment).filter { (_, streamSegment) ->
        streamSegment.liquidPresent
    }.map { (segmentToDraw, _) -> segmentToDraw }
}

fun Graphics2D.renderArc(
    pipeSegment: PipeSegment.Arc,
    pipeRadius: Float,
    liquidStream: LiquidStream
) {
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
