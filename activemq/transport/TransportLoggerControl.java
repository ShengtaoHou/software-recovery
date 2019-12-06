// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.apache.activemq.broker.jmx.ManagedRegionBroker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerView;
import org.apache.activemq.broker.jmx.ManagementContext;

public class TransportLoggerControl implements TransportLoggerControlMBean
{
    public TransportLoggerControl(final ManagementContext managementContext) {
    }
    
    @Override
    public void disableAllTransportLoggers() {
        TransportLoggerView.disableAllTransportLoggers();
    }
    
    @Override
    public void enableAllTransportLoggers() {
        TransportLoggerView.enableAllTransportLoggers();
    }
    
    @Override
    public void reloadLog4jProperties() throws Throwable {
        new BrokerView(null, null).reloadLog4jProperties();
    }
}
