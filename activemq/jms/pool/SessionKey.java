// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

public class SessionKey
{
    private final boolean transacted;
    private final int ackMode;
    private int hash;
    
    public SessionKey(final boolean transacted, final int ackMode) {
        this.transacted = transacted;
        this.ackMode = ackMode;
        this.hash = ackMode;
        if (transacted) {
            this.hash = 31 * this.hash + 1;
        }
    }
    
    @Override
    public int hashCode() {
        return this.hash;
    }
    
    @Override
    public boolean equals(final Object that) {
        return this == that || (that instanceof SessionKey && this.equals((SessionKey)that));
    }
    
    public boolean equals(final SessionKey that) {
        return this.transacted == that.transacted && this.ackMode == that.ackMode;
    }
    
    public boolean isTransacted() {
        return this.transacted;
    }
    
    public int getAckMode() {
        return this.ackMode;
    }
}
