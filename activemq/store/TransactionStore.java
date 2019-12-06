// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import java.io.IOException;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.Service;

public interface TransactionStore extends Service
{
    void prepare(final TransactionId p0) throws IOException;
    
    void commit(final TransactionId p0, final boolean p1, final Runnable p2, final Runnable p3) throws IOException;
    
    void rollback(final TransactionId p0) throws IOException;
    
    void recover(final TransactionRecoveryListener p0) throws IOException;
}
