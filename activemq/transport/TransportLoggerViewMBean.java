// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

public interface TransportLoggerViewMBean
{
    boolean isLogging();
    
    void setLogging(final boolean p0);
    
    void enableLogging();
    
    void disableLogging();
}
