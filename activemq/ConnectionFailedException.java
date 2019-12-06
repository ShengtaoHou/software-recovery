// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.io.IOException;
import javax.jms.JMSException;

public class ConnectionFailedException extends JMSException
{
    private static final long serialVersionUID = 2288453203492073973L;
    
    public ConnectionFailedException(final IOException cause) {
        super("The JMS connection has failed: " + extractMessage(cause));
        this.initCause(cause);
        this.setLinkedException(cause);
    }
    
    public ConnectionFailedException() {
        super("The JMS connection has failed due to a Transport problem");
    }
    
    private static String extractMessage(final IOException cause) {
        String m = cause.getMessage();
        if (m == null || m.length() == 0) {
            m = cause.toString();
        }
        return m;
    }
}
