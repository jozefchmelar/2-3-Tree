import Tree.TwoThreeTree
import Tree.TwoThreeTree.*
import Tree.node.Node
import Tree.node.*
import extensions.emptyLinkedList
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.util.*

//http://www.allsyllabus.com/aj/note/Computer_Science/Analysis_and_Design_of_Algorithms/Unit5/Construction%20of%20AVL%20tree.php#.WdoSaa10Dq0
//https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/2-3_insertion.svg/2000px-2-3_insertion.svg.png

@Suppress("UNUSED_CHANGED_VALUE")
class TestyVkladania : StringSpec() {
    val tree = TwoThreeTree<Int, Int>()
    val key  = listOf(9, 5, 8, 3, 2, 4, 7)
    val k    =   listOf(
        10,20,5,114,17,8,6,9,12,3,50,123,147,
        9687,45,46,4,150,180,200,190,7,13,9688,
        9689,11,14,19,15,16,149,148,1,2,201,202,
        220,230,240,250,55,56,57,58,798798,4654,654,65465,465,465,465,465,465,987,897,98,654,564,654,798768,156,4,9878,94152,1,48465465,798,798,465,465,41,3,1984198160
        ,524,984,98,894,651,32,546596,984,984,864,98498,465465465,65,4,498,4,74,498,98498
    ).distinct()

    val value = 4
    val rnd = Random(150)
    val seed = 4564894

    internal fun TwoThreeTree<Int, Int>.put(int: Int) = this.put(int, value)
    internal fun n(i: Int) = Node.TwoNode(i with value)
    internal fun n(i: Int, j: Int) = Node.ThreeNode(i with value, j with value)


    init {
     "prepis testy ako clovek"{
         true shouldBe true
     }
        "main"{
            k.forEach{tree.put(it)}
                .let { tree.insertedKeys.sorted() }
                .let (::println)
              tree.printInOrder()
        }

    }
}