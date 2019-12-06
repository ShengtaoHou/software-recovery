// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.SubscriptionRecovery;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.ConnectionContext;

public class NoSubscriptionRecoveryPolicy implements SubscriptionRecoveryPolicy
{
    @Override
    public SubscriptionRecoveryPolicy copy() {
        return this;
    }
    
    @Override
    public boolean add(final ConnectionContext context, final MessageReference node) throws Exception {
        return true;
    }
    
    @Override
    public void recover(final ConnectionContext context, final Topic topic, final SubscriptionRecovery sub) throws Exception {
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
    }
    
    @Override
    public Message[] browse(final ActiveMQDestination dest) throws Exception {
        return new Message[0];
    }
    
    @Override
    public void setBroker(final Broker broker) {
    }
}
