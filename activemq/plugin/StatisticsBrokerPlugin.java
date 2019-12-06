// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.plugin;

import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.Broker;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerPlugin;

public class StatisticsBrokerPlugin implements BrokerPlugin
{
    private static Logger LOG;
    
    @Override
    public Broker installPlugin(final Broker broker) throws Exception {
        final StatisticsBroker answer = new StatisticsBroker(broker);
        StatisticsBrokerPlugin.LOG.info("Installing StaticsBroker");
        return answer;
    }
    
    static {
        StatisticsBrokerPlugin.LOG = LoggerFactory.getLogger(StatisticsBrokerPlugin.class);
    }
}
