// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public interface Marshaller<T>
{
    void writePayload(final T p0, final DataOutput p1) throws IOException;
    
    T readPayload(final DataInput p0) throws IOException;
    
    int getFixedSize();
    
    boolean isDeepCopySupported();
    
    T deepCopy(final T p0);
}
