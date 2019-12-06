// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.net.URI;

public interface CompositeTransport extends Transport
{
    void add(final boolean p0, final URI[] p1);
    
    void remove(final boolean p0, final URI[] p1);
}
