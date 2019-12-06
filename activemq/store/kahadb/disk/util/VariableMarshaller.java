// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

public abstract class VariableMarshaller<T> implements Marshaller<T>
{
    @Override
    public int getFixedSize() {
        return -1;
    }
    
    @Override
    public boolean isDeepCopySupported() {
        return false;
    }
    
    @Override
    public T deepCopy(final T source) {
        throw new UnsupportedOperationException();
    }
}
