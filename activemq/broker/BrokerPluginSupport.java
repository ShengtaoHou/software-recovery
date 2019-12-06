// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class BrokerPluginSupport extends MutableBrokerFilter implements BrokerPlugin
{
    private static final Logger LOG;
    
    public BrokerPluginSupport() {
        super(null);
    }
    
    @Override
    public Broker installPlugin(final Broker broker) throws Exception {
        this.setNext(broker);
        return this;
    }
    
    @Override
    public void start() throws Exception {
        super.start();
        BrokerPluginSupport.LOG.info("Broker Plugin {} started", this.getClass().getName());
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        BrokerPluginSupport.LOG.info("Broker Plugin {} stopped", this.getClass().getName());
    }
    
    static {
        LOG = LoggerFactory.getLogger(BrokerPluginSupport.class);
    }
}
