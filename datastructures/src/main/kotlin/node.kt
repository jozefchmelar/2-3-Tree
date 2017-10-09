data class Chmelar(val l:String)

sealed class Node<K : Comparable<K>, V>{
    abstract var keyValue1 : KeyValue<K, V>?
    abstract var keyValue2 : KeyValue<K, V>?
    abstract var left      : Node    <K, V>?
    abstract var right     : Node    <K, V>?
    abstract var middle    : Node    <K, V>?
    abstract var parent    : Node    <K, V>?

    fun addLeft(node:Node<K,V>){
        node.parent=this
        left=node
    }

    fun addRight(node:Node<K,V>){
        node.parent=this
        right=node
    }

     fun addMiddle(node:Node<K,V>){
        node.parent=this
        middle=node
    }
    abstract fun addToNode(keyValue2: KeyValue<K,V>): V

    fun isTwoNode()   = keyValue2==null
    fun isThreeNode() = !isTwoNode()
    

    data class TwoThreeNode<K  : Comparable<K>, V>(
        override var keyValue1 : KeyValue<K, V>? = null,
        override var keyValue2 : KeyValue<K, V>? = null,
        override var left      : Node    <K, V>? = null,
        override var right     : Node    <K, V>? = null,
        override var middle    : Node    <K, V>? = null,
        override var parent    : Node    <K, V>? = null
    )  :Node<K,V>(){

        override fun addToNode(toAdd: KeyValue<K, V>): V {
            if (toAdd.key > keyValue1!!.key)
                keyValue2 = toAdd
            else {
                keyValue2 = keyValue1
                keyValue1 = toAdd
            }
            return toAdd.value
        }

        override fun toString()= """

            keys       : ${keyValue1?.key},  ${keyValue2?.key}
            left       : ${left?.keyValue1?.  key}, ${left?.keyValue2?.key}
            middle     : ${middle?.keyValue1?.key}, ${middle?.keyValue2?.key}
            right      : ${right?.keyValue1?. key}, ${right?.keyValue2?.key}
            parent     : ${parent?.keyValue1?.key}, ${parent?.keyValue2?.key}

            """.trimIndent()

        override fun equals(other: Any?): Boolean {
            return when(other){
                is TwoThreeNode<*, *> ->
                    (
                        keyValue1 == other.keyValue1 &&
                            keyValue2 == other.keyValue2 &&
                            left == other.left &&
                            right == other.right &&
                            middle == other.middle &&
                            parent?.keyValue1 == other.parent?.keyValue1 &&
                            if(parent !=null && other.parent!=null ){
                                parent!!.keyValue1==other.parent!!.keyValue1 &&
                                parent!!.keyValue2==other.parent!!.keyValue2
                            }else true

                     )
                else -> false
            }
        }

    }

    data class FourNode <K  : Comparable<K>, V>(
        override var keyValue1 : KeyValue<K, V>? = null,
        override var keyValue2 : KeyValue<K, V>? = null,
                 var keyValue3 : KeyValue<K, V>? = null,
        override var left      : Node    <K, V>? = null,
        override var right     : Node    <K, V>? = null,
        override var middle    : Node    <K, V>? = null,
                 var middle2   : Node    <K, V>? = null,
        override var parent    : Node    <K, V>? = null
    ) :Node<K,V>() {

        override fun addToNode(keyValue2: KeyValue<K, V>)  = throw IllegalStateException("cant add do four node")

        fun addMiddle2(node:Node<K,V>){
            node.parent=this
            middle2=node
        }

        override fun toString()= """

            keys       : ${keyValue1?.key},  ${keyValue2?.key}, ${keyValue3?.key}
            left       : ${left?.keyValue1?.  key}, ${left?.keyValue2?.key}
            middle     : ${middle?.keyValue1?.key}, ${middle?.keyValue2?.key}
            middle2    : ${middle2?.keyValue1?.key}, ${middle2?.keyValue2?.key}
            right      : ${right?.keyValue1?. key}, ${right?.keyValue2?.key}
            parent     : ${parent?.keyValue1?.key}, ${parent?.keyValue2?.key}

            """.trimIndent()
    }

}






data class KeyValue<K : Comparable<K>, V>(val key: K, var value: V)
infix fun <K : Comparable<K>, V> K.with(value: V) = KeyValue(this, value)
inline fun <K : Comparable<K>, V> KeyValue<K, V>.asTwoNode() = Node.TwoThreeNode(this)
