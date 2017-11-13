import Operation.Delete
import Operation.Insert
import Tree.HablaTest
import Tree.TwoThreeTree
import Tree.node.Node
import Tree.node.with
import extensions.emptyLinkedList
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.LocalDate
import java.util.*


fun rnd()= Random(5)
fun rndLocalDate() = LocalDate.of(1980+rnd().nextInt(37),rnd().nextInt(11)+1,rnd().nextInt(27)+1)
fun maybe(f:()->Unit) = if(rnd().nextBoolean()) f() else {}
 enum class Operation{
    Insert,Delete ;
  companion object {
      fun random() = if(Random().nextInt(100) > 40 ) Insert else Delete
  } }

@Suppress("UNUSED_CHANGED_VALUE")
class InsertionTest : StringSpec() {
    val tree = TwoThreeTree<Int, Int>()
    val value = Random().nextInt()
    val numberOfGenerators = 7

    internal fun TwoThreeTree<Int, Int>.put(int: Int) = this.put(int, value)
    internal fun n(i: Int) = Node.TwoNode(i with value)
    internal fun n(i: Int, j: Int) = Node.ThreeNode(i with value, j with value)

    init {

        val seeds = Collections.nCopies(numberOfGenerators, Any()).map { Math.abs(UUID.randomUUID().hashCode().toLong()) }
        val randomGenerators = Collections.nCopies(numberOfGenerators, Any()).mapIndexed { index, _ -> Random(seeds[index]) }

        "[xx] delete kv1 from three node"{
            val tree = TwoThreeTree<HablaTest, HablaTest>()
            val keys = (1..1000).map { HablaTest("$it","habla")}.toMutableList()
            keys.forEach { tree.put(it,it) }
            val deletedKeys = mutableListOf<HablaTest>()
            val keyToDelete = getRandomSubList(keys,keys.size)
            val s = keyToDelete.mapIndexed { index, it ->
                val keys = tree.insertedKeys
                val deleted = tree.delete(it)
                if(deleted)
                    deletedKeys.add(it)
                val result=(tree.getInorder() == (keys-it).sorted() && deleted)// && leavesAreOnSameLevel(tree))
                if(!result)
                    println("$index $it")
                result
            }
            s.all{it==true} shouldBe true
            //val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted.all { it==true } )//&& leavesAreOnSameLevel(tree) )
            //result shouldBe true
        }
        "tetetwe"{

            tree.put(73) // true
            tree.put(36) // true
            tree.put(76) // true
            tree.delete(73) // true
            tree.put(52) // true
            tree.put(84) // true
            tree.delete(36) // true
            tree.put(74) // true
            tree.delete(84) // true
            tree.delete(76) // true
            tree.delete(52) // true
            tree.put(68) // true
            tree.delete(68) // true
            tree.put(16) // true
            tree.delete(74) // true
            tree.delete(16) // true // nic
            tree.put(73) // true
            tree.put(71) // true
            tree.put(65) // true
            tree.put(78) // true
            tree.put(15) // true
            tree.put(7) // true
            tree.put(6) // true
            tree.put(42) // true
            tree.delete(6) // true
            tree.delete(73) // true
            tree.delete(42) // true
            tree.put(0) // true
            tree.put(95) // true
            tree.put(44) // true
            tree.put(89) // true
            tree.delete(71) // true
            tree.put(73) // false
            tree.put(70) // false
            tree.put(10) // false
            tree.delete(95) // false
            tree.delete(10) // false
            tree.delete(15) // false
            tree.delete(0) // false
            tree.delete(70) // false
            tree.delete(65) // false
            tree.put(29) // false
            tree.delete(73) // false
            tree.put(63) // false
            tree.put(65) // false


        }
            fun Triple<Operation, Int, Boolean>.toCode(){
                var s =""
                s+= if(first==Insert) "tree.put($second)" else "tree.delete($second)"
                s+= " // $third"
                println(s)

            }
        "[xy] delete"{
            val inserted = mutableListOf<Int>()
            val results = mutableListOf<Boolean>()
            val history = mutableListOf<Triple<Operation, Int, Boolean>>()
            try {
                for (i in 1..5000) {
                    when (Operation.random()) {
                        Insert -> {
                            val ins = Random().nextInt(10000)
                            if (!inserted.contains(ins)) {
                                inserted.add(ins)
                                tree.put(ins)
                                val res = tree.getInorder() == inserted.sorted() && leavesAreOnSameLevel(tree)
                                results.add(res)
                                history.add(Triple(Insert, ins, res))
                            }
                        }
                        Delete -> {
                            if (inserted.isNotEmpty()) {
                                val toDel = inserted[Random().nextInt(inserted.size)]
                                tree.delete(toDel)
                                inserted.remove(toDel)
                                val res = tree.getInorder() == inserted.sorted()// && leavesAreOnSameLevel(tree)
                                results.add(res)
                                history.add(Triple(Delete, toDel, res))
                            }
                        }
                    }
                }
                println(history)
                history.all { it.third==true  } shouldBe true
            } catch (e:Exception){
                println(history)
                history.forEach { it.toCode() }
            }
        }

//region other
        "[01] delete kv1 from three node"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 53)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[02] delete kv2 from three node"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 53)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 53
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[03] Parent=TwoNode Left=ThreeNode Right=TwoNode, delete RIGHT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54,53)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }//.config(enabled=false)

        "[04] Parent=TwoNode Left=TwoNode Right=ThreeNode,  delete LEFT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[05] Parent=TwoNode Left=TwoNode Right=ThreeNode,  delete LEFT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95)//, 112, 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[06] Parent=ThreeNode Kids=TwoNodes, delete LEFT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[07] Parent=ThreeNode Kids=TwoNodes, delete MIDDLE"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[08] Parent=ThreeNode Kids=TwoNodes, delete RIGHT"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[09] Parent=ThreeNode Kids=TwoNodes, delete ParentKv1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            var deleted = false
            with(tree){
                deleted=  delete(keyToDelete)
            }
            val inorder = tree.getInorder()
            val sorted  =  (keys-keyToDelete).sorted()
            val leaves =  leavesAreOnSameLevel(tree)

            inorder shouldBe sorted
            leaves shouldBe true
            deleted shouldBe true
        }

        "[10] Parent=ThreeNode Kids=TwoNodes, delete ParentKv2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[11] Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete Middle"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[12] Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete ParentKV1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[13] Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete ParentKV2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[14] Parent=ThreeNode Left=ThreeNode Middle=TwoNode Right=TwoNode, delete Right"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,52)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[15] Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete Left"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[16] Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete ParentKv1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[17] Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete ParentKv2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[18] Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=TwoNode, delete Right"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,55)// 911, 150)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[19] Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Left"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[20] Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Middle"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[22] Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Parent KV1"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 54
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[23] Parent=ThreeNode Left=TwoNode  Middle=TwoNode Right=ThreeNode, delete Parent KV2"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 95
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[24] Parent=ThreeNode Left=ThreeNode  Middle=ThreeNode Right=TwoNode, delete Right"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,51,55)
            keys.forEach { tree.put(it) }
            val keyToDelete = 112
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[25] Parent=ThreeNode Left=ThreeNode  Middle=ThreeNode Right=ThreeNode, delete Middle"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,51,911)
            keys.forEach { tree.put(it) }
            val keyToDelete = 90
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[26] Parent=ThreeNode Left=TwoNode  Middle=ThreeNode Right=ThreeNode, delete Left"{
            val keys = listOf(4, 15, 18, 47, 90, 25, 50, 54, 95, 112,60,911)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 50
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[27] Bigger set 1 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 2
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[28] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 23
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[29] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 83
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[30] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 81
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[31] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 98
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[32] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 59
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
        "[33] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 33
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        "[33] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 81
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        "[33] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 98
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[34] Bigger set 2 "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14, 45, 54, 23, 1, 94, 22, 72, 44, 83, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 6
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[34] merging test 1 ,deleting from left most two node "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14)//, 45, 54, 23, 1)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 49
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        "[35] merging test 1 ,deleting 34brother "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14)//, 45, 54, 23, 1)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 49
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[35] merging test 2 ,deleting 34brother "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34, 14)//, 45, 54, 23, 1)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 68
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[36] merging test 2 ,deleting  "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 59
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[37] merging test 3 deleting TwoRoot TwoChild  "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 67
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[39] merging test 3 deleting TwoRoot TwoChild  "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 81
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[40] merging test 3 deleting stuff from left side "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 16
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[41] merging test 3 deleting stuff from left side "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 15
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[41] merging test 3 deleting stuff from left side "{
            val keys = listOf(68, 42, 15, 16, 6, 81, 98, 2, 67, 33, 49, 62, 96, 59, 17, 7, 34)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 17
            val deleted = tree
                .
                    delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[42] new keyset delete  "{
            val keys = listOf(80, 95, 93, 15, 19, 61, 37, 78, 31, 26, 63, 67, 62, 43, 72, 28, 32, 16, 47, 73, 6, 86, 49, 55)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 61
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[43] testik"{
            val keys = listOf(80, 95, 93, 15, 19, 61, 37, 78, 31, 26, 63, 67, 62, 43, 72, 28, 32, 16, 47, 73, 6, 86, 49, 55,27,40,41)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 19
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[44] testik"{
            val keys = listOf(80, 95, 93, 15, 19, 61, 37, 78, 31, 26, 63, 67, 62, 43, 72, 28, 32, 16, 47, 73, 6, 86, 49, 55,27,40,41)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 15
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[45] testik"{
            val keys = listOf(80, 95, 93, 15, 19, 61, 37, 78, 31, 26, 63, 67, 62, 43, 72, 28, 32, 16, 47, 73, 6, 86, 49, 55,27,40,41)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 27
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[46] testik"{
            val keys = listOf(80, 95, 93, 15, 19, 61, 37, 78, 31, 26, 63, 67, 62, 43, 72, 28, 32, 16, 47, 73, 6, 86, 49, 55,27,40,41)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = 93
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }

        "[47] final test"{
            val rnd  = Random(987)
            val keys = Collections.nCopies(9564,Any()).map { rnd.nextInt(999) }.distinct().toMutableList()
            println(keys)
            keys.forEach { tree.put(it,4)}
            val keyToDelete = keys[rnd.nextInt(keys.size)]
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true

        }
       /* (1..393).map{ Random(it.toLong()) }.forEachIndexed { indexx, rnd ->
            val index=indexx.toLong()
            "[$index] testik"{
            val rnd = Random(index)
            val keys = Collections.nCopies(1000,Any()).map { rnd.nextInt(5000) }.distinct().toMutableList()
            keys.forEach { tree.put(it,4)}
            val keyToDelete = keys[Random(index).nextInt(keys.size)]
            val deleted = tree.delete(keyToDelete)
            val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
            result shouldBe true
        }
        }*/

        listOf(Random(987),Random(456),Random(8945),Random(962),Random(2659),Random(986547),Random(987987987),Random(986231),Random())
            .forEachIndexed { index,rnd ->
            "[$index] RND test"{
                val keys = Collections.nCopies(4000,Any()).map { rnd.nextInt(9999) }.distinct().toMutableList()
                keys.forEach { tree.put(it,4)}
                val keyToDelete = keys[rnd.nextInt(keys.size)]
                val deleted = tree.delete(keyToDelete)
                val result=(tree.getInorder() == (keys-keyToDelete).sorted() && deleted && leavesAreOnSameLevel(tree) )
                result shouldBe true

            }
        }
//endregion

    }
//
//    fun leavesAreOnSameLevel(tree:TwoThreeTree<Int,Int>):Boolean{
//        val leafs = emptyLinkedList<Node<Int, Int>>()
//        tree.inorder { node ->
//            if (node.isLeaf()) leafs += node
//        }
//
//        val size: Boolean = leafs.map {
//            val stack = emptyLinkedList<Node<Int, Int>>()
//            var n: Node<Int, Int>? = it
//            while (n != null) {
//                stack.push(n)
//                n = n.parent
//            }
//            stack.size
//        }.distinct().size == 1
//        return size
//    }

    fun <A:Comparable<A>,B> leavesAreOnSameLevel(tree:TwoThreeTree<A,B>):Boolean{
        val leafs = emptyLinkedList<Node<A, B>>()
        tree.inorder { node ->
            if (node.isLeaf()) leafs += node
        }

        val size: Boolean = leafs.map {
            val stack = emptyLinkedList<Node<A, B>>()
            var n: Node<A, B>? = it
            while (n != null) {
                stack.push(n)
                n = n.parent
            }
            stack.size
        }.distinct().size == 1
        return size
    }

}

fun <T> getRandomSubList(input: MutableList<T>, subsetSize: Int): List<T> {
    val r = Random(20)
    val inputSize = input.size
    for (i in 0 until subsetSize) {
        val indexToSwap = i + r.nextInt(inputSize - i)
        val temp = input[i]
        input[i] = input[indexToSwap]
        input[indexToSwap] = temp
    }
    return input.subList(0, subsetSize)
}

fun measureTime(name: String, f: () -> Unit) {
    println(name)
    val starTime    = System.currentTimeMillis() / 1000
    f()
    val elapsedTime = System.currentTimeMillis() / 1000 - starTime
    println("$name took $elapsedTime seconds")
}