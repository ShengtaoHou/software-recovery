// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.io.IOException;

public class InactivityIOException extends IOException
{
    private static final long serialVersionUID = 5816001466763503220L;
    
    public InactivityIOException() {
    }
    
    public InactivityIOException(final String message) {
        super(message);
    }
}
