// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.JMSException;

public class AlreadyClosedException extends JMSException
{
    private static final long serialVersionUID = -3203104889571618702L;
    
    public AlreadyClosedException() {
        super("this connection");
    }
    
    public AlreadyClosedException(final String description) {
        super("Cannot use " + description + " as it has already been closed", "AMQ-1001");
    }
}
