// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.filter.BooleanExpression;

public class ConsumerInfo extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 5;
    public static final byte HIGH_PRIORITY = 10;
    public static final byte NORMAL_PRIORITY = 0;
    public static final byte NETWORK_CONSUMER_PRIORITY = -5;
    public static final byte LOW_PRIORITY = -10;
    protected ConsumerId consumerId;
    protected ActiveMQDestination destination;
    protected int prefetchSize;
    protected int maximumPendingMessageLimit;
    protected boolean browser;
    protected boolean dispatchAsync;
    protected String selector;
    protected String clientId;
    protected String subscriptionName;
    protected boolean noLocal;
    protected boolean exclusive;
    protected boolean retroactive;
    protected byte priority;
    protected BrokerId[] brokerPath;
    protected boolean optimizedAcknowledge;
    protected transient int currentPrefetchSize;
    protected boolean noRangeAcks;
    protected BooleanExpression additionalPredicate;
    protected transient boolean networkSubscription;
    protected transient List<ConsumerId> networkConsumerIds;
    private transient long lastDeliveredSequenceId;
    
    public ConsumerInfo() {
    }
    
    public ConsumerInfo(final ConsumerId consumerId) {
        this.consumerId = consumerId;
    }
    
    public ConsumerInfo(final SessionInfo sessionInfo, final long consumerId) {
        this.consumerId = new ConsumerId(sessionInfo.getSessionId(), consumerId);
    }
    
    public ConsumerInfo copy() {
        final ConsumerInfo info = new ConsumerInfo();
        this.copy(info);
        return info;
    }
    
    public void copy(final ConsumerInfo info) {
        super.copy(info);
        info.consumerId = this.consumerId;
        info.destination = this.destination;
        info.prefetchSize = this.prefetchSize;
        info.maximumPendingMessageLimit = this.maximumPendingMessageLimit;
        info.browser = this.browser;
        info.dispatchAsync = this.dispatchAsync;
        info.selector = this.selector;
        info.clientId = this.clientId;
        info.subscriptionName = this.subscriptionName;
        info.noLocal = this.noLocal;
        info.exclusive = this.exclusive;
        info.retroactive = this.retroactive;
        info.priority = this.priority;
        info.brokerPath = this.brokerPath;
        info.networkSubscription = this.networkSubscription;
        if (this.networkConsumerIds != null) {
            if (info.networkConsumerIds == null) {
                info.networkConsumerIds = new ArrayList<ConsumerId>();
            }
            info.networkConsumerIds.addAll(this.networkConsumerIds);
        }
    }
    
    public boolean isDurable() {
        return this.subscriptionName != null;
    }
    
    @Override
    public byte getDataStructureType() {
        return 5;
    }
    
    public ConsumerId getConsumerId() {
        return this.consumerId;
    }
    
    public void setConsumerId(final ConsumerId consumerId) {
        this.consumerId = consumerId;
    }
    
    public boolean isBrowser() {
        return this.browser;
    }
    
    public void setBrowser(final boolean browser) {
        this.browser = browser;
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public int getPrefetchSize() {
        return this.prefetchSize;
    }
    
    public void setPrefetchSize(final int prefetchSize) {
        this.prefetchSize = prefetchSize;
        this.currentPrefetchSize = prefetchSize;
    }
    
    public int getMaximumPendingMessageLimit() {
        return this.maximumPendingMessageLimit;
    }
    
    public void setMaximumPendingMessageLimit(final int maximumPendingMessageLimit) {
        this.maximumPendingMessageLimit = maximumPendingMessageLimit;
    }
    
    public boolean isDispatchAsync() {
        return this.dispatchAsync;
    }
    
    public void setDispatchAsync(final boolean dispatchAsync) {
        this.dispatchAsync = dispatchAsync;
    }
    
    public String getSelector() {
        return this.selector;
    }
    
    public void setSelector(final String selector) {
        this.selector = selector;
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
    
    public String getSubscriptionName() {
        return this.subscriptionName;
    }
    
    public void setSubscriptionName(final String durableSubscriptionId) {
        this.subscriptionName = durableSubscriptionId;
    }
    
    public boolean isNoLocal() {
        return this.noLocal;
    }
    
    public void setNoLocal(final boolean noLocal) {
        this.noLocal = noLocal;
    }
    
    public boolean isExclusive() {
        return this.exclusive;
    }
    
    public void setExclusive(final boolean exclusive) {
        this.exclusive = exclusive;
    }
    
    public boolean isRetroactive() {
        return this.retroactive;
    }
    
    public void setRetroactive(final boolean retroactive) {
        this.retroactive = retroactive;
    }
    
    public RemoveInfo createRemoveCommand() {
        final RemoveInfo command = new RemoveInfo(this.getConsumerId());
        command.setResponseRequired(this.isResponseRequired());
        return command;
    }
    
    public byte getPriority() {
        return this.priority;
    }
    
    public void setPriority(final byte priority) {
        this.priority = priority;
    }
    
    public BrokerId[] getBrokerPath() {
        return this.brokerPath;
    }
    
    public void setBrokerPath(final BrokerId[] brokerPath) {
        this.brokerPath = brokerPath;
    }
    
    public BooleanExpression getAdditionalPredicate() {
        return this.additionalPredicate;
    }
    
    public void setAdditionalPredicate(final BooleanExpression additionalPredicate) {
        this.additionalPredicate = additionalPredicate;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processAddConsumer(this);
    }
    
    public boolean isNetworkSubscription() {
        return this.networkSubscription;
    }
    
    public void setNetworkSubscription(final boolean networkSubscription) {
        this.networkSubscription = networkSubscription;
    }
    
    public boolean isOptimizedAcknowledge() {
        return this.optimizedAcknowledge;
    }
    
    public void setOptimizedAcknowledge(final boolean optimizedAcknowledge) {
        this.optimizedAcknowledge = optimizedAcknowledge;
    }
    
    public int getCurrentPrefetchSize() {
        return this.currentPrefetchSize;
    }
    
    public void setCurrentPrefetchSize(final int currentPrefetchSize) {
        this.currentPrefetchSize = currentPrefetchSize;
    }
    
    public boolean isNoRangeAcks() {
        return this.noRangeAcks;
    }
    
    public void setNoRangeAcks(final boolean noRangeAcks) {
        this.noRangeAcks = noRangeAcks;
    }
    
    public synchronized void addNetworkConsumerId(final ConsumerId networkConsumerId) {
        if (this.networkConsumerIds == null) {
            this.networkConsumerIds = new ArrayList<ConsumerId>();
        }
        this.networkConsumerIds.add(networkConsumerId);
    }
    
    public synchronized void removeNetworkConsumerId(final ConsumerId networkConsumerId) {
        if (this.networkConsumerIds != null) {
            this.networkConsumerIds.remove(networkConsumerId);
            if (this.networkConsumerIds.isEmpty()) {
                this.networkConsumerIds = null;
            }
        }
    }
    
    public synchronized boolean isNetworkConsumersEmpty() {
        return this.networkConsumerIds == null || this.networkConsumerIds.isEmpty();
    }
    
    public synchronized List<ConsumerId> getNetworkConsumerIds() {
        final List<ConsumerId> result = new ArrayList<ConsumerId>();
        if (this.networkConsumerIds != null) {
            result.addAll(this.networkConsumerIds);
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        return (this.consumerId == null) ? 0 : this.consumerId.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ConsumerInfo other = (ConsumerInfo)obj;
        return (this.consumerId != null || other.consumerId == null) && this.consumerId.equals(other.consumerId);
    }
    
    public ConsumerId[] getNetworkConsumerPath() {
        ConsumerId[] result = null;
        if (this.networkConsumerIds != null) {
            result = this.networkConsumerIds.toArray(new ConsumerId[0]);
        }
        return result;
    }
    
    public void setNetworkConsumerPath(final ConsumerId[] consumerPath) {
        if (consumerPath != null) {
            for (int i = 0; i < consumerPath.length; ++i) {
                this.addNetworkConsumerId(consumerPath[i]);
            }
        }
    }
    
    public void setLastDeliveredSequenceId(final long lastDeliveredSequenceId) {
        this.lastDeliveredSequenceId = lastDeliveredSequenceId;
    }
    
    public long getLastDeliveredSequenceId() {
        return this.lastDeliveredSequenceId;
    }
}
