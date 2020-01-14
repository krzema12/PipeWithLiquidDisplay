package it.krzeminski.examples.piping

import it.krzeminski.model.*

val spiral = Pipe(
    initialOrientation = 0.degrees,
    initialPosition = Point(500.0f, 380.0f),
    radius = 10.0f,
    pipeSegments = (1..46)
        .map { PipeSegment.Arc(
            radius = 10.0f + 7.0f*it, angle = 90.degrees, direction = Direction.LEFT)
        })
