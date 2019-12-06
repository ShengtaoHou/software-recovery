// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.view;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.BrokerRegistry;
import java.util.HashMap;
import java.util.Map;

public class MessageBrokerViewRegistry
{
    private static final MessageBrokerViewRegistry INSTANCE;
    private final Object mutex;
    private final Map<String, MessageBrokerView> brokerViews;
    
    public MessageBrokerViewRegistry() {
        this.mutex = new Object();
        this.brokerViews = new HashMap<String, MessageBrokerView>();
    }
    
    public static MessageBrokerViewRegistry getInstance() {
        return MessageBrokerViewRegistry.INSTANCE;
    }
    
    public MessageBrokerView lookup(final String brokerName) {
        MessageBrokerView result = null;
        synchronized (this.mutex) {
            result = this.brokerViews.get(brokerName);
            if (result == null) {
                final BrokerService brokerService = BrokerRegistry.getInstance().lookup(brokerName);
                if (brokerService != null) {
                    result = new MessageBrokerView(brokerService);
                    this.brokerViews.put(brokerName, result);
                }
            }
        }
        return result;
    }
    
    static {
        INSTANCE = new MessageBrokerViewRegistry();
    }
}
