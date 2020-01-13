package it.krzeminski.examples.piping

import it.krzeminski.model.*

val parallelLines = Pipe(
    initialOrientation = 0.degrees,
    initialPosition = Point(300.0f, 300.0f),
    radius = 10.0f,
    pipeSegments = listOf(
        PipeSegment.Straight(length = 100.0f),
        PipeSegment.Arc(radius = 50.0f, angle = 70.degrees, direction = Direction.RIGHT),
        PipeSegment.Straight(length = 100.0f),
        PipeSegment.Arc(radius = 50.0f, angle = 70.degrees, direction = Direction.LEFT),
        PipeSegment.Straight(length = 100.0f),
        PipeSegment.Arc(radius = 50.0f, angle = 70.degrees, direction = Direction.RIGHT),
        PipeSegment.Straight(length = 100.0f),
        PipeSegment.Arc(radius = 50.0f, angle = 70.degrees, direction = Direction.LEFT),
        PipeSegment.Straight(length = 100.0f),
        PipeSegment.Arc(radius = 50.0f, angle = 70.degrees, direction = Direction.RIGHT),
        PipeSegment.Straight(length = 100.0f),
        PipeSegment.Arc(radius = 50.0f, angle = 70.degrees, direction = Direction.LEFT)
//        PipeSegment.Arc(radius = 15.0f, angle = 90.degrees, direction = Direction.RIGHT),
//        PipeSegment.Straight(length = 200.0f),
//        PipeSegment.Arc(radius = 15.0f, angle = 90.degrees, direction = Direction.LEFT),
//        PipeSegment.Straight(length = 200.0f),
//        PipeSegment.Arc(radius = 15.0f, angle = 90.degrees, direction = Direction.RIGHT),
//        PipeSegment.Straight(length = 200.0f),
//        PipeSegment.Arc(radius = 15.0f, angle = 90.degrees, direction = Direction.LEFT)
    )
)
