// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.apache.activemq.util.IntrospectionSupport;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.Connector;
import java.util.Map;

public class SocketConnectorFactory
{
    private Map<String, Object> transportOptions;
    
    public Connector createConnector() throws Exception {
        final SelectChannelConnector connector = new SelectChannelConnector();
        IntrospectionSupport.setProperties(connector, this.transportOptions, "");
        return (Connector)connector;
    }
    
    public Map<String, Object> getTransportOptions() {
        return this.transportOptions;
    }
    
    public void setTransportOptions(final Map<String, Object> transportOptions) {
        this.transportOptions = transportOptions;
    }
}
