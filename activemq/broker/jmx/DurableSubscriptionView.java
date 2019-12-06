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
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import org.apache.activemq.broker.BrokerService;

public class DurableSubscriptionView extends SubscriptionView implements DurableSubscriptionViewMBean
{
    protected ManagedRegionBroker broker;
    protected BrokerService brokerService;
    protected String subscriptionName;
    protected DurableTopicSubscription durableSub;
    
    public DurableSubscriptionView(final ManagedRegionBroker broker, final BrokerService brokerService, final String clientId, final String userName, final Subscription sub) {
        super(clientId, userName, sub);
        this.broker = broker;
        this.brokerService = brokerService;
        this.durableSub = (DurableTopicSubscription)sub;
        if (sub != null) {
            this.subscriptionName = sub.getConsumerInfo().getSubscriptionName();
        }
    }
    
    @Override
    public String getSubscriptionName() {
        return this.subscriptionName;
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
        info.setSubscriptionName(this.subscriptionName);
        final ConnectionContext context = new ConnectionContext();
        context.setBroker(this.broker);
        context.setClientId(this.clientId);
        this.brokerService.getBroker().removeSubscription(context, info);
    }
    
    @Override
    public String toString() {
        return "ActiveDurableSubscriptionView: " + this.getClientId() + ":" + this.getSubscriptionName();
    }
    
    @Override
    public int cursorSize() {
        if (this.durableSub != null && this.durableSub.getPending() != null) {
            return this.durableSub.getPending().size();
        }
        return 0;
    }
    
    @Override
    public boolean doesCursorHaveMessagesBuffered() {
        return this.durableSub != null && this.durableSub.getPending() != null && this.durableSub.getPending().hasMessagesBufferedToDeliver();
    }
    
    @Override
    public boolean doesCursorHaveSpace() {
        return this.durableSub != null && this.durableSub.getPending() != null && this.durableSub.getPending().hasSpace();
    }
    
    @Override
    public long getCursorMemoryUsage() {
        if (this.durableSub != null && this.durableSub.getPending() != null && this.durableSub.getPending().getSystemUsage() != null) {
            return this.durableSub.getPending().getSystemUsage().getMemoryUsage().getUsage();
        }
        return 0L;
    }
    
    @Override
    public int getCursorPercentUsage() {
        if (this.durableSub != null && this.durableSub.getPending() != null && this.durableSub.getPending().getSystemUsage() != null) {
            return this.durableSub.getPending().getSystemUsage().getMemoryUsage().getPercentUsage();
        }
        return 0;
    }
    
    @Override
    public boolean isCursorFull() {
        return this.durableSub != null && this.durableSub.getPending() != null && this.durableSub.getPending().isFull();
    }
    
    @Override
    public boolean isActive() {
        return this.durableSub.isActive();
    }
}
