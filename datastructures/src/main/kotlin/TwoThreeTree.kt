package Tree
import Tree.node.KeyValue
import Tree.node.Node
import Tree.node.Node.FourNode
import Tree.node.Node.TwoNode
import Tree.node.with
import extensions.emptyLinkedList
import extensions.isNotNull

class TwoThreeTree<K:Comparable<K>,V>{

    internal var root : Node<K,V>? = null

    fun put(key:K,value:V){

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
    }

    private fun insert(twoNode  : Node.TwoNode  <K,V>,  key: K, value: V) {
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

    private fun insert(threeNode: Node.ThreeNode<K, V>, key: K, value: V) {
        val fourNode = threeNode.toFourNode(key with value)
        val parent   = threeNode.parent

        when(parent){

            is Node.TwoNode    -> {
                val addingFromLeft   = threeNode == parent.left
                val addingFromRight  = threeNode == parent.right

                val newParent =
                    if (addingFromLeft)
                        parent.toThreeNode(fourNode.keyValue2)
                            .addMiddle    (fourNode.keyValue3)
                            .addLeft      (fourNode.keyValue1)
                            .addRight     (parent.right!!)
                    else
                        parent.toThreeNode(fourNode.keyValue2)
                            .addMiddle    (fourNode.keyValue1)
                            .addLeft      (parent.left!!)
                            .addRight     (fourNode.keyValue3)


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

                insert(tempFourNode, key,value)

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

    private fun insert(fourNode: Node.FourNode <K,V>, key: K, value: V) {
        val parent = fourNode.parent
        when(parent){
            is Node.TwoNode   -> {
                val newParent = parent.toThreeNode(fourNode.keyValue2)
                insert(newParent,key,value)
            }
            is Node.ThreeNode -> {
                val newParent = parent.toFourNode(fourNode.keyValue2)
                insert(newParent,key,value)
            }
            is Node.FourNode  -> throw FourNodeInsertionException()
            null -> root =  TwoNode(fourNode.keyValue2)
                .addLeft(
                    TwoNode(fourNode.keyValue1)
                        .addLeft    (fourNode.left  !!.keyValue1)
                        .addRight   (fourNode.middle!!.keyValue1)
                )
                .addRight(
                    TwoNode(fourNode.keyValue3)
                        .addLeft    (fourNode.middle2!!.keyValue1)
                        .addRight   (fourNode.right  !!.keyValue1)
                )
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

    fun traverse(traversal: Traversal,f: (node:Node<K,V>)  -> Unit)  {
        when(traversal){
            Traversal.Inorder -> inorder(root,f)
        }
    }


    internal fun inorder(node: Node<K, V>?, visit: (node:Node<K,V>)  -> Unit ) {
        val stack = emptyLinkedList<Node<K,V>>()
        var node = node
        while(stack.isNotEmpty() || node !=null){
            if(node.isNotNull()){
                stack.push(node)
                node = node!!.left
            }else{
                node = stack.pop()
                visit(node)
                node = node.right
            }
        }
    }



    internal fun inorder2(node: Node<K, V>?, visit: (node: Node<K, V>) -> Unit) {
        val stack = emptyLinkedList<Node<K,V>>()
        var node  = node
        while(stack.isNotEmpty() || node !=null){
            if(node.isNotNull()){
                stack.push(node)
                node =  when(node){
                    is Node.TwoNode   -> node.left
                    is Node.ThreeNode -> node.middle
                    else -> throw FourNodeException()
                }
            }else{
                node = stack.pop()
                visit(node)
                node = node.right
            }
        }
        }

    fun getInorder(): List<KeyValue<K, V>> {
        val iterative = emptyLinkedList<KeyValue<K,V>>()
        traverse(Traversal.Inorder,{
            when(it){
                is Node.TwoNode   -> iterative.push(it.keyValue1)
                is Node.ThreeNode -> {
                    iterative.push(it.keyValue1)
                    iterative.push(it.keyValue2)
                }
            }
        })
        return iterative
    }

    enum class Traversal{Inorder}

}
class FourNodeInsertionException : Exception ("You shouldn't insert values into four node ")
class FourNodeException          : Exception ("Four node can't be in 23 tree")

fun main(args: Array<String>) {
    val tree = TwoThreeTree<Int,Int>()
}