// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

public class WrappedException extends RuntimeException
{
    private static final long serialVersionUID = 3257290240212217905L;
    
    public WrappedException(final Throwable original) {
        super(original);
    }
}
