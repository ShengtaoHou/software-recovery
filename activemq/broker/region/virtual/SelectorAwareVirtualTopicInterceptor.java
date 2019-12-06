// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.virtual;

import org.slf4j.LoggerFactory;
import org.apache.activemq.selector.SelectorParser;
import java.io.IOException;
import java.util.List;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.filter.NonCachedMessageEvaluationContext;
import java.util.Iterator;
import java.util.Set;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.plugin.SubQueueSelectorCacheBroker;
import org.apache.activemq.filter.BooleanExpression;
import org.apache.activemq.util.LRUCache;
import org.slf4j.Logger;

public class SelectorAwareVirtualTopicInterceptor extends VirtualTopicInterceptor
{
    private static final Logger LOG;
    LRUCache<String, BooleanExpression> expressionCache;
    private SubQueueSelectorCacheBroker selectorCachePlugin;
    
    public SelectorAwareVirtualTopicInterceptor(final Destination next, final String prefix, final String postfix, final boolean local) {
        super(next, prefix, postfix, local);
        this.expressionCache = new LRUCache<String, BooleanExpression>();
    }
    
    @Override
    protected void send(final ProducerBrokerExchange context, final Message message, final ActiveMQDestination destination) throws Exception {
        final Broker broker = context.getConnectionContext().getBroker();
        final Set<Destination> destinations = broker.getDestinations(destination);
        for (final Destination dest : destinations) {
            if (this.matchesSomeConsumer(broker, message, dest)) {
                dest.send(context, message.copy());
            }
        }
    }
    
    private boolean matchesSomeConsumer(final Broker broker, final Message message, final Destination dest) throws IOException {
        boolean matches = false;
        final MessageEvaluationContext msgContext = new NonCachedMessageEvaluationContext();
        msgContext.setDestination(dest.getActiveMQDestination());
        msgContext.setMessageReference(message);
        final List<Subscription> subs = dest.getConsumers();
        for (final Subscription sub : subs) {
            if (sub.matches(message, msgContext)) {
                matches = true;
                break;
            }
        }
        if (!matches) {
            matches = this.tryMatchingCachedSubs(broker, dest, msgContext);
        }
        return matches;
    }
    
    private boolean tryMatchingCachedSubs(final Broker broker, final Destination dest, final MessageEvaluationContext msgContext) {
        boolean matches = false;
        SelectorAwareVirtualTopicInterceptor.LOG.debug("No active consumer match found. Will try cache if configured...");
        final SubQueueSelectorCacheBroker cache = this.getSubQueueSelectorCacheBrokerPlugin(broker);
        if (cache != null) {
            final Set<String> selectors = cache.getSelector(dest.getActiveMQDestination().getQualifiedName());
            for (final String selector : selectors) {
                try {
                    final BooleanExpression expression = this.getExpression(selector);
                    matches = expression.matches(msgContext);
                    if (matches) {
                        return true;
                    }
                    continue;
                }
                catch (Exception e) {
                    SelectorAwareVirtualTopicInterceptor.LOG.error(e.getMessage(), e);
                }
            }
        }
        return matches;
    }
    
    private BooleanExpression getExpression(final String selector) throws Exception {
        BooleanExpression result;
        synchronized (this.expressionCache) {
            result = this.expressionCache.get(selector);
            if (result == null) {
                result = this.compileSelector(selector);
                this.expressionCache.put(selector, result);
            }
        }
        return result;
    }
    
    private SubQueueSelectorCacheBroker getSubQueueSelectorCacheBrokerPlugin(final Broker broker) {
        if (this.selectorCachePlugin == null) {
            this.selectorCachePlugin = (SubQueueSelectorCacheBroker)broker.getAdaptor(SubQueueSelectorCacheBroker.class);
        }
        return this.selectorCachePlugin;
    }
    
    private BooleanExpression compileSelector(final String selectorExpression) throws Exception {
        return SelectorParser.parse(selectorExpression);
    }
    
    static {
        LOG = LoggerFactory.getLogger(SelectorAwareVirtualTopicInterceptor.class);
    }
}
