data class Chmelar(val l:String)

sealed class Node<K : Comparable<K>, V>{
    abstract var keyValue1 : KeyValue<K, V>?
    abstract var keyValue2 : KeyValue<K, V>?
    abstract var left      : Node    <K, V>?
    abstract var right     : Node    <K, V>?
    abstract var middle    : Node    <K, V>?
    abstract var parent    : Node    <K, V>?
    fun isTwoNode()   = keyValue2==null
    fun isThreeNode() = !isTwoNode()
    abstract fun addLeft(node:Node<K,V>)
    abstract fun addRight(node:Node<K,V>)
    abstract fun addMiddle(node:Node<K,V>)

    data class TwoThreeNode<K  : Comparable<K>, V>(
        override var keyValue1 : KeyValue<K, V>? = null,
        override var keyValue2 : KeyValue<K, V>? = null,
        override var left      : Node    <K, V>? = null,
        override var right     : Node    <K, V>? = null,
        override var middle    : Node    <K, V>? = null,
        override var parent    : Node    <K, V>? = null
    )  :Node<K,V>(){
        override fun addLeft(node:Node<K,V>){
            node.parent=this
            left=node
        }
        override fun addRight(node:Node<K,V>){
            node.parent=this
            right=node
        }
        override fun addMiddle(node:Node<K,V>){
            node.parent=this
            middle=node
        }

        override fun toString()= """
            key value1:$keyValue1
            key value2:$keyValue2
            left : $left
            midde: $middle
            right: $right
            parent: ${parent?.keyValue1}
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
        override fun addLeft(node: Node<K, V>) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addRight(node: Node<K, V>) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addMiddle(node: Node<K, V>) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

}






data class KeyValue<K : Comparable<K>, V>(val key: K, var value: V)
infix fun <K : Comparable<K>, V> K.with(value: V) = KeyValue(this, value)
