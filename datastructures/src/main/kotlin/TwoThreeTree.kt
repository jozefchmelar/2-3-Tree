package Tree
import Tree.node.KeyValue
import Tree.node.Node
import Tree.node.Node.FourNode
import Tree.node.Node.TwoNode
import Tree.node.Node.ThreeNode
import Tree.node.with
import extensions.emptyLinkedList
import java.util.*


class TwoThreeTree<K:Comparable<K>,V>{

    internal var root : Node<K,V>? = null
    val insertedKeys = emptyLinkedList<K>()

    fun put(key:K,value:V){

        if(root ==null) {
            root = (Node.TwoNode(key with value))
            insertedKeys.add(key)
            return
        }

        val foundNode = getNode(key)

        when(foundNode){
            is Node.TwoNode   -> insert(foundNode,key,value)
            is Node.ThreeNode -> insert(foundNode,key,value)
            is Node.FourNode  -> throw FourNodeInsertionException()
        }
        insertedKeys.add(key)

        println()
    }

    private fun insert(twoNode  : Node.TwoNode   <K,V>,  key: K, value: V) {
        val threeNode = twoNode.toThreeNode(key with value)
        val parent    = twoNode.parent

        when(parent){
            is Node.TwoNode,
            is Node.ThreeNode -> twoNode.replaceWith(threeNode)
            is Node.FourNode  -> throw FourNodeInsertionException()
            null              -> root = threeNode
        }
    }

    private fun insert(threeNode: Node.ThreeNode <K,V>,  key: K, value: V) {
        val fourNode = threeNode.toFourNode(key with value)
        val parent   = threeNode.parent

        when(parent){
            is Node.TwoNode    -> {
                val originPosition = getNodePosition(fourNode)

                val newParent = when(originPosition) {
                        Position.Left ->
                            parent.toThreeNode(fourNode.keyValue2)
                                .addMiddle  (fourNode.keyValue3)
                                .addLeft    (fourNode.keyValue1)
                                .addRight   (parent.right!!)

                        Position.Right ->
                            parent.toThreeNode(fourNode.keyValue2)
                                .addMiddle  (fourNode.keyValue1)
                                .addLeft    (parent.left!!)
                                .addRight   (fourNode.keyValue3)

                        Position.Middle -> throw IllegalStateException()
                    }

                if(parent==root)
                    root = newParent
                else
                    fourNode.parent!!.replace(newParent)

            }

            is Node.ThreeNode  -> {

                val originPosition = getNodePosition(fourNode)

                val tempFourNode = when(originPosition) {
                    Position.Left ->
                        parent.toFourNode(fourNode.keyValue2)
                        .addMiddle2 (parent.middle!!)
                        .addMiddle  (fourNode.keyValue3)
                        .addLeft    (fourNode.keyValue1)
                        .addRight   (parent.right!!)

                    Position.Middle ->
                        parent.toFourNode(fourNode.keyValue2)
                        .addMiddle2 (fourNode.keyValue3)
                        .addMiddle  (fourNode.keyValue1)
                        .addLeft    (parent.left!!)
                        .addRight   (parent.right!!)

                    Position.Right ->
                        parent.toFourNode(fourNode.keyValue2)
                        .addMiddle2 (fourNode.keyValue1)
                        .addMiddle  (parent.middle!!)
                        .addLeft    (parent.left!!)
                        .addRight   (fourNode.keyValue3)

                } as FourNode<K, V>

                insert(tempFourNode, key,value)

            }

            is Node.FourNode   -> throw FourNodeInsertionException()

            null               ->
                root = TwoNode(fourNode.keyValue2)
                        .addLeft  (fourNode.keyValue1)
                        .addRight (fourNode.keyValue3)

        }
    }

    private fun getNodePosition(node: Node<K, V>):Position {
        val parent = node.parent!!
        return  when (node) {
            is Node.TwoNode -> when (parent) {
                is Node.TwoNode -> TODO()
                is Node.ThreeNode -> TODO()
                is Node.FourNode -> TODO()
            }
            is Node.ThreeNode -> when (parent) {
                is Node.TwoNode -> TODO()
                is Node.ThreeNode -> TODO()
                is Node.FourNode -> TODO()
            }
            is Node.FourNode -> when (parent) {
                is Node.TwoNode -> {
                    val leftKeyVal = node.parent!!.left!!.keyValue1
                    val rightKeyVal = node.parent!!.right!!.keyValue1
                    val addingFromLeft = leftKeyVal == node.keyValue1 || leftKeyVal == node.keyValue2 || leftKeyVal == node.keyValue3
                    val addingFromRight = rightKeyVal == node.keyValue1 || rightKeyVal == node.keyValue2 || rightKeyVal == node.keyValue3
                    when {
                        addingFromLeft  -> Position.Left
                        addingFromRight -> Position.Right
                        else -> throw IllegalStateException("This node is not a child of it's parent")
                    }
                }
                is Node.ThreeNode -> {
                    val leftKeyVal = parent.left!!.keyValue1
                    val midKeyVal = parent.middle!!.keyValue1
                    val rightKeyVal = parent.right!!.keyValue1

                    val addingFromLeft = leftKeyVal == node.keyValue1 || leftKeyVal == node.keyValue2 || leftKeyVal == node.keyValue3
                    val addingFromMid = midKeyVal == node.keyValue1 || midKeyVal == node.keyValue2 || midKeyVal == node.keyValue3
                    val addingFromRight = rightKeyVal == node.keyValue1 || rightKeyVal == node.keyValue2 || rightKeyVal == node.keyValue3

                    when {
                        addingFromLeft -> Position.Left
                        addingFromMid -> Position.Middle
                        addingFromRight -> Position.Right
                        else -> throw IllegalStateException("This node is not a child of it's parent")
                    }
                }
                is Node.FourNode -> TODO()
            }
        }
    }

    private  fun insert(fourNode: Node.FourNode   <K,V>,  key: K, value: V) {
        val parent = fourNode.parent
        when(parent){
            is TwoNode   -> {
                val originPosition = getNodePosition(fourNode)

                val splitted = fourNode.split()

                val newParent = parent.toThreeNode(fourNode.keyValue2)

                when(originPosition){
                    Position.Left   -> newParent.addMiddle(splitted.right!!).addLeft (splitted.left !!).addRight(parent.right!!)
                    Position.Right  -> newParent.addMiddle(splitted.left !!).addRight(splitted.right!!).addLeft(parent.left !!)
                    Position.Middle -> IllegalStateException("must add to parent from some side.")
                }
                if(parent==root)
                    root= newParent
                else
                    fourNode.parent!!.replace(newParent)

            }
            is ThreeNode -> {

                val originPosition = getNodePosition(fourNode)

                val splitted = fourNode.split()

                val newFourNodeParent = when(originPosition) {
                    Position.Left  -> {
                        parent.toFourNode(fourNode.keyValue2)
                            .addMiddle2(parent  .middle!!)
                            .addMiddle (splitted.right !!)
                            .addLeft   (splitted.left  !!)
                            .addRight  (parent  .right !!)
                    }

                    Position.Middle   -> {
                        parent.toFourNode(fourNode.keyValue2)
                            .addMiddle2 (splitted.right!!)
                            .addMiddle  (splitted.left !!)
                            .addLeft    (parent  .left !!)
                            .addRight   (parent  .right!!)
                    }

                    Position.Right  -> {
                        parent.toFourNode(fourNode.keyValue2)
                            .addMiddle2(splitted.left  !!)
                            .addMiddle (parent  .middle!!)
                            .addLeft   (parent  .left  !!)
                            .addRight  (splitted.right !!)
                    }

                } as FourNode

                insert(newFourNodeParent,key,value)

            }
            is FourNode  -> throw FourNodeInsertionException()
            null -> root =  fourNode.split()
        }
    }

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

    private enum class Position{Left,Middle,Right}

 }
class FourNodeInsertionException : Exception ("You shouldn't insert values into four node ")
class FourNodeException          : Exception ("Four node can't be in 23 tree")
