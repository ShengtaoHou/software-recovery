// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import javax.jms.TopicConnection;
import javax.jms.Topic;

public class InboundTopicBridge extends TopicBridge
{
    String inboundTopicName;
    String localTopicName;
    
    public InboundTopicBridge(final String inboundTopicName) {
        this.inboundTopicName = inboundTopicName;
        this.localTopicName = inboundTopicName;
    }
    
    public InboundTopicBridge() {
    }
    
    public String getInboundTopicName() {
        return this.inboundTopicName;
    }
    
    public void setInboundTopicName(final String inboundTopicName) {
        this.inboundTopicName = inboundTopicName;
        if (this.localTopicName == null) {
            this.localTopicName = inboundTopicName;
        }
    }
    
    public String getLocalTopicName() {
        return this.localTopicName;
    }
    
    public void setLocalTopicName(final String localTopicName) {
        this.localTopicName = localTopicName;
    }
}
