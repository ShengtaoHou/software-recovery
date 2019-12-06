// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.io.IOException;

public class ProtocolException extends IOException
{
    private static final long serialVersionUID = -2869735532997332242L;
    private final boolean fatal;
    
    public ProtocolException() {
        this((String)null);
    }
    
    public ProtocolException(final String s) {
        this(s, false);
    }
    
    public ProtocolException(final String s, final boolean fatal) {
        this(s, fatal, null);
    }
    
    public ProtocolException(final String s, final boolean fatal, final Throwable cause) {
        super(s);
        this.fatal = fatal;
        this.initCause(cause);
    }
    
    public boolean isFatal() {
        return this.fatal;
    }
}
