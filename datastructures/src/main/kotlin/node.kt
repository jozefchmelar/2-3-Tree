package Tree.node

import Tree.FourNodeInsertionException

/*
abstract class Node<K : Comparable<K>, V>(
    var keyValue1: KeyValue<K, V>,
    var left: Node<K, V>?,
    var right: Node<K, V>?,
    var parent: Node<K, V>?
)

class TwoNode<K : Comparable<K>, V>(keyValue1: KeyValue<K, V>, left: Node<K, V>?, right: Node<K, V>?, parent: Node<K, V>?) : Node<K, V>(keyValue1,
    left,
    right,
    parent) {

    override fun toString() = """

            keys       : ${keyValue1.key}
            left       : ${left?.keyValue1?.key},
            right      : ${right?.keyValue1?.key},
            parent     : ${parent?.keyValue1?.key},

            """.trimIndent()
}

open class ThreeNode<K : Comparable<K>, V>(
    keyValue1: KeyValue<K, V>,
    var keyValue2: KeyValue<K, V>,
    left: Node<K, V>?,
    var middle: Node<K, V>?,
    right: Node<K, V>?,
    parent: Node<K, V>?
) : Node<K, V>
(keyValue1,
    left,
    right,
    parent)

class FourNode<K : Comparable<K>, V> : ThreeNode<K, V> {
    constructor(
        keyValue1: KeyValue<K, V>,
        keyValue2: KeyValue<K, V>,
        left: Node<K, V>?,
        middle: Node<K, V>?,
        middle2: Node<K, V>?,
        right: Node<K, V>?,
        parent: Node<K, V>?
    ) : super(keyValue1, keyValue2, middle,
        left,
        right,
        parent)

    override fun toString(): String {
        return """

            keys       : ${keyValue1?.key},  ${keyValue2?.key}, ${keyValue3?.key}
            left       : ${left?.keyValue1?.key}, ${left?.keyValue2?.key}
            middle     : ${middle?.keyValue1?.key}, ${middle?.keyValue2?.key}
            middle2    : ${middle2?.keyValue1?.key}, ${middle2?.keyValue2?.key}
            right      : ${right?.keyValue1?.key}, ${right?.keyValue2?.key}
            parent     : ${parent?.keyValue1?.key}, ${parent?.keyValue2?.key}

            """.trimIndent()
    }
}
*/



sealed class Node<K : Comparable<K>, V> {

    abstract var keyValue1 : KeyValue<K, V>
    abstract var left      : Node<K, V>?
    abstract var right     : Node<K, V>?
    abstract var parent    : Node<K, V>?
    abstract override fun toString(): String
    abstract override fun equals(other: Any?): Boolean

    fun replaceWith(node:Node<K,V>){
        val nodeParent = node.parent
        when(nodeParent){
            is Node.TwoNode -> when {
                this == nodeParent.left     -> nodeParent.addLeft (node)
                this == nodeParent.right    -> nodeParent.addRight(node)
                else                        -> throw IllegalArgumentException("can't replace this node, cause he doesn't belong to any parent ")

            }
            is Node.ThreeNode -> when {
                this == nodeParent.left     -> nodeParent.addLeft(node)
                this == nodeParent.middle   -> nodeParent.addMiddle(node)
                this == nodeParent.right    -> nodeParent.addRight(node)
                else                        -> throw IllegalArgumentException("can't replace this node, cause he doesn't belong to any parent ")

            }
            is Node.FourNode -> throw FourNodeInsertionException()
            null -> TODO()
        }
    }

    fun addLeft(node: Node<K, V>)   : Node<K, V> {
        node.parent = this
        left = node
        return this
    }

    fun isLeaf   () = left == null && right == null
    fun isNotLeaf() = !isLeaf()

    fun clearParent(): Node<K, V> {
        parent=null
        return this
    }

    fun addLeft(keyValue: KeyValue<K,V>) = addLeft(TwoNode(keyValue))

    fun addRight(node: Node<K, V>)  : Node<K, V> {
        node.parent = this
        right = node
        return this
    }

    fun addRight(keyValue: KeyValue<K,V>) = addRight(TwoNode(keyValue))

    fun setAsParent(node: Node<K,V>): Node<K, V> {
        this.parent=node
        return this
    }

    fun isOrphan() = parent == null

    data class TwoNode<K : Comparable<K>, V>(
        override var keyValue1  : KeyValue<K, V>,
        override var left       : Node<K, V>? = null,
        override var right      : Node<K, V>? = null,
        override var parent     : Node<K, V>? = null
    )                           : Node<K, V>()
    {

        fun toThreeNode(toAdd: KeyValue<K, V>) =
            if (toAdd.key > keyValue1.key)
                ThreeNode(
                    this.keyValue1,
                    toAdd,
                    left   = left,
                    middle = right,
                    right  = null,
                    parent = parent
                ).apply {
                    left  ?.parent = this
                    right ?.parent = this
                }
            else
                ThreeNode(
                    toAdd,
                    this.keyValue1,
                    left    = left,
                    middle  = right,
                    right   = null,
                    parent  = parent
                ).apply {
                    left  ?.parent = this
                    right ?.parent = this
                }

        override fun toString(): String = "$keyValue1"

        override fun equals(other: Any?) =
            if (other is TwoNode<*, *>)
                      keyValue1.key == other.keyValue1.key
            else false
    }

    data class ThreeNode<K : Comparable<K>, V>(
        override var keyValue1  : KeyValue<K, V>,
                 var keyValue2  : KeyValue<K, V>,
        override var left       : Node<K, V>? = null,
                 var middle     : Node<K, V>? = null,
        override var right      : Node<K, V>? = null,
        override var parent     : Node<K, V>? = null
    )                           : Node<K, V>() {

        override fun equals(other: Any?) =
            if (other is ThreeNode<*, *>)
                (
                    keyValue1.key   == other.keyValue1.key  &&
                    keyValue2.key   == other.keyValue2.key
                )
            else false


        override fun toString(): String = "$keyValue1, $keyValue2"

        fun addMiddle(node: Node<K, V>): ThreeNode<K, V> {
            node.parent = this
            middle = node
            return this
        }

        fun addMiddle(keyValue: KeyValue<K,V>) = addMiddle(TwoNode(keyValue))
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
                    left  ?.parent = this
                    middle?.parent = this
                    right ?.parent = this
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
                    left  ?.parent = this
                    middle?.parent = this
                    right ?.parent = this
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
                    left  ?.parent = this
                    middle?.parent = this
                    right ?.parent = this
                }

                else -> throw IllegalStateException("Key's can't be equal")
            }
        //endregion
    }

    data class FourNode<K : Comparable<K>, V>(
        override var keyValue1  : KeyValue<K, V>,
                 var keyValue2  : KeyValue<K, V>,
                 var keyValue3  : KeyValue<K, V>,
        override var left       : Node<K, V>? = null,
                 var middle     : Node<K, V>? = null,
                 var middle2    : Node<K, V>? = null,
        override var right      : Node<K, V>? = null,
        override var parent     : Node<K, V>? = null
    ) : Node<K, V>() {

        override fun equals(other: Any?) =
            if (other is FourNode<*, *>)
                (
                    keyValue1.key == other.keyValue1.key &&
                    keyValue2.key == other.keyValue2.key &&
                    keyValue3.key == other.keyValue3.key
                )
            else false

        override fun toString(): String =  "$keyValue1, $keyValue2, $keyValue3"


        fun addMiddle2(node: Node<K, V>): FourNode<K, V> {
            node.parent = this
            middle2     = node
            return this
        }
        fun addMiddle2(keyValue: KeyValue<K,V>) = addMiddle2(TwoNode(keyValue))

        fun addMiddle(node: Node<K, V>): FourNode<K, V> {
            node.parent = this
            middle      = node
            return this
        }

        fun addMiddle(keyValue: KeyValue<K,V>) = addMiddle(TwoNode(keyValue))

        fun split(): TwoNode<K, V> =
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
                ) as TwoNode

    }

}


data class KeyValue<K : Comparable<K>, V>(val key: K, var value: V) {
    override fun toString() = "$key"// $value"
}
//data class KeyValue<K : Comparable<K>, V>(val key: K, var value: V)
infix fun <K : Comparable<K>, V> K.with(value: V) = KeyValue(this, value)
//inline fun <K : Comparable<K>, V> KeyValue<K, V>.asTwoNode() = Node.TwoThreeNode(this)
//inline fun  <K : Comparable<K>, V>  Node.FourNode<K, V>.toTwoNode() = Node.TwoThreeNode(this.keyValue2
//    ,left=this.keyValue1!!.asTwoNode(),right = this.keyValue3!!.asTwoNode())
//inline fun <K: Comparable<K>,V>Node<K,V>.toMutableNode() = TwoThreeTree.MutableNode(this)

