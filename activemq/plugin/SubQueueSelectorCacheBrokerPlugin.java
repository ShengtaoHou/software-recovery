// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.plugin;

import org.apache.activemq.broker.Broker;
import java.io.File;
import org.apache.activemq.broker.BrokerPlugin;

public class SubQueueSelectorCacheBrokerPlugin implements BrokerPlugin
{
    private File persistFile;
    
    @Override
    public Broker installPlugin(final Broker broker) throws Exception {
        return new SubQueueSelectorCacheBroker(broker, this.persistFile);
    }
    
    public void setPersistFile(final File persistFile) {
        this.persistFile = persistFile;
    }
    
    public File getPersistFile() {
        return this.persistFile;
    }
}
