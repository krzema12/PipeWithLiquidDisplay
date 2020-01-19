package it.krzeminski.examples.piping

import it.krzeminski.model.*
import it.krzeminski.repeat

val parallelLines = Pipe(
    initialOrientation = 90.degrees,
    initialPosition = Point(30.0f, 50.0f),
    radius = 10.0f,
    pipeSegments = listOf(
        PipeSegment.Straight(length = 650.0f),
        PipeSegment.Arc(radius = 14.0f, angle = 180.degrees, direction = Direction.LEFT),
        PipeSegment.Straight(length = 650.0f),
        PipeSegment.Arc(radius = 14.0f, angle = 180.degrees, direction = Direction.RIGHT))
        .repeat(17)
    )
