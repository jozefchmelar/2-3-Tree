package Tree
import Tree.TwoThreeTree.Position.*
import Tree.node.KeyValue
import Tree.node.Node
import Tree.node.Node.FourNode
import Tree.node.Node.TwoNode
import Tree.node.Node.ThreeNode
import Tree.node.with
import extensions.emptyLinkedList
import org.jetbrains.annotations.ReadOnly
import java.security.Key
import java.util.*
import java.util.LinkedList




class TwoThreeTree<K:Comparable<K>,V>{

    internal var root : Node<K,V>? = null

    //for testing purposes only
    val insertedKeys = emptyLinkedList<K>()

    fun put(key:K,value:V){

        if(root ==null) {
            root = (TwoNode(key with value))
            insertedKeys.add(key)
            return
        }

        val foundNode = getNode(key)

        when(foundNode){
            is TwoNode   -> insert(foundNode,key,value)
            is ThreeNode -> insert(foundNode,key,value)
            is FourNode  -> throw FourNodeInsertionException()
            else         -> throw IllegalStateException("Node to insert value to was not found")
        }

        insertedKeys.add(key)
    }

    private fun insert(twoNode: Node.TwoNode<K, V>, key: K, value: V) {
        val threeNode = twoNode.toThreeNode(key with value)

        when(twoNode.parent){
            is TwoNode,
            is ThreeNode -> twoNode.replaceWith(threeNode)
            is FourNode  -> throw FourNodeException()
            null         -> root = threeNode
        }
    }

    private fun insert(threeNode: Node.ThreeNode<K, V>, key: K, value: V) {
        val fourNode = threeNode.toFourNode(key with value)
        val parent   = threeNode.parent

        when(parent){
            is Node.TwoNode    -> {
                val originPosition = getNodePosition(fourNode)

                val newParent = when (originPosition) {
                        Left ->
                            parent.toThreeNode(fourNode.keyValue2)
                                .addMiddle   (fourNode.keyValue3)
                                .addLeft     (fourNode.keyValue1)
                                .addRight    (parent.right!!)

                        Right ->
                            parent.toThreeNode(fourNode.keyValue2)
                                .addMiddle    (fourNode.keyValue1)
                                .addLeft      (parent.left!!)
                                .addRight     (fourNode.keyValue3)

                        Middle -> throw IllegalStateException("Parent is a TwoNode therefore he doesn't have a middle child")
                    }

                if (parent == root)
                    root = newParent
                else
                    fourNode.parent!!.replaceWith(newParent)

            }

            is Node.ThreeNode  -> {
                val originPosition = getNodePosition(fourNode)

                val tempFourNode = when (originPosition) {
                    Left ->
                        parent.toFourNode(fourNode.keyValue2)
                            .addMiddle2  (parent.middle!!)
                            .addMiddle   (fourNode.keyValue3)
                            .addLeft     (fourNode.keyValue1)
                            .addRight    (parent.right!!)

                    Middle ->
                        parent.toFourNode(fourNode.keyValue2)
                            .addMiddle2  (fourNode.keyValue3)
                            .addMiddle   (fourNode.keyValue1)
                            .addLeft     (parent.left!!)
                            .addRight    (parent.right!!)

                    Right ->
                        parent.toFourNode(fourNode.keyValue2)
                            .addMiddle2  (fourNode.keyValue1)
                            .addMiddle   (parent.middle!!)
                            .addLeft     (parent.left!!)
                            .addRight    (fourNode.keyValue3)

                } as FourNode<K, V>

                insert(tempFourNode, key,value)

            }

            is Node.FourNode   -> throw FourNodeInsertionException()

            null               ->
                root = TwoNode    (fourNode.keyValue2)
                        .addLeft  (fourNode.keyValue1)
                        .addRight (fourNode.keyValue3)

        }
    }

    private fun insert(fourNode: Node.FourNode<K, V>, key: K, value: V) {

        val parent = fourNode.parent

        when(parent){
            is TwoNode   -> {
                val originPosition = getNodePosition(fourNode)

                val splitted = fourNode.split()

                val newParent = parent.toThreeNode(fourNode.keyValue2)

                when(originPosition){
                    Left   -> newParent
                                .addMiddle (splitted.right!!)
                                .addLeft   (splitted.left !!)
                                .addRight  (parent  .right!!)
                    Right  -> newParent
                                .addMiddle (splitted.left !!)
                                .addRight  (splitted.right!!)
                                .addLeft   (parent  .left !!)

                    Middle -> IllegalStateException("must add to parent from some side.")
                }
                if (parent == root)
                    root = newParent
                else
                    fourNode.parent!!.replaceWith(newParent)

            }
            is ThreeNode -> {
                val originPosition    = getNodePosition(fourNode)
                val newFourNodeParent = parent.merge(originPosition, fourNode)

                //val


                insert(newFourNodeParent, key, value)
            }

            is FourNode  -> throw FourNodeInsertionException()
            null         -> root =  fourNode.split()
        }
    }

    private fun ThreeNode<K, V>.merge(fromSide: Position, fourNode:FourNode<K,V>): FourNode<K, V> {
        val splitted = fourNode.split()
        return  when (fromSide) {
            Left -> {
                this.toFourNode (fourNode.keyValue2)
                    .addMiddle2 (this.middle!!)
                    .addMiddle  (splitted.right!!)
                    .addLeft    (splitted.left!!)
                    .addRight   (this.right!!)
            }

            Middle -> {
                this.toFourNode (fourNode.keyValue2)
                    .addMiddle2 (splitted.right!!)
                    .addMiddle  (splitted.left!!)
                    .addLeft    (this.left!!)
                    .addRight   (this.right!!)
            }

            Right -> {
                this.toFourNode (fourNode.keyValue2)
                    .addMiddle2 (splitted.left!!)
                    .addMiddle  (this.middle!!)
                    .addLeft    (this.left!!)
                    .addRight   (splitted.right!!)
            }
        } as FourNode
    }

    fun getNode(key: K) : Node<K, V>? {
        var parent: Node<K, V>? = root

        while(true){
            when(parent){
                is Node.TwoNode   -> {
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
                is Node.FourNode  -> throw FourNodeException()
                null              -> return (parent)
            }
        }
    }

    fun printInOrder(){
        val list = emptyLinkedList<Node<K,V>>()

        println(inorder(/*root*/))
    }


    fun inorder(): List<Node<K, V>> {

        val stack = emptyLinkedList<Node<K, V>>()
        val pushLeft = { _node: Node<K, V>? ->
            var node = _node
            while (node != null) {
                if (node is ThreeNode) {
                    stack.push(
                        TwoNode(keyValue1 = node.keyValue2,left =  null, right =  node.right)
                    )
                    stack.push(
                        TwoNode(keyValue1 = node.keyValue1,left =  null, right = node.middle)
                    )
                    node = node.left
                } else {
                    stack.push(node)
                    node = node.left
                }
            }
        }

        var n: Node<K, V>?
        val result = emptyLinkedList<Node<K, V>>()
        pushLeft(root)
        while (stack.isNotEmpty()) {
            n = stack.pop()
            result.add(n)
            pushLeft(n.right)
        }
        return result
    }


    private fun getNodePosition(node: Node<K, V>):Position {
        val parent = node.parent!!
        return  when (node) {
            is Node.FourNode  -> when (parent) {
                is Node.TwoNode -> {
                    val leftKeyVal      = node.parent!!.left !!.keyValue1
                    val rightKeyVal     = node.parent!!.right!!.keyValue1
                    val addingFromLeft  = leftKeyVal  == node.keyValue1 || leftKeyVal  == node.keyValue2 || leftKeyVal  == node.keyValue3
                    val addingFromRight = rightKeyVal == node.keyValue1 || rightKeyVal == node.keyValue2 || rightKeyVal == node.keyValue3
                    when {
                        addingFromLeft  -> Left
                        addingFromRight -> Right
                        else -> throw IllegalStateException("This node is not a child of it's parent")
                    }
                }
                is Node.ThreeNode -> {
                    val leftKeyVal      = parent.left  !!.keyValue1
                    val midKeyVal       = parent.middle!!.keyValue1
                    val rightKeyVal     = parent.right !!.keyValue1

                    val addingFromLeft  = leftKeyVal  == node.keyValue1 || leftKeyVal  == node.keyValue2 || leftKeyVal  == node.keyValue3
                    val addingFromMid   = midKeyVal   == node.keyValue1 || midKeyVal   == node.keyValue2 || midKeyVal   == node.keyValue3
                    val addingFromRight = rightKeyVal == node.keyValue1 || rightKeyVal == node.keyValue2 || rightKeyVal == node.keyValue3

                    when {
                        addingFromLeft  -> Left
                        addingFromMid   -> Middle
                        addingFromRight -> Right
                        else -> throw IllegalStateException("This node is not a child of it's parent")
                    }
                }
                is Node.FourNode -> TODO()
            }
            is Node.TwoNode,
            is Node.ThreeNode    -> TODO()
        }
    }

    private enum class Position{Left,Middle,Right}

 }

class FourNodeInsertionException : Exception ("You shouldn't insert values into four node ")
class FourNodeException          : Exception ("Four node can't be in 23 tree")
