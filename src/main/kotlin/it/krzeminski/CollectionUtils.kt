package it.krzeminski

fun <E> List<E>.repeat(times: Int): List<E> {
    return mutableListOf<E>().apply {
        repeat(times) {
            addAll(this@repeat)
        }
    }
}

fun <T> List<T>.cumulativeSum(startElement: T, sumOperation: (T, T) -> T): List<T> {
    return this.fold(listOf(startElement)) {
            acc, width -> acc + sumOperation(acc.last(), width)
    }
}

fun <T> cutSequentialItems(
    items: List<T>,
    cuttingIntervals: List<Float>,
    widthExtractor: (T) -> Float,
    itemBuilder: (T, Float) -> T
): List<T> {
    val itemsWidthCumulativeSum = items
        .map { widthExtractor(it) }
        .cumulativeSum(0.0f, Float::plus)
    val cuttingIntervalsCumulativeSum = cuttingIntervals
        .cumulativeSum(0.0f, Float::plus)

    val itemsWithStartPositions = items zip itemsWidthCumulativeSum
    return itemsWithStartPositions.map { (item, position) ->
        val cuttingPointsForThisItem = cuttingIntervalsCumulativeSum
            .filter { cuttingPosition ->
                cuttingPosition > position && cuttingPosition < (position + widthExtractor(item))
            }.map { it - position }

        if (cuttingPointsForThisItem.isEmpty()) {
            listOf(item)
        } else {
            val widths = (listOf(0.0f) + cuttingPointsForThisItem).zipWithNext { a, b -> b - a }
            widths.map { width -> itemBuilder(item, width) } +
                    itemBuilder(item, widthExtractor(item) - cuttingPointsForThisItem.last())
        }
    }.flatten()
}

fun <T> groupSequentialItems(
    items: List<T>,
    groupingIntervals: List<Float>,
    widthExtractor: (T) -> Float
): List<List<T>> {
    if (groupingIntervals.isEmpty()) {
        return listOf(items)
    }

    val itemsWidthCumulativeSum = items
        .map { widthExtractor(it) }
        .cumulativeSum(0.0f, Float::plus)
        .drop(1)
    val itemsForThisGroup = (items zip itemsWidthCumulativeSum)
        // TODO the '0.05' constant is to compensate for a bug that makes this algorithm not take
        // some small liquid segment that would perfectly fit a given pipe segment, but it doesn't
        // because of some float-related imprecise calculations. To get rid of this, it would be probably
        // a good idea to merge the two functions: cutSequentialItems and groupSequentialItems.
        .takeWhile { (_, cumulativeSum) -> cumulativeSum <= groupingIntervals.first() + 0.05 }
        .map { (item, _) -> item }
    return listOf(itemsForThisGroup) + groupSequentialItems(
        items.drop(itemsForThisGroup.size),
        groupingIntervals.drop(1),
        widthExtractor)
}
