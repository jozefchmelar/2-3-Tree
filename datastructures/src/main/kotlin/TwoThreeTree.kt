package Tree
import Tree.node.KeyValue
import Tree.node.Node
import Tree.node.Node.FourNode
import Tree.node.Node.TwoNode
import Tree.node.Node.ThreeNode
import Tree.node.with
import extensions.emptyLinkedList
import extensions.isNotNull
import org.jetbrains.jps.api.CmdlineRemoteProto.Message.KeyValuePair
import java.util.*


class TwoThreeTree<K:Comparable<K>,V>{

    internal var root : Node<K,V>? = null
    val insertedKeys = emptyLinkedList<K>()

    fun put(key:K,value:V){
        insertedKeys.add(key)
        if(root ==null) {
            root = (Node.TwoNode(key with value))
            return
        }

        val foundNode = getNode(key)

        when(foundNode){
            is Node.TwoNode   -> insert(foundNode,key,value)
            is Node.ThreeNode -> insert(foundNode,key,value)
            is Node.FourNode  -> throw FourNodeInsertionException()
        }
        println()
    }

    private fun insert(twoNode  : Node.TwoNode   <K,V>,  key: K, value: V) {
        val threeNode = twoNode.toThreeNode(key with value)
        val parent    = twoNode.parent

        when(parent){
            is Node.TwoNode   ->
                twoNode.replaceWith(threeNode)
            is Node.ThreeNode ->
                twoNode.replaceWith(threeNode)
            is Node.FourNode  -> throw FourNodeInsertionException()
            null              ->
                root = threeNode
        }
    }

    private fun insert(threeNode: Node.ThreeNode <K,V>,  key: K, value: V) {
        val fourNode = threeNode.toFourNode(key with value)
        val parent   = threeNode.parent

        when(parent){

            is Node.TwoNode    -> {
                val addingFromLeft   = threeNode == parent.left
                val addingFromRight  = threeNode == parent.right

                val newParent =
                    when {
                        addingFromLeft ->
                            parent.toThreeNode(fourNode.keyValue2)
                                .addMiddle  (fourNode.keyValue3)
                                .addLeft    (fourNode.keyValue1)
                                .addRight   (parent.right!!)

                        addingFromRight ->
                            parent.toThreeNode(fourNode.keyValue2)
                                .addMiddle  (fourNode.keyValue1)
                                .addLeft    (parent.left!!)
                                .addRight   (fourNode.keyValue3)

                        else -> throw IllegalStateException()

                    }

                if(parent==root)
                    root = newParent
                else
                    threeNode.parent!!.replace(newParent)

            }
            is Node.ThreeNode  -> {
                val addingFromLeft   = threeNode == parent.left
                val addingFromMiddle = threeNode == parent.middle
                val addingFromRight  = threeNode == parent.right

                val tempFourNode = when {
                    addingFromLeft ->
                        parent.toFourNode(fourNode.keyValue2)
                        .addMiddle2 (parent.middle!!)
                        .addMiddle  (fourNode.keyValue3)
                        .addLeft    (fourNode.keyValue1)
                        .addRight   (parent.right!!)

                    addingFromMiddle ->
                        parent.toFourNode(fourNode.keyValue2)
                        .addMiddle2 (fourNode.keyValue3)
                        .addMiddle  (fourNode.keyValue1)
                        .addLeft    (parent.left!!)
                        .addRight   (parent.right!!)

                    addingFromRight ->
                        parent.toFourNode(fourNode.keyValue2)
                        .addMiddle2 (fourNode.keyValue1)
                        .addMiddle  (parent.middle!!)
                        .addLeft    (parent.left!!)
                        .addRight   (fourNode.keyValue3)
                    else -> throw IllegalStateException()
                } as FourNode<K, V>

                insert(tempFourNode,parent, key,value)

            }

            is Node.FourNode   -> throw FourNodeInsertionException()

            null               -> {
                val newParent = TwoNode(fourNode.keyValue2)
                newParent.addLeft  (fourNode.keyValue1)
                newParent.addRight (fourNode.keyValue3)
                root = (newParent)
            }
        }
    }

    private fun insert(fourNode: Node.FourNode   <K,V>,threeNode: Node.ThreeNode <K,V>,  key: K, value: V) {
        val parent = fourNode.parent
        when(parent){
            is TwoNode   -> {
                val leftKeyVal       = fourNode.parent!!.left !!.keyValue1
                val rightKeyVal      = fourNode.parent!!.right!!.keyValue1
                val addingFromLeft   = leftKeyVal  == fourNode.keyValue1  ||  leftKeyVal  == fourNode.keyValue2  || leftKeyVal  == fourNode.keyValue3
                val addingFromRight  = rightKeyVal == fourNode.keyValue1  ||  rightKeyVal == fourNode.keyValue2  || rightKeyVal == fourNode.keyValue3

                val newParent = parent.toThreeNode(fourNode.keyValue2)
                val splitted: TwoNode<K, V> = fourNode.split() as TwoNode<K, V>

                when{
                    addingFromLeft  -> newParent.addMiddle(splitted.right!!).addLeft (splitted.left!!)
                    addingFromRight -> newParent.addMiddle(splitted.left!!) .addRight(splitted.right!!)
                    else            -> IllegalStateException("must add to parent from some side.")
                }
                if(parent==root)
                    root= newParent
                else
                    TODO()

            }
            is ThreeNode -> {
                val newParent = parent.toFourNode(fourNode.keyValue2)
          //      insert(newParent,key,value)
            }
            is FourNode  -> throw FourNodeInsertionException()
            null -> root =  fourNode.split()
        }
    }

    private fun splitFourNode(fourNode:Node.FourNode<K,V>): Node.TwoNode<K, V> =
        TwoNode(fourNode.keyValue2)
            .addLeft(
                TwoNode(fourNode.keyValue1)
                    .addLeft    (fourNode.left  !!.keyValue1)
                    .addRight   (fourNode.middle!!.keyValue1)
            )
            .addRight(
                TwoNode(fourNode.keyValue3)
                    .addLeft    (fourNode.middle2!!.keyValue1)
                    .addRight   (fourNode.right  !!.keyValue1)
            ) as TwoNode

    private fun Node.FourNode<K,V>.split() =
        TwoNode(this.keyValue2)
            .addLeft(
                TwoNode(this.keyValue1)
                    .addLeft    (this.left  !!)
                    .addRight   (this.middle!!)
            )
            .addRight(
                TwoNode(this.keyValue3)
                    .addLeft    (this.middle2!!)
                    .addRight   (this.right  !!)
            )

    fun getNode(key: K) : Node<K, V>? {
        var parent: Node<K, V>? = root

        while(true){
            when(parent){
                is Node.TwoNode -> {
                     if (key < parent.keyValue1.key){
                         if(parent.left == null ) return parent
                            else parent = parent.left
                     }
                    else{
                         if(parent.right == null ) return parent
                         else parent = parent.right
                     }
                }
                is Node.ThreeNode -> {
                    when{
                        key == parent.keyValue1    -> return (parent)
                        key == parent.keyValue2    -> return (parent)
                        key < parent.keyValue1.key -> if(parent.left   != null) parent = parent.left   else return (parent)
                        key > parent.keyValue1.key
                            &&
                        key < parent.keyValue2.key -> if(parent.middle != null) parent = parent.middle else return (parent)
                        key > parent.keyValue2.key -> if(parent.right  != null) parent = parent.right  else return (parent)
                    }
                }
                is Node.FourNode  -> throw FourNodeInsertionException()

                null -> return (parent)
            }
        }
    }

    fun traverseTree( visit  : (KeyValue<K,V>)  -> Unit){
        val stack = LinkedList<KeyValue<K,V>>()
        var node = root!!
        while(node.left!=null){
            when{
                node.left == null -> when(node){
                    is Node.TwoNode   -> stack.push(node.keyValue1)
                    is Node.ThreeNode -> {
                        stack.push(node.keyValue1)
                        stack.push(node.keyValue2)
                    }
                }
                node is TwoNode ->{
                    node = node.left!!
                    stack.push(node.keyValue1)
                    node = node.right!!
        //            stack.

                }
            }
        }
    }

    fun traverseTree(curNode: Node<K,V>, treeItems: LinkedList<KeyValue<K, V>>) {

        //If leaf node.
        when {
            curNode.left == null -> when(curNode){
                is Node.TwoNode   ->  treeItems.add(curNode.keyValue1)

                is Node.ThreeNode -> {
                    treeItems.add(curNode.keyValue1)
                    treeItems.add(curNode.keyValue2)
                }
            }
            curNode is TwoNode
                //If TwoNode.
            -> {
                val left = curNode.left!!
                traverseTree(left, treeItems) //Add lesser values first.
                treeItems.add(curNode.keyValue1) //Then this value.
                val right = curNode.right!!
                traverseTree(right, treeItems) //And greater values.
            }
            curNode is Node.ThreeNode -> {
                val left = curNode.left!!
                traverseTree(left, treeItems) //Lesser values.
                treeItems.add(curNode.keyValue1) //Low value.

                val middle = curNode.middle!!
                traverseTree(middle, treeItems) //Middle values.
                treeItems.add(curNode.keyValue2) //High value.

                val right = curNode.right!!
                traverseTree(right, treeItems) //Higher values.
            }
        }
    }


    enum class Direction{Left,Middle,Right}
}
class FourNodeInsertionException : Exception ("You shouldn't insert values into four node ")
class FourNodeException          : Exception ("Four node can't be in 23 tree")

