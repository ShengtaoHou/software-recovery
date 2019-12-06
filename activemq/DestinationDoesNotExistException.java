// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.JMSException;

public class DestinationDoesNotExistException extends JMSException
{
    public DestinationDoesNotExistException(final String destination) {
        super(destination);
    }
    
    public boolean isTemporary() {
        return this.getMessage().startsWith("temp-");
    }
    
    @Override
    public String getLocalizedMessage() {
        return "The destination " + this.getMessage() + " does not exist.";
    }
}
