// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.network.jms.JmsConnector;

public class JmsConnectorView implements JmsConnectorViewMBean
{
    private final JmsConnector connector;
    
    public JmsConnectorView(final JmsConnector connector) {
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
