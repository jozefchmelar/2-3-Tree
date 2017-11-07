package Tree.node

import Tree.*
import Tree.Position.*

sealed class Sibling<K : Comparable<K>, V> {
    abstract fun closestSibling():Node<K,V>
    abstract fun threeNodeSibling():Node.ThreeNode<K,V>
    data class TwoSiblings<K : Comparable<K>, V>(
        var first           : Node<K, V>,
        val firstPosition   : Position,
        var second          : Node<K, V>,
        val secondPosition  : Position
    ) : Sibling<K, V>() {

        override fun threeNodeSibling(): Node.ThreeNode<K, V> {
            if (this.hasThreeNodeSibling()) {
                if (first is Node.ThreeNode)
                    return first as Node.ThreeNode
                else if (second is Node.ThreeNode)
                    return second as Node.ThreeNode
                else throw Exception("No ThreeNode sibling")
            } else throw Exception("No ThreeNode sibling")
        }


        override fun closestSibling()=first
    }

    data class OneSibling<K : Comparable<K>, V>(var sibling: Node<K, V>, val side: Position) : Sibling<K , V>() {
        override fun closestSibling() = sibling
        override fun threeNodeSibling(): Node.ThreeNode<K, V> = if (sibling is Node.ThreeNode) sibling as  Node.ThreeNode else throw Exception("No ThreeNode sibling")

    }

    class NoSiblings<K : Comparable<K>,V> : Sibling<K,V>() {
        override fun threeNodeSibling(): Node.ThreeNode<K, V> =  throw Exception("No ThreeNode sibling")
        override fun closestSibling()=throw IllegalStateException("")
    }
}

fun <K : Comparable<K>, V> Sibling<K,V>.hasThreeNodeSibling() : Boolean = when(this){
    is Sibling.TwoSiblings<K,V> -> this.first   is Node.ThreeNode<K, V> || this.second is Node.ThreeNode<K,V>
    is Sibling.OneSibling<K,V>  -> this.sibling is Node.ThreeNode<K,V>
    Sibling.NoSiblings<K,V>()   -> false
    else -> false
}

fun <K : Comparable<K>, V> Sibling<K, V>.twoThreeNodeSiblings() :Boolean = (this is Sibling.TwoSiblings && (this.first is Node.ThreeNode<K, V> && (this.first is Node.ThreeNode<K, V> || this.second is Node.ThreeNode<K, V>)))


fun <K : Comparable<K>, V> Sibling<K,V>.hasNotThreeNodeSibling() : Boolean = !this.hasThreeNodeSibling()



sealed class Node<K : Comparable<K>, V> {

    abstract var keyValue1: KeyValue<K, V>
    abstract var left: Node<K, V>?
    abstract var right: Node<K, V>?
    abstract var parent: Node<K, V>?
    abstract override fun toString(): String
    abstract override fun equals(other: Any?): Boolean
    var deleted: Boolean = false

    fun replaceWith(node: Node<K, V>) {
        val nodeParent = node.parent
        when (nodeParent) {
            is Node.TwoNode -> when {
                this partialyEquals nodeParent.left -> nodeParent.addLeft(node)
                this partialyEquals nodeParent.right -> nodeParent.addRight(node)
                else -> throw IllegalArgumentException("can't replace this node, cause he doesn't belong to any parent ")

            }
            is Node.ThreeNode -> when {
                this partialyEquals nodeParent.left   -> nodeParent.addLeft(node)
                this partialyEquals nodeParent.middle -> nodeParent.addMiddle(node)
                this partialyEquals nodeParent.right  -> nodeParent.addRight(node)
                else -> {
                   if(this is ThreeNode){
                     val l = nodeParent.left
                     val m = nodeParent.middle
                     val r = nodeParent.right
                     when{
                         l is ThreeNode ->{
                            if(this.keyValue1 == l.keyValue1 || this.keyValue2 == l.keyValue2)
                                nodeParent.addLeft(node)
                         }

                         m is ThreeNode -> {
                             if (this.keyValue1 == m.keyValue1 || this.keyValue2 == m.keyValue2)
                                 nodeParent.addMiddle(node)
                         }

                         r is ThreeNode -> {
                             if (this.keyValue1 == r.keyValue1 || this.keyValue2==r.keyValue2)
                                 nodeParent.addRight(node)
                         }

                     }
                   }
                    //throw IllegalArgumentException("can't replace this node, cause he doesn't belong to any parent ")
                }

            }
            is Node.FourNode -> throw FourNodeInsertionException()
            null -> when (node.getPosition()) {
                Left   -> node.parent!!.addLeft(node)
                Middle -> (node.parent as ThreeNode).addMiddle(node)
                Right  -> node.parent!!.addRight(node)
            }
        }
    }


    fun addLeft(node: Node<K, V>): Node<K, V> {
        node.parent = this
        left = node
        return this
    }

    fun isLeaf()  = left == null && right == null

    fun hasKids() = left != null && right != null

    fun clearParent(): Node<K, V> {
        parent = null
        return this
    }

    fun addLeft(keyValue: KeyValue<K, V>) = addLeft(TwoNode(keyValue))

    fun setNewParent(newParent: Node<K, V>): Node<K, V> {
        this.parent = newParent
        return this
    }

    fun addEmptyRight() : Node<K,V>{
        right = null
        return this
    }

    fun addEmptyLeft() : Node<K,V>{
        left = null
        return this
    }
    fun addRight(node: Node<K, V>): Node<K, V> {
        node.parent = this
        right = node
        return this
    }

    fun addRight(keyValue: KeyValue<K, V>) = addRight(TwoNode(keyValue))

    fun getSibling() : Node<K,V>?{
        val siblings = getSiblings()
        if(siblings is Sibling.NoSiblings) return null
        return when(getPosition()){
            Left   -> siblings.closestSibling()
            Middle -> {
                val sibling = siblings as Sibling.TwoSiblings
                if(sibling.first is TwoNode && sibling.second is ThreeNode)
                    sibling.second
                else sibling.first

            }
            Right  -> siblings.closestSibling()
        }
    }

    fun getSiblings(): Sibling<K, V> {

        val parent = this.parent
        return when (parent) {
            is Node.TwoNode -> {
                when {
                    parent.left  == this -> Sibling.OneSibling(parent.right!!, Right)
                    parent.right == this -> Sibling.OneSibling(parent.left!!, Left)
                    else -> throw IllegalStateException("node is not a child of it's parent")
                }
            }
            is Node.ThreeNode -> when {
                parent.left   == this -> Sibling.TwoSiblings(parent.middle!!, Middle, parent.right!!, Right)
                parent.middle == this -> Sibling.TwoSiblings(parent.left!!, Left, parent.right!!, Right)
                parent.right  == this -> Sibling.TwoSiblings(parent.middle!!, Middle, parent.left!!, Left)
                else -> throw IllegalStateException("node is not a child of it's parent")
            }
            is Node.FourNode -> throw FourNodeException()
            null             -> Sibling.NoSiblings()
        }
    }

    fun isOrphan() = parent == null

    data class TwoNode<K : Comparable<K>, V>(
        override var keyValue1: KeyValue<K, V>,
        override var left: Node<K, V>? = null,
        override var right: Node<K, V>? = null,
        override var parent: Node<K, V>? = null
    ) : Node<K, V>() {

        fun toThreeNode(toAdd: KeyValue<K, V>) =
            if (toAdd.key > keyValue1.key)
                ThreeNode(
                    this.keyValue1,
                    toAdd,
                    left = left,
                    middle = right,
                    right = null,
                    parent = parent
                ).apply {
                    left?.parent = this
                    right?.parent = this
                }
            else
                ThreeNode(
                    toAdd,
                    this.keyValue1,
                    left = left,
                    middle = right,
                    right = null,
                    parent = parent
                ).apply {
                    left?.parent = this
                    right?.parent = this
                }

        override fun toString(): String = "$keyValue1"

        override fun equals(other: Any?) =
            if (other is TwoNode<*, *>)
                keyValue1.key == other.keyValue1.key
            else false
    }

    data class ThreeNode<K : Comparable<K>, V>(
        override var keyValue1: KeyValue<K, V>,
        var keyValue2: KeyValue<K, V>,
        override var left: Node<K, V>? = null,
        var middle: Node<K, V>? = null,
        override var right: Node<K, V>? = null,
        override var parent: Node<K, V>? = null
    ) : Node<K, V>() {

        override fun equals(other: Any?) =
            if (other is ThreeNode<*, *>)
                (   keyValue1.key == other.keyValue1.key &&
                    keyValue2.key == other.keyValue2.key)
            else false

        override fun toString(): String = "$keyValue1, $keyValue2"

        fun addMiddle(node: Node<K, V>): ThreeNode<K, V> {
            node.parent = this
            middle = node
            return this
        }

        fun addMiddle(keyValue: KeyValue<K, V>) = addMiddle(TwoNode(keyValue))
        //region too long stuff
        fun toFourNode(toAdd: KeyValue<K, V>) =
            when {
                toAdd.key < keyValue1.key -> FourNode(
                    toAdd,
                    keyValue1,
                    keyValue2,
                    left    = left,
                    middle  = middle,
                    right   = right,
                    middle2 = null,
                    parent  = parent
                ).apply {
                    left?.parent   = this
                    middle?.parent = this
                    right?.parent  = this
                }

                toAdd.key > keyValue1.key && toAdd.key < keyValue2.key -> FourNode(
                    keyValue1,
                    toAdd,
                    keyValue2,
                    left    = left,
                    middle  = middle,
                    right   = right,
                    middle2 = null,
                    parent  = parent
                ).apply {
                    left?.parent   = this
                    middle?.parent = this
                    right?.parent  = this
                }

                toAdd.key > keyValue2.key -> FourNode(
                    keyValue1,
                    keyValue2,
                    toAdd,
                    left    = left,
                    middle  = middle,
                    middle2 = right,
                    right   = null,
                    parent  = parent
                ).apply {
                    left?.parent   = this
                    middle?.parent = this
                    right?.parent  = this
                }

                else -> throw IllegalStateException("Key's can't be equal")
            }
        //endregion
    }

    data class FourNode<K : Comparable<K>, V>(
        override var keyValue1: KeyValue<K, V>,
        var keyValue2: KeyValue<K, V>,
        var keyValue3: KeyValue<K, V>,
        override var left: Node<K, V>? = null,
        var middle: Node<K, V>? = null,
        var middle2: Node<K, V>? = null,
        override var right: Node<K, V>? = null,
        override var parent: Node<K, V>? = null
    ) : Node<K, V>() {

        override fun equals(other: Any?) =
            if (other is FourNode<*, *>)
                (       keyValue1.key == other.keyValue1.key &&
                        keyValue2.key == other.keyValue2.key &&
                        keyValue3.key == other.keyValue3.key)
            else false

        override fun toString(): String = "$keyValue1, $keyValue2, $keyValue3"


        fun addMiddle2(node: Node<K, V>): FourNode<K, V> {
            node.parent = this
            middle2 = node
            return this
        }

        fun addMiddle2(keyValue: KeyValue<K, V>) = addMiddle2(TwoNode(keyValue))

        fun addMiddle(node: Node<K, V>): FourNode<K, V> {
            node.parent = this
            middle = node
            return this
        }

        fun addMiddle(keyValue: KeyValue<K, V>) = addMiddle(TwoNode(keyValue))

        fun split(): TwoNode<K, V> =
            TwoNode(this.keyValue2)
                .addLeft(
                    TwoNode(this.keyValue1)
                        .addLeft(this.left!!)
                        .addRight(this.middle!!)
                )
                .addRight(
                    TwoNode(this.keyValue3)
                        .addLeft(this.middle2!!)
                        .addRight(this.right!!)
                ) as TwoNode

    }
}

data class KeyValue<K : Comparable<K>, V>(val key: K, var value: V) {
    override fun toString() = "$key"// $value"
}

infix fun <K : Comparable<K>, V> K.with(value: V) = KeyValue(this, value)

