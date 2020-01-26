package it.krzeminski.examples.piping

import it.krzeminski.model.*
import it.krzeminski.repeat
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

val parallelLinesEyeShaped = Pipe(
    initialOrientation = 90.degrees,
    initialPosition = Point(30.0f, 50.0f + 470.0f),
    radius = 7.0f,
    pipeSegments = (0..89)
        .map { Pair(it, it.toFloat()*PI.toFloat()/89.0f) }
        .map { Pair(it.first, abs(sin(it.second))*940.0f) }
        .flatMap { (i, length) ->
            listOf(
                PipeSegment.Straight(length = length),
                PipeSegment.Arc(radius = 10.0f, angle = 180.degrees, direction = if (i % 2 != 0) Direction.RIGHT else Direction.LEFT))
        }
    )
