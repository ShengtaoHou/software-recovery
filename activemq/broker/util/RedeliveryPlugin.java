// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.state.ProducerState;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.ConnectionContext;
import java.util.Iterator;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.filter.AnyDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerPluginSupport;

public class RedeliveryPlugin extends BrokerPluginSupport
{
    private static final Logger LOG;
    public static final String REDELIVERY_DELAY = "redeliveryDelay";
    RedeliveryPolicyMap redeliveryPolicyMap;
    boolean sendToDlqIfMaxRetriesExceeded;
    private boolean fallbackToDeadLetter;
    
    public RedeliveryPlugin() {
        this.redeliveryPolicyMap = new RedeliveryPolicyMap();
        this.sendToDlqIfMaxRetriesExceeded = true;
        this.fallbackToDeadLetter = true;
    }
    
    @Override
    public Broker installPlugin(final Broker broker) throws Exception {
        if (!broker.getBrokerService().isSchedulerSupport()) {
            throw new IllegalStateException("RedeliveryPlugin requires schedulerSupport=true on the broker");
        }
        this.validatePolicyDelay(1000L);
        return super.installPlugin(broker);
    }
    
    private void validatePolicyDelay(final long limit) {
        final ActiveMQDestination matchAll = new AnyDestination(new ActiveMQDestination[] { new ActiveMQQueue(">"), new ActiveMQTopic(">") });
        for (final Object entry : this.redeliveryPolicyMap.get(matchAll)) {
            final RedeliveryPolicy redeliveryPolicy = (RedeliveryPolicy)entry;
            this.validateLimit(limit, redeliveryPolicy);
        }
        final RedeliveryPolicy defaultEntry = this.redeliveryPolicyMap.getDefaultEntry();
        if (defaultEntry != null) {
            this.validateLimit(limit, defaultEntry);
        }
    }
    
    private void validateLimit(final long limit, final RedeliveryPolicy redeliveryPolicy) {
        if (redeliveryPolicy.getInitialRedeliveryDelay() < limit) {
            throw new IllegalStateException("RedeliveryPolicy initialRedeliveryDelay must exceed: " + limit + ". " + redeliveryPolicy);
        }
        if (redeliveryPolicy.getRedeliveryDelay() < limit) {
            throw new IllegalStateException("RedeliveryPolicy redeliveryDelay must exceed: " + limit + ". " + redeliveryPolicy);
        }
    }
    
    public RedeliveryPolicyMap getRedeliveryPolicyMap() {
        return this.redeliveryPolicyMap;
    }
    
    public void setRedeliveryPolicyMap(final RedeliveryPolicyMap redeliveryPolicyMap) {
        this.redeliveryPolicyMap = redeliveryPolicyMap;
    }
    
    public boolean isSendToDlqIfMaxRetriesExceeded() {
        return this.sendToDlqIfMaxRetriesExceeded;
    }
    
    public void setSendToDlqIfMaxRetriesExceeded(final boolean sendToDlqIfMaxRetriesExceeded) {
        this.sendToDlqIfMaxRetriesExceeded = sendToDlqIfMaxRetriesExceeded;
    }
    
    public boolean isFallbackToDeadLetter() {
        return this.fallbackToDeadLetter;
    }
    
    public void setFallbackToDeadLetter(final boolean fallbackToDeadLetter) {
        this.fallbackToDeadLetter = fallbackToDeadLetter;
    }
    
    @Override
    public boolean sendToDeadLetterQueue(final ConnectionContext context, final MessageReference messageReference, final Subscription subscription, final Throwable poisonCause) {
        if (messageReference.isExpired()) {
            return super.sendToDeadLetterQueue(context, messageReference, subscription, poisonCause);
        }
        try {
            final Destination regionDestination = (Destination)messageReference.getRegionDestination();
            final RedeliveryPolicy redeliveryPolicy = this.redeliveryPolicyMap.getEntryFor(regionDestination.getActiveMQDestination());
            if (redeliveryPolicy != null) {
                final int maximumRedeliveries = redeliveryPolicy.getMaximumRedeliveries();
                int redeliveryCount = messageReference.getRedeliveryCounter();
                if (-1 == maximumRedeliveries || redeliveryCount < maximumRedeliveries) {
                    final long delay = (redeliveryCount == 0) ? redeliveryPolicy.getInitialRedeliveryDelay() : redeliveryPolicy.getNextRedeliveryDelay(this.getExistingDelay(messageReference));
                    this.scheduleRedelivery(context, messageReference, delay, ++redeliveryCount);
                }
                else {
                    if (this.isSendToDlqIfMaxRetriesExceeded()) {
                        return super.sendToDeadLetterQueue(context, messageReference, subscription, poisonCause);
                    }
                    RedeliveryPlugin.LOG.debug("Discarding message that exceeds max redelivery count({}), {}", (Object)maximumRedeliveries, messageReference.getMessageId());
                }
            }
            else {
                if (this.isFallbackToDeadLetter()) {
                    return super.sendToDeadLetterQueue(context, messageReference, subscription, poisonCause);
                }
                RedeliveryPlugin.LOG.debug("Ignoring dlq request for: {}, RedeliveryPolicy not found (and no fallback) for: {}", messageReference.getMessageId(), regionDestination.getActiveMQDestination());
            }
            return false;
        }
        catch (Exception exception) {
            final RuntimeException toThrow = new RuntimeException("Failed to schedule redelivery for: " + messageReference.getMessageId(), exception);
            RedeliveryPlugin.LOG.error(toThrow.toString(), exception);
            throw toThrow;
        }
    }
    
    private void scheduleRedelivery(final ConnectionContext context, final MessageReference messageReference, final long delay, final int redeliveryCount) throws Exception {
        if (RedeliveryPlugin.LOG.isTraceEnabled()) {
            final Destination regionDestination = (Destination)messageReference.getRegionDestination();
            RedeliveryPlugin.LOG.trace("redelivery #{} of: {} with delay: {}, dest: {}", redeliveryCount, messageReference.getMessageId(), delay, regionDestination.getActiveMQDestination());
        }
        final Message old = messageReference.getMessage();
        final Message message = old.copy();
        message.setTransactionId(null);
        message.setMemoryUsage(null);
        message.removeProperty("scheduledJobId");
        message.setProperty("redeliveryDelay", delay);
        message.setProperty("AMQ_SCHEDULED_DELAY", delay);
        message.setRedeliveryCounter(redeliveryCount);
        final boolean originalFlowControl = context.isProducerFlowControl();
        try {
            context.setProducerFlowControl(false);
            final ProducerInfo info = new ProducerInfo();
            final ProducerState state = new ProducerState(info);
            final ProducerBrokerExchange producerExchange = new ProducerBrokerExchange();
            producerExchange.setProducerState(state);
            producerExchange.setMutable(true);
            producerExchange.setConnectionContext(context);
            context.getBroker().send(producerExchange, message);
        }
        finally {
            context.setProducerFlowControl(originalFlowControl);
        }
    }
    
    private int getExistingDelay(final MessageReference messageReference) throws IOException {
        final Object val = messageReference.getMessage().getProperty("redeliveryDelay");
        if (val instanceof Long) {
            return ((Long)val).intValue();
        }
        return 0;
    }
    
    static {
        LOG = LoggerFactory.getLogger(RedeliveryPlugin.class);
    }
}
