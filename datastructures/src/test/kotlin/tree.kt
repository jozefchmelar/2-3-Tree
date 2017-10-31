import Tree.TwoThreeTree
import Tree.node.Node
import Tree.node.*
import extensions.emptyLinkedList
import extensions.powerOf
import io.kotlintest.TestCaseConfig
import io.kotlintest.matchers.shouldBe
import io.kotlintest.milliseconds
import io.kotlintest.seconds
import io.kotlintest.specs.StringSpec
import java.lang.Math.abs
import java.util.*
import java.text.NumberFormat
import kotlin.collections.HashSet
import com.intellij.util.containers.ContainerUtil.subList



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

        "[01] delete kv1 from three node"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 53)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[02] delete kv2 from three node"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 53)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 53
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[03] Parent=TwoNode Left=ThreeNode Right=TwoNode, delete RIGHT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54,53)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }//.config(enabled=false)

        "[04] Parent=TwoNode Left=TwoNode Right=ThreeNode,  delete LEFT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[05] Parent=TwoNode Left=TwoNode Right=ThreeNode,  delete LEFT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[06] Parent=ThreeNode Kids=TwoNodes, delete LEFT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[07] Parent=ThreeNode Kids=TwoNodes, delete MIDDLE"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[08] Parent=ThreeNode Kids=TwoNodes, delete RIGHT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[09] Parent=ThreeNode Kids=TwoNodes, delete ParentKv1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            var deleted = false
            with(tree){
                deleted=  delete(keyToDelete)
            }
            val inorder = tree.getInorder()
            val sorted  =  (keys-keyToDelete).sorted()
            val leaves =  leavesAreOnSameLevel(tree)

            inorder shouldBe sorted
            leaves shouldBe true
            deleted shouldBe true
        }

        "[10] Parent=ThreeNode Kids=TwoNodes, delete ParentKv2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[11] Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete Middle"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[12] Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete ParentKV1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[13] Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete ParentKV2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[14] Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete Right"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[15] Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete Left"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[16] Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete ParentKv1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[17] Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete ParentKv2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[18] Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete Right"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[19] Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Left"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[20] Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Middle"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[22] Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Parent KV1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[23] Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Parent KV2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[24] Parent=ThreeNode Left=ThreeNode  Middle=ThreeNode Right=TwoNode, delete Right"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,51,55)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[25] Parent=ThreeNode Left=ThreeNode  Middle=ThreeNode Right=ThreeNode, delete Middle"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,51,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[26] Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=ThreeNode, delete Left"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,60,911)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[27] Bigger set 1 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 2
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[28] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 23
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[29] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 83
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[30] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 81
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[31] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 98
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[32] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 59
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[33] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 33
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        "[33] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 81
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        "[33] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 98
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[34] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 6
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[34] merging test 1 ,deleting from left most two node "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14)//, 45, 54, 23, 1)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 49
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        "[35] merging test 1 ,deleting 34brother "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14)//, 45, 54, 23, 1)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 49
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[35] merging test 2 ,deleting 34brother "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14)//, 45, 54, 23, 1)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 68
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[36] merging test 2 ,deleting  "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 59
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[37] merging test 3 deleting TwoRoot TwoChild  "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 67
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[39] merging test 3 deleting TwoRoot TwoChild  "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 81
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[40] merging test 3 deleting stuff from left side "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 16
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[41] merging test 3 deleting stuff from left side "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 15
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[41] merging test 3 deleting stuff from left side "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 17
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[42] new keyset "{
            val keys = listOf(80, 95, 93, 15, 19, 61, 37, 78, 31, 26, 63, 67, 62, 43, 72, 28, 32, 16, 47, 73, 6, 86, 49, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 17
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
            true shouldBe true
        }

/*

        randomGenerators.forEachIndexed { index, generator ->

            "[${seeds[index]}] test inserting  " {

                val numberOfValues = 10.powerOf(6)

                val keys: HashSet<Int> = HashSet()

                for (i in 0..numberOfValues) {
                    keys += abs(generator.nextInt())
                }

                measureTime("[] Putting $numberOfValues keys in tree") {
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

fun <T> getRandomSubList(input: MutableList<T>, subsetSize: Int): List<T> {
    val r = Random()
    val inputSize = input.size
    for (i in 0 until subsetSize) {
        val indexToSwap = i + r.nextInt(inputSize - i)
        val temp = input[i]
        input[i] = input[indexToSwap]
        input[indexToSwap] = temp
    }
    return input.subList(0, subsetSize)
}

fun measureTime(name: String, f: () -> Unit) {
    println(name)
    val starTime    = System.currentTimeMillis() / 1000
    f()
    val elapsedTime = System.currentTimeMillis() / 1000 - starTime
    println("$name took $elapsedTime seconds")
}