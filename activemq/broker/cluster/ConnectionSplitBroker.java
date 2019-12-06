// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.cluster;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.ConnectionContext;
import java.util.ArrayList;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.ConsumerInfo;
import java.util.List;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerFilter;

public class ConnectionSplitBroker extends BrokerFilter
{
    private static final Logger LOG;
    private List<ConsumerInfo> networkConsumerList;
    
    public ConnectionSplitBroker(final Broker next) {
        super(next);
        this.networkConsumerList = new ArrayList<ConsumerInfo>();
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        final ActiveMQDestination dest = info.getDestination();
        synchronized (this.networkConsumerList) {
            if (info.isNetworkSubscription()) {
                this.networkConsumerList.add(info);
            }
            else if (!this.networkConsumerList.isEmpty()) {
                final List<ConsumerInfo> gcList = new ArrayList<ConsumerInfo>();
                for (final ConsumerInfo nc : this.networkConsumerList) {
                    if (!nc.isNetworkConsumersEmpty()) {
                        for (final ConsumerId id : nc.getNetworkConsumerIds()) {
                            if (id.equals(info.getConsumerId())) {
                                nc.removeNetworkConsumerId(id);
                                if (!nc.isNetworkConsumersEmpty()) {
                                    continue;
                                }
                                gcList.add(nc);
                            }
                        }
                    }
                }
                for (final ConsumerInfo nc : gcList) {
                    this.networkConsumerList.remove(nc);
                    super.removeConsumer(context, nc);
                    ConnectionSplitBroker.LOG.warn("Removed stale network consumer {}", nc);
                }
            }
        }
        return super.addConsumer(context, info);
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        if (info.isNetworkSubscription()) {
            synchronized (this.networkConsumerList) {
                this.networkConsumerList.remove(info);
            }
        }
        super.removeConsumer(context, info);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ConnectionSplitBroker.class);
    }
}
