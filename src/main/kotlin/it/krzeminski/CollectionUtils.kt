package it.krzeminski

fun <E> List<E>.repeat(times: Int): List<E> {
    return mutableListOf<E>().apply {
        repeat(times) {
            addAll(this@repeat)
        }
    }
}
