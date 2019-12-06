// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.apache.activemq.Service;

public interface ServiceListener
{
    void started(final Service p0);
    
    void stopped(final Service p0);
}
