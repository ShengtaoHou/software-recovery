// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import javax.jms.QueueConnection;
import javax.jms.Queue;

public class InboundQueueBridge extends QueueBridge
{
    String inboundQueueName;
    String localQueueName;
    
    public InboundQueueBridge(final String inboundQueueName) {
        this.inboundQueueName = inboundQueueName;
        this.localQueueName = inboundQueueName;
    }
    
    public InboundQueueBridge() {
    }
    
    public String getInboundQueueName() {
        return this.inboundQueueName;
    }
    
    public void setInboundQueueName(final String inboundQueueName) {
        this.inboundQueueName = inboundQueueName;
        if (this.localQueueName == null) {
            this.localQueueName = inboundQueueName;
        }
    }
    
    public String getLocalQueueName() {
        return this.localQueueName;
    }
    
    public void setLocalQueueName(final String localQueueName) {
        this.localQueueName = localQueueName;
    }
}
