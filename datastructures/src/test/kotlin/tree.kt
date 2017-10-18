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
        9687,45,46,9688
        ,
        4,150,180,200,190,7,13,9688,
        9689,11,14,19,15,16,149,148,1,2,201,202,
        220,230,240,250,55,56,57,58
    )

    val value = 4
    val rnd = Random(150)
    val seed = 4564894

    internal fun TwoThreeTree<Int, Int>.put(int: Int) = this.put(int, value)
    internal fun n(i: Int) = Node.TwoNode(i with value)
    internal fun n(i: Int, j: Int) = Node.ThreeNode(i with value, j with value)

  //TODO pridat testy na overnie po kazdom vkladani ci su splnene podmienky dva tri stromu ( po kazdom vkladani overit ze dlzka listov je vzdy rovnaka)
  //TODO nahodny generator vkladania a mazania s velkym mnozstvom dat

    init {
     /*   "check node"{
            tree.getNode(9) shouldBe tree.root
        }
        "i. first node is root"{
            tree.put(9)
            tree.root shouldBe n(key[0])
        }
        "check node"{
            tree.put(9)
            tree.getNode(5) shouldBe tree.root
        }
        "ii second input is in root"{
            tree.put(9)
            tree.put(5)
            tree.root shouldBe n(key[1], key[0])
        }
        "iii split two node "{
            tree.put(key[0])
            tree.put(key[1])
            tree.put(key[2])
            val r = n(8)
            r.addLeft(n(5))
            r.addRight(n(9))

            tree.root shouldBe r
        }
        "correct node to add to before doing IV"{
            tree.put(key[0])
            tree.put(key[1])
            tree.put(key[2])
            val r = n(8)
            r.addLeft(n(5))
            r.addRight(n(9))

            tree.getNode(key[3]) shouldBe r.left
        }
        "iv "{
            tree.put(key[0])
            tree.put(key[1])
            tree.put(key[2])
            tree.put(key[3])
            val r =  n(8)
                .addLeft(n(3, 5))
                .addRight(n(9))

            tree.root shouldBe r
        }
        "check correct node"{
            tree.put(key[0])
            tree.put(key[1])
            tree.put(key[2])
            tree.put(key[3])
            val r =  n(8)
                .addLeft(n(3, 5))
                .addRight(n(9))
            tree.getNode(key[4]) shouldBe r.left
         }
        "V insert 2"{
            tree.put(key[0])
            tree.put(key[1])
            tree.put(key[2])
            tree.put(key[3])
            tree.put(key[4])
            val r = n(3,8)
            r.addMiddle(n(5))
            r.addLeft(n(2))
            r.addRight(n(9))
            tree.root shouldBe  r
        }

        "check correct node"{
            tree.put(key[0])
            tree.put(key[1])
            tree.put(key[2])
            tree.put(key[3])
            tree.put(key[4])
            val r = n(3,8)
            r.addMiddle(n(5))
            r.addLeft(n(2))
            r.addRight(n(9))
            tree.getNode(key[5]) shouldBe  r.middle
        }
        "VI insert 4"{
            tree.put(key[0])
            tree.put(key[1])
            tree.put(key[2])
            tree.put(key[3])
            tree.put(key[4])
            tree.put(key[5])

            val r = n(3,8)
            r.addMiddle(n(4,5))
            r.addLeft(n(2))
            r.addRight(n(9))
            tree.root shouldBe  r
        }

        "check correct node"{
            tree.put(key[0])
            tree.put(key[1])
            tree.put(key[2])
            tree.put(key[3])
            tree.put(key[4])
            tree.put(key[5])

            val r = n(3,8)
            r.addMiddle(n(4,5))
            r.addLeft(n(2))
            r.addRight(n(9))
            tree.getNode(6) shouldBe  r.middle
        }
        "VII insert 7"{
            tree.put(key[0])
            tree.put(key[1])
            tree.put(key[2])
            tree.put(key[3])
            tree.put(key[4])
            tree.put(key[5])
            tree.put(key[6])

            val r = n(5)
                .addLeft(
                    n(3)
                    .addLeft(n(2))
                    .addRight(n(4))
                )
                .addRight(
                    n(8)
                    .addLeft(n(7))
                    .addRight(n(9))
                )
            tree.root shouldBe r
        }
        "----------------WIKIPEDIA TESTS-----------"{
            true shouldBe true
        }
        "insert in a 2-node"{
            tree.root = n(5)
                .addLeft(n(2))
                .addRight(n(6,9))

            tree.put(4)
            tree.root shouldBe  n(5)
                .addLeft (n(2,4))
                .addRight(n(6,9))
        }
        "check node "{
            tree.root =
                n(5)
                .addLeft (n(2,4))
                .addRight(n(6,9))
            tree.getNode(10) shouldBe  tree.root!!.right
        }

        "insert in a 3node (2node parent)"{
            tree.root =
                n(5)
                .addLeft (n(2,4))
                .addRight(n(6,9))

            tree.put(10)

            tree.root shouldBe  n(5,9).addMiddle(n(6)).addLeft(n(2,4)).addRight(n(10))
        }
        "check node "{
            tree.root = n(5,9).addMiddle(n(6)).addLeft(n(2,4)).addRight(n(10))
            tree.getNode(1) shouldBe tree.root!!.left
        }
        "insert in a 3 node(3node parent) from left "{
            tree.root =  n(5,9).addMiddle(n(6)).addLeft(n(2,4)).addRight(n(10))
            tree.put(1)

            tree.root shouldBe n(5).
                addLeft(
                    n(2)
                        .addLeft (n(1))
                        .addRight(n(4))
                )
                .addRight(
                    n(9)
                        .addLeft (n(6))
                        .addRight(n(10))
                )

        }
        "insert in a 3 node (3 node parent) from right"{
            tree.root = n(3,8)
                .addMiddle  (n(5))
                .addLeft    (n(2))
                .addRight   (n(9,10))
            tree.put(12)

            tree.root shouldBe n(8)
                .addLeft(
                    n(3)
                        .addLeft (n(2))
                        .addRight(n(5))
                )
                .addRight(
                    n(10)
                        .addLeft (n(9))
                        .addRight(n(12))
                )
        }

        "test inorder with sorting algorithm"{
            val values = listOf(3,8,5,2,9,10)
            tree.root = n(3,8)
                .addMiddle  (n(5))
                .addLeft    (n(2))
                .addRight   (n(9,10))

            val iterative = emptyLinkedList<Int>()
            tree.inorder2(tree.root,{
                when(it){
                    is Node.TwoNode   -> iterative.push(it.keyValue1.key)
                    is Node.ThreeNode -> {
                        iterative.push(it.keyValue1.key)
                        iterative.push(it.keyValue2.key)
                    }
                }
            })

            val sorted = values.sorted().reversed()
            iterative shouldBe sorted
        }*/

        "test "{
            var i = 0
            k.forEachIndexed{index,item ->
                if (index <= k.indexOf(123) )
                    tree.put(item)
            }

            val xpcted = n(10).addLeft(
                n(6)
                    .addLeft (n(3,5))
                    .addRight(n(8,9))
            ).addRight(
                n(20,114)
                    .addMiddle(n(50))
                    .addLeft  (n(12,17))
                    .addRight (n(123))
            )

            val ll = emptyLinkedList<KeyValue<Int,Int>>()
            val rr = emptyLinkedList<KeyValue<Int,Int>>()
//            tree.traverseTree(tree.root!!,ll)
//            tree.traverseTree(xpcted,rr)
//            println(ll)
            ll shouldBe   rr
        }.config(enabled = false)

        "test2 "{
            var i = 0
            k.forEachIndexed{index,item ->
              //  if (index <= k.indexOf(4) )
                    tree.put(item)
            }

//            println()
            tree.printInOrder()
//            println()
            println("[" + tree.insertedKeys.sorted().joinToString(", ") + "]")
//            println(tree.insertedKeys.joinToString(", "))

        //    ll shouldBe   rr
        }

    }
}