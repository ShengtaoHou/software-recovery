// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.apache.activemq.filter.DestinationFilter;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ActiveMQDestination;
import java.io.IOException;
import org.apache.activemq.transport.Transport;
import org.slf4j.Logger;

public class DurableConduitBridge extends ConduitBridge
{
    private static final Logger LOG;
    
    @Override
    public String toString() {
        return "DurableConduitBridge:" + this.configuration.getBrokerName() + "->" + this.getRemoteBrokerName();
    }
    
    public DurableConduitBridge(final NetworkBridgeConfiguration configuration, final Transport localBroker, final Transport remoteBroker) {
        super(configuration, localBroker, remoteBroker);
    }
    
    @Override
    protected void setupStaticDestinations() {
        super.setupStaticDestinations();
        final ActiveMQDestination[] dests = (ActiveMQDestination[])(this.configuration.isDynamicOnly() ? null : this.durableDestinations);
        if (dests != null) {
            for (final ActiveMQDestination dest : dests) {
                if (this.isPermissableDestination(dest) && !this.doesConsumerExist(dest)) {
                    final DemandSubscription sub = this.createDemandSubscription(dest);
                    sub.setStaticallyIncluded(true);
                    if (dest.isTopic()) {
                        sub.getLocalInfo().setSubscriptionName(this.getSubscriberName(dest));
                    }
                    try {
                        this.addSubscription(sub);
                    }
                    catch (IOException e) {
                        DurableConduitBridge.LOG.error("Failed to add static destination {}", dest, e);
                    }
                    DurableConduitBridge.LOG.trace("Forwarding messages for durable destination: {}", dest);
                }
            }
        }
    }
    
    @Override
    protected DemandSubscription createDemandSubscription(final ConsumerInfo info) throws IOException {
        if (this.addToAlreadyInterestedConsumers(info)) {
            return null;
        }
        info.addNetworkConsumerId(info.getConsumerId());
        if (info.isDurable()) {
            info.setSubscriptionName(this.getSubscriberName(info.getDestination()));
            info.setConsumerId(new ConsumerId(this.localSessionInfo.getSessionId(), this.consumerIdGenerator.getNextSequenceId()));
        }
        info.setSelector(null);
        return this.doCreateDemandSubscription(info);
    }
    
    protected String getSubscriberName(final ActiveMQDestination dest) {
        final String subscriberName = "NC-DS_" + this.configuration.getBrokerName() + "_" + dest.getPhysicalName();
        return subscriberName;
    }
    
    protected boolean doesConsumerExist(final ActiveMQDestination dest) {
        final DestinationFilter filter = DestinationFilter.parseFilter(dest);
        for (final DemandSubscription ds : this.subscriptionMapByLocalId.values()) {
            if (filter.matches(ds.getLocalInfo().getDestination())) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LOG = LoggerFactory.getLogger(DurableConduitBridge.class);
    }
}
