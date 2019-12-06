// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import java.util.Iterator;
import org.apache.activemq.util.ByteSequence;
import java.io.IOException;

public interface PList
{
    String getName();
    
    void destroy() throws IOException;
    
    Object addFirst(final String p0, final ByteSequence p1) throws IOException;
    
    Object addLast(final String p0, final ByteSequence p1) throws IOException;
    
    boolean remove(final Object p0) throws IOException;
    
    boolean isEmpty();
    
    PListIterator iterator() throws IOException;
    
    long size();
    
    public interface PListIterator extends Iterator<PListEntry>
    {
        void release();
    }
}
