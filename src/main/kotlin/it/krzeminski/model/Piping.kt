package it.krzeminski.model

data class Pipe(
    val initialOrientation: Degrees,
    val initialPosition: Point,
    val radius: Float,
    val pipeSegments: List<PipeSegment>
)

sealed class PipeSegment {
    class Straight(
        val length: Float
    ) : PipeSegment()

    class Arc(
        val radius: Float,
        val angle: Degrees,
        val direction: Direction
    ) : PipeSegment()
}

data class Point(
    val x: Float,
    val y: Float
)

enum class Direction {
    LEFT,
    RIGHT
}

inline class Degrees(val angle: Float)

val Int.degrees get() = Degrees(angle = this.toFloat())
