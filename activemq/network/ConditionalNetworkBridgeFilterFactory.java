// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.Destination;
import java.util.Arrays;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.command.Message;
import org.slf4j.Logger;
import org.apache.activemq.command.NetworkBridgeFilter;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.command.ConsumerInfo;

public class ConditionalNetworkBridgeFilterFactory implements NetworkBridgeFilterFactory
{
    boolean replayWhenNoConsumers;
    int replayDelay;
    int rateLimit;
    int rateDuration;
    
    public ConditionalNetworkBridgeFilterFactory() {
        this.replayWhenNoConsumers = false;
        this.replayDelay = 0;
        this.rateLimit = 0;
        this.rateDuration = 1000;
    }
    
    @Override
    public NetworkBridgeFilter create(final ConsumerInfo info, final BrokerId[] remoteBrokerPath, final int messageTTL, final int consumerTTL) {
        final ConditionalNetworkBridgeFilter filter = new ConditionalNetworkBridgeFilter();
        filter.setNetworkBrokerId(remoteBrokerPath[0]);
        filter.setMessageTTL(messageTTL);
        filter.setConsumerTTL(consumerTTL);
        filter.setAllowReplayWhenNoConsumers(this.isReplayWhenNoConsumers());
        filter.setRateLimit(this.getRateLimit());
        filter.setRateDuration(this.getRateDuration());
        filter.setReplayDelay(this.getReplayDelay());
        return filter;
    }
    
    public void setReplayWhenNoConsumers(final boolean replayWhenNoConsumers) {
        this.replayWhenNoConsumers = replayWhenNoConsumers;
    }
    
    public boolean isReplayWhenNoConsumers() {
        return this.replayWhenNoConsumers;
    }
    
    public void setRateLimit(final int rateLimit) {
        this.rateLimit = rateLimit;
    }
    
    public int getRateLimit() {
        return this.rateLimit;
    }
    
    public int getRateDuration() {
        return this.rateDuration;
    }
    
    public void setRateDuration(final int rateDuration) {
        this.rateDuration = rateDuration;
    }
    
    public int getReplayDelay() {
        return this.replayDelay;
    }
    
    public void setReplayDelay(final int replayDelay) {
        this.replayDelay = replayDelay;
    }
    
    private static class ConditionalNetworkBridgeFilter extends NetworkBridgeFilter
    {
        static final Logger LOG;
        private int rateLimit;
        private int rateDuration;
        private boolean allowReplayWhenNoConsumers;
        private int replayDelay;
        private int matchCount;
        private long rateDurationEnd;
        
        private ConditionalNetworkBridgeFilter() {
            this.rateDuration = 1000;
            this.allowReplayWhenNoConsumers = true;
            this.replayDelay = 1000;
        }
        
        @Override
        protected boolean matchesForwardingFilter(final Message message, final MessageEvaluationContext mec) {
            boolean match = true;
            if (mec.getDestination().isQueue() && NetworkBridgeFilter.contains(message.getBrokerPath(), this.networkBrokerId)) {
                match = (this.allowReplayWhenNoConsumers && this.hasNoLocalConsumers(message, mec) && this.hasNotJustArrived(message));
                if (match) {
                    ConditionalNetworkBridgeFilter.LOG.trace("Replaying [{}] for [{}] back to origin in the absence of a local consumer", message.getMessageId(), message.getDestination());
                }
                else {
                    ConditionalNetworkBridgeFilter.LOG.trace("Suppressing replay of [{}] for [{}] back to origin {}", message.getMessageId(), message.getDestination(), Arrays.asList(message.getBrokerPath()));
                }
            }
            else {
                match = super.matchesForwardingFilter(message, mec);
            }
            if (match && this.rateLimitExceeded()) {
                ConditionalNetworkBridgeFilter.LOG.trace("Throttled network consumer rejecting [{}] for [{}] {}>{}/{}", message.getMessageId(), message.getDestination(), this.matchCount, this.rateLimit, this.rateDuration);
                match = false;
            }
            return match;
        }
        
        private boolean hasNotJustArrived(final Message message) {
            return this.replayDelay == 0 || message.getBrokerInTime() + this.replayDelay < System.currentTimeMillis();
        }
        
        private boolean hasNoLocalConsumers(final Message message, final MessageEvaluationContext mec) {
            final Destination regionDestination = (Destination)mec.getMessageReference().getRegionDestination();
            final List<Subscription> consumers = regionDestination.getConsumers();
            for (final Subscription sub : consumers) {
                if (!sub.getConsumerInfo().isNetworkSubscription() && !sub.getConsumerInfo().isBrowser()) {
                    ConditionalNetworkBridgeFilter.LOG.trace("Not replaying [{}] for [{}] to origin due to existing local consumer: {}", message.getMessageId(), message.getDestination(), sub.getConsumerInfo());
                    return false;
                }
            }
            return true;
        }
        
        private boolean rateLimitExceeded() {
            if (this.rateLimit == 0) {
                return false;
            }
            if (this.rateDurationEnd < System.currentTimeMillis()) {
                this.rateDurationEnd = System.currentTimeMillis() + this.rateDuration;
                this.matchCount = 0;
            }
            return ++this.matchCount > this.rateLimit;
        }
        
        public void setReplayDelay(final int replayDelay) {
            this.replayDelay = replayDelay;
        }
        
        public void setRateLimit(final int rateLimit) {
            this.rateLimit = rateLimit;
        }
        
        public void setRateDuration(final int rateDuration) {
            this.rateDuration = rateDuration;
        }
        
        public void setAllowReplayWhenNoConsumers(final boolean allowReplayWhenNoConsumers) {
            this.allowReplayWhenNoConsumers = allowReplayWhenNoConsumers;
        }
        
        static {
            LOG = LoggerFactory.getLogger(ConditionalNetworkBridgeFilter.class);
        }
    }
}
