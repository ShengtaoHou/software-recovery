// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.zeroconf;

import org.slf4j.LoggerFactory;
import org.apache.activemq.util.MapHelper;
import java.net.UnknownHostException;
import javax.jmdns.ServiceEvent;
import org.apache.activemq.command.DiscoveryEvent;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.activemq.util.JMSExceptionSupport;
import java.io.IOException;
import javax.jmdns.ServiceInfo;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.activemq.transport.discovery.DiscoveryListener;
import java.net.InetAddress;
import javax.jmdns.JmDNS;
import org.slf4j.Logger;
import javax.jmdns.ServiceListener;
import org.apache.activemq.transport.discovery.DiscoveryAgent;

public class ZeroconfDiscoveryAgent implements DiscoveryAgent, ServiceListener
{
    private static final Logger LOG;
    private static final String TYPE_SUFFIX = "ActiveMQ-5.";
    private JmDNS jmdns;
    private InetAddress localAddress;
    private String localhost;
    private int weight;
    private int priority;
    private String typeSuffix;
    private DiscoveryListener listener;
    private String group;
    private final CopyOnWriteArrayList<ServiceInfo> serviceInfos;
    
    public ZeroconfDiscoveryAgent() {
        this.typeSuffix = "ActiveMQ-5.";
        this.group = "default";
        this.serviceInfos = new CopyOnWriteArrayList<ServiceInfo>();
    }
    
    public void start() throws Exception {
        if (this.group == null) {
            throw new IOException("You must specify a group to discover");
        }
        String type = this.getType();
        if (!type.endsWith(".")) {
            ZeroconfDiscoveryAgent.LOG.warn("The type '{}' should end with '.' to be a valid Rendezvous type", type);
            type += ".";
        }
        try {
            this.getJmdns();
            if (this.listener != null) {
                ZeroconfDiscoveryAgent.LOG.info("Discovering service of type: {}", type);
                this.jmdns.addServiceListener(type, (ServiceListener)this);
            }
        }
        catch (IOException e) {
            JMSExceptionSupport.create("Failed to start JmDNS service: " + e, e);
        }
    }
    
    public void stop() {
        if (this.jmdns != null) {
            for (final ServiceInfo si : this.serviceInfos) {
                this.jmdns.unregisterService(si);
            }
            final JmDNS closeTarget = this.jmdns;
            final Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        if (JmDNSFactory.onClose(ZeroconfDiscoveryAgent.this.getLocalAddress())) {
                            closeTarget.close();
                        }
                    }
                    catch (IOException e) {
                        ZeroconfDiscoveryAgent.LOG.debug("Error closing JmDNS {}. This exception will be ignored.", ZeroconfDiscoveryAgent.this.getLocalhost(), e);
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
            this.jmdns = null;
        }
    }
    
    @Override
    public void registerService(final String name) throws IOException {
        final ServiceInfo si = this.createServiceInfo(name, new HashMap());
        this.serviceInfos.add(si);
        this.getJmdns().registerService(si);
    }
    
    public void addService(final JmDNS jmDNS, final String type, final String name) {
        ZeroconfDiscoveryAgent.LOG.debug("addService with type: {} name: {}", type, name);
        if (this.listener != null) {
            this.listener.onServiceAdd(new DiscoveryEvent(name));
        }
        jmDNS.requestServiceInfo(type, name);
    }
    
    public void removeService(final JmDNS jmDNS, final String type, final String name) {
        ZeroconfDiscoveryAgent.LOG.debug("removeService with type: {} name: {}", type, name);
        if (this.listener != null) {
            this.listener.onServiceRemove(new DiscoveryEvent(name));
        }
    }
    
    public void serviceAdded(final ServiceEvent event) {
        this.addService(event.getDNS(), event.getType(), event.getName());
    }
    
    public void serviceRemoved(final ServiceEvent event) {
        this.removeService(event.getDNS(), event.getType(), event.getName());
    }
    
    public void serviceResolved(final ServiceEvent event) {
    }
    
    public void resolveService(final JmDNS jmDNS, final String type, final String name, final ServiceInfo serviceInfo) {
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public void setPriority(final int priority) {
        this.priority = priority;
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public void setWeight(final int weight) {
        this.weight = weight;
    }
    
    public JmDNS getJmdns() throws IOException {
        if (this.jmdns == null) {
            this.jmdns = this.createJmDNS();
        }
        return this.jmdns;
    }
    
    public void setJmdns(final JmDNS jmdns) {
        this.jmdns = jmdns;
    }
    
    public InetAddress getLocalAddress() throws UnknownHostException {
        if (this.localAddress == null) {
            this.localAddress = this.createLocalAddress();
        }
        return this.localAddress;
    }
    
    public void setLocalAddress(final InetAddress localAddress) {
        this.localAddress = localAddress;
    }
    
    public String getLocalhost() {
        return this.localhost;
    }
    
    public void setLocalhost(final String localhost) {
        this.localhost = localhost;
    }
    
    protected ServiceInfo createServiceInfo(final String name, final Map map) {
        final int port = MapHelper.getInt(map, "port", 0);
        final String type = this.getType();
        ZeroconfDiscoveryAgent.LOG.debug("Registering service type: {} name: {} details: {}", type, name, map);
        return ServiceInfo.create(type, name + "." + type, port, this.weight, this.priority, "");
    }
    
    protected JmDNS createJmDNS() throws IOException {
        return JmDNSFactory.create(this.getLocalAddress());
    }
    
    protected InetAddress createLocalAddress() throws UnknownHostException {
        if (this.localhost != null) {
            return InetAddress.getByName(this.localhost);
        }
        return InetAddress.getLocalHost();
    }
    
    @Override
    public void setDiscoveryListener(final DiscoveryListener listener) {
        this.listener = listener;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public void setGroup(final String group) {
        this.group = group;
    }
    
    public void setType(final String typeSuffix) {
        this.typeSuffix = typeSuffix;
    }
    
    public String getType() {
        if (this.typeSuffix == null || this.typeSuffix.isEmpty()) {
            this.typeSuffix = "ActiveMQ-5.";
        }
        return "_" + this.group + "." + this.typeSuffix;
    }
    
    @Override
    public void serviceFailed(final DiscoveryEvent event) throws IOException {
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZeroconfDiscoveryAgent.class);
    }
}
