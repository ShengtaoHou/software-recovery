// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.peer;

import org.apache.activemq.transport.TransportServer;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.BrokerFactoryHandler;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.vm.VMTransportFactory;
import org.apache.activemq.transport.Transport;
import java.net.URI;
import org.apache.activemq.util.IdGenerator;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.transport.TransportFactory;

public class PeerTransportFactory extends TransportFactory
{
    public static final ConcurrentHashMap BROKERS;
    public static final ConcurrentHashMap CONNECTORS;
    public static final ConcurrentHashMap SERVERS;
    private static final IdGenerator ID_GENERATOR;
    
    @Override
    public Transport doConnect(final URI location) throws Exception {
        final VMTransportFactory vmTransportFactory = this.createTransportFactory(location);
        return vmTransportFactory.doConnect(location);
    }
    
    @Override
    public Transport doCompositeConnect(final URI location) throws Exception {
        final VMTransportFactory vmTransportFactory = this.createTransportFactory(location);
        return vmTransportFactory.doCompositeConnect(location);
    }
    
    private VMTransportFactory createTransportFactory(final URI location) throws IOException {
        try {
            String group = location.getHost();
            String broker = URISupport.stripPrefix(location.getPath(), "/");
            if (group == null) {
                group = "default";
            }
            if (broker == null || broker.length() == 0) {
                broker = PeerTransportFactory.ID_GENERATOR.generateSanitizedId();
            }
            final Map<String, String> brokerOptions = new HashMap<String, String>(URISupport.parseParameters(location));
            if (!brokerOptions.containsKey("persistent")) {
                brokerOptions.put("persistent", "false");
            }
            final URI finalLocation = new URI("vm://" + broker);
            final String finalBroker = broker;
            final String finalGroup = group;
            final VMTransportFactory rc = new VMTransportFactory() {
                @Override
                public Transport doConnect(final URI ignore) throws Exception {
                    return super.doConnect(finalLocation);
                }
                
                @Override
                public Transport doCompositeConnect(final URI ignore) throws Exception {
                    return super.doCompositeConnect(finalLocation);
                }
            };
            rc.setBrokerFactoryHandler(new BrokerFactoryHandler() {
                @Override
                public BrokerService createBroker(final URI brokerURI) throws Exception {
                    final BrokerService service = new BrokerService();
                    IntrospectionSupport.setProperties(service, brokerOptions);
                    service.setBrokerName(finalBroker);
                    final TransportConnector c = service.addConnector("tcp://0.0.0.0:0");
                    c.setDiscoveryUri(new URI("multicast://default?group=" + finalGroup));
                    service.addNetworkConnector("multicast://default?group=" + finalGroup);
                    return service;
                }
            });
            return rc;
        }
        catch (URISyntaxException e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        throw new IOException("This protocol does not support being bound.");
    }
    
    static {
        BROKERS = new ConcurrentHashMap();
        CONNECTORS = new ConcurrentHashMap();
        SERVERS = new ConcurrentHashMap();
        ID_GENERATOR = new IdGenerator("peer-");
    }
}
