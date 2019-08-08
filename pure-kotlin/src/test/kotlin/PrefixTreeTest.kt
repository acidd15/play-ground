import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The prefix tree example.
 *
 * https://www.futurice.com/blog/data-structures-for-fast-autocomplete/
 */
class PrefixTree {
    private var root: PrefixTreeNode = PrefixTreeNode()

    fun add(keyword: String, weight: Long = 1) {
        var current = root

        keyword.forEachIndexed { idx, c ->
            current = current.addChild(c)

            if (idx == keyword.length - 1) {
                current.markAsEnd(keyword, weight)
            }
        }
    }

    private fun searchStartNode(keyword: String): PrefixTreeNode {
        var current = root

        keyword.forEach { c ->
            current.children[c]?.also { node ->
                current = node
            } ?: return@forEach
        }

        return current
    }

    private fun search(node: PrefixTreeNode): List<PrefixTreeNode> {
        val result = mutableListOf<PrefixTreeNode>()

        if (node.weight > 0) {
            result.add(node)
        }

        node.children.forEach { _, u ->
            result.addAll(search(u))
        }

        return result
    }

    enum class SortedBy {
        ASC,
        DESC
    }

    fun find(keyword: String, sortedBy: SortedBy = SortedBy.ASC): List<String> {
        val startNode = searchStartNode(keyword)
        if (root == startNode) return mutableListOf()

        return search(startNode).let { result ->
            when (sortedBy) {
                SortedBy.ASC -> result.sortedBy { it.weight }
                SortedBy.DESC -> result.sortedByDescending { it.weight }
            }.map {
                it.endValue!!
            }.toList()
        }
    }
}

data class PrefixTreeNode (
    var weight: Long = 0,
    var endValue: String? = null,
    val children: MutableMap<Char, PrefixTreeNode> = mutableMapOf()
) {
    fun addChild(c: Char): PrefixTreeNode {
        return if (children.containsKey(c)) {
            children[c]!!
        } else {
            PrefixTreeNode().also {
                children[c] = it
            }
        }
    }

    fun markAsEnd(keyword: String, weight: Long) {
        this.weight = weight
        endValue = keyword
    }
}

class PrefixTreeTest {

    @Test
    fun `Should find 4 keywords and sorted by weight asc`() {
        val tree = PrefixTree()

        tree.add("keyword1")
        tree.add("keyword2")
        tree.add("keyword3")
        tree.add("keyword4", 7)
        tree.add("key")

        val list = tree.find("keyw", PrefixTree.SortedBy.DESC)

        assertEquals("keyword4,keyword1,keyword2,keyword3", list.joinToString(separator = ","))
    }

    @Test
    fun `Should find 4 keywords and sorted by weight desc`() {
        val tree = PrefixTree()

        tree.add("keyword1")
        tree.add("keyword2")
        tree.add("keyword3")
        tree.add("keyword4", 7)
        tree.add("key")

        val list = tree.find("keyw")

        assertEquals("keyword1,keyword2,keyword3,keyword4", list.joinToString(separator = ","))
    }

    @Test
    fun `Should find 5 keywords and sorted by weight asc`() {
        val tree = PrefixTree()

        tree.add("keyword1")
        tree.add("keyword2")
        tree.add("keyword3")
        tree.add("keyword4", 7)
        tree.add("key")

        val list = tree.find("key")

        assertEquals("key,keyword1,keyword2,keyword3,keyword4", list.joinToString(separator = ","))
    }

    @Test
    fun `Should find only 1 keyword`() {
        val tree = PrefixTree()

        tree.add("keyword1")
        tree.add("keyword2")
        tree.add("keyword3")
        tree.add("keyword4", 7)
        tree.add("key")

        val list = tree.find("keyword4")

        assertEquals("keyword4", list.joinToString(separator = ","))
    }

    @Test
    fun `Should find nothing`() {
        val tree = PrefixTree()

        tree.add("keyword1")
        tree.add("keyword2")
        tree.add("keyword3")
        tree.add("keyword4", 7)
        tree.add("key")

        run {
            val list = tree.find("haha")

            assertEquals(0, list.size)
        }

        run {
            val list = tree.find("hahahahah")

            assertEquals(0, list.size)
        }
    }
}