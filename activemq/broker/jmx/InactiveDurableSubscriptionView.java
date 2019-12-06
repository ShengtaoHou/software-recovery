// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeData;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.SubscriptionInfo;

public class InactiveDurableSubscriptionView extends DurableSubscriptionView implements DurableSubscriptionViewMBean
{
    protected SubscriptionInfo subscriptionInfo;
    
    public InactiveDurableSubscriptionView(final ManagedRegionBroker broker, final BrokerService brokerService, final String clientId, final SubscriptionInfo subInfo, final Subscription subscription) {
        super(broker, brokerService, clientId, null, subscription);
        this.broker = broker;
        this.subscriptionInfo = subInfo;
    }
    
    @Override
    public long getSubscriptionId() {
        return -1L;
    }
    
    @Override
    public String getDestinationName() {
        return this.subscriptionInfo.getDestination().getPhysicalName();
    }
    
    @Override
    public boolean isDestinationQueue() {
        return false;
    }
    
    @Override
    public boolean isDestinationTopic() {
        return true;
    }
    
    @Override
    public boolean isDestinationTemporary() {
        return false;
    }
    
    @Override
    public String getSubscriptionName() {
        return this.subscriptionInfo.getSubscriptionName();
    }
    
    @Override
    public boolean isActive() {
        return false;
    }
    
    @Override
    protected ConsumerInfo getConsumerInfo() {
        return null;
    }
    
    @Override
    public CompositeData[] browse() throws OpenDataException {
        return this.broker.browse(this);
    }
    
    @Override
    public TabularData browseAsTable() throws OpenDataException {
        return this.broker.browseAsTable(this);
    }
    
    @Override
    public void destroy() throws Exception {
        final RemoveSubscriptionInfo info = new RemoveSubscriptionInfo();
        info.setClientId(this.clientId);
        info.setSubscriptionName(this.subscriptionInfo.getSubscriptionName());
        final ConnectionContext context = new ConnectionContext();
        context.setBroker(this.broker);
        context.setClientId(this.clientId);
        this.brokerService.getBroker().removeSubscription(context, info);
    }
    
    @Override
    public String toString() {
        return "InactiveDurableSubscriptionView: " + this.getClientId() + ":" + this.getSubscriptionName();
    }
    
    @Override
    public String getSelector() {
        return this.subscriptionInfo.getSelector();
    }
}
