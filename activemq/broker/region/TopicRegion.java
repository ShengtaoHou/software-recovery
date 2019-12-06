// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.slf4j.LoggerFactory;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.SubscriptionInfo;
import java.util.Set;
import org.apache.activemq.store.TopicMessageStore;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.jms.InvalidDestinationException;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.JMSException;
import org.apache.activemq.command.ConsumerInfo;
import java.util.Iterator;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import java.util.Map;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.usage.SystemUsage;
import java.util.TimerTask;
import java.util.Timer;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.util.SubscriptionKey;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class TopicRegion extends AbstractRegion
{
    private static final Logger LOG;
    protected final ConcurrentHashMap<SubscriptionKey, DurableTopicSubscription> durableSubscriptions;
    private final LongSequenceGenerator recoveredDurableSubIdGenerator;
    private final SessionId recoveredDurableSubSessionId;
    private boolean keepDurableSubsActive;
    private Timer cleanupTimer;
    private TimerTask cleanupTask;
    
    public TopicRegion(final RegionBroker broker, final DestinationStatistics destinationStatistics, final SystemUsage memoryManager, final TaskRunnerFactory taskRunnerFactory, final DestinationFactory destinationFactory) {
        super(broker, destinationStatistics, memoryManager, taskRunnerFactory, destinationFactory);
        this.durableSubscriptions = new ConcurrentHashMap<SubscriptionKey, DurableTopicSubscription>();
        this.recoveredDurableSubIdGenerator = new LongSequenceGenerator();
        this.recoveredDurableSubSessionId = new SessionId(new ConnectionId("OFFLINE"), this.recoveredDurableSubIdGenerator.getNextSequenceId());
        if (broker.getBrokerService().getOfflineDurableSubscriberTaskSchedule() != -1L && broker.getBrokerService().getOfflineDurableSubscriberTimeout() != -1L) {
            this.cleanupTimer = new Timer("ActiveMQ Durable Subscriber Cleanup Timer", true);
            this.cleanupTask = new TimerTask() {
                @Override
                public void run() {
                    TopicRegion.this.doCleanup();
                }
            };
            this.cleanupTimer.schedule(this.cleanupTask, broker.getBrokerService().getOfflineDurableSubscriberTaskSchedule(), broker.getBrokerService().getOfflineDurableSubscriberTaskSchedule());
        }
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        if (this.cleanupTimer != null) {
            this.cleanupTimer.cancel();
        }
    }
    
    public void doCleanup() {
        final long now = System.currentTimeMillis();
        for (final Map.Entry<SubscriptionKey, DurableTopicSubscription> entry : this.durableSubscriptions.entrySet()) {
            final DurableTopicSubscription sub = entry.getValue();
            if (!sub.isActive()) {
                final long offline = sub.getOfflineTimestamp();
                if (offline == -1L || now - offline < this.broker.getBrokerService().getOfflineDurableSubscriberTimeout()) {
                    continue;
                }
                TopicRegion.LOG.info("Destroying durable subscriber due to inactivity: {}", sub);
                try {
                    final RemoveSubscriptionInfo info = new RemoveSubscriptionInfo();
                    info.setClientId(entry.getKey().getClientId());
                    info.setSubscriptionName(entry.getKey().getSubscriptionName());
                    final ConnectionContext context = new ConnectionContext();
                    context.setBroker(this.broker);
                    context.setClientId(entry.getKey().getClientId());
                    this.removeSubscription(context, info);
                }
                catch (Exception e) {
                    TopicRegion.LOG.error("Failed to remove inactive durable subscriber", e);
                }
            }
        }
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        if (info.isDurable()) {
            final ActiveMQDestination destination = info.getDestination();
            if (!destination.isPattern()) {
                this.lookup(context, destination, true);
            }
            final String clientId = context.getClientId();
            final String subscriptionName = info.getSubscriptionName();
            final SubscriptionKey key = new SubscriptionKey(clientId, subscriptionName);
            DurableTopicSubscription sub = this.durableSubscriptions.get(key);
            if (sub != null) {
                if (sub.isActive()) {
                    throw new JMSException("Durable consumer is in use for client: " + clientId + " and subscriptionName: " + subscriptionName);
                }
                if (this.hasDurableSubChanged(info, sub.getConsumerInfo())) {
                    this.durableSubscriptions.remove(key);
                    this.destinationsLock.readLock().lock();
                    try {
                        for (final Destination dest : this.destinations.values()) {
                            if (dest instanceof Topic) {
                                final Topic topic = (Topic)dest;
                                topic.deleteSubscription(context, key);
                            }
                        }
                    }
                    finally {
                        this.destinationsLock.readLock().unlock();
                    }
                    super.removeConsumer(context, sub.getConsumerInfo());
                    super.addConsumer(context, info);
                    sub = this.durableSubscriptions.get(key);
                }
                else {
                    if (sub.getConsumerInfo().getConsumerId() != null) {
                        this.subscriptions.remove(sub.getConsumerInfo().getConsumerId());
                    }
                    this.subscriptions.put(info.getConsumerId(), sub);
                }
            }
            else {
                super.addConsumer(context, info);
                sub = this.durableSubscriptions.get(key);
                if (sub == null) {
                    throw new JMSException("Cannot use the same consumerId: " + info.getConsumerId() + " for two different durable subscriptions clientID: " + key.getClientId() + " subscriberName: " + key.getSubscriptionName());
                }
            }
            sub.activate(this.usageManager, context, info, this.broker);
            return sub;
        }
        return super.addConsumer(context, info);
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        if (info.isDurable()) {
            final SubscriptionKey key = new SubscriptionKey(context.getClientId(), info.getSubscriptionName());
            final DurableTopicSubscription sub = this.durableSubscriptions.get(key);
            if (sub != null) {
                sub.deactivate(this.keepDurableSubsActive);
            }
        }
        else {
            super.removeConsumer(context, info);
        }
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final RemoveSubscriptionInfo info) throws Exception {
        final SubscriptionKey key = new SubscriptionKey(info.getClientId(), info.getSubscriptionName());
        final DurableTopicSubscription sub = this.durableSubscriptions.get(key);
        if (sub == null) {
            throw new InvalidDestinationException("No durable subscription exists for: " + info.getSubscriptionName());
        }
        if (sub.isActive()) {
            throw new JMSException("Durable consumer is in use");
        }
        this.durableSubscriptions.remove(key);
        this.destinationsLock.readLock().lock();
        try {
            for (final Destination dest : this.destinations.values()) {
                if (dest instanceof Topic) {
                    final Topic topic = (Topic)dest;
                    topic.deleteSubscription(context, key);
                }
                else {
                    if (!(dest instanceof DestinationFilter)) {
                        continue;
                    }
                    final DestinationFilter filter = (DestinationFilter)dest;
                    filter.deleteSubscription(context, key);
                }
            }
        }
        finally {
            this.destinationsLock.readLock().unlock();
        }
        if (this.subscriptions.get(sub.getConsumerInfo().getConsumerId()) != null) {
            super.removeConsumer(context, sub.getConsumerInfo());
        }
        else {
            this.destroySubscription(sub);
        }
    }
    
    @Override
    public String toString() {
        return "TopicRegion: destinations=" + this.destinations.size() + ", subscriptions=" + this.subscriptions.size() + ", memory=" + this.usageManager.getMemoryUsage().getPercentUsage() + "%";
    }
    
    @Override
    protected List<Subscription> addSubscriptionsForDestination(final ConnectionContext context, final Destination dest) throws Exception {
        final List<Subscription> rc = super.addSubscriptionsForDestination(context, dest);
        final Set<Subscription> dupChecker = new HashSet<Subscription>(rc);
        final TopicMessageStore store = (TopicMessageStore)dest.getMessageStore();
        if (store != null) {
            final SubscriptionInfo[] infos = store.getAllSubscriptions();
            for (int i = 0; i < infos.length; ++i) {
                final SubscriptionInfo info = infos[i];
                TopicRegion.LOG.debug("Restoring durable subscription: {}", info);
                final SubscriptionKey key = new SubscriptionKey(info);
                DurableTopicSubscription sub = this.durableSubscriptions.get(key);
                final ConsumerInfo consumerInfo = this.createInactiveConsumerInfo(info);
                if (sub == null) {
                    final ConnectionContext c = new ConnectionContext();
                    c.setBroker(context.getBroker());
                    c.setClientId(key.getClientId());
                    c.setConnectionId(consumerInfo.getConsumerId().getParentId().getParentId());
                    sub = (DurableTopicSubscription)this.createSubscription(c, consumerInfo);
                    sub.setOfflineTimestamp(System.currentTimeMillis());
                }
                if (!dupChecker.contains(sub)) {
                    dupChecker.add(sub);
                    rc.add(sub);
                    dest.addSubscription(context, sub);
                }
            }
            this.durableSubscriptions.values();
            for (final DurableTopicSubscription sub2 : this.durableSubscriptions.values()) {
                if (dupChecker.contains(sub2)) {
                    continue;
                }
                if (!sub2.matches(dest.getActiveMQDestination())) {
                    continue;
                }
                rc.add(sub2);
                dest.addSubscription(context, sub2);
            }
        }
        return rc;
    }
    
    public ConsumerInfo createInactiveConsumerInfo(final SubscriptionInfo info) {
        final ConsumerInfo rc = new ConsumerInfo();
        rc.setSelector(info.getSelector());
        rc.setSubscriptionName(info.getSubscriptionName());
        rc.setDestination(info.getSubscribedDestination());
        rc.setConsumerId(this.createConsumerId());
        return rc;
    }
    
    private ConsumerId createConsumerId() {
        return new ConsumerId(this.recoveredDurableSubSessionId, this.recoveredDurableSubIdGenerator.getNextSequenceId());
    }
    
    protected void configureTopic(final Topic topic, final ActiveMQDestination destination) {
        if (this.broker.getDestinationPolicy() != null) {
            final PolicyEntry entry = this.broker.getDestinationPolicy().getEntryFor(destination);
            if (entry != null) {
                entry.configure(this.broker, topic);
            }
        }
    }
    
    @Override
    protected Subscription createSubscription(final ConnectionContext context, final ConsumerInfo info) throws JMSException {
        final ActiveMQDestination destination = info.getDestination();
        if (info.isDurable()) {
            if (AdvisorySupport.isAdvisoryTopic(info.getDestination())) {
                throw new JMSException("Cannot create a durable subscription for an advisory Topic");
            }
            final SubscriptionKey key = new SubscriptionKey(context.getClientId(), info.getSubscriptionName());
            DurableTopicSubscription sub = this.durableSubscriptions.get(key);
            if (sub == null) {
                sub = new DurableTopicSubscription(this.broker, this.usageManager, context, info, this.keepDurableSubsActive);
                if (destination != null && this.broker.getDestinationPolicy() != null) {
                    final PolicyEntry entry = this.broker.getDestinationPolicy().getEntryFor(destination);
                    if (entry != null) {
                        entry.configure(this.broker, this.usageManager, sub);
                    }
                }
                this.durableSubscriptions.put(key, sub);
                return sub;
            }
            throw new JMSException("That durable subscription is already active.");
        }
        else {
            try {
                final TopicSubscription answer = new TopicSubscription(this.broker, context, info, this.usageManager);
                if (destination != null && this.broker.getDestinationPolicy() != null) {
                    final PolicyEntry entry2 = this.broker.getDestinationPolicy().getEntryFor(destination);
                    if (entry2 != null) {
                        entry2.configure(this.broker, this.usageManager, answer);
                    }
                }
                answer.init();
                return answer;
            }
            catch (Exception e) {
                TopicRegion.LOG.error("Failed to create TopicSubscription ", e);
                final JMSException jmsEx = new JMSException("Couldn't create TopicSubscription");
                jmsEx.setLinkedException(e);
                throw jmsEx;
            }
        }
    }
    
    private boolean hasDurableSubChanged(final ConsumerInfo info1, final ConsumerInfo info2) {
        return (info1.getSelector() != null ^ info2.getSelector() != null) || (info1.getSelector() != null && !info1.getSelector().equals(info2.getSelector())) || !info1.getDestination().equals(info2.getDestination());
    }
    
    @Override
    protected Set<ActiveMQDestination> getInactiveDestinations() {
        final Set<ActiveMQDestination> inactiveDestinations = super.getInactiveDestinations();
        final Iterator<ActiveMQDestination> iter = inactiveDestinations.iterator();
        while (iter.hasNext()) {
            final ActiveMQDestination dest = iter.next();
            if (!dest.isTopic()) {
                iter.remove();
            }
        }
        return inactiveDestinations;
    }
    
    public boolean isKeepDurableSubsActive() {
        return this.keepDurableSubsActive;
    }
    
    public void setKeepDurableSubsActive(final boolean keepDurableSubsActive) {
        this.keepDurableSubsActive = keepDurableSubsActive;
    }
    
    public boolean durableSubscriptionExists(final SubscriptionKey key) {
        return this.durableSubscriptions.containsKey(key);
    }
    
    public DurableTopicSubscription getDurableSubscription(final SubscriptionKey key) {
        return this.durableSubscriptions.get(key);
    }
    
    static {
        LOG = LoggerFactory.getLogger(TopicRegion.class);
    }
}
