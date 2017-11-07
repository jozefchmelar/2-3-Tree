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

class MySet<T:Comparable<T>>(list:MutableList<T>){

    var items: MutableList<T>  = list

    fun insert(element:T) {
        if (exists(element)) {
            return
        }
        for (i in 0..this.items.count() - 1){
            if (this.items[i] > element){
                this.items.add(i, element)
                return
            }
        }
        this.items.add(element)
    }


    fun findElementPosition(element:T):Int?{
        var rangeStart = 0
        var rangeEnd = this.items.count()
        while (rangeStart < rangeEnd) {
            val midIndex = rangeStart + (rangeEnd - rangeStart)/2
            if (this.items[midIndex] == element) {
                return midIndex
            } else if (this.items[midIndex] < element){
                rangeStart = midIndex + 1
            } else {
                rangeEnd = midIndex
            }
        }
        return null
    }

    override fun toString():String = this.items.toString()

    fun isEmpty():Boolean = this.items.isEmpty()

    fun exists(element:T):Boolean = findElementPosition(element) != null

    fun count():Int = this.items.count()

    fun remove(element:T) {
        val position = findElementPosition(element)
        if (position != null) {
            this.items.removeAt(position)
        }
    }

    fun removeAll() =  this.items.removeAll(this.items)

    fun max():T? {
        if (count() != 0) {
            return this.items[count() - 1]
        } else {
            return null
        }
    }
    fun min():T? {
        if (count() != 0) {
            return this.items[0]
        } else {
            return null
        }
    }
}
