package Tree
import Tree.Position.*
import Tree.node.*
import Tree.node.Node.*
import com.intellij.util.containers.isNullOrEmpty
import extensions.Queue
import extensions.emptyLinkedList
import java.util.*


class TwoThreeTree<K:Comparable<K>,V>  {
    internal var root : Node<K,V>? = null

    //for testing purposes only
    val insertedKeys = emptyLinkedList<K>()

    fun put(key:K,value:V){

        if(root ==null) {
            root = TwoNode(key with value)
            insertedKeys.add(key)
            return
        }

        val foundNode = getNode(key)

        when(foundNode){
            is TwoNode   -> insert(foundNode,key,value)
            is ThreeNode -> insert(foundNode,key,value)
            else         -> throw IllegalStateException("Node to insert value to was not found")
        }

        insertedKeys.add(key)
    }

    fun leftMost(node:Node<K,V>): Node<K, V> {
        if(node.right!=null){
            var n: Node<K, V>? =node.right
            while(n?.left!=null){
                n=n.left
            }
            return n!!
        }else return node

    }
    @Suppress("UNCHECKED_CAST")
    fun delete(key: K): Boolean {
        val deleteNode = find(key)
        if (deleteNode != null) {
            if (deleteNode.isLeaf() && root == deleteNode) {
                root = null
                return true
            }
            if (deleteNode is ThreeNode && deleteNode.isLeaf()) {
                deleteNode.deleteFromNode(key)
                return true
            } else {

                val inorderSuccessorNode = inoredSuccesor(deleteNode,key)
                val inorderSuccessorKv   = when {
                    key > inorderSuccessorNode?.keyValue1!!.key -> (inorderSuccessorNode as ThreeNode).keyValue2
                    else -> inorderSuccessorNode.keyValue1
                }

                //swap kv with Inorder successor
                var delKev:KeyValue<K,V>? = null
                when(deleteNode){
                    is Node.TwoNode   -> {
                        delKev=deleteNode.keyValue1
                        deleteNode.keyValue1=inorderSuccessorKv

                        when(inorderSuccessorNode){
                            is Node.TwoNode   -> inorderSuccessorNode.keyValue1 = delKev
                            is Node.ThreeNode -> {
                                if(key > inorderSuccessorNode.keyValue1.key )inorderSuccessorNode.keyValue2=delKev
                                else inorderSuccessorNode.keyValue1=delKev
                            }
                        }
                    }
                    is Node.ThreeNode -> {
                        if (inorderSuccessorKv.key > deleteNode.keyValue2.key){
                            delKev = deleteNode.keyValue2
                            deleteNode.keyValue2=inorderSuccessorKv
                        }else{
                            delKev = deleteNode.keyValue1
                            deleteNode.keyValue1=inorderSuccessorKv
                        }
                        inorderSuccessorNode.keyValue1 = delKev
                    }
                }
                val deletedKv = delKev!!
                //al de
                //ok let's delete some stuff
                /*.
                node element otca lavy +  pravy + lavz szn + middle syn + right syn lebo ak je sucet 4 vies to spravit
                a ak je to viac ako 5 tiez to vies hned spravit a iba
                ak je ich menej ako 4 idem do metody kde riesim tieto restrukturalizacne veci ...
                 */
                val deleteNodeParent= deleteNode.parent
                var keyValuesCount=0
                when(deleteNodeParent){
                    is Node.TwoNode   -> {
                        keyValuesCount++
                        when(deleteNodeParent.left){
                            is Node.TwoNode   -> keyValuesCount++
                            is Node.ThreeNode -> keyValuesCount+=2
                        }
                        when(deleteNodeParent.right){
                            is Node.TwoNode   -> keyValuesCount++
                            is Node.ThreeNode -> keyValuesCount+=2
                        }
                    }

                    is Node.ThreeNode -> {
                        keyValuesCount+=2
                        when(deleteNodeParent.left){
                            is Node.TwoNode   -> keyValuesCount++
                            is Node.ThreeNode -> keyValuesCount+=2
                        }
                        when(deleteNodeParent.middle){
                            is Node.TwoNode   -> keyValuesCount++
                            is Node.ThreeNode -> keyValuesCount+=2
                        }
                        when(deleteNodeParent.right){
                            is Node.TwoNode   -> keyValuesCount++
                            is Node.ThreeNode -> keyValuesCount+=2
                        }
                    }

                }

                if(keyValuesCount >= 4 ){
                    when(deleteNodeParent){
                        is Node.TwoNode   -> when(deleteNode){
                            is Node.TwoNode   -> {

                                if(inorderSuccessorNode is ThreeNode){
                                  deleteNode.keyValue1=inorderSuccessorKv
                                    inorderSuccessorNode.deleteFromNode(delKev.key)
                                    return true
                                }
                                val sibling = deleteNode.getSiblings().closestSibling()
                                when(sibling.getPosition()){
                                    Left   -> {
                                        (sibling as ThreeNode).deleteFromNode(sibling.keyValue2.key)
                                        deleteNode.keyValue1=deleteNodeParent.keyValue1
                                        deleteNodeParent.keyValue1=sibling.keyValue2
                                        return true
                                    }
                                    Middle -> throw IllegalStateException()
                                    Right  -> {

                                        (sibling as ThreeNode).deleteFromNode(sibling.keyValue1.key)
                                        deleteNodeParent.keyValue1=sibling.keyValue1
                                        return true
                                    }
                                }

                            }
                            is Node.ThreeNode -> {
                                if( deleteNode.left    == inorderSuccessorNode ||
                                    deleteNode.middle  == inorderSuccessorNode ||
                                    deleteNode.right   == inorderSuccessorNode ) {
                                    when (inorderSuccessorNode.getPosition()) {
                                        Left -> TODO()
                                        Middle -> {
                                            val siblings = inorderSuccessorNode.getSiblings() as Sibling.TwoSiblings
                                            if(siblings.hasThreeNodeSibling()){
                                                if(siblings.twoThreeNodeSiblings()){
                                                    val delKv1=deleteNode.keyValue1
                                                    deleteNode.keyValue1=(siblings.closestSibling() as ThreeNode).keyValue2
                                                    deleteNode.middle!!.keyValue1=delKv1
                                                    (siblings.closestSibling() as ThreeNode).deleteFromNode(deleteNode.keyValue1.key)
                                                    return true
                                                }
                                                val newM = deleteNode.keyValue2
                                                val newP = (siblings.second as ThreeNode).keyValue1
                                                deleteNode.keyValue2=newP
                                                inorderSuccessorNode.keyValue1=newM
                                                (siblings.second as ThreeNode).deleteFromNode(newP.key)
                                                return true
                                            }else{
                                                if(inorderSuccessorNode is TwoNode){
                                                deleteNode.replaceWith(
                                                    TwoNode(deleteNode.keyValue2, parent = deleteNodeParent)
                                                        .addLeft(ThreeNode(deleteNode.left!!.keyValue1, deleteNode.keyValue1))
                                                        .addRight(deleteNode.right!!)
                                                )
                                                return true}else{
                                                    (inorderSuccessorNode as ThreeNode).deleteFromNode(key)
                                                    return true
                                                }
                                            }

                               /*             if (siblings.hasNotThreeNodeSibling()) {
                                            //TODO IF inorderSuccesrNode is TwoNode.... takt toto co mam, inak else a daco spravit
                                                if(inorderSuccessorNode is TwoNode)
                                                    deleteNode.replaceWith(
                                                        TwoNode(deleteNode.keyValue2, parent = deleteNodeParent)
                                                            .addLeft(ThreeNode(deleteNode.left!!.keyValue1, deleteNode.keyValue1))
                                                            .addRight(deleteNode.right!!)
                                                    )
                                                else
                                                {
                                                    (inorderSuccessorNode as ThreeNode).deleteFromNode(key)
                                                 }
                                                return true
                                            } else if (siblings.hasThreeNodeSibling()) {
                                                val sibling = siblings.closestSibling() as ThreeNode
                                                when(sibling.getPosition()){
                                                    Left   -> {
                                                        val delKv1=deleteNode.keyValue1
                                                        deleteNode.keyValue1=sibling.keyValue2
                                                        deleteNode.middle!!.keyValue1=delKv1
                                                        sibling.deleteFromNode(deleteNode.keyValue1.key)
                                                        return true
                                                    }
                                                    Middle -> TODO()
                                                    Right  -> TODO()
                                                }
                                            }*/

                                        }
                                        Right -> {
                                            val siblings = inorderSuccessorNode.getSiblings().closestSibling()
                                            if(inorderSuccessorNode is ThreeNode){
                                                val newP = inorderSuccessorNode.keyValue1
                                                deleteNode.keyValue2=inorderSuccessorKv
                                                inorderSuccessorNode.deleteFromNode(inorderSuccessorNode.keyValue1.key)
                                                return true
                                            }
                                            if (siblings is TwoNode) {
                                                deleteNode.replaceWith(
                                                    TwoNode(deleteNode.keyValue1, parent = deleteNodeParent)
                                                        .addLeft(deleteNode.left!!)
                                                        .addRight(ThreeNode(deleteNode.middle!!.keyValue1, deleteNode.keyValue2))
                                                )
                                                return true
                                            }else {
                                                val newParentKv = (siblings as ThreeNode).keyValue2
                                                val newRightKv = deleteNode.keyValue2
                                                inorderSuccessorNode.keyValue1=newRightKv
                                                deleteNode.keyValue2=newParentKv
                                                siblings.deleteFromNode(newParentKv.key)
                                                return true

                                            }
                                        }
                                    }
                                }else TODO()


                            }
                        }

                        is Node.ThreeNode -> when(deleteNode){
                            is Node.TwoNode   -> {
                                when(deleteNode.getPosition()){
                                    Left   -> {
                                        val sibling = deleteNode.getSiblings().closestSibling()
                                        if(sibling is TwoNode){
                                        deleteNodeParent.replaceWith(
                                            TwoNode(keyValue1 = deleteNodeParent.keyValue2, parent = deleteNodeParent.parent)
                                                .addLeft(ThreeNode(inorderSuccessorKv,deleteNodeParent.middle!!.keyValue1))
                                                .addRight(deleteNodeParent.right!!)
                                        )
                                        return true}else{
                                            val pKv=sibling.keyValue1
                                            deleteNodeParent.keyValue1 = pKv
                                            (sibling as ThreeNode).deleteFromNode(pKv.key)
                                            return true
                                        }
                                    }
                                    Middle -> {

                                        val siblings = deleteNode.getSiblings() as Sibling.TwoSiblings
                                        if (siblings.hasThreeNodeSibling()) {
                                            if (siblings.twoThreeNodeSiblings()) {
                                                deleteNodeParent.keyValue2=inorderSuccessorKv
                                                deleteNode.keyValue1=deleteNodeParent.keyValue1
                                                val sibling = siblings.first as ThreeNode
                                                deleteNodeParent.keyValue1=sibling.keyValue2
                                                sibling.deleteFromNode( deleteNodeParent.keyValue1.key)
                                                return true
                                            }
                                            val sibling = siblings.second
                                            deleteNodeParent.keyValue2=sibling.keyValue1
                                            (sibling as ThreeNode).deleteFromNode(sibling.keyValue1.key)
                                            return true
                                        } else if (siblings.closestSibling() is TwoNode) {
                                            deleteNodeParent.replaceWith(
                                                TwoNode(keyValue1 = deleteNodeParent.keyValue1, parent = deleteNodeParent.parent)
                                                    .addLeft(deleteNodeParent.left!!)
                                                    .addRight(ThreeNode(inorderSuccessorKv, deleteNodeParent.right!!.keyValue1))
                                            )
                                            return true
                                        } else {
                                            TODO()

                                        }
                                    }
                                    Right  -> {
                                        val sibling = deleteNode.getSiblings().closestSibling()
                                        if (sibling is TwoNode) {
                                            deleteNodeParent.replaceWith(
                                                TwoNode(keyValue1 = deleteNodeParent.keyValue1, parent = deleteNodeParent.parent)
                                                    .addLeft(deleteNodeParent.left!!)
                                                    .addRight(ThreeNode(deleteNodeParent.middle!!.keyValue1, deleteNodeParent.keyValue2))
                                            )
                                            return true
                                        } else {
                                            val newRight  = deleteNodeParent.keyValue2
                                            val newParent = (sibling as ThreeNode).keyValue2

                                            inorderSuccessorNode.keyValue1=newRight
                                            deleteNodeParent.keyValue2=newParent
                                            sibling.deleteFromNode(newParent.key)
                                            return true
                                        }
                                    }
                                }

                            }
                            is Node.ThreeNode ->TODO()
                        }
                    }

                }else{
                    var i = 0
                    /*
                     1. Locate node n, which contains item I
                     2. If node n is not a leaf  swap I with inorder successor
                      deletion always begins at a leaf
                     3. If leaf node n contains another item, just delete item I
                         else
                     try to redistribute nodes from siblings (see next slide)
                     if not possible, merge node (see next slide)
                     Del
                    */
                    var father: Node<K, V>? = inorderSuccessorNode
                    var deleteNode = deleteNode
                    var once = true
                    while(true){
                        if (i++ > 40) throw Exception("loop")
                        var parentlessNewNode: Node<K, V>?=null


                        if(once) {
                            if(inorderSuccessorNode is ThreeNode){
                                inorderSuccessorNode.deleteFromNode(key)
                                return true
                            }
                            val sibl = inorderSuccessorNode.getSiblings().closestSibling()
                            if(sibl is ThreeNode){
                                when(inorderSuccessorNode.getPosition()){
                                    Left   -> {
                                        inorderSuccessorNode.keyValue1=sibl.keyValue1
                                        sibl.deleteFromNode(sibl.keyValue1.key)
                                        val left =  inorderSuccessorNode.keyValue1
                                        val par  =  inorderSuccessorNode.parent!!.keyValue1
                                        inorderSuccessorNode.keyValue1=par
                                        inorderSuccessorNode.parent!!.keyValue1=left

                                        return true
                                    }
                                    Middle -> TODO()
                                    Right  -> TODO()
                                }
                            }
                            when (deleteNode) {
                                is Node.TwoNode -> {
                                    if(deleteNode.hasKids()){
                                        println()
                                        parentlessNewNode = ThreeNode(deleteNode.left!!.keyValue1,deleteNode.keyValue1,parent=deleteNode)
                                        father=deleteNode

                                    }       else
                                    parentlessNewNode = (father!!.left as TwoNode).toThreeNode(father.right!!.keyValue1).copy(parent = inorderSuccessorNode)
                                }
                                is Node.ThreeNode -> TODO()
                            }
                            once=false
                        }
                        val siblings =father?.getSiblings()

                        when(siblings){
                            is Sibling.TwoSiblings -> TODO()
                            is Sibling.OneSibling  -> {
                                if(siblings.sibling is ThreeNode){
                                    TODO()
                                }else{
                                    when(father?.getPosition()){
                                        Left -> {
                                            val parent  = father.parent
                                          //  val test = parent?.getSiblings()
                                            val sibling = siblings.sibling as TwoNode
                                            val nejakyNode= ThreeNode(parent!!.keyValue1,sibling.keyValue1)
                                                .addMiddle(sibling.left!!)
                                                .addRight(sibling.right!!)
                                                .addLeft(parentlessNewNode!!)

                                            //parent.parent=parent
                                            parent.left=nejakyNode

                                            father = nejakyNode.setNewParent(parent)
                                            deleteNode=father.parent
                                            father=deleteNode
                                            println()

                                        }
                                        Middle -> TODO()
                                        Right -> {
                                            val nejakyNode = ThreeNode(siblings.sibling.keyValue1,father.parent!!.keyValue1)
                                                .addMiddle(siblings.sibling.right!!)
                                                .addLeft(siblings.sibling.left!!)
                                                .addRight(father.left!!)
                                            nejakyNode.setNewParent(father.parent!!)

                                            val newParent = nejakyNode.parent
                                            newParent!!.addRight(nejakyNode)
                                                newParent.left=null
                                            father = newParent




                                        }


                                        null -> TODO()
                                    }
                                }
                            }
                            is Sibling.NoSiblings  -> {
                                root=father!!.right
                                return true
                            }
                        }


                        println()
                    }

                }
                /*
                1. Locate node n, which contains item I
                2. If node n is not a leaf  swap I with inorder successor
                 deletion always begins at a leaf
                3. If leaf node n contains another item, just delete item I
                    else
                try to redistribute nodes from siblings (see next slide)
                if not possible, merge node (see next slide)
                Del
                 */
                TODO()
                var i = 0
                while (inorderSuccessorNode != null) {
                    if (i++ > 40) throw Exception("loop")

/* posledny smutny pokus
                    if(node.hasKids()){
//                        when(node){
//                            is Node.TwoNode        -> node.replaceWith(TwoNode(inorderSuccessorKv,node.left,node.right,parent = node.parent))
//                            is Node.ThreeNode      -> when(key){
//                                node.keyValue1.key -> node.replaceWith(ThreeNode(inorderSuccessorKv,node.keyValue2,    left = node.left,middle = node.middle,right = node.right,parent = node.parent))
//                                node.keyValue2.key -> node.replaceWith(ThreeNode(node.keyValue1    ,inorderSuccessorKv,left = node.left,middle = node.middle,right = node.right,parent = node.parent))
//                            }
//                        }

                    }else if(inorderSuccessorNode.isLeaf()){
                        //ThreeNode in leaf is already managed up in the code
                        val node     = node as TwoNode
                        val parent   = node.parent
                        val siblings = node.getSiblings()
                        if(siblings.hasThreeNodeSibling()){
                            when(parent){
                                is Node.TwoNode   -> {
                                    val sibling = (siblings as Sibling.OneSibling).sibling as ThreeNode
                                    when(node.getPosition()){
                                        Left   -> {
                                            parent.replaceWith(
                                                TwoNode(sibling.keyValue1,parent=parent.parent)
                                                    .addLeft(parent.keyValue1)
                                                    .addRight(sibling.keyValue2)
                                            )
                                            return true
                                        }
                                        Middle -> throw IllegalStateException("two node parent cant' have middle child")
                                        Right  -> {
                                            parent.replaceWith(
                                                TwoNode(sibling.keyValue2,parent=parent.parent)
                                                    .addLeft(sibling.keyValue1)
                                                    .addRight(parent.keyValue1)
                                            )
                                            return true
                                        }
                                    }
                                }
                                is Node.ThreeNode -> {
                                    val sibling  = siblings.closestSibling()
                                    when(node.getPosition()){
                                        Left   -> {
                                            when(sibling)
                                            {
                                                is Node.TwoNode   -> {
                                                    parent.replaceWith(
                                                        TwoNode(parent.keyValue2,parent=parent.parent)
                                                            .addLeft(ThreeNode(parent.keyValue1,sibling.keyValue1))
                                                            .addRight(parent.right!!)
                                                    )
                                                    return true
                                                }
                                                is Node.ThreeNode -> {
                                                    parent.replaceWith(
                                                        ThreeNode(sibling.keyValue1,parent.keyValue2,parent = parent.parent)
                                                            .addMiddle(sibling.keyValue2)
                                                            .addLeft(parent.keyValue1)
                                                            .addRight(parent.right!!)
                                                    )
                                                    return true
                                                }
                                            }
                                        }

                                        Middle -> {
                                            when(siblings){
                                                is Sibling.TwoSiblings -> {
                                                    if(siblings.first is ThreeNode || (siblings.first is ThreeNode && siblings.second is ThreeNode)){
                                                        parent.replaceWith(
                                                            ThreeNode((siblings.first as ThreeNode<K, V>).keyValue2,parent.keyValue2,parent=parent.parent)
                                                                .addMiddle(parent.keyValue1)
                                                                .addLeft(sibling.keyValue1)
                                                                .addRight(parent.right!!)
                                                        )
                                                        return true
                                                    }else if(siblings.first !is ThreeNode && siblings.second is ThreeNode){
                                                        parent.replaceWith(
                                                            ThreeNode(parent.keyValue1,(siblings.second as ThreeNode<K, V>).keyValue1,parent=parent.parent)
                                                                .addMiddle(parent.keyValue2)
                                                                .addLeft(parent.left!!)
                                                                .addRight((siblings.second as ThreeNode<K, V>).keyValue2)
                                                        )
                                                        return true
                                                    }

                                                }
                                            }

                                        }
                                        Right  -> {
                                            when(sibling){
                                                is Node.TwoNode -> {
                                                        parent.replaceWith(
                                                            TwoNode(parent.keyValue1,parent=parent.parent)
                                                                .addLeft(parent.left!!)
                                                                .addRight(ThreeNode(sibling.keyValue1,parent.keyValue2))
                                                        )
                                                    return true
                                                }
                                                is Node.ThreeNode -> {
                                                    parent.replaceWith(
                                                        ThreeNode(parent.keyValue1,sibling.keyValue2,parent=parent.parent)
                                                            .addMiddle(sibling.keyValue1)
                                                            .addLeft(parent.left!!)
                                                            .addRight(parent.keyValue2)
                                                    )
                                                    return true
                                                }
                                             }

                                        }
                                    }

                                }
                                null -> TODO()
                            }
                        }else {
                            when(parent){
                                is Node.TwoNode   -> {
                                    TODO()
                                }
                                is Node.ThreeNode -> {
                                    when(node.getPosition()){
                                        Left    -> {
                                            parent.replaceWith(
                                                TwoNode(parent.keyValue2,parent=parent.parent)
                                                    .addLeft(ThreeNode(parent.keyValue1,siblings.closestSibling().keyValue1))
                                                    .addRight(parent.right!!)
                                            )
                                            return true
                                        }
                                        Middle  -> {
                                            parent.replaceWith(
                                                TwoNode(parent.keyValue2,parent=parent.parent)
                                                    .addLeft(ThreeNode(siblings.closestSibling().keyValue1,parent.keyValue1))
                                                    .addRight(parent.right!!)
                                            )
                                            return true
                                        }
                                        Right   -> {
                                            parent.replaceWith(
                                                TwoNode(parent.keyValue1,parent=parent.parent)
                                                    .addLeft(parent.left!!)
                                                    .addRight(ThreeNode(siblings.closestSibling().keyValue1,parent.keyValue2))
                                            )
                                            return true
                                        }
                                    }
                                }
                                null -> TODO()
                            }
                        }
                    }

                    when(inorderSuccessorNode){
                        is Node.TwoNode   -> {
                            val siblings = inorderSuccessorNode.getSiblings()
                            if(siblings.closestSibling() is ThreeNode ){
                                val sibling = siblings.closestSibling()
                                val parent  = sibling.parent
                                when(inorderSuccessorNode.getPosition()){
                                    Left -> {
                                        TODO()
                                    }
                                    Middle -> {
                                        when(siblings){
                                            is Sibling.TwoSiblings -> {
                                                if(siblings.first is ThreeNode || (siblings.first is ThreeNode && siblings.second is ThreeNode)){
                                                    val sibling = siblings.first as ThreeNode
                                                    sibling.deleteFromNode(sibling.keyValue2.key)
                                                    sibling.parent!!.keyValue1 = sibling.keyValue2
                                                    return true
                                                }else{
                                                    TODO()
                                                }
                                            }
                                            is Sibling.OneSibling -> TODO()
                                        }
                                    }
                                    Right -> {
                                        when(parent){
                                            is Node.TwoNode   -> {
                                                //parent.replaceWith()
                                                TODO()
                                            }
                                            is Node.ThreeNode -> {
                                                if(sibling.getPosition() == Middle){
                                                    val sibling = (sibling as ThreeNode)
                                                    (sibling as ThreeNode).deleteFromNode(sibling.keyValue2.key)
                                                    parent.keyValue2=sibling.keyValue2
                                                    return true
                                                }
                                                    parent.replaceWith(
                                                        TwoNode(parent.keyValue1, parent = parent.parent)
                                                            .addLeft(parent.left!!)
                                                            .addRight(ThreeNode(parent.middle!!.keyValue1, (sibling as ThreeNode).keyValue2))
                                                    )
                                                    return true


                                            }
                                            null -> TODO()
                                        }
                                    }
                                }
                            }else{
                                val parent  = inorderSuccessorNode.parent
                                val insNode = inorderSuccessorNode
                                when(parent){
                                    is Node.TwoNode   -> TODO()
                                    is Node.ThreeNode -> {
                                        when(insNode.getPosition()){
                                            Left -> TODO()
                                            Middle -> {
                                                parent.replaceWith(
                                                    TwoNode(parent.keyValue2,parent=parent.parent)
                                                        .addLeft(ThreeNode(parent.left!!.keyValue1,insNode.keyValue1))
                                                        .addRight(parent.right!!)
                                                )
                                                return true
                                            }
                                            Right -> {
                                                parent.replaceWith(
                                                    TwoNode(parent.keyValue1,parent=parent.parent)
                                                        .addLeft (parent.left!!)
                                                        .addRight(ThreeNode(parent.middle!!.keyValue1,insNode.keyValue1))
                                                )
                                                return true
                                            }
                                        }
                                    }
                                    null -> TODO()
                                }
                            }
                        }
                        is Node.ThreeNode -> {
                            inorderSuccessorNode.deleteFromNode(inorderSuccessorKv.key)
                            return true
                        }
                    }
*/

/*
//                    if (inorderSuccessorNode.getSiblings().hasNotThreeNodeSibling()) {
//                        val parent = inorderSuccessorNode.parent
//                        if (parent is ThreeNode) {
//                            if (parent.keyValue1.key == key) {
//                                val newLeft = (parent.left as TwoNode).toThreeNode(inorderSuccessorNode.keyValue1)
//                                parent.replaceWith(TwoNode(parent.keyValue2, parent = parent.parent).addLeft(newLeft).addRight(parent.right!!))
//                                return true
//                            } else if (parent.keyValue2.key == key) {
//                                val newRight = (parent.middle as TwoNode).toThreeNode(inorderSuccessorNode.keyValue1)
//                                parent.replaceWith(TwoNode(parent.keyValue1, parent = parent.parent).addLeft(parent.left!!).addRight(newRight))
//                                return true
//
//                            }
//                        }
//                    }
//
//                    if(inorderSuccessor.parent is ThreeNode){
//                        val sibl = inorderSuccessor.getSiblings()
//                        when(inorderSuccessor.getPosition()){
//                            Position.Left -> TODO()
//                            Position.Middle -> TODO()
//                            Position.Right -> TODO()
//                        }
//                    }


                    val siblings = node.getSiblings()
                    //    if(siblings.hasThreeNodeSibling())
                    when (siblings) {
                        is Sibling.TwoSiblings<K, V> -> {
                            //if i have two siblings, my parent has to be threeNode
                            val parent = node.parent as ThreeNode
                            val position = node.getPosition()
                            when (position) {
                                Left -> {
                                    //I only care about my sibling on the right side which is in the middle one
                                    if (siblings.first is TwoNode) {
                                        val newLeftKv1 = parent.keyValue1
                                        val newLeftKv2 = siblings.first.keyValue1
                                        val parentsRightSon = parent.right!!
                                        parent.replaceWith(
                                            TwoNode(
                                                keyValue1 = parent.keyValue2,
                                                parent = parent.parent
                                            )
                                                .addLeft(ThreeNode(newLeftKv1, newLeftKv2))
                                                .addRight(parentsRightSon)
                                        )
                                        return true

                                    } else {
                                        val sibling = siblings.first as ThreeNode<K, V>
                                        val newLeftKv = parent.keyValue1
                                        val newParentKv1 = sibling.keyValue1
                                        parent.left!!.keyValue1 = newLeftKv
                                        sibling.deleteFromNode(newParentKv1.key)
                                        parent.keyValue1 = newParentKv1
                                        return true
                                    }
                                }

                                Middle -> {
                                    if (siblings.first is ThreeNode || siblings.second is ThreeNode) {
                                        if (siblings.first is ThreeNode) {
                                            val sibling = siblings.first as ThreeNode<K, V>
                                            val newMiddleKv = parent.keyValue1
                                            val newParentKv1 = sibling.keyValue2
                                            sibling.deleteFromNode(newParentKv1.key)
                                            parent.middle!!.keyValue1 = newMiddleKv
                                            parent.keyValue1 = newParentKv1
                                            return true
                                        } else {
                                            //if i'm the middle of threeNode and left is 2node and right is 3node
                                            val sibling = siblings.second as ThreeNode<K, V>
                                            val newMiddleKv = parent.keyValue2
                                            val newParentKv1 = parent.right!!.keyValue1
                                            (parent.right as ThreeNode<K, V>).deleteFromNode(newParentKv1.key)
                                            parent.middle!!.keyValue1 = newMiddleKv
                                            parent.keyValue2 = newParentKv1
                                            return true
                                        }
                                    } else {
                                        val sibling = siblings.first as TwoNode<K, V>
                                        val rightToKeep = sibling.parent!!.right!!
                                        val parentKv = parent.keyValue2
                                        val newThreeNode = ThreeNode(sibling.keyValue1, parent.keyValue1)

                                        parent.replaceWith(TwoNode(parentKv, parent = parent.parent).addLeft(newThreeNode).addRight(rightToKeep))
                                        return true
                                    }
                                }

                                Right -> {
                                    //I only care about my sibling on the left side which is in the middle one
                                    if (siblings.first is TwoNode) {
                                        val sibling = siblings.first// as ThreeNode
                                        val newRightKv1 = sibling.keyValue1
                                        val newRightKv2 = parent.keyValue2
                                        val newLeft = parent.left!!
                                        parent.replaceWith(TwoNode(
                                            keyValue1 = parent.keyValue1,
                                            parent = parent.parent
                                        )
                                            .addRight(ThreeNode(newRightKv1, newRightKv2))
                                            .addLeft(newLeft)
                                        )
                                        return true

                                    } else {
                                        val sibling = siblings.first as ThreeNode<K, V>
                                        val newRightKv = parent.keyValue2
                                        val newParentKv2 = sibling.keyValue2
                                        parent.right!!.keyValue1 = newRightKv
                                        sibling.deleteFromNode(newParentKv2.key)
                                        parent.keyValue2 = newParentKv2
                                        return true
                                    }

                                }
                            }
                        }
                        is Sibling.OneSibling -> {
                            when (siblings.side) {
                                Left -> {
                                    val sibling = siblings.sibling
                                    when (sibling) {
                                        is Node.TwoNode -> {
                                            println() //TODO()
                                        }
                                        is Node.ThreeNode -> {
                                            val newRightKV = sibling.parent!!.keyValue1
                                            val newParentKv = sibling.keyValue2 as KeyValue<K, V>
                                            (sibling as ThreeNode<K, V>).deleteFromNode(newParentKv.key)
                                            sibling.parent!!.keyValue1 = newParentKv
                                            sibling.parent!!.right!!.keyValue1 = newRightKV as KeyValue<K, V>


                                            return true
                                        }
                                    }
                                }
                                Middle -> TODO()
                                Right -> {
                                    val sibling = siblings.sibling
                                    when (sibling) {
                                        is Node.TwoNode -> TODO()
                                        is Node.ThreeNode -> {
                                            //my sibling is on right side thereofre I have twondoe parent and and I'm on the left side
                                            // left side key is parent key, and parent key is left side of the three node sibling
                                            val newLeftNode = sibling.parent
                                            val newParentKv = sibling.keyValue1
                                            val sibling = sibling as ThreeNode<K, V>

                                            sibling.replaceWith(TwoNode(keyValue1 = sibling.keyValue2, parent = sibling.parent))
                                            if (sibling.isLeaf())
                                                node.keyValue1 = newLeftNode!!.keyValue1 as KeyValue<K, V>
                                            else
                                                node.replaceWith(newLeftNode as Node<K, V>)
                                            sibling.parent!!.keyValue1 = newParentKv as KeyValue<K, V>
                                            return true
                                        }
                                        else -> throw Exception("nope")
                                    }

                                }
                            }
                        }
                        Sibling.NoSiblings<K, V>() -> TODO()
                    }



                    if (inorderSuccessorNode is ThreeNode && inorderSuccessorNode.hasKids()) {

                    }*/

                }

            }
        }
        return false
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
                            parent.toThreeNode(fourNode.keyValue2!!)
                                .addMiddle   (fourNode.keyValue3!!)
                                .addLeft     (fourNode.keyValue1!!)
                                .addRight    (parent.right!!)

                        Right ->
                            parent.toThreeNode(fourNode.keyValue2!!)
                                .addMiddle    (fourNode.keyValue1!!)
                                .addLeft      (parent.left!!)
                                .addRight     (fourNode.keyValue3!!)

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
                        parent.toFourNode(fourNode.keyValue2!!)
                            .addMiddle2  (parent.middle!!)
                            .addMiddle   (fourNode.keyValue3!!)
                            .addLeft     (fourNode.keyValue1!!)
                            .addRight    (parent.right!!)

                    Middle ->
                        parent.toFourNode(fourNode.keyValue2!!)
                            .addMiddle2  (fourNode.keyValue3!!)
                            .addMiddle   (fourNode.keyValue1!!)
                            .addLeft     (parent.left!!)
                            .addRight    (parent.right!!)

                    Right ->
                        parent.toFourNode(fourNode.keyValue2!!)
                            .addMiddle2  (fourNode.keyValue1!!)
                            .addMiddle   (parent.middle!!)
                            .addLeft     (parent.left!!)
                            .addRight    (fourNode.keyValue3!!)

                } as FourNode<K, V>

                insert(tempFourNode, key,value)

            }

            is Node.FourNode   -> throw FourNodeInsertionException()

            null               ->
                root = TwoNode    (fourNode.keyValue2!!)
                        .addLeft  (fourNode.keyValue1!!)
                        .addRight (fourNode.keyValue3!!)

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
                    println()
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
                    else
                        fourNode.parent!!.replaceWith(newParent)
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
                    root= (fourNode as FourNode).split()
                }
            }
        }

    }

     private fun ThreeNode<K, V>.merge(fromSide: Position, fourNode:FourNode<K,V>): FourNode<K, V> {
        val splitted = fourNode.split()
        return  when (fromSide) {
            Left -> {
                this.toFourNode (fourNode.keyValue2!!)
                    .addMiddle2 (this.middle!!)
                    .addMiddle  (splitted.right!!)
                    .addLeft    (splitted.left!!)
                    .addRight   (this.right!!)
            }

            Middle -> {
                this.toFourNode (fourNode.keyValue2!!)
                    .addMiddle2 (splitted.right!!)
                    .addMiddle  (splitted.left!!)
                    .addLeft    (this.left!!)
                    .addRight   (this.right!!)
            }

            Right -> {
                this.toFourNode (fourNode.keyValue2!!)
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
                    if (key == parent.keyValue1!!.key) return if( parent.keyValue1!!.key==key) parent else null
                    if (key < parent.keyValue1!!.key){
                        if(parent.left == null ) return if( parent.keyValue1!!.key==key) parent else null
                        else parent = parent.left
                    }
                    else{
                        if(parent.right == null ) return if( parent.keyValue1!!.key==key) parent else null
                        else parent = parent.right
                    }
                }
                is Node.ThreeNode -> {
                    when{
                        key == parent.keyValue1!!.key    -> return if( parent.keyValue1!!.key==key) parent else null
                        key == parent.keyValue2!!.key     ->return if( parent.keyValue2!!!!.key==key) parent else null
                        key < parent.keyValue1!!.key -> if(parent.left   != null) parent = parent.left   else return if( parent.keyValue1!!.key==key) parent else null
                        key > parent.keyValue1!!.key
                            &&
                            key < parent.keyValue2!!.key -> if(parent.middle != null) parent = parent.middle else return if( parent.keyValue1!!.key==key) parent else null
                        key > parent.keyValue2!!.key -> if(parent.right  != null) parent = parent.right  else return if( parent.keyValue1!!.key==key) parent else null
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
                     if (key < parent.keyValue1!!.key){
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
                        key == parent.keyValue1!!.key    -> return (parent)
                        key == parent.keyValue2!!.key    -> return (parent)
                        key < parent.keyValue1!!.key -> if(parent.left   != null) parent = parent.left   else return (parent)
                        key > parent.keyValue1!!.key
                            &&
                        key < parent.keyValue2!!.key -> if(parent.middle != null) parent = parent.middle else return (parent)
                        key > parent.keyValue2!!.key -> if(parent.right  != null) parent = parent.right  else return (parent)
                    }
                }
                is Node.FourNode  -> throw FourNodeException()
                null              -> return (parent)
            }
        }
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

    fun levelOrder(node: Node<K, V>?): MutableList<Node<K, V>>? {
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

    fun inoredSuccesor(node: Node<K, V>,key:K): Node<K, V>? {
        val found = emptyLinkedList<Node<K, V>>()
        var result: Node<K, V>? = null
        var once=true
        inorder { currentNode ->
            found.add(currentNode)
            if (found.size > 2) {
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

    fun inorder(node: Node<K, V> = root!!,visit : (Node<K,V>) -> Unit) {

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
                println()
                TODO()
            }
        }
    }
 }

private fun <K:Comparable<K>, V> Node<K, V>.hasKey(key: K): Boolean =when(this){
//    is Node.EmptyNode -> throw IllegalStateException("Empty node in tree")

    is Node.TwoNode -> keyValue1!!.key == key
    is Node.ThreeNode -> keyValue1!!.key == key || keyValue2!!.key == key
    is Node.FourNode -> TODO()
}

fun <K : Comparable<K>, V> Node<K, V>.getPosition() : Position {
    val parent = parent!!
    return when{
        parent.left == this  -> Left
        parent.right == this -> Right
        (parent is ThreeNode) && parent.middle == this -> Middle
        else -> throw IllegalStateException("")
    }
    }
fun <K : Comparable<K>, V> ThreeNode<K, V>.deleteFromNode(key: K) =
    when (key) {
        this.keyValue1!!.key -> this.replaceWith(TwoNode(this.keyValue2, parent = this.parent))
        this.keyValue2!!.key -> this.replaceWith(TwoNode(this.keyValue1, parent = this.parent))
        else -> throw IllegalStateException("this value is not in this node")
    }

enum class Position{Left,Middle,Right}

class FourNodeInsertionException : Exception ("You shouldn't insert values into four node ")
class FourNodeException          : Exception ("Four node can't be in 23 tree")
