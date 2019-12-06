// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.http;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.command.DiscoveryEvent;
import java.util.Scanner;
import org.apache.http.client.methods.HttpGet;
import java.util.Set;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.client.methods.HttpPut;
import java.io.IOException;
import org.apache.http.impl.client.DefaultHttpClient;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.Service;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.activemq.transport.discovery.DiscoveryListener;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.apache.activemq.util.Suspendable;
import org.apache.activemq.transport.discovery.DiscoveryAgent;

public class HTTPDiscoveryAgent implements DiscoveryAgent, Suspendable
{
    private static final Logger LOG;
    private String registryURL;
    private HttpClient httpClient;
    private AtomicBoolean running;
    private final AtomicReference<DiscoveryListener> discoveryListener;
    private final HashSet<String> registeredServices;
    private final HashMap<String, SimpleDiscoveryEvent> discoveredServices;
    private Thread thread;
    private long updateInterval;
    private String brokerName;
    private boolean startEmbeddRegistry;
    private Service jetty;
    private AtomicInteger startCounter;
    private long initialReconnectDelay;
    private long maxReconnectDelay;
    private long backOffMultiplier;
    private boolean useExponentialBackOff;
    private int maxReconnectAttempts;
    private final Object sleepMutex;
    private final Object updateMutex;
    private UpdateState updateState;
    private long minConnectTime;
    
    public HTTPDiscoveryAgent() {
        this.registryURL = "http://localhost:8080/discovery-registry/default";
        this.httpClient = (HttpClient)new DefaultHttpClient();
        this.running = new AtomicBoolean();
        this.discoveryListener = new AtomicReference<DiscoveryListener>();
        this.registeredServices = new HashSet<String>();
        this.discoveredServices = new HashMap<String, SimpleDiscoveryEvent>();
        this.updateInterval = 10000L;
        this.startEmbeddRegistry = false;
        this.startCounter = new AtomicInteger(0);
        this.initialReconnectDelay = 1000L;
        this.maxReconnectDelay = 30000L;
        this.backOffMultiplier = 2L;
        this.useExponentialBackOff = true;
        this.sleepMutex = new Object();
        this.updateMutex = new Object();
        this.updateState = UpdateState.RESUMED;
        this.minConnectTime = 5000L;
    }
    
    public String getGroup() {
        return null;
    }
    
    @Override
    public void registerService(final String service) throws IOException {
        synchronized (this.registeredServices) {
            this.registeredServices.add(service);
        }
        this.doRegister(service);
    }
    
    private synchronized void doRegister(final String service) {
        final String url = this.registryURL;
        try {
            final HttpPut method = new HttpPut(url);
            method.addHeader("service", service);
            final ResponseHandler<String> handler = (ResponseHandler<String>)new BasicResponseHandler();
            final String responseBody = (String)this.httpClient.execute((HttpUriRequest)method, (ResponseHandler)handler);
            HTTPDiscoveryAgent.LOG.debug("PUT to " + url + " got a " + responseBody);
        }
        catch (Exception e) {
            HTTPDiscoveryAgent.LOG.debug("PUT to " + url + " failed with: " + e);
        }
    }
    
    private synchronized void doUnRegister(final String service) {
        final String url = this.registryURL;
        try {
            final HttpDelete method = new HttpDelete(url);
            method.addHeader("service", service);
            final ResponseHandler<String> handler = (ResponseHandler<String>)new BasicResponseHandler();
            final String responseBody = (String)this.httpClient.execute((HttpUriRequest)method, (ResponseHandler)handler);
            HTTPDiscoveryAgent.LOG.debug("DELETE to " + url + " got a " + responseBody);
        }
        catch (Exception e) {
            HTTPDiscoveryAgent.LOG.debug("DELETE to " + url + " failed with: " + e);
        }
    }
    
    private synchronized Set<String> doLookup(final long freshness) {
        final String url = this.registryURL + "?freshness=" + freshness;
        try {
            final HttpGet method = new HttpGet(url);
            final ResponseHandler<String> handler = (ResponseHandler<String>)new BasicResponseHandler();
            final String response = (String)this.httpClient.execute((HttpUriRequest)method, (ResponseHandler)handler);
            HTTPDiscoveryAgent.LOG.debug("GET to " + url + " got a " + response);
            final Set<String> rc = new HashSet<String>();
            final Scanner scanner = new Scanner(response);
            while (scanner.hasNextLine()) {
                final String service = scanner.nextLine();
                if (service.trim().length() != 0) {
                    rc.add(service);
                }
            }
            return rc;
        }
        catch (Exception e) {
            HTTPDiscoveryAgent.LOG.debug("GET to " + url + " failed with: " + e);
            return null;
        }
    }
    
    @Override
    public void serviceFailed(final DiscoveryEvent devent) throws IOException {
        final SimpleDiscoveryEvent event = (SimpleDiscoveryEvent)devent;
        if (event.failed.compareAndSet(false, true)) {
            this.discoveryListener.get().onServiceRemove(event);
            if (!event.removed.get()) {
                final Thread thread = new Thread() {
                    @Override
                    public void run() {
                        if (event.connectTime + HTTPDiscoveryAgent.this.minConnectTime > System.currentTimeMillis()) {
                            HTTPDiscoveryAgent.LOG.debug("Failure occured soon after the discovery event was generated.  It will be clasified as a connection failure: " + event);
                            event.connectFailures++;
                            if (HTTPDiscoveryAgent.this.maxReconnectAttempts > 0 && event.connectFailures >= HTTPDiscoveryAgent.this.maxReconnectAttempts) {
                                HTTPDiscoveryAgent.LOG.debug("Reconnect attempts exceeded " + HTTPDiscoveryAgent.this.maxReconnectAttempts + " tries.  Reconnecting has been disabled.");
                                return;
                            }
                            synchronized (HTTPDiscoveryAgent.this.sleepMutex) {
                                try {
                                    if (!HTTPDiscoveryAgent.this.running.get() || event.removed.get()) {
                                        return;
                                    }
                                    HTTPDiscoveryAgent.LOG.debug("Waiting " + event.reconnectDelay + " ms before attepting to reconnect.");
                                    HTTPDiscoveryAgent.this.sleepMutex.wait(event.reconnectDelay);
                                }
                                catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                            }
                            if (!HTTPDiscoveryAgent.this.useExponentialBackOff) {
                                event.reconnectDelay = HTTPDiscoveryAgent.this.initialReconnectDelay;
                            }
                            else {
                                event.reconnectDelay *= HTTPDiscoveryAgent.this.backOffMultiplier;
                                if (event.reconnectDelay > HTTPDiscoveryAgent.this.maxReconnectDelay) {
                                    event.reconnectDelay = HTTPDiscoveryAgent.this.maxReconnectDelay;
                                }
                            }
                        }
                        else {
                            event.connectFailures = 0;
                            event.reconnectDelay = HTTPDiscoveryAgent.this.initialReconnectDelay;
                        }
                        if (!HTTPDiscoveryAgent.this.running.get() || event.removed.get()) {
                            return;
                        }
                        event.connectTime = System.currentTimeMillis();
                        event.failed.set(false);
                        HTTPDiscoveryAgent.this.discoveryListener.get().onServiceAdd(event);
                    }
                };
                thread.setDaemon(true);
                thread.start();
            }
        }
    }
    
    public void setBrokerName(final String brokerName) {
        this.brokerName = brokerName;
    }
    
    @Override
    public void setDiscoveryListener(final DiscoveryListener discoveryListener) {
        this.discoveryListener.set(discoveryListener);
    }
    
    public void setGroup(final String group) {
    }
    
    @Override
    public void start() throws Exception {
        if (this.startCounter.addAndGet(1) == 1) {
            if (this.startEmbeddRegistry) {
                this.jetty = this.createEmbeddedJettyServer();
                final Map<String, Object> props = new HashMap<String, Object>();
                props.put("agent", this);
                IntrospectionSupport.setProperties(this.jetty, props);
                this.jetty.start();
            }
            this.running.set(true);
            (this.thread = new Thread("HTTPDiscovery Agent") {
                @Override
                public void run() {
                    while (HTTPDiscoveryAgent.this.running.get()) {
                        try {
                            HTTPDiscoveryAgent.this.update();
                            synchronized (HTTPDiscoveryAgent.this.updateMutex) {
                                do {
                                    if (HTTPDiscoveryAgent.this.updateState == UpdateState.RESUMING) {
                                        HTTPDiscoveryAgent.this.updateState = UpdateState.RESUMED;
                                    }
                                    else {
                                        HTTPDiscoveryAgent.this.updateMutex.wait(HTTPDiscoveryAgent.this.updateInterval);
                                    }
                                } while (HTTPDiscoveryAgent.this.updateState == UpdateState.SUSPENDED && HTTPDiscoveryAgent.this.running.get());
                            }
                            continue;
                        }
                        catch (InterruptedException e) {
                            return;
                        }
                        break;
                    }
                }
            }).setDaemon(true);
            this.thread.start();
        }
    }
    
    private Service createEmbeddedJettyServer() throws Exception {
        final Class<?> clazz = HTTPDiscoveryAgent.class.getClassLoader().loadClass("org.apache.activemq.transport.discovery.http.EmbeddedJettyServer");
        return (Service)clazz.newInstance();
    }
    
    private void update() {
        synchronized (this.registeredServices) {
            for (final String service : this.registeredServices) {
                this.doRegister(service);
            }
        }
        final DiscoveryListener discoveryListener = this.discoveryListener.get();
        if (discoveryListener != null) {
            final Set<String> activeServices = this.doLookup(this.updateInterval * 3L);
            if (activeServices != null) {
                synchronized (this.discoveredServices) {
                    final HashSet<String> removedServices = new HashSet<String>(this.discoveredServices.keySet());
                    removedServices.removeAll(activeServices);
                    final HashSet<String> addedServices = new HashSet<String>(activeServices);
                    addedServices.removeAll(this.discoveredServices.keySet());
                    addedServices.removeAll(removedServices);
                    for (final String service2 : addedServices) {
                        final SimpleDiscoveryEvent e = new SimpleDiscoveryEvent(service2);
                        this.discoveredServices.put(service2, e);
                        discoveryListener.onServiceAdd(e);
                    }
                    for (final String service2 : removedServices) {
                        final SimpleDiscoveryEvent e = this.discoveredServices.remove(service2);
                        if (e != null) {
                            e.removed.set(true);
                        }
                        discoveryListener.onServiceRemove(e);
                    }
                }
            }
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (this.startCounter.decrementAndGet() == 0) {
            this.resume();
            this.running.set(false);
            if (this.thread != null) {
                this.thread.join(this.updateInterval * 3L);
                this.thread = null;
            }
            if (this.jetty != null) {
                this.jetty.stop();
                this.jetty = null;
            }
        }
    }
    
    public String getRegistryURL() {
        return this.registryURL;
    }
    
    public void setRegistryURL(final String discoveryRegistryURL) {
        this.registryURL = discoveryRegistryURL;
    }
    
    public long getUpdateInterval() {
        return this.updateInterval;
    }
    
    public void setUpdateInterval(final long updateInterval) {
        this.updateInterval = updateInterval;
    }
    
    public boolean isStartEmbeddRegistry() {
        return this.startEmbeddRegistry;
    }
    
    public void setStartEmbeddRegistry(final boolean startEmbeddRegistry) {
        this.startEmbeddRegistry = startEmbeddRegistry;
    }
    
    @Override
    public void suspend() throws Exception {
        synchronized (this.updateMutex) {
            this.updateState = UpdateState.SUSPENDED;
        }
    }
    
    @Override
    public void resume() throws Exception {
        synchronized (this.updateMutex) {
            this.updateState = UpdateState.RESUMING;
            this.updateMutex.notify();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(HTTPDiscoveryAgent.class);
    }
    
    enum UpdateState
    {
        SUSPENDED, 
        RESUMING, 
        RESUMED;
    }
    
    class SimpleDiscoveryEvent extends DiscoveryEvent
    {
        private int connectFailures;
        private long reconnectDelay;
        private long connectTime;
        private AtomicBoolean failed;
        private AtomicBoolean removed;
        
        public SimpleDiscoveryEvent(final String service) {
            super(service);
            this.reconnectDelay = HTTPDiscoveryAgent.this.initialReconnectDelay;
            this.connectTime = System.currentTimeMillis();
            this.failed = new AtomicBoolean(false);
            this.removed = new AtomicBoolean(false);
        }
    }
}
