// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.vm;

import org.slf4j.LoggerFactory;
import org.apache.activemq.Service;
import org.apache.activemq.util.ServiceSupport;
import org.apache.activemq.transport.MarshallingTransportFilter;
import java.util.HashMap;
import org.apache.activemq.transport.TransportServer;
import org.slf4j.MDC;
import org.apache.activemq.broker.BrokerFactory;
import java.io.IOException;
import org.apache.activemq.broker.BrokerRegistry;
import java.net.URISyntaxException;
import org.apache.activemq.util.IOExceptionSupport;
import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.Transport;
import java.net.URI;
import org.apache.activemq.broker.BrokerFactoryHandler;
import org.slf4j.Logger;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.BrokerService;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.transport.TransportFactory;

public class VMTransportFactory extends TransportFactory
{
    public static final ConcurrentHashMap<String, BrokerService> BROKERS;
    public static final ConcurrentHashMap<String, TransportConnector> CONNECTORS;
    public static final ConcurrentHashMap<String, VMTransportServer> SERVERS;
    private static final Logger LOG;
    BrokerFactoryHandler brokerFactoryHandler;
    
    @Override
    public Transport doConnect(final URI location) throws Exception {
        return VMTransportServer.configure(this.doCompositeConnect(location));
    }
    
    @Override
    public Transport doCompositeConnect(URI location) throws Exception {
        boolean create = true;
        int waitForStart = -1;
        final URISupport.CompositeData data = URISupport.parseComposite(location);
        URI brokerURI;
        String host;
        Map<String, String> options;
        if (data.getComponents().length == 1 && "broker".equals(data.getComponents()[0].getScheme())) {
            brokerURI = data.getComponents()[0];
            final URISupport.CompositeData brokerData = URISupport.parseComposite(brokerURI);
            host = brokerData.getParameters().get("brokerName");
            if (host == null) {
                host = "localhost";
            }
            if (brokerData.getPath() != null) {
                host = brokerData.getPath();
            }
            options = data.getParameters();
            location = new URI("vm://" + host);
        }
        else {
            try {
                host = extractHost(location);
                options = URISupport.parseParameters(location);
                final String config = options.remove("brokerConfig");
                if (config != null) {
                    brokerURI = new URI(config);
                }
                else {
                    final Map brokerOptions = IntrospectionSupport.extractProperties(options, "broker.");
                    brokerURI = new URI("broker://()/" + host + "?" + URISupport.createQueryString(brokerOptions));
                }
                if ("false".equals(options.remove("create"))) {
                    create = false;
                }
                final String waitForStartString = options.remove("waitForStart");
                if (waitForStartString != null) {
                    waitForStart = Integer.parseInt(waitForStartString);
                }
            }
            catch (URISyntaxException e1) {
                throw IOExceptionSupport.create(e1);
            }
            location = new URI("vm://" + host);
        }
        if (host == null) {
            host = "localhost";
        }
        VMTransportServer server = VMTransportFactory.SERVERS.get(host);
        if (!this.validateBroker(host) || server == null) {
            BrokerService broker = null;
            synchronized (BrokerRegistry.getInstance().getRegistryMutext()) {
                broker = this.lookupBroker(BrokerRegistry.getInstance(), host, waitForStart);
                if (broker == null) {
                    if (!create) {
                        throw new IOException("Broker named '" + host + "' does not exist.");
                    }
                    try {
                        if (this.brokerFactoryHandler != null) {
                            broker = this.brokerFactoryHandler.createBroker(brokerURI);
                        }
                        else {
                            broker = BrokerFactory.createBroker(brokerURI);
                        }
                        broker.start();
                        MDC.put("activemq.broker", broker.getBrokerName());
                    }
                    catch (URISyntaxException e2) {
                        throw IOExceptionSupport.create(e2);
                    }
                    VMTransportFactory.BROKERS.put(host, broker);
                    BrokerRegistry.getInstance().getRegistryMutext().notifyAll();
                }
                server = VMTransportFactory.SERVERS.get(host);
                if (server == null) {
                    server = (VMTransportServer)this.bind(location, true);
                    final TransportConnector connector = new TransportConnector(server);
                    connector.setBrokerService(broker);
                    connector.setUri(location);
                    connector.setTaskRunnerFactory(broker.getTaskRunnerFactory());
                    connector.start();
                    VMTransportFactory.CONNECTORS.put(host, connector);
                }
            }
        }
        final VMTransport vmtransport = server.connect();
        IntrospectionSupport.setProperties(vmtransport.peer, new HashMap(options));
        IntrospectionSupport.setProperties(vmtransport, options);
        Transport transport = vmtransport;
        if (vmtransport.isMarshal()) {
            final Map<String, String> optionsCopy = new HashMap<String, String>(options);
            transport = new MarshallingTransportFilter(transport, this.createWireFormat(options), this.createWireFormat(optionsCopy));
        }
        if (!options.isEmpty()) {
            throw new IllegalArgumentException("Invalid connect parameters: " + options);
        }
        return transport;
    }
    
    private static String extractHost(final URI location) {
        String host = location.getHost();
        if (host == null || host.length() == 0) {
            host = location.getAuthority();
            if (host == null || host.length() == 0) {
                host = "localhost";
            }
        }
        return host;
    }
    
    private BrokerService lookupBroker(final BrokerRegistry registry, final String brokerName, final int waitForStart) {
        BrokerService broker = null;
        synchronized (registry.getRegistryMutext()) {
            broker = registry.lookup(brokerName);
            if (broker == null && waitForStart > 0) {
                for (long expiry = System.currentTimeMillis() + waitForStart; (broker == null || !broker.isStarted()) && expiry > System.currentTimeMillis(); broker = registry.lookup(brokerName)) {
                    final long timeout = Math.max(0L, expiry - System.currentTimeMillis());
                    try {
                        VMTransportFactory.LOG.debug("waiting for broker named: " + brokerName + " to start");
                        registry.getRegistryMutext().wait(timeout);
                    }
                    catch (InterruptedException ex) {}
                }
            }
        }
        return broker;
    }
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        return this.bind(location, false);
    }
    
    private TransportServer bind(final URI location, final boolean dispose) throws IOException {
        final String host = extractHost(location);
        VMTransportFactory.LOG.debug("binding to broker: " + host);
        final VMTransportServer server = new VMTransportServer(location, dispose);
        final Object currentBoundValue = VMTransportFactory.SERVERS.get(host);
        if (currentBoundValue != null) {
            throw new IOException("VMTransportServer already bound at: " + location);
        }
        VMTransportFactory.SERVERS.put(host, server);
        return server;
    }
    
    public static void stopped(final VMTransportServer server) {
        final String host = extractHost(server.getBindURI());
        stopped(host);
    }
    
    public static void stopped(final String host) {
        VMTransportFactory.SERVERS.remove(host);
        final TransportConnector connector = VMTransportFactory.CONNECTORS.remove(host);
        if (connector != null) {
            VMTransportFactory.LOG.debug("Shutting down VM connectors for broker: " + host);
            ServiceSupport.dispose(connector);
            final BrokerService broker = VMTransportFactory.BROKERS.remove(host);
            if (broker != null) {
                ServiceSupport.dispose(broker);
            }
            MDC.remove("activemq.broker");
        }
    }
    
    public BrokerFactoryHandler getBrokerFactoryHandler() {
        return this.brokerFactoryHandler;
    }
    
    public void setBrokerFactoryHandler(final BrokerFactoryHandler brokerFactoryHandler) {
        this.brokerFactoryHandler = brokerFactoryHandler;
    }
    
    private boolean validateBroker(final String host) {
        boolean result = true;
        if (VMTransportFactory.BROKERS.containsKey(host) || VMTransportFactory.SERVERS.containsKey(host) || VMTransportFactory.CONNECTORS.containsKey(host)) {
            final TransportConnector connector = VMTransportFactory.CONNECTORS.get(host);
            if (BrokerRegistry.getInstance().lookup(host) == null || (connector != null && connector.getBroker().isStopped())) {
                result = false;
                VMTransportFactory.BROKERS.remove(host);
                VMTransportFactory.SERVERS.remove(host);
                if (connector != null) {
                    VMTransportFactory.CONNECTORS.remove(host);
                    if (connector != null) {
                        ServiceSupport.dispose(connector);
                    }
                }
            }
        }
        return result;
    }
    
    static {
        BROKERS = new ConcurrentHashMap<String, BrokerService>();
        CONNECTORS = new ConcurrentHashMap<String, TransportConnector>();
        SERVERS = new ConcurrentHashMap<String, VMTransportServer>();
        LOG = LoggerFactory.getLogger(VMTransportFactory.class);
    }
}
