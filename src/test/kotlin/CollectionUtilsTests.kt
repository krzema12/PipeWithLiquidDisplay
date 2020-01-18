import it.krzeminski.cutSequentialItems
import it.krzeminski.groupSequentialItems
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class CollectionUtilsTests {

    data class Item(val id: Int, val width: Float)

    @Test
    fun cutSequentialItems_genericCase() {
        val items = listOf(
            Item(1, 20.0f),
            Item(2, 30.0f),
            Item(3, 10.0f))
        val cuttingIntervals = listOf(
            25.0f,
            10.0f,
            20.0f)

        val itemsAfterCutting = cutSequentialItems(
            items,
            cuttingIntervals,
            Item::width,
            { item, width -> item.copy(width = width) })

        assertThat(itemsAfterCutting, `is`(listOf(
            Item(1, 20.0f),
            Item(2, 5.0f),
            Item(2, 10.0f),
            Item(2, 15.0f),
            Item(3, 5.0f),
            Item(3, 5.0f)
        )))
    }

    @Test
    fun groupSequentialItems_genericCase() {
        val items = listOf(
            Item(1, 20.0f),
            Item(2, 5.0f),
            Item(2, 10.0f),
            Item(2, 15.0f),
            Item(3, 5.0f),
            Item(3, 5.0f))
        val groupingIntervals = listOf(
            25.0f,
            10.0f,
            20.0f)

        val groupedItems = groupSequentialItems(
            items,
            groupingIntervals,
            Item::width)

        assertThat(groupedItems, `is`(listOf(
            listOf(Item(1, 20.0f), Item(2, 5.0f)),
            listOf(Item(2, 10.0f)),
            listOf(Item(2, 15.0f), Item(3, 5.0f)),
            listOf(Item(3, 5.0f))
        )))
    }
}