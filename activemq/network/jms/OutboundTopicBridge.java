// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import javax.jms.TopicConnection;
import javax.jms.Topic;

public class OutboundTopicBridge extends TopicBridge
{
    String outboundTopicName;
    String localTopicName;
    
    public OutboundTopicBridge(final String outboundTopicName) {
        this.outboundTopicName = outboundTopicName;
        this.localTopicName = outboundTopicName;
    }
    
    public OutboundTopicBridge() {
    }
    
    public String getOutboundTopicName() {
        return this.outboundTopicName;
    }
    
    public void setOutboundTopicName(final String outboundTopicName) {
        this.outboundTopicName = outboundTopicName;
        if (this.localTopicName == null) {
            this.localTopicName = outboundTopicName;
        }
    }
    
    public String getLocalTopicName() {
        return this.localTopicName;
    }
    
    public void setLocalTopicName(final String localTopicName) {
        this.localTopicName = localTopicName;
    }
}
