// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.RemoveInfo;
import org.apache.activemq.state.CommandVisitor;
import org.apache.activemq.command.Command;
import org.apache.activemq.command.ConsumerControl;
import org.apache.activemq.transport.InactivityIOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.broker.Connection;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.activemq.broker.ConnectionContext;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.broker.region.Subscription;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.thread.Scheduler;
import org.slf4j.Logger;

public class AbortSlowConsumerStrategy implements SlowConsumerStrategy, Runnable
{
    private static final Logger LOG;
    protected String name;
    protected Scheduler scheduler;
    protected Broker broker;
    protected final AtomicBoolean taskStarted;
    protected final Map<Subscription, SlowConsumerEntry> slowConsumers;
    private long maxSlowCount;
    private long maxSlowDuration;
    private long checkPeriod;
    private boolean abortConnection;
    
    public AbortSlowConsumerStrategy() {
        this.name = "AbortSlowConsumerStrategy@" + this.hashCode();
        this.taskStarted = new AtomicBoolean(false);
        this.slowConsumers = new ConcurrentHashMap<Subscription, SlowConsumerEntry>();
        this.maxSlowCount = -1L;
        this.maxSlowDuration = 30000L;
        this.checkPeriod = 30000L;
        this.abortConnection = false;
    }
    
    @Override
    public void setBrokerService(final Broker broker) {
        this.scheduler = broker.getScheduler();
        this.broker = broker;
    }
    
    @Override
    public void slowConsumer(final ConnectionContext context, final Subscription subs) {
        if (this.maxSlowCount < 0L && this.maxSlowDuration < 0L) {
            AbortSlowConsumerStrategy.LOG.info("no limits set, slowConsumer strategy has nothing to do");
            return;
        }
        if (this.taskStarted.compareAndSet(false, true)) {
            this.scheduler.executePeriodically(this, this.checkPeriod);
        }
        if (!this.slowConsumers.containsKey(subs)) {
            this.slowConsumers.put(subs, new SlowConsumerEntry(context));
        }
        else if (this.maxSlowCount > 0L) {
            this.slowConsumers.get(subs).slow();
        }
    }
    
    @Override
    public void run() {
        if (this.maxSlowDuration > 0L) {
            for (final SlowConsumerEntry entry : this.slowConsumers.values()) {
                entry.mark();
            }
        }
        final HashMap<Subscription, SlowConsumerEntry> toAbort = new HashMap<Subscription, SlowConsumerEntry>();
        for (final Map.Entry<Subscription, SlowConsumerEntry> entry2 : this.slowConsumers.entrySet()) {
            if (entry2.getKey().isSlowConsumer()) {
                if ((this.maxSlowDuration <= 0L || entry2.getValue().markCount * this.checkPeriod < this.maxSlowDuration) && (this.maxSlowCount <= 0L || entry2.getValue().slowCount < this.maxSlowCount)) {
                    continue;
                }
                toAbort.put(entry2.getKey(), entry2.getValue());
                this.slowConsumers.remove(entry2.getKey());
            }
            else {
                AbortSlowConsumerStrategy.LOG.info("sub: " + entry2.getKey().getConsumerInfo().getConsumerId() + " is no longer slow");
                this.slowConsumers.remove(entry2.getKey());
            }
        }
        this.abortSubscription(toAbort, this.abortConnection);
    }
    
    protected void abortSubscription(final Map<Subscription, SlowConsumerEntry> toAbort, final boolean abortSubscriberConnection) {
        final Map<Connection, List<Subscription>> abortMap = new HashMap<Connection, List<Subscription>>();
        for (final Map.Entry<Subscription, SlowConsumerEntry> entry : toAbort.entrySet()) {
            final ConnectionContext connectionContext = entry.getValue().context;
            if (connectionContext == null) {
                continue;
            }
            final Connection connection = connectionContext.getConnection();
            if (connection == null) {
                AbortSlowConsumerStrategy.LOG.debug("slowConsumer abort ignored, no connection in context:" + connectionContext);
            }
            if (!abortMap.containsKey(connection)) {
                abortMap.put(connection, new ArrayList<Subscription>());
            }
            abortMap.get(connection).add(entry.getKey());
        }
        for (final Map.Entry<Connection, List<Subscription>> entry2 : abortMap.entrySet()) {
            final Connection connection2 = entry2.getKey();
            final List<Subscription> subscriptions = entry2.getValue();
            if (abortSubscriberConnection) {
                AbortSlowConsumerStrategy.LOG.info("aborting connection:{} with {} slow consumers", connection2.getConnectionId(), subscriptions.size());
                if (AbortSlowConsumerStrategy.LOG.isTraceEnabled()) {
                    for (final Subscription subscription : subscriptions) {
                        AbortSlowConsumerStrategy.LOG.trace("Connection {} being aborted because of slow consumer: {} on destination: {}", connection2.getConnectionId(), subscription.getConsumerInfo().getConsumerId(), subscription.getActiveMQDestination());
                    }
                }
                try {
                    this.scheduler.executeAfterDelay(new Runnable() {
                        @Override
                        public void run() {
                            connection2.serviceException(new InactivityIOException(subscriptions.size() + " Consumers was slow too often (>" + AbortSlowConsumerStrategy.this.maxSlowCount + ") or too long (>" + AbortSlowConsumerStrategy.this.maxSlowDuration + "): "));
                        }
                    }, 0L);
                }
                catch (Exception e2) {
                    AbortSlowConsumerStrategy.LOG.info("exception on aborting connection {} with {} slow consumers", connection2.getConnectionId(), subscriptions.size());
                }
            }
            else {
                for (final Subscription subToClose : subscriptions) {
                    final Subscription subscription = subToClose;
                    AbortSlowConsumerStrategy.LOG.info("aborting slow consumer: {} for destination:{}", subscription.getConsumerInfo().getConsumerId(), subscription.getActiveMQDestination());
                    try {
                        final ConsumerControl stopConsumer = new ConsumerControl();
                        stopConsumer.setConsumerId(subscription.getConsumerInfo().getConsumerId());
                        stopConsumer.setClose(true);
                        connection2.dispatchAsync(stopConsumer);
                    }
                    catch (Exception e) {
                        AbortSlowConsumerStrategy.LOG.info("exception on aborting slow consumer: {}", subscription.getConsumerInfo().getConsumerId(), e);
                    }
                    try {
                        this.scheduler.executeAfterDelay(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final RemoveInfo removeCommand = subToClose.getConsumerInfo().createRemoveCommand();
                                    if (connection2 instanceof CommandVisitor) {
                                        removeCommand.visit((CommandVisitor)connection2);
                                    }
                                    else {
                                        connection2.service(removeCommand);
                                    }
                                }
                                catch (IllegalStateException ex) {}
                                catch (Exception e) {
                                    AbortSlowConsumerStrategy.LOG.info("exception on local remove of slow consumer: {}", subToClose.getConsumerInfo().getConsumerId(), e);
                                }
                            }
                        }, 1000L);
                    }
                    catch (Exception e) {
                        AbortSlowConsumerStrategy.LOG.info("exception on local remove of slow consumer: {}", subscription.getConsumerInfo().getConsumerId(), e);
                    }
                }
            }
        }
    }
    
    public void abortConsumer(final Subscription sub, final boolean abortSubscriberConnection) {
        if (sub != null) {
            final SlowConsumerEntry entry = this.slowConsumers.remove(sub);
            if (entry != null) {
                final Map<Subscription, SlowConsumerEntry> toAbort = new HashMap<Subscription, SlowConsumerEntry>();
                toAbort.put(sub, entry);
                this.abortSubscription(toAbort, abortSubscriberConnection);
            }
            else {
                AbortSlowConsumerStrategy.LOG.warn("cannot abort subscription as it no longer exists in the map of slow consumers: " + sub);
            }
        }
    }
    
    public long getMaxSlowCount() {
        return this.maxSlowCount;
    }
    
    public void setMaxSlowCount(final long maxSlowCount) {
        this.maxSlowCount = maxSlowCount;
    }
    
    public long getMaxSlowDuration() {
        return this.maxSlowDuration;
    }
    
    public void setMaxSlowDuration(final long maxSlowDuration) {
        this.maxSlowDuration = maxSlowDuration;
    }
    
    public long getCheckPeriod() {
        return this.checkPeriod;
    }
    
    public void setCheckPeriod(final long checkPeriod) {
        this.checkPeriod = checkPeriod;
    }
    
    public boolean isAbortConnection() {
        return this.abortConnection;
    }
    
    public void setAbortConnection(final boolean abortConnection) {
        this.abortConnection = abortConnection;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Map<Subscription, SlowConsumerEntry> getSlowConsumers() {
        return this.slowConsumers;
    }
    
    @Override
    public void addDestination(final Destination destination) {
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbortSlowConsumerStrategy.class);
    }
}
