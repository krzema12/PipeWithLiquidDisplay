package it.krzeminski.model

data class LiquidStream(
    val streamSegment: List<LiquidStreamSegment>
)

data class LiquidStreamSegment(
    val color: Color,
    val volume: Float
)

data class Color(
    val red: Float,
    val green: Float,
    val blue: Float
)
