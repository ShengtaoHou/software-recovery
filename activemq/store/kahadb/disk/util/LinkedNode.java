// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

public class LinkedNode<T extends LinkedNode<T>>
{
    protected LinkedNodeList<T> list;
    protected T next;
    protected T prev;
    
    private T getThis() {
        return (T)this;
    }
    
    public T getHeadNode() {
        return this.list.head;
    }
    
    public T getTailNode() {
        return this.list.head.prev;
    }
    
    public T getNext() {
        return (T)(this.isTailNode() ? null : this.next);
    }
    
    public T getPrevious() {
        return (T)(this.isHeadNode() ? null : this.prev);
    }
    
    public T getNextCircular() {
        return this.next;
    }
    
    public T getPreviousCircular() {
        return this.prev;
    }
    
    public boolean isHeadNode() {
        return this.list.head == this;
    }
    
    public boolean isTailNode() {
        return this.list.head.prev == this;
    }
    
    public void linkAfter(final T node) {
        if (node == this) {
            throw new IllegalArgumentException("You cannot link to yourself");
        }
        if (node.list != null) {
            throw new IllegalArgumentException("You only insert nodes that are not in a list");
        }
        if (this.list == null) {
            throw new IllegalArgumentException("This node is not yet in a list");
        }
        node.list = this.list;
        node.prev = this.getThis();
        node.next = this.next;
        this.next.prev = node;
        this.next = node;
        final LinkedNodeList<T> list = this.list;
        ++list.size;
    }
    
    public void linkAfter(final LinkedNodeList<T> rightList) {
        if (rightList == this.list) {
            throw new IllegalArgumentException("You cannot link to yourself");
        }
        if (this.list == null) {
            throw new IllegalArgumentException("This node is not yet in a list");
        }
        final T rightHead = rightList.head;
        final T rightTail = rightList.head.prev;
        this.list.reparent(rightList);
        rightHead.prev = this.getThis();
        rightTail.next = this.next;
        this.next.prev = rightTail;
        this.next = rightHead;
    }
    
    public void linkBefore(final T node) {
        if (node == this) {
            throw new IllegalArgumentException("You cannot link to yourself");
        }
        if (node.list != null) {
            throw new IllegalArgumentException("You only insert nodes that are not in a list");
        }
        if (this.list == null) {
            throw new IllegalArgumentException("This node is not yet in a list");
        }
        node.list = this.list;
        node.next = this.getThis();
        node.prev = this.prev;
        this.prev.next = node;
        this.prev = node;
        if (this == this.list.head) {
            this.list.head = node;
        }
        final LinkedNodeList<T> list = this.list;
        ++list.size;
    }
    
    public void linkBefore(final LinkedNodeList<T> leftList) {
        if (leftList == this.list) {
            throw new IllegalArgumentException("You cannot link to yourself");
        }
        if (this.list == null) {
            throw new IllegalArgumentException("This node is not yet in a list");
        }
        final T leftHead = leftList.head;
        final T leftTail = leftList.head.prev;
        this.list.reparent(leftList);
        leftTail.next = this.getThis();
        leftHead.prev = this.prev;
        this.prev.next = leftHead;
        this.prev = leftTail;
        if (this.isHeadNode()) {
            this.list.head = leftHead;
        }
    }
    
    public void linkToTail(final LinkedNodeList<T> target) {
        if (this.list != null) {
            throw new IllegalArgumentException("This node is already linked to a node");
        }
        if (target.head == null) {
            final LinkedNode<T> this2 = (LinkedNode<T>)this.getThis();
            target.head = (T)this2;
            this.prev = (T)this2;
            this.next = (T)this2;
            this.list = target;
            final LinkedNodeList<T> list = this.list;
            ++list.size;
        }
        else {
            target.head.prev.linkAfter(this.getThis());
        }
    }
    
    public void linkToHead(final LinkedNodeList<T> target) {
        if (this.list != null) {
            throw new IllegalArgumentException("This node is already linked to a list");
        }
        if (target.head == null) {
            final LinkedNode<T> this2 = (LinkedNode<T>)this.getThis();
            target.head = (T)this2;
            this.prev = (T)this2;
            this.next = (T)this2;
            this.list = target;
            final LinkedNodeList<T> list = this.list;
            ++list.size;
        }
        else {
            target.head.linkBefore(this.getThis());
        }
    }
    
    public boolean unlink() {
        if (this.list == null) {
            return false;
        }
        if (this.getThis() == this.prev) {
            this.list.head = null;
        }
        else {
            this.next.prev = this.prev;
            this.prev.next = this.next;
            if (this.isHeadNode()) {
                this.list.head = this.next;
            }
        }
        final LinkedNodeList<T> list = this.list;
        --list.size;
        this.list = null;
        return true;
    }
    
    public LinkedNodeList<T> splitAfter() {
        if (this.isTailNode()) {
            return new LinkedNodeList<T>();
        }
        final LinkedNodeList<T> newList = new LinkedNodeList<T>();
        newList.head = this.next;
        newList.head.prev = this.list.head.prev;
        newList.head.prev.next = newList.head;
        this.next = this.list.head;
        this.list.head.prev = this.getThis();
        T n = newList.head;
        do {
            n.list = newList;
            n = n.next;
            final LinkedNodeList<T> list = newList;
            ++list.size;
            final LinkedNodeList<T> list2 = this.list;
            --list2.size;
        } while (n != newList.head);
        return newList;
    }
    
    public LinkedNodeList<T> splitBefore() {
        if (this.isHeadNode()) {
            return new LinkedNodeList<T>();
        }
        final LinkedNodeList<T> newList = new LinkedNodeList<T>();
        newList.head = this.list.head;
        this.list.head = (T)this.getThis();
        final T newListTail = this.prev;
        this.prev = newList.head.prev;
        this.prev.next = this.getThis();
        newList.head.prev = newListTail;
        newListTail.next = newList.head;
        T n = newList.head;
        do {
            n.list = newList;
            n = n.next;
            final LinkedNodeList<T> list = newList;
            ++list.size;
            final LinkedNodeList<T> list2 = this.list;
            --list2.size;
        } while (n != newList.head);
        return newList;
    }
    
    public boolean isLinked() {
        return this.list != null;
    }
    
    public LinkedNodeList<T> getList() {
        return this.list;
    }
}
