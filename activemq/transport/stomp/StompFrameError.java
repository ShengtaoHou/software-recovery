// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

public class StompFrameError extends StompFrame
{
    private final ProtocolException exception;
    
    public StompFrameError(final ProtocolException exception) {
        this.exception = exception;
    }
    
    public ProtocolException getException() {
        return this.exception;
    }
}
