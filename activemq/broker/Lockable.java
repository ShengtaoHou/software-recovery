// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.io.IOException;

public interface Lockable
{
    void setUseLock(final boolean p0);
    
    Locker createDefaultLocker() throws IOException;
    
    void setLocker(final Locker p0) throws IOException;
    
    void setLockKeepAlivePeriod(final long p0);
    
    long getLockKeepAlivePeriod();
}
