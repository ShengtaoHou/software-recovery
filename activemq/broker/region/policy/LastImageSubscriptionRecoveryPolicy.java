// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.Broker;
import java.util.List;
import org.apache.activemq.filter.DestinationFilter;
import java.util.ArrayList;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.SubscriptionRecovery;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.MessageReference;

public class LastImageSubscriptionRecoveryPolicy implements SubscriptionRecoveryPolicy
{
    private volatile MessageReference lastImage;
    
    @Override
    public boolean add(final ConnectionContext context, final MessageReference node) throws Exception {
        this.lastImage = node;
        return true;
    }
    
    @Override
    public void recover(final ConnectionContext context, final Topic topic, final SubscriptionRecovery sub) throws Exception {
        final MessageReference node = this.lastImage;
        if (node != null) {
            sub.addRecoveredMessage(context, node);
        }
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
    }
    
    @Override
    public Message[] browse(final ActiveMQDestination destination) throws Exception {
        final List<Message> result = new ArrayList<Message>();
        if (this.lastImage != null) {
            final DestinationFilter filter = DestinationFilter.parseFilter(destination);
            if (filter.matches(this.lastImage.getMessage().getDestination())) {
                result.add(this.lastImage.getMessage());
            }
        }
        return result.toArray(new Message[result.size()]);
    }
    
    @Override
    public SubscriptionRecoveryPolicy copy() {
        return new LastImageSubscriptionRecoveryPolicy();
    }
    
    @Override
    public void setBroker(final Broker broker) {
    }
}
