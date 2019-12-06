// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.apache.activemq.store.PersistenceAdapter;
import java.io.IOException;
import org.apache.activemq.Service;

public interface Locker extends Service
{
    boolean keepAlive() throws IOException;
    
    void setLockAcquireSleepInterval(final long p0);
    
    void setName(final String p0);
    
    void setFailIfLocked(final boolean p0);
    
    void setLockable(final LockableServiceSupport p0);
    
    void configure(final PersistenceAdapter p0) throws IOException;
}
