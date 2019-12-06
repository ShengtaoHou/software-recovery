// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import org.apache.activemq.transport.MutexTransport;
import java.util.HashMap;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.broker.BrokerContext;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.transport.tcp.TcpTransportFactory;

public class StompTransportFactory extends TcpTransportFactory implements BrokerServiceAware
{
    private BrokerContext brokerContext;
    
    public StompTransportFactory() {
        this.brokerContext = null;
    }
    
    @Override
    protected String getDefaultWireFormatType() {
        return "stomp";
    }
    
    @Override
    public Transport compositeConfigure(Transport transport, final WireFormat format, final Map options) {
        transport = new StompTransportFilter(transport, format, this.brokerContext);
        IntrospectionSupport.setProperties(transport, options);
        return super.compositeConfigure(transport, format, options);
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerContext = brokerService.getBrokerContext();
    }
    
    @Override
    public Transport serverConfigure(Transport transport, final WireFormat format, final HashMap options) throws Exception {
        transport = super.serverConfigure(transport, format, options);
        final MutexTransport mutex = transport.narrow(MutexTransport.class);
        if (mutex != null) {
            mutex.setSyncOnCommand(true);
        }
        return transport;
    }
    
    @Override
    protected Transport createInactivityMonitor(final Transport transport, final WireFormat format) {
        final StompInactivityMonitor monitor = new StompInactivityMonitor(transport, format);
        final StompTransportFilter filter = transport.narrow(StompTransportFilter.class);
        filter.setInactivityMonitor(monitor);
        return monitor;
    }
}
