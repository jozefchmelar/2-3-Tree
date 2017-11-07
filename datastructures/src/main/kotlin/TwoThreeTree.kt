package Tree
import Tree.Position.*
import Tree.node.*
import Tree.node.Node.*
import extensions.MySet
import extensions.Queue
import extensions.emptyLinkedList
import org.apache.http.annotation.Experimental
import java.util.*

data class HablaTest(val id:String,val habla:String="gsdkiogksdopgksdopgksdopgksd") : Comparable<HablaTest> {
    override fun equals(other: Any?): Boolean {
        return (other is HablaTest) && other.id == id
    }
    //Compares this object with the specified object for order.
    // Returns zero if this object is equal to the specified other object,
    // a negative number if it's less than other, or a positive number if it's greater than other.


    override fun compareTo(other: HablaTest) :Int{ when {
        id == other.id -> return  0
        id < other.id  -> return -1
        id > other.id  -> return  1
        else -> TODO()
    }
    }
}

class TwoThreeTree<K:Comparable<K>,V>  {
    internal var root : Node<K,V>? = null

    //for testing purposes only
    val insertedKeys = emptyLinkedList<K>()
    var keylist = emptyLinkedList<K>()
    val keySet = MySet(keylist)
    fun get(key: K ) : V?{
        val found = find(key)
        return when(found){
            is Node.TwoNode   -> found.keyValue1.value
            is Node.ThreeNode -> if(key > found.keyValue1.key) found.keyValue2.value else found.keyValue1.value
            else  -> null
        }
    }
    fun put(key:K,value:V) : Boolean{
        if(keySet.exists(key)) return false
        if(root ==null) {
            root = TwoNode(key with value)
            insertedKeys.add(key)
            keySet.insert(key)
            return true
        }

        val foundNode = getNode(key)

        when(foundNode){
            is TwoNode   -> insert(foundNode,key,value)
            is ThreeNode -> insert(foundNode,key,value)
            else         -> throw IllegalStateException("Node to insert value to was not found")
        }

        insertedKeys.add(key)
        keySet.insert(key)
        return true

    }


    private fun swapKeys(deleteNode: Node<K, V>, inorderSuccesor: Node<K, V>, key: K) {

        if (deleteNode is ThreeNode && deleteNode.keyValue2.key == key) {
            val delNode = deleteNode.keyValue2
            val inoNode = inorderSuccesor.keyValue1
            inorderSuccesor.keyValue1=delNode
            deleteNode.keyValue2=inoNode
        }else{
            val delNode = deleteNode.keyValue1
            val inoNode = inorderSuccesor.keyValue1
            inorderSuccesor.keyValue1=delNode
            deleteNode.keyValue1=inoNode
        }

    }

    fun delete(key: K): Boolean {
        keySet.remove(key)
        insertedKeys.remove(key)
        if(root==null) return false
        //  Locate node n, which contains item I
        val deleteNode = find(key) ?: return false
        if (deleteNode == root && deleteNode.isLeaf()) {
            val r = root
            when (r) {
                is Node.TwoNode -> {
                    root = null
                    return true
                }
                is Node.ThreeNode -> {
                    if (key == r.keyValue1.key)
                        root = TwoNode(r.keyValue2)
                    else root = TwoNode(r.keyValue1)
                    return true
                }
            }
        }
        // if delete node is leaf three node just delete the key from it
        if (deleteNode.isLeaf()) {
            if (deleteNode is ThreeNode) {
                deleteNode.deleteFromNode(key)
                return true
            } else if (deleteNode is TwoNode) {
                if (deleteFromLeaf(deleteNode, deleteNode.parent,key)) {
                    return true
                }
            }
        }

        // 2. If node n is not a leaf  swap I with inorder successor
        val inorderSuccessorNode = inoredSuccesor(deleteNode, key) ?:
            throw Exception("successor not found ")
        val inosKv = inorderSuccessorNode.keyValue1
        if(!deleteNode.isLeaf())
            swapKeys(deleteNode, inorderSuccessorNode, key)
        val afterswap = inorderSuccessorNode.keyValue1

        //If leaf node n contains another item, just delete item I
        if (inorderSuccessorNode is ThreeNode && inorderSuccessorNode.isLeaf()) {
            inorderSuccessorNode.deleteFromNode(afterswap) //afterswap ....tu bol pred tym
            return true
        } else {

            var currentDeleteNode: Node<K, V> = if(deleteNode.isLeaf()) deleteNode else if(inorderSuccessorNode.isLeaf()) inorderSuccessorNode
            else TODO()
            var parentless   : Node<K,V>?     = null
            //var i = 0
            while (true) {
            //    if (i++ > 40) throw Exception("loop")
                val sibling = currentDeleteNode.getSibling()
                val parent  = currentDeleteNode.parent

                when (sibling) {
                //try to redistribute nodes from siblings
                    is Node.ThreeNode -> {
                        redistribute(currentDeleteNode, sibling, parent,parentless)
                        return true
                    }
                //if not possible, merge node (see next slide)
                    is Node.TwoNode -> {
                        when (parent) {
                            is Node.TwoNode -> {
                               if(currentDeleteNode.isLeaf()){
                                   parentless        = leafMerge(currentDeleteNode,parent)
                                   currentDeleteNode = parent
                               } else {
                                   parentless = internalMerge(sibling,parent,parentless!!)
                                   currentDeleteNode = parent
                               }
                            }
                            is Node.ThreeNode -> {
                                when (currentDeleteNode.getPosition()) {
                                    Left  -> {
                                        val newThree = sibling
                                            .toThreeNode(parent.keyValue1)
                                            .apply {
                                                sibling.right?.let { addRight  (it) }
                                                sibling.left ?.let { addMiddle (it) }
                                                parentless   ?.let { addLeft   (it) }
                                            }
                                        parent.addLeft(newThree)
                                        parent.right?.let { parent.addMiddle(it) }
                                        val nl = parent.left
                                        val nr = parent.middle
                                        if(parent!=root)
                                            parent.deleteFromNode(parent.keyValue1,nl,nr)
                                        else
                                        {
                                            root = TwoNode((root!! as ThreeNode).keyValue2)
                                                .addLeft(newThree)
                                                .addRight(root!!.right!!)
                                            return true
                                        }
                                        return true
                                    }

                                    Middle -> {

                                            val newThree = sibling
                                                .toThreeNode(parent.keyValue1)
                                                .apply {
                                                    sibling.right?.let { addMiddle  (it) }
                                                    sibling.left ?.let { addLeft    (it) }
                                                    parentless   ?.let { addRight   (it) }
                                                }
                                            parent.addLeft(newThree)
                                            parent.right?.let { parent.addMiddle(it) }
                                        val nl = parent.left
                                        val nr = parent.middle
                                            if(parent!=root)
                                                parent.deleteFromNode(parent.keyValue1,nl,nr)
                                            else
                                            {
                                                root = TwoNode((root!! as ThreeNode).keyValue2)
                                                    .addLeft(newThree)
                                                    .addRight(root!!.right!!)
                                                return true
                                            }
                                            return true

                                    }

                                    Right -> {
                                        val newThree = sibling
                                            .toThreeNode(parent.keyValue2)
                                            .apply {
                                                sibling.right?.let { addMiddle  (it) }
                                                sibling.left ?.let { addLeft    (it) }
                                                parentless   ?.let { addRight   (it) }
                                            }
                                        parent.addRight(newThree)
                                        parent.left?.let { parent.addMiddle(it) }

                                        if(parent!=root)
                                            parent.deleteFromNode(parent.keyValue2,parent.middle,newThree)
                                        else{
                                            root = TwoNode((root!! as ThreeNode).keyValue1)
                                                .addRight(newThree)
                                                .addLeft(root!!.left!!)
                                            return true
                                        }
                                        return true
                                    }

                                }
                            }
                            null -> TODO()
                        }
                    }
                    null -> {

                        if(currentDeleteNode==root){
                            root = parentless?.clearParent()
                            return true
                        }
                        else{
                            TODO()
                        }

                    }
                }
            }
        }

    }

    fun inInterval(min:K,max:K): List<V> {
            val res = mutableListOf<V>()
        intervalSearch(min,max){
            when(it){
                is Node.TwoNode   -> res.add(it.keyValue1.value)
                is Node.ThreeNode -> {
                    res.add(it.keyValue1.value)
                    res.add(it.keyValue2.value)
                }

            }
        }
        return res
    }

    private fun intervalSearch(min: K, max: K, f: (Node<K, V>?) -> Unit): List<KeyValue<K, V>> {
        var actual: Node<K, V>? = root
        val queue = Queue<Node<K, V>?>(emptyLinkedList())
        val nodes = emptyLinkedList<KeyValue<K, V>>()
        queue.enqueue(actual)
        while (true) {
            if (queue.count() > 0)
                actual = queue.dequeue()
            else
                return nodes

            if (actual
                is Node.TwoNode) {
                if (actual.keyValue1.key in min..max) {
                    nodes.addLast(actual.keyValue1)
                    f(actual)
                    if (actual.hasKids()) {
                        queue.enqueue(actual.left)
                        queue.enqueue(actual.right)
                    }
                } else {
                    //actuale key < min don't go left
                    //if actual key > max
                    if (actual.isLeaf()) {
                        if (actual.keyValue1.key > min) {
                            queue.enqueue(actual.left)
                        }
                        if (actual.keyValue1.key > max) {
                            queue.enqueue(actual.right)
                        }
                    }
                }
            } else if (actual is Node.ThreeNode)
            {
                if (actual.keyValue1.key in min..max)
                {
                    nodes.addLast(actual.keyValue1)
                    f(actual)
                    if (actual.hasKids())
                        queue.enqueue(actual.left)

                }
                else
                {
                    //i'lltake left one only if if key > min
                    if (actual.hasKids())
                        if (actual.keyValue1.key > min)
                            queue.enqueue(actual.left)

                }
                if (actual.keyValue2.key in min..max) {
                    nodes.addLast(actual.keyValue2)
                    f(actual)
                    if (actual.hasKids())
                        queue.enqueue(actual.right)

                }
                else
                {
                    if(actual.hasKids())
                        if(actual.keyValue2.key > max)
                            queue.enqueue(actual.right)
                }
                if(actual.hasKids()){
                    queue.enqueue(actual.middle)
                }
            }
        }
    }
    private fun redistribute(deleteNode: Node<K, V>, threeNodeSibling: Node.ThreeNode<K, V>, parent: Node<K, V>?,parentLess:Node<K,V>?) = when(parent){
        is Node.TwoNode   -> redistribute(deleteNode, threeNodeSibling, parent,parentLess)
        is Node.ThreeNode -> redistribute(deleteNode, threeNodeSibling, parent,parentLess)
        is Node.FourNode  -> throw FourNodeException()
        null              -> TODO()
    }
    private fun redistribute(deleteNode: Node<K, V>, threeNodeSibling: Node.ThreeNode<K, V>, parent: Node.ThreeNode<K, V>,parentLess:Node<K,V>?) {
        when(deleteNode.getPosition()) {
            Left -> {
                val p = parent.keyValue1
                val s = threeNodeSibling.keyValue1
                deleteNode.keyValue1 = p
                parent.keyValue1 = s
                if (parentLess==null)
                threeNodeSibling.deleteFromNode(s)
                else{
                    val sl = threeNodeSibling.left!!
                    val sm = threeNodeSibling.middle!!
                    val sr = threeNodeSibling.right!!
                    deleteNode
                        .addLeft(parentLess)
                        .addRight(sl)
                    threeNodeSibling.deleteFromNode(s,sm,sr)

                }
            }
            Middle -> {
                when (threeNodeSibling.getPosition()) {
                    Left -> {
                        val p = parent.keyValue1
                        val s = threeNodeSibling.keyValue2
                        deleteNode.keyValue1 = p
                        parent.keyValue1 = s
                        if(parentLess==null)
                            threeNodeSibling.deleteFromNode(s)
                        else{
                            val sl = threeNodeSibling.left!!
                            val sm = threeNodeSibling.middle!!
                            val sr = threeNodeSibling.right!!
                            deleteNode
                                .addRight(parentLess)
                                .addLeft(sr)
                            threeNodeSibling.deleteFromNode(s,sl,sm)

                        }
                    }
                    Middle -> throw IllegalStateException("middle node wont have middle sibling")
                    Right -> {
                        val p = parent.keyValue2
                        val s = threeNodeSibling.keyValue1
                        val sl = threeNodeSibling.left
                        val sm = threeNodeSibling.middle
                        val sr = threeNodeSibling.right
                        deleteNode.keyValue1 = p
                        parent.keyValue2 = s
                        if(parentLess==null)
                        threeNodeSibling.deleteFromNode(s)
                        else
                        {
                            deleteNode
                                .addLeft(parentLess)
                                .addRight(sl!!)
                            threeNodeSibling.deleteFromNode(s,sm!!,sr!!)

                        }
                    }
                }
            }
            Right -> {
                val p = parent.keyValue2
                val s = threeNodeSibling.keyValue2
                deleteNode.keyValue1 = p
                parent.keyValue2 = s
                if(parentLess==null)
                 threeNodeSibling.deleteFromNode(s)
                else{
                    val sl = threeNodeSibling.left!!
                    val sm = threeNodeSibling.middle!!
                    val sr = threeNodeSibling.right!!
                    deleteNode
                        .addRight(parentLess)
                        .addLeft(sr)
                    threeNodeSibling.deleteFromNode(s,sl,sm)

                }
            }
        }
    }

    private fun redistribute(deleteNode: Node<K, V>, threeNodeSibling: Node.ThreeNode<K, V>, parent: Node.TwoNode<K, V>,parentLess:Node<K,V>?) {
        val p = parent.keyValue1
        when(deleteNode.getPosition()){
            Left -> {
                val s  = threeNodeSibling.keyValue1
                val sl = threeNodeSibling.left
                val sm = threeNodeSibling.middle
                val sr = threeNodeSibling.right
                parent.keyValue1     = s
                deleteNode.keyValue1 = p
                sr?.let { deleteNode.addLeft(it) }
                if(parentLess==null)
                    threeNodeSibling.deleteFromNode(s)
                else
                {
                    deleteNode.apply {
                        addLeft(parentLess)
                        addRight(sl!!)
                    }
                    threeNodeSibling.deleteFromNode(s,sm!!,sr!!)

                }

            }
            Right -> {
                val s = threeNodeSibling.keyValue2
                val sl = threeNodeSibling.left
                val sm = threeNodeSibling.middle
                val sr = threeNodeSibling.right
                parent.keyValue1     = s
                deleteNode.keyValue1 = p
                if(parentLess==null)
                    threeNodeSibling.deleteFromNode(s)
                else{
                    deleteNode.apply {
                        addRight(parentLess)
                        addLeft(sr!!)
                    }
                    threeNodeSibling.deleteFromNode(s,sl!!,sm!!)

                }
            }
            Middle -> throw Exception("two node parent with middle child?")
        }
    }

    private fun deleteFromLeaf(deleteNode: TwoNode<K, V>, parent: Node<K, V>?,key:K): Boolean = when(parent){
        is Node.TwoNode   -> deleteFromLeaf(deleteNode,parent,key)
        is Node.ThreeNode -> deleteFromLeaf(deleteNode,parent,key)
        is Node.FourNode  -> throw FourNodeException()
        null -> TODO()
    }
    private fun deleteFromLeaf(deleteNode: TwoNode<K, V>, parent: TwoNode<K, V>,key:K): Boolean {
        if(deleteNode.getSiblings().hasThreeNodeSibling()){
            val sibling = deleteNode.getSiblings().threeNodeSibling()
            when(deleteNode.getPosition()){
                Left   -> {
                    val p = parent .keyValue1
                    val s = sibling.keyValue1
                    sibling.deleteFromNode(s)
                    parent.keyValue1=s
                    deleteNode.keyValue1=p
                    return true
                }
                Middle -> throw IllegalStateException("TwoNode wont have a a middle child")
                Right  -> {
                    val p = parent .keyValue1
                    val s = sibling.keyValue2
                    sibling.deleteFromNode(s)
                    parent.keyValue1=s
                    deleteNode.keyValue1=p
                    return true
                }
            }
        }else{
            return false
        }
    }

    private fun leafMerge(leafNode : Node<K, V>, futureEmptyParent: TwoNode<K, V>): ThreeNode<K,V> {
        if (leafNode.hasKids()) throw IllegalArgumentException("leaf node is expected")
        return when (leafNode.getPosition()) {
            Left   -> ThreeNode(futureEmptyParent.keyValue1, futureEmptyParent.right!!.keyValue1)
            Middle -> throw IllegalStateException("Should not happen")
            Right  -> ThreeNode(futureEmptyParent.left!!.keyValue1, futureEmptyParent.keyValue1)
        }
    }

    private fun internalMerge(toMergeWith: TwoNode<K, V>, futureEmptyParent: TwoNode<K, V>, mergedChild: Node<K, V>) : ThreeNode<K,V>{
        if (toMergeWith.isLeaf()) throw IllegalArgumentException("internal node is expected")
        return when(toMergeWith.getPosition()){
            Left   -> {
                val l = toMergeWith.left
                val r = toMergeWith.right
                val merged =  toMergeWith.toThreeNode(futureEmptyParent.keyValue1)
                l?.let { merged.addLeft  (it) }
                r?.let { merged.addMiddle(it) }
                merged.apply{ addRight(mergedChild) }
            }
            Middle -> throw IllegalStateException("Should not happen")
            Right  -> {
                val l = toMergeWith.left
                val r = toMergeWith.right
                val merged = toMergeWith.toThreeNode(futureEmptyParent.keyValue1)
                r?.let { merged.addRight(it) }
                l?.let { merged.addMiddle(it) }
                merged.apply { addLeft(mergedChild) }
            }
        }
    }

    private fun deleteFromLeaf(deleteNode: TwoNode<K, V>, parent: ThreeNode<K, V>,key:K): Boolean {
        when(deleteNode.getPosition()){
            Left   -> {
                val sibling = deleteNode.getSiblings().closestSibling()
                if(sibling is ThreeNode){
                    val p = parent .keyValue1
                    val s = sibling.keyValue1
                    deleteNode.keyValue1 = p
                    parent.keyValue1     = s
                    sibling.deleteFromNode(s)
                    return true
                }else{
                    val p  = parent .keyValue1
                    val pr = parent.right!!
                    val s  = sibling.keyValue1
                    parent.addLeft(ThreeNode(p, s))
                    parent.addMiddle(pr) // middle becomes right
                    if(parent!=root)
                    parent.deleteFromNode(p,parent.left,parent.middle)
                    else
                        root = TwoNode(parent.keyValue2).addLeft(ThreeNode(p,s)).addRight(pr)
                    return true
                }
            }
            Middle -> {
                val sibling = deleteNode.getSiblings()
                if(sibling.hasThreeNodeSibling()){
                    val sibling = deleteNode.getSiblings().threeNodeSibling()
                    when(sibling.getPosition()){
                        Left  -> {
                            val p = parent.keyValue1
                            val s = sibling.keyValue2
                            deleteNode.keyValue1 = p
                            parent.keyValue1 = s
                            sibling.deleteFromNode(s)
                            return true
                        }
                        Right -> {
                            val p = parent.keyValue2
                            val s = sibling.keyValue1
                            deleteNode.keyValue1 = p
                            parent.keyValue2= s
                            sibling.deleteFromNode(s)
                            return true
                        }
                        Middle -> throw IllegalStateException("middle child cant have a middle sibling :)")
                    }
                } else{
                    val p = parent.keyValue1
                    val pr = parent.right!!
                    val s = deleteNode.getSiblings().closestSibling() as TwoNode
                    parent.addLeft(s.toThreeNode(p))
                    parent.addMiddle(pr) // middle becomes right
                    if(parent==root) {
                        root = TwoNode(parent.keyValue2).addLeft(s.toThreeNode(p)).addRight(pr)

                        return true
                    } else
                        parent.deleteFromNode(p, s.toThreeNode(p), pr)
                    return true
                }
            }
            Right  -> {
                val sibling = deleteNode.getSiblings().closestSibling()
                if(sibling is ThreeNode){
                    val p = parent .keyValue2
                    val s = sibling.keyValue2
                    deleteNode.keyValue1 = p
                    parent.keyValue2     = s
                    sibling.deleteFromNode(s)
                    return true
                }else{
                    val p = parent .keyValue2
                    val s = sibling.keyValue1
                    if(parent!=root)
                    parent.deleteFromNode(p, parent.left!!, ThreeNode(s, p))
                    else
                        root = TwoNode(parent.keyValue1).addLeft(parent.left!!).addRight(ThreeNode(s,p))
                    return true
                }
            }
        }
    }

    private fun insert(twoNode: Node.TwoNode<K, V>, key: K, value: V) {
        val threeNode = twoNode.toThreeNode(key with value)

        when(twoNode.parent){
            is TwoNode,
            is ThreeNode -> twoNode.replaceWith(threeNode)
            is FourNode  -> throw FourNodeException()
            null         -> root = threeNode
        }
    }

    private fun insert(threeNode: Node.ThreeNode<K, V>, key: K, value: V) {
        val fourNode = threeNode.toFourNode(key with value)
        val parent   = threeNode.parent

        when(parent){
            is Node.TwoNode    -> {
                val originPosition = getNodePosition(fourNode)

                val newParent = when (originPosition) {
                        Left ->
                            parent.toThreeNode(fourNode.keyValue2)
                                .addMiddle   (fourNode.keyValue3)
                                .addLeft     (fourNode.keyValue1)
                                .addRight    (parent.right!!)

                        Right ->
                            parent.toThreeNode(fourNode.keyValue2)
                                .addMiddle    (fourNode.keyValue1)
                                .addLeft      (parent.left!!)
                                .addRight     (fourNode.keyValue3)

                        Middle -> throw IllegalStateException("Parent is a TwoNode therefore he doesn't have a middle child")
                    }

                if (parent == root)
                    root = newParent
                else
                    fourNode.parent!!.replaceWith(newParent)

            }

            is Node.ThreeNode  -> {
                val originPosition = getNodePosition(fourNode)

                val tempFourNode = when (originPosition) {
                    Left ->
                        parent.toFourNode(fourNode.keyValue2)
                            .addMiddle2  (parent.middle!!)
                            .addMiddle   (fourNode.keyValue3)
                            .addLeft     (fourNode.keyValue1)
                            .addRight    (parent.right!!)

                    Middle ->
                        parent.toFourNode(fourNode.keyValue2)
                            .addMiddle2  (fourNode.keyValue3)
                            .addMiddle   (fourNode.keyValue1)
                            .addLeft     (parent.left!!)
                            .addRight    (parent.right!!)

                    Right ->
                        parent.toFourNode(fourNode.keyValue2)
                            .addMiddle2  (fourNode.keyValue1)
                            .addMiddle   (parent.middle!!)
                            .addLeft     (parent.left!!)
                            .addRight    (fourNode.keyValue3)

                } as FourNode<K, V>

                insert(tempFourNode, key,value)

            }

            is Node.FourNode   -> throw FourNodeInsertionException()

            null               ->
                root = TwoNode    (fourNode.keyValue2)
                        .addLeft  (fourNode.keyValue1)
                        .addRight (fourNode.keyValue3)

        }
    }

    private fun insert(fourNode: Node.FourNode<K, V>, key: K, value: V) {
        //val parent = fourNode//.parent
        var fourNode = fourNode
        var n = fourNode.parent
        val stack = emptyLinkedList<Node<K,V>?>()

        while(n!=null){
            stack.add(n)
            n=n.parent
        }


        if(stack.isEmpty()){
            val parent = fourNode.parent
            when(parent){
                is Node.TwoNode   -> {
                    val originPosition = getNodePosition(fourNode)
                    val splitted = fourNode.split()
                    val newParent = parent.toThreeNode(fourNode.keyValue2!!)
                    when(originPosition){
                        Left   -> newParent
                            .addMiddle (splitted.right!!)
                            .addLeft   (splitted.left !!)
                            .addRight  (parent  .right!!)
                        Right  -> newParent
                            .addMiddle (splitted.left !!)
                            .addRight  (splitted.right!!)
                            .addLeft   (parent  .left !!)

                        Middle -> IllegalStateException("must add to parent from some side.")
                    }
                    if (parent == root)
                        root = newParent
                    else
                        fourNode.parent!!.replaceWith(newParent)
                    return
                }
                is Node.ThreeNode -> {
                    val originPosition    = getNodePosition(fourNode)
                    val newFourNodeParent = parent.merge(originPosition, fourNode)
                }
                null ->{
                    val splitted = fourNode.split()
                    root= splitted
                }
            }
        }else{

        }
        while(stack.isNotEmpty()){
            val parent = stack.pop()//?.parent
            when(parent){
                is Node.TwoNode   -> {
                    val originPosition = getNodePosition(fourNode)

                    val splitted = fourNode.split()

                    val newParent = parent.toThreeNode(fourNode.keyValue2!!)

                    when(originPosition){
                        Left   -> newParent
                            .addMiddle (splitted.right!!)
                            .addLeft   (splitted.left !!)
                            .addRight  (parent  .right!!)
                        Right  -> newParent
                            .addMiddle (splitted.left !!)
                            .addRight  (splitted.right!!)
                            .addLeft   (parent  .left !!)

                        Middle -> IllegalStateException("must add to parent from some side.")
                    }
                    if (parent == root)
                        root = newParent
//                    else if (fourNode.parent == null)
//                        root = fourNode.split()
                    else {
                        if (fourNode.parent?.parent != null)
                            fourNode.parent!!.replaceWith(newParent)
                        else {
                            root = newParent
                            return
                        }
                    }
                    return
                }
                is Node.ThreeNode -> {
                    val originPosition    = getNodePosition(fourNode)
                    val newFourNodeParent = parent.merge(originPosition, fourNode)
                    fourNode = newFourNodeParent
                    if(parent == root ){
                        root = fourNode.split()
                    }else{
                       // fourNode.parent!!.replaceWith(newFourNodeParent)
                        fourNode = newFourNodeParent
                      //  stack.push(parent)
                    }
                }
                null ->{
                    root= fourNode.split()
                }
            }
        }

    }

    private fun ThreeNode<K, V>.merge(fromSide: Position, fourNode:FourNode<K,V>): FourNode<K, V> {
        val splitted = fourNode.split()
        return  when (fromSide) {
            Left -> {
                this.toFourNode (fourNode.keyValue2)
                    .addMiddle2 (this.middle!!)
                    .addMiddle  (splitted.right!!)
                    .addLeft    (splitted.left!!)
                    .addRight   (this.right!!)
            }

            Middle -> {
                this.toFourNode (fourNode.keyValue2)
                    .addMiddle2 (splitted.right!!)
                    .addMiddle  (splitted.left!!)
                    .addLeft    (this.left!!)
                    .addRight   (this.right!!)
            }

            Right -> {
                this.toFourNode (fourNode.keyValue2)
                    .addMiddle2 (splitted.left!!)
                    .addMiddle  (this.middle!!)
                    .addLeft    (this.left!!)
                    .addRight   (splitted.right!!)
            }
        } as FourNode
    }

    fun find(key:K): Node<K, V>?{
        var parent: Node<K, V>? = root

        while(true){
            when(parent){
                is Node.TwoNode   -> {
                    if (key == parent.keyValue1.key) return if( parent.keyValue1.key==key) parent else null
                    if (key < parent.keyValue1 .key){
                        if(parent.left == null ) return if( parent.keyValue1.key==key) parent else null
                        else parent = parent.left
                    }
                    else{
                        if(parent.right == null ) return if( parent.keyValue1.key==key) parent else null
                        else parent = parent.right
                    }
                }
                is Node.ThreeNode -> {
                    when{
                        key == parent.keyValue1.key    ->                                                       return if ( parent.keyValue1.key==key) parent else null
                        key == parent.keyValue2.key    ->                                                       return if ( parent.keyValue2.key==key) parent else null
                        key < parent.keyValue1.key     -> if(parent.left   != null) parent = parent.left   else return if ( parent.keyValue1.key==key) parent else null
                        key > parent.keyValue1.key
                            &&
                            key < parent.keyValue2.key -> if (parent.middle != null) parent = parent.middle else return if ( parent.keyValue1.key==key) parent else null
                        key > parent.keyValue2.key     -> if (parent.right != null)  parent = parent.right  else return if ( parent.keyValue1.key==key) parent else null
                    }
                }
                is Node.FourNode  -> throw FourNodeException()
                null              -> return (parent)
            }
        }
    }

    fun getNode(key: K) : Node<K, V>? {
        var parent: Node<K, V>? = root

        while(true){
            when(parent){
                is Node.TwoNode   -> {
                     if (key < parent.keyValue1.key){
                         if(parent.left == null ) return parent
                            else parent = parent.left
                     }
                    else{
                         if(parent.right == null ) return parent
                         else parent = parent.right
                     }
                }
                is Node.ThreeNode -> {
                    when{
                        key == parent.keyValue1.key    -> return (parent)
                        key == parent.keyValue2.key    -> return (parent)
                        key < parent.keyValue1.key -> if(parent.left   != null) parent = parent.left   else return (parent)
                        key > parent.keyValue1.key
                            &&
                        key < parent.keyValue2.key -> if(parent.middle != null) parent = parent.middle else return (parent)
                        key > parent.keyValue2.key -> if(parent.right  != null) parent = parent.right  else return (parent)
                    }
                }
                is Node.FourNode  -> throw FourNodeException()
                null              -> return (parent)
            }
        }
    }

    fun getValuesInorder() : List<V>{
        val list = emptyLinkedList<V>()
        root?.let {
            inorder(it){
                list += it.keyValue1.value
            }
        }
        return list
    }

    fun getInorder(): List<K> {
        val list = emptyLinkedList<K>()
        root?.let { root ->
            inorder(root) {
                list += it.keyValue1.key
            }
        }
        return list
    }

    private fun levelOrder(node: Node<K, V>?): MutableList<Node<K, V>>? {
        val queue = Queue<Node<K, V>>(emptyLinkedList())
        val helpQueue = Queue<Node<K, V>>(emptyLinkedList())

        if (node == null) return null

        var node = node
        while (node != null) {
            queue.enqueue(node)

            if (!node.isLeaf()) when (node) {
                is Node.TwoNode -> {
                    helpQueue.enqueue(node.left!!)
                    helpQueue.enqueue(node.right!!)
                }
                is Node.ThreeNode -> {
                    helpQueue.enqueue(node.left!!)
                    helpQueue.enqueue(node.middle!!)
                    helpQueue.enqueue(node.right!!)
                }
            }
            node = if (helpQueue.items.size > 0) {
                helpQueue.dequeue()
            } else
                null
        }
        return queue.items
    }

    private fun inoredSuccesor(node: Node<K, V>, key:K): Node<K, V>? {
        val found = emptyLinkedList<Node<K, V>>()
        var result: Node<K, V>? = null
        var once=true
        inorder { currentNode ->
            found.add(currentNode)
            if (found.size > 1) {
                val predposledny = found[found.size - 2]
                val kv1 = node.keyValue1
                val kv2 = (node as? ThreeNode)?.keyValue2

                if (predposledny.keyValue1.key == key || predposledny.keyValue1.key == key && once) {
                    result = currentNode //TODO omg ...
                    once = false
                }
            }
        }

        if(found.last.keyValue1.key==key){
          //  found.removeLast()
        }
        result = if(result!=null)
             find(result!!.keyValue1.key)
        else find(found.last.keyValue1.key)

        return result
    }

    internal fun inorder(node: Node<K, V>? = root,visit : (Node<K,V>) -> Unit) {
        if(node==null) return
        val stack = emptyLinkedList<Node<K, V>>()
        val pushLeft = { _node: Node<K, V>? ->
            var node = _node
            while (node != null) {
                if (node is ThreeNode) {
                    stack.push(
                        TwoNode(keyValue1 = node.keyValue2, left = null, right = node.right,  parent = node.parent)
                    )
                    stack.push(
                        TwoNode(keyValue1 = node.keyValue1, left = null, right = node.middle, parent = node.parent)
                    )
                    node = node.left
                } else {
                    stack.push(node)
                    node = node.left
                }
            }
        }

        var n: Node<K, V>?
        pushLeft(node)
        while (stack.isNotEmpty()) {
            n = stack.pop()
            visit(n)
            pushLeft(n.right)
        }
    }

    private fun getNodePosition(node: Node<K, V>):Position {
        val parent = node.parent!!
        return  when (node) {
//            is Node.EmptyNode -> throw IllegalStateException("Empty node in tree")
            is Node.FourNode  ->
                when (parent) {
//                    is Node.EmptyNode -> throw IllegalStateException("Empty node in tree")

                    is Node.TwoNode -> {
                    val leftKeyVal      = node.parent!!.left !!.keyValue1
                    val rightKeyVal     = node.parent!!.right!!.keyValue1
                    val addingFromLeft  = leftKeyVal  == node.keyValue1 || leftKeyVal  == node.keyValue2 || leftKeyVal  == node.keyValue3
                    val addingFromRight = rightKeyVal == node.keyValue1 || rightKeyVal == node.keyValue2 || rightKeyVal == node.keyValue3
                    when {
                        addingFromLeft  -> Left
                        addingFromRight -> Right
                        else -> throw IllegalStateException("This node is not a child of it's parent")
                    }
                }
                is Node.ThreeNode -> {
                    val leftKeyVal      = parent.left!!.keyValue1
                    val midKeyVal       = parent.middle!!.keyValue1
                    val rightKeyVal     = parent.right!!.keyValue1

                    val addingFromLeft  = leftKeyVal  == node.keyValue1 || leftKeyVal  == node.keyValue2 || leftKeyVal  == node.keyValue3
                    val addingFromMid   = midKeyVal   == node.keyValue1 || midKeyVal   == node.keyValue2 || midKeyVal   == node.keyValue3
                    val addingFromRight = rightKeyVal == node.keyValue1 || rightKeyVal == node.keyValue2 || rightKeyVal == node.keyValue3

                    when {
                        addingFromLeft  -> Left
                        addingFromMid   -> Middle
                        addingFromRight -> Right
                        else -> throw IllegalStateException("This node is not a child of it's parent")
                    }
                }
                is Node.FourNode -> TODO()
                }
            is Node.TwoNode      -> {
                when(parent){
                    is Node.TwoNode   -> {
                        if(node.keyValue1==parent.left!!.keyValue1)
                            Left
                        else
                            Right
                    }
                    is Node.ThreeNode -> when(node){
                        parent.left     -> Left
                        parent.middle   -> Middle
                        parent.right    -> Right
                    }
                    is Node.FourNode  -> TODO()
                }
                TODO()
            }
            is Node.ThreeNode    -> {
                TODO()
            }
        }
    }
 }

private fun <K:Comparable<K>, V> Node<K, V>.isRightOrLeft(): Boolean = getPosition()==Left || getPosition()==Right

private fun <K:Comparable<K>, V> Node<K, V>.hasKey(key: K): Boolean =when(this){
//    is Node.EmptyNode -> throw IllegalStateException("Empty node in tree")

    is Node.TwoNode -> keyValue1!!.key == key
    is Node.ThreeNode -> keyValue1!!.key == key || keyValue2!!.key == key
    is Node.FourNode -> TODO()
}

infix fun <K : Comparable<K>, V> Node<K, V>?.partialyEquals(other:Node<K,V>?) : Boolean {
    return when(this){
        is Node.TwoNode   -> when(other){
            is Node.TwoNode   ->
                    this.keyValue1 == other.keyValue1

            is Node.ThreeNode ->
                    this.keyValue1 == other.keyValue1 ||
                    this.keyValue1 == other.keyValue2

            is Node.FourNode  ->
                    this.keyValue1 == other.keyValue1 ||
                    this.keyValue1 == other.keyValue2 ||
                    this.keyValue1 == other.keyValue3
            null            -> true
        }
        is Node.ThreeNode -> when(other){
            is Node.TwoNode   ->
                    this.keyValue1 == other.keyValue1 ||
                    this.keyValue2 == other.keyValue1

            is Node.ThreeNode ->
                    this.keyValue1 == other.keyValue1 ||
                    this.keyValue1 == other.keyValue2 ||
                    this.keyValue2 == other.keyValue1 ||
                    this.keyValue2 == other.keyValue2

            is Node.FourNode  ->
                    this.keyValue1 == other.keyValue1 ||
                    this.keyValue1 == other.keyValue2 ||
                    this.keyValue1 == other.keyValue3 ||
                    this.keyValue2 == other.keyValue1 ||
                    this.keyValue2 == other.keyValue2 ||
                    this.keyValue2 == other.keyValue3 ||
                    this.keyValue2 == other.keyValue2
            null -> true
        }
        is Node.FourNode  ->  when(other){
            is Node.TwoNode   ->
                    this.keyValue1 == other.keyValue1 ||
                    this.keyValue3 == other.keyValue1 ||
                    this.keyValue2 == other.keyValue1

            is Node.ThreeNode ->
                    this.keyValue1 == other.keyValue1 ||
                    this.keyValue1 == other.keyValue2 ||
                    this.keyValue2 == other.keyValue1 ||
                    this.keyValue2 == other.keyValue2 ||
                    this.keyValue3 == other.keyValue1 ||
                    this.keyValue3 == other.keyValue2

            is Node.FourNode  ->
                    this.keyValue1 == other.keyValue1 ||
                    this.keyValue1 == other.keyValue2 ||
                    this.keyValue1 == other.keyValue3 ||
                    this.keyValue2 == other.keyValue1 ||
                    this.keyValue2 == other.keyValue2 ||
                    this.keyValue2 == other.keyValue3 ||
                    this.keyValue3 == other.keyValue1 ||
                    this.keyValue3 == other.keyValue2 ||
                    this.keyValue3 == other.keyValue3
            null -> true
        }
        null -> true
    }
}
fun <K : Comparable<K>, V> Node<K, V>.getPosition() : Position {
    val parent = parent!!
    return when{
        parent.left  partialyEquals this -> Left
        parent.right partialyEquals this -> Right
        (parent is ThreeNode) && parent.middle partialyEquals this -> Middle
        else -> throw IllegalStateException("")
    }
    }

fun <K : Comparable<K>, V> ThreeNode<K, V>.asTwoNode(removeKey: K): TwoNode<K,V> {
    when (this) {
        this.keyValue1.key -> return TwoNode(this.keyValue2, parent = this.parent,left = left,right = middle)
        this.keyValue2.key -> return TwoNode(this.keyValue1, parent = this.parent,left = middle,right = right)
        else -> throw IllegalStateException("this value is not in this node")
    }
}

fun <K : Comparable<K>, V> ThreeNode<K, V>.deleteFromNode(kv: KeyValue<K,V>) = deleteFromNode(kv.key)

fun <K : Comparable<K>, V> ThreeNode<K, V>.deleteFromNode(key: K) =
    when (key) {/// tuto okotinu preppisat
        this.keyValue1.key -> this.replaceWith(TwoNode(this.keyValue2, parent = this.parent,left = left, right = middle))//.addLeft(left).addRight(middle) )
        this.keyValue2.key -> this.replaceWith(TwoNode(this.keyValue1, parent = this.parent,left = middle,right = right))//.addLeft(middle).addRight(right))
        else -> throw IllegalStateException("this value is not in this node")
    }

fun <K : Comparable<K>, V> ThreeNode<K, V>.deleteFromNode(kv: KeyValue<K,V>,newLeft:Node<K,V>?,newRight:Node<K,V>?) = deleteFromNode(kv.key,newLeft,newRight)

fun <K : Comparable<K>, V> ThreeNode<K, V>.deleteFromNode(key: K,newLeft:Node<K,V>?,newRight:Node<K,V>?) =
    when (key) {
        this.keyValue1.key -> this.replaceWith(TwoNode(this.keyValue2, parent = this.parent).apply{newLeft?.let{addLeft(it)}; newRight?.let{addRight(it)}})//.addLeft(newLeft).addRight(newRight))
        this.keyValue2.key -> this.replaceWith(TwoNode(this.keyValue1, parent = this.parent).apply{newLeft?.let{addLeft(it)}; newRight?.let{addRight(it)}})//.addLeft(newLeft).addRight(newRight))
        else -> throw IllegalStateException("this value is not in this node")
    }

enum class Position{Left,Middle,Right}

class FourNodeInsertionException : Exception ("You shouldn't insert values into four node ")
class FourNodeException          : Exception ("Four node can't be in 23 tree")
