// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import java.util.AbstractList;

public class InsertionCountList<T> extends AbstractList<T>
{
    int size;
    
    public InsertionCountList() {
        this.size = 0;
    }
    
    @Override
    public void add(final int index, final T element) {
        ++this.size;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public T get(final int index) {
        return null;
    }
}
