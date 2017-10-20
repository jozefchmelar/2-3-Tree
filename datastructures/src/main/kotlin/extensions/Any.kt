package extensions

fun <A> A?.isNotNull() = this !=null

fun Int.powerOf(number:Number) : Int = Math.pow(this.toDouble(),number.toDouble()).toInt()