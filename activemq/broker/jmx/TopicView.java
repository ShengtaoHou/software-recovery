// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Topic;

public class TopicView extends DestinationView implements TopicViewMBean
{
    public TopicView(final ManagedRegionBroker broker, final Topic destination) {
        super(broker, destination);
    }
}
