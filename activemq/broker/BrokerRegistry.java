// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

public class BrokerRegistry
{
    private static final Logger LOG;
    private static final BrokerRegistry INSTANCE;
    private final Object mutex;
    private final Map<String, BrokerService> brokers;
    
    public BrokerRegistry() {
        this.mutex = new Object();
        this.brokers = new HashMap<String, BrokerService>();
    }
    
    public static BrokerRegistry getInstance() {
        return BrokerRegistry.INSTANCE;
    }
    
    public BrokerService lookup(final String brokerName) {
        BrokerService result = null;
        synchronized (this.mutex) {
            result = this.brokers.get(brokerName);
            if (result == null && brokerName != null && brokerName.equals("localhost")) {
                result = this.findFirst();
                if (result != null) {
                    BrokerRegistry.LOG.warn("Broker localhost not started so using {} instead", result.getBrokerName());
                }
            }
            if (result == null && (brokerName == null || brokerName.isEmpty() || brokerName.equals("null"))) {
                result = this.findFirst();
            }
        }
        return result;
    }
    
    public BrokerService findFirst() {
        synchronized (this.mutex) {
            final Iterator<BrokerService> iter = this.brokers.values().iterator();
            if (iter.hasNext()) {
                return iter.next();
            }
            return null;
        }
    }
    
    public void bind(final String brokerName, final BrokerService broker) {
        synchronized (this.mutex) {
            this.brokers.put(brokerName, broker);
            this.mutex.notifyAll();
        }
    }
    
    public void unbind(final String brokerName) {
        synchronized (this.mutex) {
            this.brokers.remove(brokerName);
        }
    }
    
    public Object getRegistryMutext() {
        return this.mutex;
    }
    
    public Map<String, BrokerService> getBrokers() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends BrokerService>)this.brokers);
    }
    
    static {
        LOG = LoggerFactory.getLogger(BrokerRegistry.class);
        INSTANCE = new BrokerRegistry();
    }
}
