// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.IllegalStateException;

public class NotStartedException extends IllegalStateException
{
    private static final long serialVersionUID = -4907909323529887659L;
    
    public NotStartedException() {
        super("IllegalState: This service has not yet been started", "AMQ-1003");
    }
}
