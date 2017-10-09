import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

//http://www.allsyllabus.com/aj/note/Computer_Science/Analysis_and_Design_of_Algorithms/Unit5/Construction%20of%20AVL%20tree.php#.WdoSaa10Dq0
//https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/2-3_insertion.svg/2000px-2-3_insertion.svg.png

class MySpec : StringSpec() {
    val tree= TwoThreeTree<Int,Int>()
    val key= listOf(9, 5, 8, 3, 2, 4, 7)
    val value = 4

    private fun TwoThreeTree<Int,Int>.put(int:Int) = this.put(int,value)
    private fun n(i:Int)       = Node.TwoThreeNode(i with value)
    private fun n(i:Int,j:Int) = Node.TwoThreeNode(i with value,j with value)


    init {
        "i. first node is root"{
            tree.put(key[0])
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
            val resultRoot = Node.TwoThreeNode(3 with value,8 with value)
            resultRoot.addLeft(Node.TwoThreeNode(2 with value))
            resultRoot.addMiddle(Node.TwoThreeNode(5 with value))
            resultRoot.addRight(Node.TwoThreeNode(9  with value))

            val root  = Node.TwoThreeNode(8 with value)
            val left  = Node.TwoThreeNode(3 with value,5 with  value)
            val right = Node.TwoThreeNode(9 with value)
            root.addLeft(left)
            root.addRight(right)

            tree.root = root

            tree.put(2,value)

            root shouldBe resultRoot

        }

        ///////////////////// wiki testy
        "insert in  2node "{
            //https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/2-3_insertion.svg/2000px-2-3_insertion.svg.png
            val root =   n(5)
            val left =   n(2)
            val right=   n(6,9)
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
            val root =   n(5)
            val left =   n(2, 4)
            val right=   n(6,9 )
            root.addLeft(left)
            root.addMiddle(right) //middle is right in twonode
            tree.root = root
            tree.put(10,value)
            val resultRoot   = n(5  , 9 )
            val resultLeft   = n(2  , 4 )
            val resultMiddle = n(6  )
            val resultRight  = n(10 )
            resultRoot.addLeft(resultLeft)
            resultRoot.addMiddle(resultMiddle)
            resultRoot.addRight(resultRight)
            tree.root shouldBe resultRoot
        }

        "1 check if I have correct node before inserting 3node in 3node parent"{
            val root  =   n(5,9)
            val left  =   n(2,4)
            val right =   n(10)
            val middle=   n(6)
            root.addLeft(left)
            root.addMiddle(middle)
            root.addRight(right)
            tree.root=root
            tree.getNode(1, root) shouldBe left
        }

        "2 check if I have correct node before inserting 3node in 3node parent"{
            val root  =   n(3,8)
            val left  =   n(2)
            val middle=   n(4,5)
            val right =   n(9)
            root.addLeft(left)
            root.addMiddle(middle)
            root.addRight(right)
            tree.root=root
            tree.getNode(7, root) shouldBe middle
        }

        "insert in a 3node.. 3 node parent)"{
            //https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/2-3_insertion.svg/2000px-2-3_insertion.svg.png
            val root  =   n(5,9)
            val left  =   n(2,4)
            val right =   n(10)
            val middle=   n(6 )
            with(root){
                addLeft(left)
                addMiddle(middle)
                addRight(right)
            }
            tree.root = root
            tree.put(1,value)

            val n5  = n(5)
            val n2  = n(2)
            val n9  = n(9)
            val n1  = n(1)
            val n4  = n(4)
            val n6  = n(6)
            val n10 = n(10)
            n2.addLeft(n1)
            n2.addRight(n4)
            n9.addLeft(n6)
            n9.addRight(n10)
            n5.addRight(n9)
            n5.addLeft(n2)

            tree.root shouldBe n5
        }

        "2 insert in a 3node.. 3 node parent)"{
            val root  =   n(3,8)
            val left  =   n(2)
            val right =   n(9)
            val middle=   n(4 ,5)
            root.addLeft(left)
            root.addMiddle(middle)
            root.addRight(right)
            tree.root=root
            tree.put(7,value)

            val n5  = n(5)
            val n3  = n(3)
            val n8  = n(8)
            val n2  = n(2)
            val n4  = n(4)
            val n7  = n(7)
            val n9  = n(9)

            n5.addLeft(n3)
            n3.addLeft(n2)
            n3.addRight(n4)
            n5.addRight(n8)
            n8.addLeft(n7)
            n8.addRight(n9)

            tree.root shouldBe n5
        }


    }



}