import Tree.TwoThreeTree
import Tree.TwoThreeTree.*
import Tree.node.Node
import Tree.node.*
import extensions.emptyLinkedList
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.lang.Math.abs
import java.lang.Math.pow
import java.util.*

//http://www.allsyllabus.com/aj/note/Computer_Science/Analysis_and_Design_of_Algorithms/Unit5/Construction%20of%20AVL%20tree.php#.WdoSaa10Dq0
//https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/2-3_insertion.svg/2000px-2-3_insertion.svg.png

@Suppress("UNUSED_CHANGED_VALUE")
class TestyVkladania : StringSpec() {
    val tree = TwoThreeTree<Int, Int>()
    val value = Random().nextInt()
    val numberOfGenerators = 10


    internal fun TwoThreeTree<Int, Int>.put(int: Int) = this.put(int, value)
    internal fun n(i: Int) = Node.TwoNode(i with value)
    internal fun n(i: Int, j: Int) = Node.ThreeNode(i with value, j with value)


    init {
        val seeds = Collections.nCopies(numberOfGenerators, Any()).map { Math.abs(UUID.randomUUID().hashCode().toLong())}
        val randomGenerators  = Collections.nCopies(numberOfGenerators, Any()).mapIndexed { index, _ -> Random(seeds[index]) }

        randomGenerators.forEachIndexed { index,generator ->

                "[${seeds[index]}] test inserting  " {

                     val numberOfValues = pow(10.0,6.0).toInt() + generator.nextInt(1000000)
                    print("Generating $numberOfValues keys")
                    val keys = Collections.nCopies(numberOfValues, Any()).map { abs(generator.nextInt()) }.distinct()
                    println(" ok")
                    print("Putting keys in tree")
                    keys.forEach { tree.put(it) }
                    println(" ok")
                    print("Sorting keys")
                    val sortedKeys = keys.sorted()
                    println(" ok")
                    print("Tree inorder")
                    val inorder = tree.getInorder()
                    println(" ok")
                    var ok = false
                    if (inorder == sortedKeys && inorder.isNotEmpty() && sortedKeys.isNotEmpty()) {
                        println("keys inserted correctly")
                        ok = true
                    } else {
                        println("keys inserted INCORRECTLY")
                    }

                    val leafs = emptyLinkedList<Node<Int, Int>>()
                    tree.inorder {
                        if (it.isLeaf()) leafs += it
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