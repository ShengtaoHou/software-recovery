// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

public class BrokerStoppedException extends IllegalStateException
{
    private static final long serialVersionUID = -3435230276850902220L;
    
    public BrokerStoppedException() {
    }
    
    public BrokerStoppedException(final String message, final Throwable cause) {
        super(message);
        this.initCause(cause);
    }
    
    public BrokerStoppedException(final String s) {
        super(s);
    }
    
    public BrokerStoppedException(final Throwable cause) {
        this.initCause(cause);
    }
}
