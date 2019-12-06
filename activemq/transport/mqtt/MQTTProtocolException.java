// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import java.io.IOException;

public class MQTTProtocolException extends IOException
{
    private static final long serialVersionUID = -2869735532997332242L;
    private final boolean fatal;
    
    public MQTTProtocolException() {
        this((String)null);
    }
    
    public MQTTProtocolException(final String s) {
        this(s, false);
    }
    
    public MQTTProtocolException(final String s, final boolean fatal) {
        this(s, fatal, null);
    }
    
    public MQTTProtocolException(final String s, final boolean fatal, final Throwable cause) {
        super(s);
        this.fatal = fatal;
        this.initCause(cause);
    }
    
    public boolean isFatal() {
        return this.fatal;
    }
}
