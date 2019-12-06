// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;
import java.util.LinkedList;
import org.apache.activemq.broker.region.Destination;
import java.util.List;
import org.slf4j.Logger;

public class AbortSlowAckConsumerStrategy extends AbortSlowConsumerStrategy
{
    private static final Logger LOG;
    private final List<Destination> destinations;
    private long maxTimeSinceLastAck;
    private boolean ignoreIdleConsumers;
    private boolean ignoreNetworkConsumers;
    
    public AbortSlowAckConsumerStrategy() {
        this.destinations = new LinkedList<Destination>();
        this.maxTimeSinceLastAck = 30000L;
        this.ignoreIdleConsumers = true;
        this.ignoreNetworkConsumers = true;
        this.name = "AbortSlowAckConsumerStrategy@" + this.hashCode();
    }
    
    @Override
    public void setBrokerService(final Broker broker) {
        super.setBrokerService(broker);
        if (this.taskStarted.compareAndSet(false, true)) {
            this.scheduler.executePeriodically(this, this.getCheckPeriod());
        }
    }
    
    @Override
    public void slowConsumer(final ConnectionContext context, final Subscription subs) {
    }
    
    @Override
    public void run() {
        if (this.maxTimeSinceLastAck < 0L) {
            AbortSlowAckConsumerStrategy.LOG.info("no limit set, slowConsumer strategy has nothing to do");
            return;
        }
        if (this.getMaxSlowDuration() > 0L) {
            for (final SlowConsumerEntry entry : this.slowConsumers.values()) {
                entry.mark();
            }
        }
        final List<Destination> disposed = new ArrayList<Destination>();
        for (final Destination destination : this.destinations) {
            if (destination.isDisposed()) {
                disposed.add(destination);
            }
            else {
                final List<Subscription> subscribers = destination.getConsumers();
                this.updateSlowConsumersList(subscribers);
            }
        }
        this.destinations.removeAll(disposed);
        this.abortAllQualifiedSlowConsumers();
    }
    
    private void updateSlowConsumersList(final List<Subscription> subscribers) {
        for (final Subscription subscriber : subscribers) {
            if (this.isIgnoreNetworkSubscriptions() && subscriber.getConsumerInfo().isNetworkSubscription()) {
                if (this.slowConsumers.remove(subscriber) == null) {
                    continue;
                }
                AbortSlowAckConsumerStrategy.LOG.info("network sub: {} is no longer slow", subscriber.getConsumerInfo().getConsumerId());
            }
            else if (this.isIgnoreIdleConsumers() && subscriber.getDispatchedQueueSize() == 0) {
                if (this.slowConsumers.remove(subscriber) == null) {
                    continue;
                }
                AbortSlowAckConsumerStrategy.LOG.info("idle sub: {} is no longer slow", subscriber.getConsumerInfo().getConsumerId());
            }
            else {
                final long lastAckTime = subscriber.getTimeOfLastMessageAck();
                final long timeDelta = System.currentTimeMillis() - lastAckTime;
                if (timeDelta > this.maxTimeSinceLastAck) {
                    if (!this.slowConsumers.containsKey(subscriber)) {
                        AbortSlowAckConsumerStrategy.LOG.debug("sub: {} is now slow", subscriber.getConsumerInfo().getConsumerId());
                        final SlowConsumerEntry entry = new SlowConsumerEntry(subscriber.getContext());
                        entry.mark();
                        this.slowConsumers.put(subscriber, entry);
                    }
                    else {
                        if (this.getMaxSlowCount() <= 0L) {
                            continue;
                        }
                        this.slowConsumers.get(subscriber).slow();
                    }
                }
                else {
                    if (this.slowConsumers.remove(subscriber) == null) {
                        continue;
                    }
                    AbortSlowAckConsumerStrategy.LOG.info("sub: {} is no longer slow", subscriber.getConsumerInfo().getConsumerId());
                }
            }
        }
    }
    
    private void abortAllQualifiedSlowConsumers() {
        final HashMap<Subscription, SlowConsumerEntry> toAbort = new HashMap<Subscription, SlowConsumerEntry>();
        for (final Map.Entry<Subscription, SlowConsumerEntry> entry : this.slowConsumers.entrySet()) {
            if ((this.getMaxSlowDuration() > 0L && entry.getValue().markCount * this.getCheckPeriod() >= this.getMaxSlowDuration()) || (this.getMaxSlowCount() > 0L && entry.getValue().slowCount >= this.getMaxSlowCount())) {
                AbortSlowAckConsumerStrategy.LOG.trace("Transferring consumer{} to the abort list: {} slow duration = {}, slow count = {}", entry.getKey().getConsumerInfo().getConsumerId(), entry.getValue().markCount * this.getCheckPeriod(), entry.getValue().getSlowCount());
                toAbort.put(entry.getKey(), entry.getValue());
                this.slowConsumers.remove(entry.getKey());
            }
            else {
                AbortSlowAckConsumerStrategy.LOG.trace("Not yet time to abort consumer {}: slow duration = {}, slow count = {}", entry.getKey().getConsumerInfo().getConsumerId(), entry.getValue().markCount * this.getCheckPeriod(), entry.getValue().slowCount);
            }
        }
        this.abortSubscription(toAbort, this.isAbortConnection());
    }
    
    @Override
    public void addDestination(final Destination destination) {
        this.destinations.add(destination);
    }
    
    public long getMaxTimeSinceLastAck() {
        return this.maxTimeSinceLastAck;
    }
    
    public void setMaxTimeSinceLastAck(final long maxTimeSinceLastAck) {
        this.maxTimeSinceLastAck = maxTimeSinceLastAck;
    }
    
    public boolean isIgnoreIdleConsumers() {
        return this.ignoreIdleConsumers;
    }
    
    public void setIgnoreIdleConsumers(final boolean ignoreIdleConsumers) {
        this.ignoreIdleConsumers = ignoreIdleConsumers;
    }
    
    public boolean isIgnoreNetworkSubscriptions() {
        return this.ignoreNetworkConsumers;
    }
    
    public void setIgnoreNetworkConsumers(final boolean ignoreNetworkConsumers) {
        this.ignoreNetworkConsumers = ignoreNetworkConsumers;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbortSlowAckConsumerStrategy.class);
    }
}
