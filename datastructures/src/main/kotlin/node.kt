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

    data class TwoThreeNode<K  : Comparable<K>, V>(
        override var keyValue1 : KeyValue<K, V>? = null,
        override var keyValue2 : KeyValue<K, V>? = null,
        override var left      : Node    <K, V>? = null,
        override var right     : Node    <K, V>? = null,
        override var middle    : Node    <K, V>? = null,
        override var parent    : Node    <K, V>? = null
    )  :Node<K,V>()

    data class FourNode <K  : Comparable<K>, V>(
        override var keyValue1 : KeyValue<K, V>? = null,
        override var keyValue2 : KeyValue<K, V>? = null,
                 var keyValue3 : KeyValue<K, V>? = null,
        override var left      : Node    <K, V>? = null,
        override var right     : Node    <K, V>? = null,
        override var middle    : Node    <K, V>? = null,
                 var middle2   : Node    <K, V>? = null,
        override var parent    : Node    <K, V>? = null
    ) :Node<K,V>()
}






data class KeyValue<K : Comparable<K>, V>(val key: K, var value: V)
infix fun <K : Comparable<K>, V> K.with(value: V) = KeyValue(this, value)
