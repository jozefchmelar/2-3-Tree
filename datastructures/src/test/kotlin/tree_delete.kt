import Tree.TwoThreeTree
import Tree.node.Node
import Tree.node.with
import extensions.emptyLinkedList
import io.kotlintest.TestCase
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.util.*

@Suppress("UNUSED_CHANGED_VALUE")
class DeletionTest : StringSpec() {
    private val tree = TwoThreeTree<Int, Int>()
    private val value = 666

    private val keys =  listOf(115, 104, 267, 13, 175, 187, 118, 266, 211, 67, 44, 53, 136, 204, 188, 282, 248, 279, 8, 172, 20, 182, 2, 190, 185, 102, 121, 116, 173,183,45,103)

    private fun TwoThreeTree<Int, Int>.put(int: Int) = this.put(int, value)
    private fun n(i: Int) = Node.TwoNode(i with value)
    private fun n(i: Int, j: Int) = Node.ThreeNode(i with value, j with value)

    fun del(key:Int) : Boolean {
        keys.forEach { tree.put(it) }
        val deleted = tree.delete(key)
        return (tree.getInorder() == (keys - key).sorted() && deleted && leavesAreOnSameLevel(tree))
     }
    init {

       "[01] delete kv1 from three leaf node"{
            del(2) shouldBe true
        }

        "[02] delete kv2 from three leaf node"{
            del(173) shouldBe true
        }

        "[03] Parent=3 Kids=[2,2,2] Delete=Left "{
            del(13) shouldBe true
        }

    }

    fun leavesAreOnSameLevel(tree: TwoThreeTree<Int, Int>): Boolean {
        val leafs = emptyLinkedList<Node<Int, Int>>()
        tree.inorder { node ->
            if (node.isLeaf()) leafs += node
        }

        val size: Boolean = leafs.map {
            val stack = emptyLinkedList<Node<Int, Int>>()
            var n: Node<Int, Int>? = it
            while (n != null) {
                stack.push(n)
                n = n.parent
            }
            stack.size
        }.distinct().size == 1
        return size
    }

}
