package extensions

import java.util.*

fun <E> emptyLinkedList() =  LinkedList<E>()
fun <E> MutableList<E>.addIfNotNull(e:E?) = e?.let(this::add)
fun <E> MutableList<E>.addIfNotNull(vararg e:E?) = e.forEach { addIfNotNull(it) }
class Queue <T>(list:MutableList<T>) {

    var items: MutableList<T> = list

    fun isEmpty(): Boolean = this.items.isEmpty()

    fun count(): Int = this.items.count()

    override fun toString() = this.items.toString()

    fun enqueue(element: T) {
        this.items.add(element)
    }

    fun dequeue(): T? {
        if (this.isEmpty()) {
            return null
        } else {
            return this.items.removeAt(0)
        }
    }

    fun peek(): T? {
        return this.items[0]
    }
}