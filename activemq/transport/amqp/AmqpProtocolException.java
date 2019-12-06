// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import java.io.IOException;

public class AmqpProtocolException extends IOException
{
    private static final long serialVersionUID = -2869735532997332242L;
    private final String symbolicName;
    private final boolean fatal;
    
    public AmqpProtocolException() {
        this((String)null);
    }
    
    public AmqpProtocolException(final String s) {
        this(s, false);
    }
    
    public AmqpProtocolException(final String s, final boolean fatal) {
        this(s, fatal, null);
    }
    
    public AmqpProtocolException(final String s, final String msg) {
        this(s, msg, false, null);
    }
    
    public AmqpProtocolException(final String s, final boolean fatal, final Throwable cause) {
        this("error", s, fatal, cause);
    }
    
    public AmqpProtocolException(final String symbolicName, final String s, final boolean fatal, final Throwable cause) {
        super(s);
        this.symbolicName = symbolicName;
        this.fatal = fatal;
        this.initCause(cause);
    }
    
    public boolean isFatal() {
        return this.fatal;
    }
    
    public String getSymbolicName() {
        return this.symbolicName;
    }
}
