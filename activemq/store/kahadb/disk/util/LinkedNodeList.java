// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.util.ArrayList;

public class LinkedNodeList<T extends LinkedNode<T>>
{
    T head;
    int size;
    
    public boolean isEmpty() {
        return this.head == null;
    }
    
    public void addLast(final T node) {
        node.linkToTail(this);
    }
    
    public void addFirst(final T node) {
        node.linkToHead(this);
    }
    
    public T getHead() {
        return this.head;
    }
    
    public T getTail() {
        return (T)((this.head != null) ? this.head.prev : null);
    }
    
    public void clear() {
        while (this.head != null) {
            this.head.unlink();
        }
    }
    
    public void addLast(final LinkedNodeList<T> list) {
        if (list.isEmpty()) {
            return;
        }
        if (this.head == null) {
            this.head = list.head;
            this.reparent(list);
        }
        else {
            this.getTail().linkAfter((LinkedNodeList<LinkedNode<LinkedNode<LinkedNode<T>>>>)list);
        }
    }
    
    public void addFirst(final LinkedNodeList<T> list) {
        if (list.isEmpty()) {
            return;
        }
        if (this.head == null) {
            this.reparent(list);
            this.head = list.head;
            list.head = null;
        }
        else {
            this.getHead().linkBefore((LinkedNodeList<LinkedNode<LinkedNode<LinkedNode<T>>>>)list);
        }
    }
    
    public T reparent(final LinkedNodeList<T> list) {
        this.size += list.size;
        T n = list.head;
        do {
            n.list = (LinkedNodeList<T>)this;
            n = (T)n.next;
        } while (n != list.head);
        list.head = null;
        list.size = 0;
        return n;
    }
    
    public T rotate() {
        if (this.head == null) {
            return null;
        }
        return this.head = this.head.getNextCircular();
    }
    
    public void rotateTo(final T head) {
        assert head != null : "Cannot rotate to a null head";
        assert head.list == this : "Cannot rotate to a node not linked to this list";
        this.head = head;
    }
    
    public int size() {
        return this.size;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (T cur = this.getHead(); cur != null; cur = cur.getNext()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(cur);
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
    
    public ArrayList<T> toArrayList() {
        final ArrayList<T> rc = new ArrayList<T>(this.size);
        for (T cur = this.head; cur != null; cur = cur.getNext()) {
            rc.add(cur);
        }
        return rc;
    }
}
