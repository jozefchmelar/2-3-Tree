import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

//http://www.allsyllabus.com/aj/note/Computer_Science/Analysis_and_Design_of_Algorithms/Unit5/Construction%20of%20AVL%20tree.php#.WdoSaa10Dq0
//https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/2-3_insertion.svg/2000px-2-3_insertion.svg.png

class MySpec : StringSpec() {
    val tree= TwoThreeTree<Int,Int>()
    val key= listOf(9, 5, 8, 3, 2, 4, 7)
    val value = 4
    init {
        "i. first node is root"{
            tree.put(key[0],value)
            tree.root shouldBe Node.TwoThreeNode(key[0] with value)
            println(tree.root)
        }

        "ii second input is in root"{
            tree.put(key[0],value)
            tree.put(key[1],value)
            tree.root shouldBe Node.TwoThreeNode(key[1] with value,key[0] with value)
            println(tree.root)

        }

        "iii third input puts key[2] to the top"{
            tree.put(key[0],value)
            tree.put(key[1],value)
            tree.put(key[2],value)
            tree.root shouldBe Node.TwoThreeNode(
                                                8 with value,
                left = Node.TwoThreeNode(5 with value),  right = Node.TwoThreeNode(9 with value))
            println(tree.root)

        }
        "iv insert 6 so it's in the left bottom child"{
            tree.put(key[0],value)
            tree.put(key[1],value)
            tree.put(key[2],value)
            tree.put(key[3],value)
            tree.root shouldBe Node.TwoThreeNode(
                8 with value,
                left = Node.TwoThreeNode(3 with value,5 with value),  right = Node.TwoThreeNode(9 with value))
            println(tree.root)

        }
        "v when I insert 2 get node should be 35 node"{
            tree.put(key[0],value)
            tree.put(key[1],value)
            tree.put(key[2],value)
            tree.put(key[3],value)
            tree.getNode(key[4],tree.root!!) shouldBe Node.TwoThreeNode(3 with value,5 with value)
        }
        "v insert 2 so I have  ThreeNode as parent"{
            tree.put(key[0],value)
            tree.put(key[1],value)
            tree.put(key[2],value)
            tree.put(key[3],value)
            tree.put(key[4],value)
            tree.root shouldBe                 Node.TwoThreeNode(3 with value,8 with value,
            left=Node.TwoThreeNode(2 with value),middle = Node.TwoThreeNode(5 with value),right = Node.TwoThreeNode(9 with value))
            println(tree.root)
        }

    }



}