import java.util.*

interface Map<K,V>{
fun get(byKey:K) : V?

fun put(key:K,value:V) : V?

fun remove(key:K)
}