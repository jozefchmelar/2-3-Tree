import Tree.TwoThreeTree
import Tree.node.Node
import Tree.node.*
import extensions.emptyLinkedList
import extensions.powerOf
import io.kotlintest.matchers.shouldBe
import io.kotlintest.milliseconds
import io.kotlintest.seconds
import io.kotlintest.specs.StringSpec
import java.lang.Math.abs
import java.util.*
import java.text.NumberFormat
import kotlin.collections.HashSet

@Suppress("UNUSED_CHANGED_VALUE")
class InsertionTest : StringSpec() {
    val tree = TwoThreeTree<Int, Int>()
    val value = Random().nextInt()
    val numberOfGenerators = 7

    internal fun TwoThreeTree<Int, Int>.put(int: Int) = this.put(int, value)
    internal fun n(i: Int) = Node.TwoNode(i with value)
    internal fun n(i: Int, j: Int) = Node.ThreeNode(i with value, j with value)

    init {

        val seeds = Collections.nCopies(numberOfGenerators, Any()).map { Math.abs(UUID.randomUUID().hashCode().toLong()) }
        val randomGenerators = Collections.nCopies(numberOfGenerators, Any()).mapIndexed { index, _ -> Random(seeds[index]) }
        "delete kv1 from three node"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 53)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "delete kv2 from three node"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 53)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 53
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }


        "Parent=TwoNode Left=ThreeNode Right=TwoNode, delete RIGHT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 53)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "Parent=TwoNode Left=TwoNode Right=ThreeNode,  delete LEFT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "Parent=TwoNode Left=TwoNode Right=ThreeNode,  delete LEFT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "Parent=ThreeNode Kids=TwoNodes, delete LEFT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        "Parent=ThreeNode Kids=TwoNodes, delete MIDDLE"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        "Parent=ThreeNode Kids=TwoNodes, delete RIGHT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        "Parent=ThreeNode Kids=TwoNodes, delete ParentKv1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }.config(timeout = 200.milliseconds,enabled = false)

        "Parent=ThreeNode Kids=TwoNodes, delete ParentKv2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }.config(timeout = 200.milliseconds,enabled = false)

        "Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete Middle"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete ParentKV1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }.config(timeout = 200.milliseconds,enabled = false)

        "Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete ParentKV2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }.config(timeout = 200.milliseconds,enabled = false)

        "Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete Right"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete Left"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete ParentKv1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }.config(timeout = 200.milliseconds,enabled = false)

        "Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete ParentKv2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }.config(timeout = 200.milliseconds,enabled = false)


        "Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete Right"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Left"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Middle"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Parent KV1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }.config(timeout = 200.milliseconds,enabled = false)

        "Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Parent KV2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }.config(timeout = 200.milliseconds,enabled = false)

        "Parent=ThreeNode Left=ThreeNode  Middle=ThreeNode Right=TwoNode, delete Right"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,51,55)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "Parent=ThreeNode Left=ThreeNode  Middle=ThreeNode Right=ThreeNode, delete Middle"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,51,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        "Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=ThreeNode, delete Left"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,60,911)
            keys.forEach { tree.put(it) }
            tree.delete(50)
            val keyToDelete = 51
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete+55).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
/*

        randomGenerators.forEachIndexed { index, generator ->

            "[${seeds[index]}] test inserting  " {

                val numberOfValues = 10.powerOf(6)

                val keys: HashSet<Int> = HashSet()

                for (i in 0..numberOfValues) {
                    keys += abs(generator.nextInt())
                }

                measureTime("Putting $numberOfValues keys in tree") {
                    keys.forEach { tree.put(it) }
                }

                println("Sorting keys")
                val sortedKeys = keys.sorted()
                println("Tree inorder")
                val inorder = tree.getInorder()

                var ok = false

                if (inorder == sortedKeys && inorder.isNotEmpty() && sortedKeys.isNotEmpty()) {
                    println("keys inserted correctly")
                    ok = true
                } else {
                    println("keys inserted INCORRECTLY")
                }

                val leafs = emptyLinkedList<Node<Int, Int>>()

                tree.inorder { node ->
                    if (node.isLeaf()) leafs += node
                }

                val levels = leafs.map {
                    val stack = emptyLinkedList<Node<Int, Int>>()
                    var n: Node<Int, Int>? = it
                    while (n != null) {
                        stack.push(n)
                        n = n.parent
                    }
                    stack.size
                }.distinct()

                println("Leaves are on level : $levels")

                (ok == true && levels.size == 1) shouldBe true

                println("----------------")
            }
                .config(enabled = false)

        }
*/
    }

    fun leavesAreOnSameLevel(tree:TwoThreeTree<Int,Int>):Boolean{
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

fun measureTime(name: String, f: () -> Unit) {
    println(name)
    val starTime    = System.currentTimeMillis() / 1000
    f()
    val elapsedTime = System.currentTimeMillis() / 1000 - starTime
    println("$name took $elapsedTime seconds")
}