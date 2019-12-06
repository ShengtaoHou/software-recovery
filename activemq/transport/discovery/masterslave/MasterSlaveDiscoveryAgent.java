// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.masterslave;

import org.slf4j.LoggerFactory;
import java.net.URI;
import org.slf4j.Logger;
import org.apache.activemq.transport.discovery.simple.SimpleDiscoveryAgent;

public class MasterSlaveDiscoveryAgent extends SimpleDiscoveryAgent
{
    private static final Logger LOG;
    private String[] msServices;
    
    public MasterSlaveDiscoveryAgent() {
        this.msServices = new String[0];
    }
    
    @Override
    public String[] getServices() {
        return this.msServices;
    }
    
    @Override
    public void setServices(final String services) {
        this.msServices = services.split(",");
        this.configureServices();
    }
    
    @Override
    public void setServices(final String[] services) {
        this.msServices = services;
        this.configureServices();
    }
    
    @Override
    public void setServices(final URI[] services) {
        this.msServices = new String[services.length];
        for (int i = 0; i < services.length; ++i) {
            this.msServices[i] = services[i].toString();
        }
        this.configureServices();
    }
    
    protected void configureServices() {
        if (this.msServices == null || this.msServices.length < 2) {
            MasterSlaveDiscoveryAgent.LOG.error("masterSlave requires at least 2 URIs");
            this.msServices = new String[0];
            throw new IllegalArgumentException("Expecting at least 2 arguments");
        }
        final StringBuffer buf = new StringBuffer();
        buf.append("failover:(");
        for (int i = 0; i < this.msServices.length - 1; ++i) {
            buf.append(this.msServices[i]);
            buf.append(',');
        }
        buf.append(this.msServices[this.msServices.length - 1]);
        buf.append(")?randomize=false&maxReconnectAttempts=0");
        super.setServices(new String[] { buf.toString() });
    }
    
    static {
        LOG = LoggerFactory.getLogger(MasterSlaveDiscoveryAgent.class);
    }
}
