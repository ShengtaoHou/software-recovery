// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

import java.io.IOException;
import org.apache.activemq.Service;

@Deprecated
public interface DatabaseLocker extends Service
{
    void setPersistenceAdapter(final JDBCPersistenceAdapter p0) throws IOException;
    
    boolean keepAlive() throws IOException;
    
    void setLockAcquireSleepInterval(final long p0);
}
