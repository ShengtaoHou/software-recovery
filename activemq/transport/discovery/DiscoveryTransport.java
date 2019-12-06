// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery;

import org.slf4j.LoggerFactory;
import org.apache.activemq.util.Suspendable;
import java.net.URISyntaxException;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.command.DiscoveryEvent;
import org.apache.activemq.Service;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.transport.Transport;
import java.util.Map;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.transport.CompositeTransport;
import org.slf4j.Logger;
import org.apache.activemq.transport.TransportFilter;

public class DiscoveryTransport extends TransportFilter implements DiscoveryListener
{
    private static final Logger LOG;
    private final CompositeTransport next;
    private DiscoveryAgent discoveryAgent;
    private final ConcurrentHashMap<String, URI> serviceURIs;
    private Map<String, String> parameters;
    
    public DiscoveryTransport(final CompositeTransport next) {
        super(next);
        this.serviceURIs = new ConcurrentHashMap<String, URI>();
        this.next = next;
    }
    
    @Override
    public void start() throws Exception {
        if (this.discoveryAgent == null) {
            throw new IllegalStateException("discoveryAgent not configured");
        }
        this.discoveryAgent.setDiscoveryListener(this);
        this.discoveryAgent.start();
        this.next.start();
    }
    
    @Override
    public void stop() throws Exception {
        final ServiceStopper ss = new ServiceStopper();
        ss.stop(this.discoveryAgent);
        ss.stop(this.next);
        ss.throwFirstException();
    }
    
    @Override
    public void onServiceAdd(final DiscoveryEvent event) {
        final String url = event.getServiceName();
        if (url != null) {
            try {
                URI uri = new URI(url);
                DiscoveryTransport.LOG.info("Adding new broker connection URL: " + uri);
                uri = URISupport.applyParameters(uri, this.parameters, "discovered.");
                this.serviceURIs.put(event.getServiceName(), uri);
                this.next.add(false, new URI[] { uri });
            }
            catch (URISyntaxException e) {
                DiscoveryTransport.LOG.warn("Could not connect to remote URI: " + url + " due to bad URI syntax: " + e, e);
            }
        }
    }
    
    @Override
    public void onServiceRemove(final DiscoveryEvent event) {
        final URI uri = this.serviceURIs.get(event.getServiceName());
        if (uri != null) {
            this.next.remove(false, new URI[] { uri });
        }
    }
    
    public DiscoveryAgent getDiscoveryAgent() {
        return this.discoveryAgent;
    }
    
    public void setDiscoveryAgent(final DiscoveryAgent discoveryAgent) {
        this.discoveryAgent = discoveryAgent;
    }
    
    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public void transportResumed() {
        if (this.discoveryAgent instanceof Suspendable) {
            try {
                ((Suspendable)this.discoveryAgent).suspend();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.transportResumed();
    }
    
    @Override
    public void transportInterupted() {
        if (this.discoveryAgent instanceof Suspendable) {
            try {
                ((Suspendable)this.discoveryAgent).resume();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.transportInterupted();
    }
    
    static {
        LOG = LoggerFactory.getLogger(DiscoveryTransport.class);
    }
}
