package it.krzeminski.model

import kotlin.math.PI

data class Pipe(
    val initialOrientation: Degrees,
    val initialPosition: Point,
    val radius: Float,
    val pipeSegments: List<PipeSegment>
)

sealed class PipeSegment {
    abstract fun volume(pipeRadius: Float): Float

    class Straight(
        val length: Float
    ) : PipeSegment() {
        override fun volume(pipeRadius: Float) =
            length * 2.0f * pipeRadius
    }

    class Arc(
        val radius: Float,
        val angle: Degrees,
        val direction: Direction
    ) : PipeSegment() {
        override fun volume(pipeRadius: Float): Float {
            val smallRadius = (radius - pipeRadius)
            val largeRadius = (radius + pipeRadius)

            return (PI.toFloat() * (largeRadius*largeRadius - smallRadius*smallRadius)) * angle.angle / 360.0f
        }
    }
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

val Degrees.radians get() = this.angle * PI / 180.0f

val Int.degrees get() = Degrees(angle = this.toFloat())
