// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.plugin;

import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.Broker;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerPlugin;

public class ForcePersistencyModeBrokerPlugin implements BrokerPlugin
{
    private static Logger LOG;
    private boolean persistenceFlag;
    
    public ForcePersistencyModeBrokerPlugin() {
        this.persistenceFlag = false;
    }
    
    @Override
    public Broker installPlugin(final Broker broker) throws Exception {
        final ForcePersistencyModeBroker pB = new ForcePersistencyModeBroker(broker);
        pB.setPersistenceFlag(this.isPersistenceForced());
        ForcePersistencyModeBrokerPlugin.LOG.info("Installing ForcePersistencyModeBroker plugin: persistency enforced={}", (Object)pB.isPersistent());
        return pB;
    }
    
    public void setPersistenceFlag(final boolean persistenceFlag) {
        this.persistenceFlag = persistenceFlag;
    }
    
    public final boolean isPersistenceForced() {
        return this.persistenceFlag;
    }
    
    static {
        ForcePersistencyModeBrokerPlugin.LOG = LoggerFactory.getLogger(ForcePersistencyModeBrokerPlugin.class);
    }
}
