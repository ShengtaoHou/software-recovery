// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.IOException;

public final class IOExceptionSupport
{
    private IOExceptionSupport() {
    }
    
    public static IOException create(final String msg, final Throwable cause) {
        final IOException exception = new IOException(msg);
        exception.initCause(cause);
        return exception;
    }
    
    public static IOException create(final String msg, final Exception cause) {
        final IOException exception = new IOException(msg);
        exception.initCause(cause);
        return exception;
    }
    
    public static IOException create(final Throwable cause) {
        final IOException exception = new IOException(cause.getMessage());
        exception.initCause(cause);
        return exception;
    }
    
    public static IOException create(final Exception cause) {
        final IOException exception = new IOException(cause.getMessage());
        exception.initCause(cause);
        return exception;
    }
}
