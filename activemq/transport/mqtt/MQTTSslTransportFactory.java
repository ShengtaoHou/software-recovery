// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import org.apache.activemq.transport.MutexTransport;
import java.util.HashMap;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.transport.tcp.SslTransportFactory;

public class MQTTSslTransportFactory extends SslTransportFactory implements BrokerServiceAware
{
    private BrokerService brokerService;
    
    public MQTTSslTransportFactory() {
        this.brokerService = null;
    }
    
    @Override
    protected String getDefaultWireFormatType() {
        return "mqtt";
    }
    
    @Override
    public Transport compositeConfigure(Transport transport, final WireFormat format, final Map options) {
        transport = new MQTTTransportFilter(transport, format, this.brokerService);
        IntrospectionSupport.setProperties(transport, options);
        return super.compositeConfigure(transport, format, options);
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
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
    }
    
    @Override
    protected Transport createInactivityMonitor(final Transport transport, final WireFormat format) {
        final MQTTInactivityMonitor monitor = new MQTTInactivityMonitor(transport, format);
        final MQTTTransportFilter filter = transport.narrow(MQTTTransportFilter.class);
        filter.setInactivityMonitor(monitor);
        return monitor;
    }
}
