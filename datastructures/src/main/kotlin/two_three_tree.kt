//import Node.FourNode
//
//class TwoThreeTree<K : Comparable<K>, V> : Map<K, V> {
//    internal var root: MutableNode<K, V>? = null
//
//
//    data class MutableNode<K: Comparable<K>, V> (var value :Node<K,V>?){
////        override fun equals(other: Any?): Boolean {
////            return if(other is MutableNode<*,*>) value!! == other else false
////        }
//    }
//
//    override fun put(key: K, value: V): V? {
//        if (root == null) {
//            root = Node.TwoThreeNode(key with value).toMutableNode()
//            return value
//        } else {
//            var foundNode = getNode(key, root!!).value
//            if (foundNode!!.isTwoNode())
//                return foundNode.addToNode(key with value)
//            else if (foundNode.isThreeNode())
//                  insertFourNode(foundNode.toFourNode(key with value), foundNode.toMutableNode())
//        }
//        return value
//    }
//
//    private fun insertFourNode(fourNode: FourNode<K, V>?, _foundNode: MutableNode<K, V>) {
//        var foundNode = _foundNode.value
//
//        when {
//            root == null -> {
//                root = Node.TwoThreeNode(fourNode!!.keyValue2,
//                    left = Node.TwoThreeNode(fourNode.keyValue1), right = Node.TwoThreeNode(fourNode.keyValue3)
//                ).toMutableNode()
//            }
//
//            foundNode == root -> {
//
//                root = Node.TwoThreeNode(fourNode!!.keyValue2).apply {
//                    addLeft (fourNode.keyValue1!!.asTwoNode())
//                    addRight(fourNode.keyValue3!!.asTwoNode())
//                }.toMutableNode()
//            }
//
//            foundNode!!.isThreeNode() && foundNode.isOrphan() -> {
//
//            }
//
//            /*                   two node parent                      */
//            foundNode!!.isThreeNode() && foundNode.parent?.isTwoNode() ?: false -> {
//                //check if i'm going to add from left node or right node
//                if (foundNode.parent?.left == foundNode) {
//                    foundNode.parent?.addToNode(fourNode?.keyValue2!!)
//                    with(foundNode.parent!!){
//                        addMiddle (fourNode?.keyValue3!!.asTwoNode())
//                        addLeft   (fourNode .keyValue1!!.asTwoNode())
//                    }
//                } else if (foundNode.parent?.middle == foundNode) { // middle is Right if TwoNode
//                    foundNode.parent?.addToNode(fourNode?.keyValue2!!)
//                    with(foundNode.parent!!){
//                        addMiddle (fourNode?.keyValue1!!.asTwoNode())
//                        addRight  (fourNode .keyValue3!!.asTwoNode())
//                    }
//                }
//
//            }
//
//        /*                  three node parent                      */
//            foundNode.isThreeNode() && foundNode.parent?.isThreeNode() ?: false -> {
//                //let's start from the left side
//                when  {
//                    foundNode == foundNode.parent?.left -> {
//
//                        var te =foundNode.parent?.toFourNode(fourNode?.keyValue2!!)
//                        with(te as Node.FourNode){
//                            addLeft    (fourNode!!.keyValue1!!.asTwoNode())
//                            addMiddle  (fourNode  .keyValue3!!.asTwoNode())
//                            addMiddle2 (foundNode.parent?.middle!!)
//                            addRight   (foundNode.parent?.right !!)
//                        }
//
//                        var splitThreeParent = te.toTwoNode()
//                        with(splitThreeParent){
//                            left?.addLeft  (te.left  !!)
//                            left?.addRight (te.middle!!)
//                            right?.addLeft (te.middle2!!)
//                            right?.addRight(te.right!!)
//                        }
//                        foundNode.setAsParent(null)
//                    }
//
//                    foundNode == foundNode.parent?.middle -> {
//                        val parentFourNode = foundNode.parent?.toFourNode(fourNode?.keyValue2!!)
//                        with(parentFourNode as Node.FourNode){
//                            addLeft    (foundNode.parent?.left !!)
//                            addMiddle  (fourNode!!.keyValue1!!.asTwoNode())
//                            addMiddle2 (fourNode  .keyValue3!!.asTwoNode())
//                            addRight   (foundNode.parent?.right !!)
//                        }
//                        println("middle")
//
//                        foundNode.parent = parentFourNode
//                    }
//                    foundNode == foundNode.parent?.right -> {
//                        val parentFourNode = foundNode.parent?.toFourNode(fourNode?.keyValue2!!)
//                        with(parentFourNode as Node.FourNode){
//                            addLeft    (fourNode!!.keyValue1!!.asTwoNode())
//                            addMiddle  (fourNode  .keyValue3!!.asTwoNode())
//                            addMiddle2 (foundNode.parent?.middle!!)
//                            addRight   (foundNode.parent?.right !!)
//                        }
//                        foundNode.parent = parentFourNode
//                        println("right")
//
//                    }
//                }
//            }
//
//            foundNode.parent?.isTwoNode() ?: false -> {
//                var node = Node.TwoThreeNode<K, V>()
//                when {
//                    fourNode!!.keyValue2!!.key < foundNode.parent!!.keyValue1!! ->
//                        node = Node.TwoThreeNode(fourNode.keyValue2, foundNode.parent!!.keyValue1)
//                    foundNode.keyValue2!!.key > foundNode.parent!!.keyValue1!! ->
//                        node = Node.TwoThreeNode(foundNode.parent!!.keyValue1, fourNode.keyValue2)
//                }
//
//                with(node) {
//                    left   = fourNode!!.keyValue1?.asTwoNode()
//                    middle = fourNode  .keyValue3?.asTwoNode()
//                    right  = foundNode.parent!!.right
//                    foundNode.parent = node
//                }
//
//            }
//        }
//    }
//
//    private operator fun K.compareTo(keyValue: KeyValue<K, V>) = this.compareTo(keyValue.key)
//
//    private fun Node<K , V>.toFourNode(key: KeyValue<K, V>) = if(isThreeNode()){
//        when {
//            key.key < this.keyValue1!! ->  FourNode(key, this.keyValue1, this.keyValue2)
//            key.key > this.keyValue1!! && key.key < this.keyValue2!! ->  FourNode(this.keyValue1, key, this.keyValue2)
//            key.key > this.keyValue2!! -> FourNode(this.keyValue1, this.keyValue2, key)
//            else -> throw IllegalStateException("can't make four node")
//        }
//    } else throw IllegalStateException("can't make four node from two node")
//
//
////    internal fun getNode(key:K):Node <K,V> = getNode(key,root!!)
//
//
//    internal fun getNode(key: K, _startNode: MutableNode<K, V>): MutableNode<K, V> {
//        var startNode=_startNode.value!!
//         return when {
//            startNode.left == null -> startNode.toMutableNode()
//            startNode.keyValue1!!.key == key -> startNode.toMutableNode()
//            key < startNode.keyValue1!!.key -> getNode(key, startNode.left!!.toMutableNode())
//            startNode.isTwoNode() -> getNode(key, startNode.middle!!.toMutableNode())
//            startNode.isThreeNode() -> when {
//                key == startNode.keyValue2 -> startNode.toMutableNode()
//                key > startNode.keyValue2!!.key -> getNode(key, startNode.right!!.toMutableNode())
//                key > startNode.keyValue1!!.key
//                    &&
//                    key < startNode.keyValue2!!.key -> getNode(key, startNode.middle!!.toMutableNode())
//                else -> throw IllegalStateException("wat")
//            }
//
//            else -> throw IllegalStateException("wat")
//        }
//    }
//
//    override fun get(byKey: K): V? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun remove(key: K) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
//
//
