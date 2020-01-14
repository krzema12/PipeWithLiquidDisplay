package it.krzeminski.model

data class LiquidStream(
    val streamSegment: List<LiquidStreamSegment>
)

data class LiquidStreamSegment(
    val liquidPresent: Boolean,
    val volume: Float
)
