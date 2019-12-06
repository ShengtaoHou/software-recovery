// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.slf4j.LoggerFactory;
import javax.management.ObjectName;
import org.apache.activemq.broker.BrokerService;
import java.util.Iterator;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.Service;
import org.apache.activemq.util.ServiceSupport;
import org.apache.activemq.transport.TransportFactory;
import org.apache.activemq.broker.SslContext;
import java.net.URISyntaxException;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.discovery.DiscoveryAgentFactory;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.command.DiscoveryEvent;
import java.net.URI;
import java.util.concurrent.ConcurrentMap;
import java.util.Map;
import org.apache.activemq.transport.discovery.DiscoveryAgent;
import org.slf4j.Logger;
import org.apache.activemq.transport.discovery.DiscoveryListener;

public class DiscoveryNetworkConnector extends NetworkConnector implements DiscoveryListener
{
    private static final Logger LOG;
    private DiscoveryAgent discoveryAgent;
    private Map<String, String> parameters;
    private final ConcurrentMap<URI, DiscoveryEvent> activeEvents;
    private URI discoveryUri;
    
    public DiscoveryNetworkConnector() {
        this.activeEvents = new ConcurrentHashMap<URI, DiscoveryEvent>();
    }
    
    public DiscoveryNetworkConnector(final URI discoveryURI) throws IOException {
        this.activeEvents = new ConcurrentHashMap<URI, DiscoveryEvent>();
        this.setUri(discoveryURI);
    }
    
    public void setUri(final URI discoveryURI) throws IOException {
        this.discoveryUri = discoveryURI;
        this.setDiscoveryAgent(DiscoveryAgentFactory.createDiscoveryAgent(discoveryURI));
        try {
            this.parameters = URISupport.parseParameters(discoveryURI);
            IntrospectionSupport.setProperties(this.getDiscoveryAgent(), this.parameters);
        }
        catch (URISyntaxException e) {
            DiscoveryNetworkConnector.LOG.warn("failed to parse query parameters from discoveryURI: {}", discoveryURI, e);
        }
    }
    
    public URI getUri() {
        return this.discoveryUri;
    }
    
    @Override
    public void onServiceAdd(final DiscoveryEvent event) {
        if (this.serviceSupport.isStopped() || this.serviceSupport.isStopping()) {
            return;
        }
        final String url = event.getServiceName();
        if (url != null) {
            URI uri;
            try {
                uri = new URI(url);
            }
            catch (URISyntaxException e) {
                DiscoveryNetworkConnector.LOG.warn("Could not connect to remote URI: {} due to bad URI syntax: ", url, e);
                return;
            }
            if (this.localURI.equals(uri)) {
                DiscoveryNetworkConnector.LOG.debug("not connecting loopback: {}", uri);
                return;
            }
            if (this.connectionFilter != null && !this.connectionFilter.connectTo(uri)) {
                DiscoveryNetworkConnector.LOG.debug("connectionFilter disallows connection to: {}", uri);
                return;
            }
            if (this.activeEvents.putIfAbsent(uri, event) != null) {
                DiscoveryNetworkConnector.LOG.debug("Discovery agent generated a duplicate onServiceAdd event for: {}", uri);
                return;
            }
            URI connectUri = uri;
            try {
                connectUri = URISupport.applyParameters(connectUri, this.parameters, "discovered.");
            }
            catch (URISyntaxException e2) {
                DiscoveryNetworkConnector.LOG.warn("could not apply query parameters: {} to: {}", (Object)new Object[] { this.parameters, connectUri }, e2);
            }
            DiscoveryNetworkConnector.LOG.info("Establishing network connection from {} to {}", this.localURI, connectUri);
            Transport remoteTransport;
            Transport localTransport;
            try {
                SslContext.setCurrentSslContext(this.getBrokerService().getSslContext());
                try {
                    remoteTransport = TransportFactory.connect(connectUri);
                }
                catch (Exception e3) {
                    DiscoveryNetworkConnector.LOG.warn("Could not connect to remote URI: {}: {}", connectUri, e3.getMessage());
                    DiscoveryNetworkConnector.LOG.debug("Connection failure exception: ", e3);
                    this.activeEvents.remove(uri);
                    return;
                }
                try {
                    localTransport = this.createLocalTransport();
                }
                catch (Exception e3) {
                    ServiceSupport.dispose(remoteTransport);
                    DiscoveryNetworkConnector.LOG.warn("Could not connect to local URI: {}: {}", this.localURI, e3.getMessage());
                    DiscoveryNetworkConnector.LOG.debug("Connection failure exception: ", e3);
                    this.activeEvents.remove(uri);
                    return;
                }
            }
            finally {
                SslContext.setCurrentSslContext(null);
            }
            final NetworkBridge bridge = this.createBridge(localTransport, remoteTransport, event);
            try {
                synchronized (this.bridges) {
                    this.bridges.put(uri, bridge);
                }
                bridge.start();
            }
            catch (Exception e4) {
                ServiceSupport.dispose(localTransport);
                ServiceSupport.dispose(remoteTransport);
                DiscoveryNetworkConnector.LOG.warn("Could not start network bridge between: {} and: {} due to: {}", this.localURI, uri, e4.getMessage());
                DiscoveryNetworkConnector.LOG.debug("Start failure exception: ", e4);
                try {
                    this.discoveryAgent.serviceFailed(event);
                }
                catch (IOException e5) {
                    DiscoveryNetworkConnector.LOG.debug("Discovery agent failure while handling failure event: {}", e5.getMessage(), e5);
                }
            }
        }
    }
    
    @Override
    public void onServiceRemove(final DiscoveryEvent event) {
        final String url = event.getServiceName();
        if (url != null) {
            URI uri;
            try {
                uri = new URI(url);
            }
            catch (URISyntaxException e) {
                DiscoveryNetworkConnector.LOG.warn("Could not connect to remote URI: {} due to bad URI syntax: ", url, e);
                return;
            }
            if (this.activeEvents.remove(uri, event)) {
                synchronized (this.bridges) {
                    this.bridges.remove(uri);
                }
            }
        }
    }
    
    public DiscoveryAgent getDiscoveryAgent() {
        return this.discoveryAgent;
    }
    
    public void setDiscoveryAgent(final DiscoveryAgent discoveryAgent) {
        this.discoveryAgent = discoveryAgent;
        if (discoveryAgent != null) {
            this.discoveryAgent.setDiscoveryListener(this);
        }
    }
    
    @Override
    protected void handleStart() throws Exception {
        if (this.discoveryAgent == null) {
            throw new IllegalStateException("You must configure the 'discoveryAgent' property");
        }
        this.discoveryAgent.start();
        super.handleStart();
    }
    
    @Override
    protected void handleStop(final ServiceStopper stopper) throws Exception {
        for (final NetworkBridge bridge : this.bridges.values()) {
            try {
                bridge.stop();
            }
            catch (Exception e) {
                stopper.onException(this, e);
            }
        }
        this.bridges.clear();
        this.activeEvents.clear();
        try {
            this.discoveryAgent.stop();
        }
        catch (Exception e2) {
            stopper.onException(this, e2);
        }
        super.handleStop(stopper);
    }
    
    protected NetworkBridge createBridge(final Transport localTransport, final Transport remoteTransport, final DiscoveryEvent event) {
        class DiscoverNetworkBridgeListener extends MBeanNetworkListener
        {
            final /* synthetic */ DiscoveryEvent val$event;
            
            public DiscoverNetworkBridgeListener(final BrokerService connectorName, final ObjectName val$event) {
                this.val$event = (DiscoveryEvent)val$event;
                super(brokerService, DiscoveryNetworkConnector.this, (ObjectName)connectorName);
            }
            
            @Override
            public void bridgeFailed() {
                if (!DiscoveryNetworkConnector.this.serviceSupport.isStopped()) {
                    try {
                        DiscoveryNetworkConnector.this.discoveryAgent.serviceFailed(this.val$event);
                    }
                    catch (IOException ex) {}
                }
            }
        }
        final NetworkBridgeListener listener = new DiscoverNetworkBridgeListener(this.getBrokerService(), this.getObjectName(), event);
        final DemandForwardingBridge result = NetworkBridgeFactory.createBridge(this, localTransport, remoteTransport, listener);
        result.setBrokerService(this.getBrokerService());
        return this.configureBridge(result);
    }
    
    @Override
    public String toString() {
        return "DiscoveryNetworkConnector:" + this.getName() + ":" + this.getBrokerService();
    }
    
    static {
        LOG = LoggerFactory.getLogger(DiscoveryNetworkConnector.class);
    }
}
