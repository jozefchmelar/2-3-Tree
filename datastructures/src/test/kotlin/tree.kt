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
            val root  = Node.TwoThreeNode(8 with value)
            val left  = Node.TwoThreeNode(5 with value)
            val right = Node.TwoThreeNode(9 with value)
            root.addLeft(left)
            root.addRight(right)
            tree.root shouldBe root

        }
        "iv insert 3 so it's in the left bottom child"{
            tree.put(key[0],value)
            tree.put(key[1],value)
            tree.put(key[2],value)
            tree.put(key[3],value)
            val root  = Node.TwoThreeNode(8 with value)
            val left  = Node.TwoThreeNode(3 with value,5 with  value)
            val right = Node.TwoThreeNode(9 with value)
            root.addLeft(left)
            root.addRight(right)
            this.tree.root shouldBe root
            println(tree.root)

        }
        "v when I insert 2 get node should be 35 node wit 8 as parent"{
            tree.put(key[0],value)
            tree.put(key[1],value)
            tree.put(key[2],value)
            tree.put(key[3],value)
            val root  = Node.TwoThreeNode(8 with value)
            val left  = Node.TwoThreeNode(3 with value,5 with  value)
            val right = Node.TwoThreeNode(9 with value)
            root.addLeft(left)
            root.addRight(right)
            tree.getNode(key[4],tree.root!!) shouldBe root.left
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

        ///////////////////// wiki testy
        "insert in  2node "{
            //https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/2-3_insertion.svg/2000px-2-3_insertion.svg.png
            val root =   Node.TwoThreeNode(5 with value)
            val left =   Node.TwoThreeNode(2 with value)
            val right=   Node.TwoThreeNode(6 with value,9 with value)
            root.addLeft(left)
            root.addRight(right)
            tree.root = root
            tree.put(4,value)
            val resultRoot =   Node.TwoThreeNode(5 with value)
            val resultLeft =   Node.TwoThreeNode(2 with value, 4 with value)
            val resultRight=   Node.TwoThreeNode(6 with value,9 with value)
            resultRoot.addRight(resultRight)
            resultRoot.addLeft(resultLeft)
            tree.root shouldBe resultRoot
        }
        "check if I have the correct node before inserting to 3node with 2node parent"{

            val root =   Node.TwoThreeNode(5 with value)
            val left =   Node.TwoThreeNode(2 with value, 4 with value)
            val right=   Node.TwoThreeNode(6 with value,9 with value)
            root.addLeft (left)
            root.addMiddle(right)  //root is TwoNode so  MIDDLE is RIGHT
            tree.root = root
            tree.getNode(10, root) shouldBe right

        }
        "insert in a 3node (2 node parent)"{
            //https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/2-3_insertion.svg/2000px-2-3_insertion.svg.png
            val root =   Node.TwoThreeNode(5 with value)
            val left =   Node.TwoThreeNode(2 with value, 4 with value)
            val right=   Node.TwoThreeNode(6 with value,9 with value)
            root.addLeft(left)
            root.addRight(right)
            tree.root = root
            tree.put(10,value)
            val resultRoot   = Node.TwoThreeNode(5  with value, 9 with value)
            val resultRight  = Node.TwoThreeNode(10 with value)
            val resultMiddle = Node.TwoThreeNode(2  with value)
            val resultleft   = Node.TwoThreeNode(2  with value, 4 with value)
            resultRoot.addRight(resultRight)
            resultRoot.addMiddle(resultMiddle)
            resultRoot.addLeft(resultleft)
            tree.root shouldBe resultRoot
        }.config(enabled = false)

        "check if I have correct node before insreting 3node in 3node parent"{
            val root  =   Node.TwoThreeNode(5 with value,9 with value)
            val left  =   Node.TwoThreeNode(2 with value, 4 with value)
            val right =   Node.TwoThreeNode(10  with value)
            val middle=   Node.TwoThreeNode(6  with value)
            root.addLeft(left)
            root.addMiddle(middle)
            root.addRight(right)
            tree.root=root
            tree.getNode(1, root) shouldBe left
        }

        "insert in a 3node.. 3 node parent)"{
            //https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/2-3_insertion.svg/2000px-2-3_insertion.svg.png
            val root  =   Node.TwoThreeNode(5 with value,9 with value)
            val left  =   Node.TwoThreeNode(2 with value, 4 with value)
            val right =   Node.TwoThreeNode(10  with value)
            val middle=   Node.TwoThreeNode(6  with value)
            root.addLeft(left)
            root.addMiddle(middle)
            root.addRight(right)
            tree.root = root
            tree.put(1,value)

            val n5  = Node.TwoThreeNode(5  with value)
            val n2  = Node.TwoThreeNode(2  with value)
            val n9  = Node.TwoThreeNode(9  with value)
            val n1  = Node.TwoThreeNode(1  with value)
            val n4  = Node.TwoThreeNode(4  with value)
            val n6  = Node.TwoThreeNode(6  with value)
            val n10 = Node.TwoThreeNode(10 with value)
            n2.addLeft(n1)
            n2.addRight(n4)
            n9.addLeft(n6)
            n9.addRight(n10)
            n5.addRight(n9)
            n5.addLeft(n2)

            tree.root shouldBe n5
        }.config(enabled = false)



    }



}