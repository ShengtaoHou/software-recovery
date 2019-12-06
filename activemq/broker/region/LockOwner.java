// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

public interface LockOwner
{
    public static final LockOwner HIGH_PRIORITY_LOCK_OWNER = new LockOwner() {
        @Override
        public int getLockPriority() {
            return Integer.MAX_VALUE;
        }
        
        @Override
        public boolean isLockExclusive() {
            return false;
        }
    };
    
    int getLockPriority();
    
    boolean isLockExclusive();
}
