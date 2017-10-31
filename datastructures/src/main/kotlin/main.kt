import Tree.TwoThreeTree
import java.util.*

fun main(args: Array<String>) {
    val rnd = Random(666)
    val tree = TwoThreeTree<Int, Int>()
    val value = rnd.nextInt(100)
    val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
        keys.forEach { tree.put(it, value) }
    if (tree.getInorder() != keys.sorted()) println("err")
    val keyToDelete = 52


    val result=(tree.getInorder() == (keys-keyToDelete).sorted())


    println()
}

