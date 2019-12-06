// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.broker.Connection;
import org.apache.activemq.transport.Transport;
import java.net.URISyntaxException;
import java.io.IOException;
import javax.management.MBeanServer;
import org.apache.activemq.transport.TransportServer;
import javax.management.ObjectName;
import org.apache.activemq.broker.TransportConnector;

public class ManagedTransportConnector extends TransportConnector
{
    static long nextConnectionId;
    private final ManagementContext managementContext;
    private final ObjectName connectorName;
    
    public ManagedTransportConnector(final ManagementContext context, final ObjectName connectorName, final TransportServer server) {
        super(server);
        this.managementContext = context;
        this.connectorName = connectorName;
    }
    
    public ManagedTransportConnector asManagedConnector(final MBeanServer mbeanServer, final ObjectName connectorName) throws IOException, URISyntaxException {
        return this;
    }
    
    @Override
    protected Connection createConnection(final Transport transport) throws IOException {
        return new ManagedTransportConnection(this, transport, this.getBroker(), this.isDisableAsyncDispatch() ? null : this.getTaskRunnerFactory(), this.getBrokerService().getTaskRunnerFactory(), this.managementContext, this.connectorName);
    }
    
    protected static synchronized long getNextConnectionId() {
        return ManagedTransportConnector.nextConnectionId++;
    }
    
    static {
        ManagedTransportConnector.nextConnectionId = 1L;
    }
}
