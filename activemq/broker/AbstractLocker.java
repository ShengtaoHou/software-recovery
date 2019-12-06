// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.io.IOException;
import org.apache.activemq.util.ServiceSupport;

public abstract class AbstractLocker extends ServiceSupport implements Locker
{
    public static final long DEFAULT_LOCK_ACQUIRE_SLEEP_INTERVAL = 10000L;
    protected String name;
    protected boolean failIfLocked;
    protected long lockAcquireSleepInterval;
    protected LockableServiceSupport lockable;
    
    public AbstractLocker() {
        this.failIfLocked = false;
        this.lockAcquireSleepInterval = 10000L;
    }
    
    @Override
    public boolean keepAlive() throws IOException {
        return true;
    }
    
    @Override
    public void setLockAcquireSleepInterval(final long lockAcquireSleepInterval) {
        this.lockAcquireSleepInterval = lockAcquireSleepInterval;
    }
    
    public long getLockAcquireSleepInterval() {
        return this.lockAcquireSleepInterval;
    }
    
    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public void setFailIfLocked(final boolean failIfLocked) {
        this.failIfLocked = failIfLocked;
    }
    
    @Override
    public void setLockable(final LockableServiceSupport lockableServiceSupport) {
        this.lockable = lockableServiceSupport;
    }
}
