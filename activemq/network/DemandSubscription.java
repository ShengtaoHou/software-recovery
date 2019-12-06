// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.activemq.command.NetworkBridgeFilter;
import org.apache.activemq.command.SubscriptionInfo;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.command.ConsumerId;
import java.util.Set;
import org.apache.activemq.command.ConsumerInfo;
import org.slf4j.Logger;

public class DemandSubscription
{
    private static final Logger LOG;
    private final ConsumerInfo remoteInfo;
    private final ConsumerInfo localInfo;
    private final Set<ConsumerId> remoteSubsIds;
    private final AtomicInteger dispatched;
    private final AtomicBoolean activeWaiter;
    private final Set<SubscriptionInfo> durableRemoteSubs;
    private SubscriptionInfo localDurableSubscriber;
    private NetworkBridgeFilter networkBridgeFilter;
    private boolean staticallyIncluded;
    
    DemandSubscription(final ConsumerInfo info) {
        this.remoteSubsIds = new CopyOnWriteArraySet<ConsumerId>();
        this.dispatched = new AtomicInteger(0);
        this.activeWaiter = new AtomicBoolean();
        this.durableRemoteSubs = new CopyOnWriteArraySet<SubscriptionInfo>();
        this.remoteInfo = info;
        (this.localInfo = info.copy()).setNetworkSubscription(true);
        this.remoteSubsIds.add(info.getConsumerId());
    }
    
    @Override
    public String toString() {
        return "DemandSub{" + this.localInfo.getConsumerId() + ",remotes:" + this.remoteSubsIds + "}";
    }
    
    public boolean add(final ConsumerId id) {
        return this.remoteSubsIds.add(id);
    }
    
    public boolean remove(final ConsumerId id) {
        return this.remoteSubsIds.remove(id);
    }
    
    public Set<SubscriptionInfo> getDurableRemoteSubs() {
        return this.durableRemoteSubs;
    }
    
    public boolean isEmpty() {
        return this.remoteSubsIds.isEmpty();
    }
    
    public int size() {
        return this.remoteSubsIds.size();
    }
    
    public ConsumerInfo getLocalInfo() {
        return this.localInfo;
    }
    
    public ConsumerInfo getRemoteInfo() {
        return this.remoteInfo;
    }
    
    public void waitForCompletion() {
        if (this.dispatched.get() > 0) {
            DemandSubscription.LOG.debug("Waiting for completion for sub: {}, dispatched: {}", this.localInfo.getConsumerId(), this.dispatched.get());
            this.activeWaiter.set(true);
            if (this.dispatched.get() > 0) {
                synchronized (this.activeWaiter) {
                    try {
                        this.activeWaiter.wait(TimeUnit.SECONDS.toMillis(30L));
                    }
                    catch (InterruptedException ex) {}
                }
                if (this.dispatched.get() > 0) {
                    DemandSubscription.LOG.warn("demand sub interrupted or timedout while waiting for outstanding responses, expect potentially {} duplicate forwards", (Object)this.dispatched.get());
                }
            }
        }
    }
    
    public void decrementOutstandingResponses() {
        if (this.dispatched.decrementAndGet() == 0 && this.activeWaiter.get()) {
            synchronized (this.activeWaiter) {
                this.activeWaiter.notifyAll();
            }
        }
    }
    
    public boolean incrementOutstandingResponses() {
        this.dispatched.incrementAndGet();
        if (this.activeWaiter.get()) {
            this.decrementOutstandingResponses();
            return false;
        }
        return true;
    }
    
    public NetworkBridgeFilter getNetworkBridgeFilter() {
        return this.networkBridgeFilter;
    }
    
    public void setNetworkBridgeFilter(final NetworkBridgeFilter networkBridgeFilter) {
        this.networkBridgeFilter = networkBridgeFilter;
    }
    
    public SubscriptionInfo getLocalDurableSubscriber() {
        return this.localDurableSubscriber;
    }
    
    public void setLocalDurableSubscriber(final SubscriptionInfo localDurableSubscriber) {
        this.localDurableSubscriber = localDurableSubscriber;
    }
    
    public boolean isStaticallyIncluded() {
        return this.staticallyIncluded;
    }
    
    public void setStaticallyIncluded(final boolean staticallyIncluded) {
        this.staticallyIncluded = staticallyIncluded;
    }
    
    static {
        LOG = LoggerFactory.getLogger(DemandSubscription.class);
    }
}
