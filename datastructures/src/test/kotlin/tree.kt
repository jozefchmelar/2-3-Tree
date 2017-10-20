import Tree.TwoThreeTree
import Tree.node.Node
import Tree.node.*
import extensions.emptyLinkedList
import extensions.powerOf
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.lang.Math.abs
import java.util.*
import java.text.NumberFormat
import kotlin.collections.HashSet


//http://www.allsyllabus.com/aj/note/Computer_Science/Analysis_and_Design_of_Algorithms/Unit5/Construction%20of%20AVL%20tree.php#.WdoSaa10Dq0
//https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/2-3_insertion.svg/2000px-2-3_insertion.svg.png

@Suppress("UNUSED_CHANGED_VALUE")
class TestyVkladania : StringSpec() {
    val tree = TwoThreeTree<Int, Int>()
    val value = Random().nextInt()
    val numberOfGenerators = 2


    internal fun TwoThreeTree<Int, Int>.put(int: Int) = this.put(int, value)
    internal fun n(i: Int) = Node.TwoNode(i with value)
    internal fun n(i: Int, j: Int) = Node.ThreeNode(i with value, j with value)


    init {
        "hardcoded tree test"

        val seeds = Collections.nCopies(numberOfGenerators, Any()).map { Math.abs(UUID.randomUUID().hashCode().toLong()) }
        val randomGenerators = Collections.nCopies(numberOfGenerators, Any()).mapIndexed { index, _ -> Random(seeds[index]) }

        randomGenerators.forEachIndexed { index, generator ->

            "[${seeds[index]}] test inserting  " {

                val numberOfValues = 10.powerOf(4)

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


        }

    }
}

fun measureTime(name: String, f: () -> Unit) {
    println(name)
    val starTime = System.currentTimeMillis() / 1000
    f()
    val elapsedTime = System.currentTimeMillis() / 1000 - starTime
    println("$name took $elapsedTime seconds")
}