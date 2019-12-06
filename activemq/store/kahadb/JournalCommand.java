// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import org.apache.activemq.store.kahadb.data.KahaEntryType;
import java.io.IOException;
import org.apache.activemq.protobuf.Message;

public interface JournalCommand<T> extends Message<T>
{
    void visit(final Visitor p0) throws IOException;
    
    KahaEntryType type();
}
