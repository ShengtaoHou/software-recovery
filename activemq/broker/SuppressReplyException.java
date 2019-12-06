// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.io.IOException;

public class SuppressReplyException extends RuntimeException
{
    public SuppressReplyException(final Throwable cause) {
        super(cause);
    }
    
    public SuppressReplyException(final String reason) {
        super(reason);
    }
    
    public SuppressReplyException(final String reason, final IOException cause) {
        super(reason, cause);
    }
}
