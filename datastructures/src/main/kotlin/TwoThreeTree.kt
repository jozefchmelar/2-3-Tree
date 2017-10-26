package Tree
import Tree.Position.*
import Tree.node.KeyValue
import Tree.node.Node
import Tree.node.Node.*
import Tree.node.Sibling
import Tree.node.with
import extensions.Queue
import extensions.emptyLinkedList
import java.util.*


class TwoThreeTree<K:Comparable<K>,V>  {

    internal var root : Node<K,V>? = null

    //for testing purposes only
    val insertedKeys = emptyLinkedList<K>()

    fun put(key:K,value:V){

        if(root ==null) {
            root = TwoNode(key with value)
            insertedKeys.add(key)
            return
        }

        val foundNode = getNode(key)

        when(foundNode){
            is TwoNode   -> insert(foundNode,key,value)
            is ThreeNode -> insert(foundNode,key,value)
            else         -> throw IllegalStateException("Node to insert value to was not found")
        }

        insertedKeys.add(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun delete(key: K): Boolean {
        var node = find(key)
        if (node != null) {
            if (node.isLeaf() && root == node) {
                root = null
                return true
            }
            if (node is ThreeNode && node.isLeaf()) {
                node.deleteFromNode(key)
                return true
            } else {
                var inorderSuccessor = inoredSuccesor(node) ?: TODO()// if (iordSccsor == null) node.parent else iordSccsor

                if (inorderSuccessor is ThreeNode && inorderSuccessor.isLeaf() /*TODO len si to myslim ze to tam ma byt*/) {
                    val node = inorderSuccessor
                    node.replaceWith(TwoNode(node.keyValue2, parent = node.parent))
                    node.parent!!.keyValue1 = node.keyValue1
                    return true
                }

                while (inorderSuccessor != null) {

                    val siblings = node.getSiblings()

                    when (siblings) {
                        is Sibling.TwoSiblings<*, *> -> {
                            //if i have two siblings, my parent has to be threeNode
                            val parent   = node.parent as ThreeNode
                            val position = node.getPosition()
                            when (position) {
                                Left -> {
                                    //I only care about my sibling on the right side which is in the middle one
                                    if (siblings.first is TwoNode) {
                                        val newLeftKv1 = parent.keyValue1
                                        val newLeftKv2 = siblings.first.keyValue1 as KeyValue<K, V>
                                        val parentsRightSon = parent.right!!
                                        parent.replaceWith(
                                            TwoNode(
                                                keyValue1 = parent.keyValue2,
                                                parent = parent.parent
                                            )
                                                .addLeft(ThreeNode(newLeftKv1, newLeftKv2))
                                                .addRight(parentsRightSon)
                                        )
                                        return true

                                    } else {
                                        val sibling = siblings.first as ThreeNode<K,V>
                                        val newLeftKv = parent.keyValue1
                                        val newParentKv1 = sibling.keyValue1
                                        parent.left!!.keyValue1 = newLeftKv
                                        sibling.deleteFromNode(newParentKv1.key)
                                        parent.keyValue1=newParentKv1
                                        return true
                                    }
                                }
                                Middle -> {
                                    if (siblings.first is ThreeNode || siblings.second is ThreeNode) {
                                        if (siblings.first is ThreeNode) {
                                            val sibling = siblings.first as ThreeNode<K, V>
                                            val newMiddleKv = parent.keyValue1
                                            val newParentKv1 = sibling.keyValue2
                                            sibling.deleteFromNode(newParentKv1.key)
                                            parent.middle!!.keyValue1 = newMiddleKv
                                            parent.keyValue1 = newParentKv1
                                            return true
                                        } else {
                                            //if i'm the middle of threeNode and left is 2node and right is 3node
                                            val sibling = siblings.second as ThreeNode<K, V>
                                            val newMiddleKv = parent.keyValue2
                                            val newParentKv1 = parent.right!!.keyValue1
                                            (parent.right as ThreeNode<K, V>).deleteFromNode(newParentKv1.key)
                                            parent.middle!!.keyValue1 = newMiddleKv
                                            parent.keyValue2 = newParentKv1
                                            return true
                                        }
                                    } else {
                                        TODO()
                                    }
                                }
                                Right -> {
                                    //I only care about my sibling on the left side which is in the middle one
                                   if(siblings.first is TwoNode){
                                       val sibling = siblings.first// as ThreeNode
                                       val newRightKv1 = sibling.keyValue1 as KeyValue<K,V>
                                       val newRightKv2 = parent.keyValue2
                                       val newLeft     = parent.left!!
                                       parent.replaceWith(TwoNode(
                                           keyValue1 = parent.keyValue1,
                                           parent = parent.parent
                                       )
                                           .addRight(ThreeNode(newRightKv1,newRightKv2))
                                           .addLeft(newLeft)
                                       )
                                       return true

                                   } else {
                                       val sibling = siblings.first as ThreeNode<K,V>
                                       val newRightKv = parent.keyValue2
                                       val newParentKv2 = sibling.keyValue2
                                       parent.right!!.keyValue1 = newRightKv
                                       sibling.deleteFromNode(newParentKv2.key)
                                       parent.keyValue2=newParentKv2
                                       return true
                                   }

                                }
                            }
                        }
                        is Sibling.OneSibling<*, *>  -> {
                            when (siblings.side) {
                                Left -> {
                                    val sibling = siblings.sibling
                                    when (sibling) {
                                        is Node.TwoNode -> {
                                            println() //TODO()
                                        }
                                        is Node.ThreeNode -> {
                                            val newRightNode = sibling.parent
                                            val newParentKv  = sibling.keyValue2
                                            val sibling = sibling as ThreeNode<K, V>

                                            if (sibling.isLeaf())
                                                sibling.deleteFromNode(sibling.keyValue1.key)
                                            else
                                                node.replaceWith(newRightNode as Node<K, V>) // ???

                                            sibling.parent!!.keyValue1 = newParentKv as KeyValue<K, V>

                                            return true
                                        }
                                    }
                                }
                                Middle -> TODO()
                                Right -> {
                                    val sibling = siblings.sibling
                                    when (sibling) {
                                        is Node.TwoNode -> TODO()
                                        is Node.ThreeNode -> {
                                            //my sibling is on right side thereofre I have twondoe parent and and I'm on the left side
                                            // left side key is parent key, and parent key is left side of the three node sibling
                                            val newLeftNode = sibling.parent
                                            val newParentKv = sibling.keyValue1
                                            val sibling = sibling as ThreeNode<K, V>

                                            sibling.replaceWith(TwoNode(keyValue1 = sibling.keyValue2, parent = sibling.parent))
                                            if (sibling.isLeaf())
                                                node.keyValue1 = newLeftNode!!.keyValue1 as KeyValue<K, V>
                                            else
                                                node.replaceWith(newLeftNode as Node<K, V>)
                                            sibling.parent!!.keyValue1 = newParentKv as KeyValue<K, V>
                                            return true
                                        }
                                        else -> throw Exception("nope")
                                    }

                                }
                            }
                        }
                        Sibling.NoSiblings -> TODO()
                    }

                    if (inorderSuccessor is ThreeNode && inorderSuccessor.hasKids()) {

                    }

                }

            }
        }
        return false
    }

    fun Node<K,V>.childrenAreLeafs() :Boolean = this.left?.isLeaf() ?: false

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
        //val parent = fourNode//.parent
        var fourNode = fourNode
        var n = fourNode.parent
        val stack = emptyLinkedList<Node<K,V>?>()

        while(n!=null){
            stack.add(n)
            n=n.parent
        }


        if(stack.isEmpty()){
            val parent = fourNode.parent
            when(parent){
                is Node.TwoNode   -> {
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
                    return
                }
                is Node.ThreeNode -> {
                    val originPosition    = getNodePosition(fourNode)
                    val newFourNodeParent = parent.merge(originPosition, fourNode)
                    println()
                }
                null ->{
                    val splitted = fourNode.split()
                    root= splitted
                }
            }
        }else{

        }
        while(stack.isNotEmpty()){
            val parent = stack.pop()//?.parent
            when(parent){
                is Node.TwoNode   -> {
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
                    return
                }
                is Node.ThreeNode -> {
                    val originPosition    = getNodePosition(fourNode)
                    val newFourNodeParent = parent.merge(originPosition, fourNode)
                    fourNode = newFourNodeParent
                    if(parent == root ){
                        root = fourNode.split()
                    }else{
                       // fourNode.parent!!.replaceWith(newFourNodeParent)
                        fourNode = newFourNodeParent
                      //  stack.push(parent)
                    }
                }
                null ->{
                    root= (fourNode as FourNode).split()
                }
            }
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

    fun find(key:K): Node<K, V>?{
        var parent: Node<K, V>? = root

        while(true){
            when(parent){
                is Node.TwoNode   -> {
                    if (key == parent.keyValue1.key) return if( parent.keyValue1.key==key) parent else null
                    if (key < parent.keyValue1.key){
                        if(parent.left == null ) return if( parent.keyValue1.key==key) parent else null
                        else parent = parent.left
                    }
                    else{
                        if(parent.right == null ) return if( parent.keyValue1.key==key) parent else null
                        else parent = parent.right
                    }
                }
                is Node.ThreeNode -> {
                    when{
                        key == parent.keyValue1.key    -> return if( parent.keyValue1.key==key) parent else null
                        key == parent.keyValue2.key     ->return if( parent.keyValue2.key==key) parent else null
                        key < parent.keyValue1.key -> if(parent.left   != null) parent = parent.left   else return if( parent.keyValue1.key==key) parent else null
                        key > parent.keyValue1.key
                            &&
                            key < parent.keyValue2.key -> if(parent.middle != null) parent = parent.middle else return if( parent.keyValue1.key==key) parent else null
                        key > parent.keyValue2.key -> if(parent.right  != null) parent = parent.right  else return if( parent.keyValue1.key==key) parent else null
                    }
                }
                is Node.FourNode  -> throw FourNodeException()
                null              -> return (parent)
            }
        }
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
                        key == parent.keyValue1.key    -> return (parent)
                        key == parent.keyValue2.key    -> return (parent)
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

    fun getInorder(): List<K> {
        val list = emptyLinkedList<K>()
        root?.let { root ->
            inorder(root) {
                list += it.keyValue1.key
            }
        }
        return list
    }

    fun levelOrder(node: Node<K, V>?): MutableList<Node<K, V>>? {
        val queue = Queue<Node<K, V>>(emptyLinkedList())
        val helpQueue = Queue<Node<K, V>>(emptyLinkedList())

        if (node == null) return null

        var node = node
        while (node != null) {
            queue.enqueue(node)

            if (!node.isLeaf()) when (node) {
                is Node.TwoNode -> {
                    helpQueue.enqueue(node.left!!)
                    helpQueue.enqueue(node.right!!)
                }
                is Node.ThreeNode -> {
                    helpQueue.enqueue(node.left!!)
                    helpQueue.enqueue(node.middle!!)
                    helpQueue.enqueue(node.right!!)
                }
            }
            node = if (helpQueue.items.size > 0) {
                helpQueue.dequeue()
            } else
                null
        }
        return queue.items
    }

    fun inoredSuccesor(node: Node<K, V>): Node<K, V>? {
        val found = emptyLinkedList<Node<K, V>>()
        var result: Node<K, V>? = null
        var once=true
        inorder { currentNode ->
            found.add(currentNode)
            if (found.size > 2) {
                val predposledny = found[found.size - 2]
                val kv1 = node.keyValue1
                val kv2 = (node as? ThreeNode)?.keyValue2

                if (predposledny.keyValue1 == kv1 || predposledny.keyValue1 == kv2 && once) {
                    result = currentNode //TODO omg ...
                    once = false
                }
            }
        }
        result = if(result!=null)
            find(result!!.keyValue1.key)
        else find(found.last.keyValue1.key)
        return result
    }

    fun inorder(node: Node<K, V> = root!!,visit : (Node<K,V>) -> Unit) {

        val stack = emptyLinkedList<Node<K, V>>()
        val pushLeft = { _node: Node<K, V>? ->
            var node = _node
            while (node != null) {
                if (node is ThreeNode) {
                    stack.push(
                        TwoNode(keyValue1 = node.keyValue2, left = null, right = node.right,  parent = node.parent)
                    )
                    stack.push(
                        TwoNode(keyValue1 = node.keyValue1, left = null, right = node.middle, parent = node.parent)
                    )
                    node = node.left
                } else {
                    stack.push(node)
                    node = node.left
                }
            }
        }

        var n: Node<K, V>?
        pushLeft(node)
        while (stack.isNotEmpty()) {
            n = stack.pop()
            visit(n)
            pushLeft(n.right)
        }
    }

    private fun getNodePosition(node: Node<K, V>):Position {
        val parent = node.parent!!
        return  when (node) {
            is Node.FourNode  ->
                when (parent) {
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
                    val leftKeyVal      = parent.left!!.keyValue1
                    val midKeyVal       = parent.middle!!.keyValue1
                    val rightKeyVal     = parent.right!!.keyValue1

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
            is Node.TwoNode      -> {
                when(parent){
                    is Node.TwoNode   -> {
                        if(node.keyValue1==parent.left!!.keyValue1)
                            Left
                        else
                            Right
                    }
                    is Node.ThreeNode -> when(node){
                        parent.left     -> Left
                        parent.middle   -> Middle
                        parent.right    -> Right
                    }
                    is Node.FourNode  -> TODO()
                }
                TODO()
            }
            is Node.ThreeNode    -> {
                println()
                TODO()
            }
        }
    }



 }

fun <K : Comparable<K>, V> Node<K, V>.getPosition() : Position {
    val parent = parent!!
    return when{
        parent.left == this  -> Position.Left
        parent.right == this -> Position.Right
        (parent is ThreeNode) && parent.middle == this -> Middle
        else -> throw IllegalStateException("")
    }
    }
fun <K : Comparable<K>, V> ThreeNode<K, V>.deleteFromNode(key: K) =
    when (key) {
        this.keyValue1.key -> this.replaceWith(TwoNode(this.keyValue2, parent = this.parent))
        this.keyValue2.key -> this.replaceWith(TwoNode(this.keyValue1, parent = this.parent))
        else -> throw IllegalStateException("this value is not in this node")
    }

enum class Position{Left,Middle,Right}

class FourNodeInsertionException : Exception ("You shouldn't insert values into four node ")
class FourNodeException          : Exception ("Four node can't be in 23 tree")
