// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.virtual;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.DestinationFilter;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.broker.region.DestinationInterceptor;

public class MirroredQueue implements DestinationInterceptor, BrokerServiceAware
{
    private static final transient Logger LOG;
    private String prefix;
    private String postfix;
    private boolean copyMessage;
    private BrokerService brokerService;
    
    public MirroredQueue() {
        this.prefix = "VirtualTopic.Mirror.";
        this.postfix = "";
        this.copyMessage = true;
    }
    
    @Override
    public Destination intercept(final Destination destination) {
        if (destination.getActiveMQDestination().isQueue()) {
            if (destination.getActiveMQDestination().isTemporary()) {
                if (!this.brokerService.isUseTempMirroredQueues()) {
                    return destination;
                }
            }
            try {
                final Destination mirrorDestination = this.getMirrorDestination(destination);
                if (mirrorDestination != null) {
                    return new DestinationFilter(destination) {
                        @Override
                        public void send(final ProducerBrokerExchange context, Message message) throws Exception {
                            message.setDestination(mirrorDestination.getActiveMQDestination());
                            mirrorDestination.send(context, message);
                            if (MirroredQueue.this.isCopyMessage()) {
                                message = message.copy();
                            }
                            message.setDestination(destination.getActiveMQDestination());
                            message.setMemoryUsage(null);
                            super.send(context, message);
                        }
                    };
                }
            }
            catch (Exception e) {
                MirroredQueue.LOG.error("Failed to lookup the mirror destination for: {}", destination, e);
            }
        }
        return destination;
    }
    
    @Override
    public void remove(final Destination destination) {
        if (this.brokerService == null) {
            throw new IllegalArgumentException("No brokerService injected!");
        }
        final ActiveMQDestination topic = this.getMirrorTopic(destination.getActiveMQDestination());
        if (topic != null) {
            try {
                this.brokerService.removeDestination(topic);
            }
            catch (Exception e) {
                MirroredQueue.LOG.error("Failed to remove mirror destination for {}", destination, e);
            }
        }
    }
    
    @Override
    public void create(final Broker broker, final ConnectionContext context, final ActiveMQDestination destination) {
    }
    
    public String getPostfix() {
        return this.postfix;
    }
    
    public void setPostfix(final String postfix) {
        this.postfix = postfix;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    public boolean isCopyMessage() {
        return this.copyMessage;
    }
    
    public void setCopyMessage(final boolean copyMessage) {
        this.copyMessage = copyMessage;
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
    }
    
    protected Destination getMirrorDestination(final Destination destination) throws Exception {
        if (this.brokerService == null) {
            throw new IllegalArgumentException("No brokerService injected!");
        }
        final ActiveMQDestination topic = this.getMirrorTopic(destination.getActiveMQDestination());
        return this.brokerService.getDestination(topic);
    }
    
    protected ActiveMQDestination getMirrorTopic(final ActiveMQDestination original) {
        return new ActiveMQTopic(this.prefix + original.getPhysicalName() + this.postfix);
    }
    
    static {
        LOG = LoggerFactory.getLogger(MirroredQueue.class);
    }
}
