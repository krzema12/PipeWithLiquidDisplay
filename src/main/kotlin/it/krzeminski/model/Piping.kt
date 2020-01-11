package it.krzeminski.model

data class Pipe(
    val pipeSegments: List<PipeSegment>
)

sealed class PipeSegment {
    class Straight(
        val startPosition: Point,
        val startRadius: Float,
        val endPosition: Point,
        val endRadius: Float
    ) : PipeSegment()

    class Arc(
        val startPosition: Point,
        val endPosition: Point,
        val startAngle: AngleDegrees,
        val endAngle: AngleDegrees
    ) : PipeSegment()
}

data class Point(
    val x: Float,
    val y: Float
)

inline class AngleDegrees(val value: Float)
