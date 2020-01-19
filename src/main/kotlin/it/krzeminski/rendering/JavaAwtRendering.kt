package it.krzeminski.rendering

import it.krzeminski.cumulativeSum
import it.krzeminski.cutSequentialItems
import it.krzeminski.groupSequentialItems
import it.krzeminski.model.*
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.cos
import kotlin.math.sin
import java.awt.geom.Arc2D
import java.awt.geom.Area
import kotlin.math.PI

fun Graphics2D.render(pipe: Pipe, liquidStream: LiquidStream) {
    translate(pipe.initialPosition.x.toInt(), pipe.initialPosition.y.toInt())
    rotate(pipe.initialOrientation.radians)

    val liquidStreamWithCursorAtEnd = liquidStream.copy(
        streamSegment = liquidStream.streamSegment + LiquidStreamSegment(true, 100.0f)
    )

    val liquidStreamCutForPipeSegments = cutSequentialItems(
        liquidStreamWithCursorAtEnd.streamSegment,
        pipe.pipeSegments.map { it.volume(pipe.radius) },
        { it.volume },
        { segment, newVolume -> segment.copy(volume = newVolume) })

    val liquidStreamSegmentsPerPipeSegment = groupSequentialItems(
        liquidStreamCutForPipeSegments,
        pipe.pipeSegments.map { it.volume(pipe.radius) },
        { it.volume })

    (pipe.pipeSegments zip liquidStreamSegmentsPerPipeSegment).forEach { (pipeSegment, liquidStreamForPipeSegment) ->
        render(pipeSegment, pipe.radius, LiquidStream(liquidStreamForPipeSegment))
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
    color = Color.BLUE
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

data class LiquidSegmentToDrawArc(
    val startAngle: Float,
    val extentAngle: Float
)

fun getLiquidSegmentToDraw(pipeSegment: PipeSegment.Straight, pipeRadius: Float, liquidStream: LiquidStream): List<LiquidSegmentToDrawStraight> {
    val drawableWidths = liquidStream.streamSegment.map {
        it.volume / (2.0f * pipeRadius)
    }
    val cumulativeDrawableWidths = drawableWidths.cumulativeSum(0.0f) { a, b -> a + b }
    val allSegmentsToDraw = (drawableWidths zip cumulativeDrawableWidths).map { (width, startX) ->
        LiquidSegmentToDrawStraight(startX, width)
    }
    return (allSegmentsToDraw zip liquidStream.streamSegment).filter { (_, streamSegment) ->
        streamSegment.liquidPresent
    }.map { (segmentToDraw, _) -> segmentToDraw }
}

fun getLiquidSegmentToDraw(pipeSegment: PipeSegment.Arc, pipeRadius: Float, liquidStream: LiquidStream): List<LiquidSegmentToDrawArc> {
    val smallRadius = (pipeSegment.radius - pipeRadius)
    val largeRadius = (pipeSegment.radius + pipeRadius)

    val drawableAngleExtents = liquidStream.streamSegment.map {
        it.volume * 360.0f / (PI.toFloat() * (largeRadius*largeRadius - smallRadius*smallRadius))
    }
    val cumulativeDrawableAngleExtents = drawableAngleExtents.cumulativeSum(0.0f) { a, b -> a + b }
    val allSegmentsToDraw = (drawableAngleExtents zip cumulativeDrawableAngleExtents).map { (extentAngle, startAngle) ->
        LiquidSegmentToDrawArc(startAngle, extentAngle)
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
    color = Color.BLUE
    getLiquidSegmentToDraw(pipeSegment, pipeRadius, liquidStream).forEach { (startAngle, extentAngle) ->
        fillArcWithoutCenter(
            pipeSegment, largeRadius, pipeRadius, largeDiameter, smallRadius, smallDiameter, startAngle, extentAngle)
    }

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

fun Graphics2D.fillArcWithoutCenter(
    pipeSegment: PipeSegment.Arc,
    largeRadius: Float,
    pipeRadius: Float,
    largeDiameter: Float,
    smallRadius: Float,
    smallDiameter: Float,
    startAngle: Float,
    endAngle: Float
) {
    val arcOuter = Arc2D.Float(
        -largeRadius, -pipeRadius,
        largeDiameter, largeDiameter,
        90.0f - startAngle, -endAngle,
        Arc2D.PIE
    )
    val arcInner = Arc2D.Float(
        -smallRadius, pipeRadius,
        smallDiameter, smallDiameter,
        90.0f - startAngle, -endAngle,
        Arc2D.PIE
    )
    val area = Area(arcOuter).apply {
        subtract(Area(arcInner))
    }
    fill(area)
}
