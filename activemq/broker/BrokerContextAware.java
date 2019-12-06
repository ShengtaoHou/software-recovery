// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

public interface BrokerContextAware
{
    void setBrokerContext(final BrokerContext p0);
    
    BrokerContext getBrokerContext();
}
