// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.view;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

public class ConnectionDotFilePlugin implements BrokerPlugin
{
    private String file;
    private boolean redrawOnRemove;
    
    public ConnectionDotFilePlugin() {
        this.file = "ActiveMQConnections.dot";
    }
    
    @Override
    public Broker installPlugin(final Broker broker) throws Exception {
        return new ConnectionDotFileInterceptor(broker, this.file, this.redrawOnRemove);
    }
    
    public String getFile() {
        return this.file;
    }
    
    public void setFile(final String file) {
        this.file = file;
    }
}
