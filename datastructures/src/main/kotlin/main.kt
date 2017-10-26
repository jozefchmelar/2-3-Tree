import Tree.TwoThreeTree
import java.util.*

fun main(args: Array<String>) {
    val rnd = Random(666)
    val tree = TwoThreeTree<Int, Int>()
    val value = rnd.nextInt(100)
    val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 53, 112, 911, 150)//,321,123 )//,147,789,963,852,258,781) //Collections.nCopies(10, Any()).map { rnd.nextInt(100) }.distinct()
    keys.forEach { tree.put(it, value) }
    if (tree.getInorder() != keys.sorted()) println("err")
    with(tree) {
        println(insertedKeys)
        delete(53)
        delete(90)

    }

    println()

    println()
}

