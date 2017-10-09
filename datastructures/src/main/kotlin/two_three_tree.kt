import Node.FourNode




class TwoThreeTree<K:Comparable<K>,V>:Map<K,V> {
    internal var root : Node<K,V>? = null

    override fun put(key: K, value: V): V? {
       if(root==null){
           root= Node.TwoThreeNode(key with value)
           return value
       }
        else {
           val foundNode = getNode(key,root!!)
           if(foundNode.isTwoNode()){
               when{
                   key < foundNode.keyValue1!! -> {
                      foundNode.keyValue2 = foundNode.keyValue1
                      foundNode.keyValue1 = key with value
                       return value
                   }
                   key > foundNode.keyValue1!! -> {
                       foundNode.keyValue2 = key with value
                       return value
                   }
               }
           }else if(foundNode.isThreeNode()){
               var fourNode : FourNode<K,V>? = null
                when{
                    key < foundNode.keyValue1!!                                 -> fourNode = FourNode(key with value, foundNode.keyValue1, foundNode.keyValue2)
                    key > foundNode.keyValue1!! && key < foundNode.keyValue2!!  -> fourNode = FourNode(foundNode.keyValue1, key with value , foundNode.keyValue2)
                    key > foundNode.keyValue2!!                                 -> fourNode = FourNode(foundNode.keyValue1, foundNode.keyValue2, key with value)
                }

               insertFourNode(fourNode,foundNode)
            return null
           }
       }
        return null
    }

    private fun insertFourNode(fourNode: FourNode<K, V>?, foundNode: Node<K, V>) {
        when{
            root == null -> {
                root =                  Node.TwoThreeNode(fourNode!!.keyValue2,
                    left = Node.TwoThreeNode(fourNode.keyValue1),right = Node.TwoThreeNode(fourNode.keyValue3)
                )
            }
            foundNode==root ->{
                root =  Node.TwoThreeNode(fourNode!!.keyValue2)
                root?.addLeft (Node.TwoThreeNode(fourNode.keyValue1))
                root?.addRight(Node.TwoThreeNode(fourNode.keyValue3))

            }
            foundNode.parent!!.isTwoNode() && foundNode.isThreeNode() ->{

            }
                foundNode.parent!!.isTwoNode() ->{
                var node = Node.TwoThreeNode<K,V>()
                when{
                    fourNode!!.keyValue2!!.key < foundNode.parent!!.keyValue1!! ->
                        node = Node.TwoThreeNode(fourNode.keyValue2,foundNode.parent!!.keyValue1)
                    foundNode.keyValue2!!.key  > foundNode.parent!!.keyValue1!! ->
                        node = Node.TwoThreeNode(foundNode.parent!!.keyValue1,fourNode.keyValue2)
                }
                with(node){
                    left   = Node.TwoThreeNode(fourNode!!.keyValue1)
                    middle = Node.TwoThreeNode(fourNode  .keyValue3)
                    right  = foundNode.parent!!.right
                foundNode.parent=node
                }

            }
        }
    }

    private operator fun K.compareTo(keyValue: KeyValue<K,V>) =  this.compareTo(keyValue.key)

    internal fun getNode(key: K, startNode: Node<K, V>): Node<K, V> {
        return when {
            startNode.left == null            -> startNode
            startNode.keyValue1!!.key == key  -> startNode
            key < startNode.keyValue1!!.key   -> getNode(key, startNode.left!!)
            startNode.isTwoNode()             -> getNode(key, startNode.middle!!)
            startNode.isThreeNode()           -> when {
                key == startNode.keyValue2          -> startNode
                key > startNode.keyValue2!!.key     -> getNode(key, startNode.right!!)
                key > startNode.keyValue1!!.key
                    &&
                    key < startNode.keyValue2!!.key -> getNode(key, startNode.middle!!)
                else -> throw IllegalStateException("wat")
            }

            else -> throw IllegalStateException("wat")
        }
    }
    override fun get(byKey: K): V? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(key: K) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}