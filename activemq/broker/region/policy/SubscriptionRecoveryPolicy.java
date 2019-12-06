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
import org.apache.activemq.Service;

public interface SubscriptionRecoveryPolicy extends Service
{
    boolean add(final ConnectionContext p0, final MessageReference p1) throws Exception;
    
    void recover(final ConnectionContext p0, final Topic p1, final SubscriptionRecovery p2) throws Exception;
    
    Message[] browse(final ActiveMQDestination p0) throws Exception;
    
    SubscriptionRecoveryPolicy copy();
    
    void setBroker(final Broker p0);
}
