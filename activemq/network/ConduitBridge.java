// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;
import org.apache.activemq.command.ConsumerId;
import java.util.Iterator;
import org.apache.activemq.command.SubscriptionInfo;
import org.apache.activemq.filter.DestinationFilter;
import java.io.IOException;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.transport.Transport;
import org.slf4j.Logger;

public class ConduitBridge extends DemandForwardingBridge
{
    private static final Logger LOG;
    
    public ConduitBridge(final NetworkBridgeConfiguration configuration, final Transport localBroker, final Transport remoteBroker) {
        super(configuration, localBroker, remoteBroker);
    }
    
    @Override
    protected DemandSubscription createDemandSubscription(final ConsumerInfo info) throws IOException {
        if (this.addToAlreadyInterestedConsumers(info)) {
            return null;
        }
        info.addNetworkConsumerId(info.getConsumerId());
        info.setSelector(null);
        return this.doCreateDemandSubscription(info);
    }
    
    protected boolean addToAlreadyInterestedConsumers(final ConsumerInfo info) {
        if (info.isNetworkSubscription()) {
            return false;
        }
        boolean matched = false;
        for (final DemandSubscription ds : this.subscriptionMapByLocalId.values()) {
            final DestinationFilter filter = DestinationFilter.parseFilter(ds.getLocalInfo().getDestination());
            if (this.canConduit(ds) && filter.matches(info.getDestination())) {
                ConduitBridge.LOG.debug("{} {} with ids {} matched (add interest) {}", this.configuration.getBrokerName(), info, info.getNetworkConsumerIds(), ds);
                if (!info.isDurable()) {
                    ds.add(info.getConsumerId());
                }
                else {
                    ds.getDurableRemoteSubs().add(new SubscriptionInfo(info.getClientId(), info.getSubscriptionName()));
                }
                matched = true;
            }
        }
        return matched;
    }
    
    private boolean canConduit(final DemandSubscription ds) {
        return ds.isStaticallyIncluded() || !ds.getRemoteInfo().isNetworkSubscription();
    }
    
    @Override
    protected void removeDemandSubscription(final ConsumerId id) throws IOException {
        final List<DemandSubscription> tmpList = new ArrayList<DemandSubscription>();
        for (final DemandSubscription ds : this.subscriptionMapByLocalId.values()) {
            if (ds.remove(id)) {
                ConduitBridge.LOG.debug("{} on {} from {} removed interest for: {} from {}", this.configuration.getBrokerName(), this.localBroker, this.remoteBrokerName, id, ds);
            }
            if (ds.isEmpty()) {
                tmpList.add(ds);
            }
        }
        for (final DemandSubscription ds : tmpList) {
            this.removeSubscription(ds);
            ConduitBridge.LOG.debug("{} on {} from {} removed {}", this.configuration.getBrokerName(), this.localBroker, this.remoteBrokerName, ds);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ConduitBridge.class);
    }
}
