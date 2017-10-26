package extensions

fun <A> A?.isNotNull() = this !=null

fun Int.powerOf(number:Number) : Int = Math.pow(this.toDouble(),number.toDouble()).toInt()

//infix fun <K : Comparable<K>, V> K.with(value: V) = KeyValue(this, value)
infix fun Boolean.and(another:Boolean) = this && another