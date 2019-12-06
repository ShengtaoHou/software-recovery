// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.view;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

public class DestinationDotFilePlugin implements BrokerPlugin
{
    private String file;
    
    public DestinationDotFilePlugin() {
        this.file = "ActiveMQDestinations.dot";
    }
    
    @Override
    public Broker installPlugin(final Broker broker) {
        return new DestinationDotFileInterceptor(broker, this.file);
    }
    
    public String getFile() {
        return this.file;
    }
    
    public void setFile(final String file) {
        this.file = file;
    }
}
