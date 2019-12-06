// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import javax.jms.QueueConnection;
import javax.jms.Queue;

public class OutboundQueueBridge extends QueueBridge
{
    String outboundQueueName;
    String localQueueName;
    
    public OutboundQueueBridge(final String outboundQueueName) {
        this.outboundQueueName = outboundQueueName;
        this.localQueueName = outboundQueueName;
    }
    
    public OutboundQueueBridge() {
    }
    
    public String getOutboundQueueName() {
        return this.outboundQueueName;
    }
    
    public void setOutboundQueueName(final String outboundQueueName) {
        this.outboundQueueName = outboundQueueName;
        if (this.localQueueName == null) {
            this.localQueueName = outboundQueueName;
        }
    }
    
    public String getLocalQueueName() {
        return this.localQueueName;
    }
    
    public void setLocalQueueName(final String localQueueName) {
        this.localQueueName = localQueueName;
    }
}
