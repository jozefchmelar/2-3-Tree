import Tree.TwoThreeTree
import java.util.*

fun main(args: Array<String>) {
    val rnd   = Random(666)
    val tree  = TwoThreeTree<Int, Int>()
    val value = rnd.nextInt(100)
    val keys  = Collections.nCopies(20,Any()).map { rnd.nextInt(100) }.distinct()
    keys.forEach { tree.put(it,value) }
    if(tree.getInorder() != keys.sorted()) println("err")
    with(tree){

       val node =  ( find(13))
        inoredSuccesor(node!!).let { println(it) }
        println(getInorder())

    }










    println()
}

