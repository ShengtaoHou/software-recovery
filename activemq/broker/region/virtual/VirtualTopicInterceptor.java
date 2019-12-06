// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.virtual;

import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.util.LRUCache;
import org.apache.activemq.broker.region.DestinationFilter;

public class VirtualTopicInterceptor extends DestinationFilter
{
    private final String prefix;
    private final String postfix;
    private final boolean local;
    private final LRUCache<ActiveMQDestination, ActiveMQQueue> cache;
    
    public VirtualTopicInterceptor(final Destination next, final String prefix, final String postfix, final boolean local) {
        super(next);
        this.cache = new LRUCache<ActiveMQDestination, ActiveMQQueue>();
        this.prefix = prefix;
        this.postfix = postfix;
        this.local = local;
    }
    
    public Topic getTopic() {
        return (Topic)this.next;
    }
    
    @Override
    public void send(final ProducerBrokerExchange context, final Message message) throws Exception {
        if (!message.isAdvisory() && (!this.local || message.getBrokerPath() == null)) {
            final ActiveMQDestination queueConsumers = this.getQueueConsumersWildcard(message.getDestination());
            this.send(context, message, queueConsumers);
        }
        super.send(context, message);
    }
    
    protected ActiveMQDestination getQueueConsumersWildcard(final ActiveMQDestination original) {
        ActiveMQQueue queue;
        synchronized (this.cache) {
            queue = this.cache.get(original);
            if (queue == null) {
                queue = new ActiveMQQueue(this.prefix + original.getPhysicalName() + this.postfix);
                this.cache.put(original, queue);
            }
        }
        return queue;
    }
}
