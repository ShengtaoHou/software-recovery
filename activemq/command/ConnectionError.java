// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class ConnectionError extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 16;
    private ConnectionId connectionId;
    private Throwable exception;
    
    @Override
    public byte getDataStructureType() {
        return 16;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processConnectionError(this);
    }
    
    public Throwable getException() {
        return this.exception;
    }
    
    public void setException(final Throwable exception) {
        this.exception = exception;
    }
    
    public ConnectionId getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final ConnectionId connectionId) {
        this.connectionId = connectionId;
    }
}
