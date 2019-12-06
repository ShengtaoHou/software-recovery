// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.proxy.ProxyConnector;

public class ProxyConnectorView implements ProxyConnectorViewMBean
{
    private final ProxyConnector connector;
    
    public ProxyConnectorView(final ProxyConnector connector) {
        this.connector = connector;
    }
    
    @Override
    public void start() throws Exception {
        this.connector.start();
    }
    
    @Override
    public void stop() throws Exception {
        this.connector.stop();
    }
}
