// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.virtual;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQDestination;

public class CompositeQueue extends CompositeDestination
{
    @Override
    public ActiveMQDestination getVirtualDestination() {
        return new ActiveMQQueue(this.getName());
    }
}
