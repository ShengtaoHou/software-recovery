// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.IllegalStateException;

public class ConnectionClosedException extends IllegalStateException
{
    private static final long serialVersionUID = -7681404582227153308L;
    
    public ConnectionClosedException() {
        super("The connection is already closed", "AlreadyClosed");
    }
}
