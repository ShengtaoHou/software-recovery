// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.plugin;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.Broker;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerFilter;

public class ForcePersistencyModeBroker extends BrokerFilter
{
    public static Logger log;
    private boolean persistence;
    
    public boolean isPersistent() {
        return this.persistence;
    }
    
    public void setPersistenceFlag(final boolean mode) {
        this.persistence = mode;
    }
    
    public ForcePersistencyModeBroker(final Broker next) {
        super(next);
        this.persistence = false;
        System.out.println(this.getBrokerSequenceId());
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        messageSend.getMessage().setPersistent(this.isPersistent());
        this.next.send(producerExchange, messageSend);
    }
    
    static {
        ForcePersistencyModeBroker.log = LoggerFactory.getLogger(ForcePersistencyModeBroker.class);
    }
}
