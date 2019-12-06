// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.virtual;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQDestination;

public class CompositeTopic extends CompositeDestination
{
    @Override
    public ActiveMQDestination getVirtualDestination() {
        return new ActiveMQTopic(this.getName());
    }
}
