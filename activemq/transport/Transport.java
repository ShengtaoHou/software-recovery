// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.net.URI;
import java.io.IOException;
import org.apache.activemq.Service;

public interface Transport extends Service
{
    void oneway(final Object p0) throws IOException;
    
    FutureResponse asyncRequest(final Object p0, final ResponseCallback p1) throws IOException;
    
    Object request(final Object p0) throws IOException;
    
    Object request(final Object p0, final int p1) throws IOException;
    
    TransportListener getTransportListener();
    
    void setTransportListener(final TransportListener p0);
    
     <T> T narrow(final Class<T> p0);
    
    String getRemoteAddress();
    
    boolean isFaultTolerant();
    
    boolean isDisposed();
    
    boolean isConnected();
    
    boolean isReconnectSupported();
    
    boolean isUpdateURIsSupported();
    
    void reconnect(final URI p0) throws IOException;
    
    void updateURIs(final boolean p0, final URI[] p1) throws IOException;
    
    int getReceiveCounter();
}
