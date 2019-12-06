// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;
import org.apache.activemq.command.ActiveMQDestination;
import java.io.IOException;
import org.apache.activemq.command.ConsumerId;
import javax.jms.JMSException;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.filter.LogicExpression;
import org.apache.activemq.filter.NoLocalExpression;
import org.apache.activemq.selector.SelectorParser;
import javax.jms.InvalidSelectorException;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.ObjectName;
import org.apache.activemq.filter.BooleanExpression;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.activemq.filter.DestinationFilter;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;
import org.slf4j.Logger;

public abstract class AbstractSubscription implements Subscription
{
    private static final Logger LOG;
    protected Broker broker;
    protected ConnectionContext context;
    protected ConsumerInfo info;
    protected final DestinationFilter destinationFilter;
    protected final CopyOnWriteArrayList<Destination> destinations;
    private BooleanExpression selectorExpression;
    private ObjectName objectName;
    private int cursorMemoryHighWaterMark;
    private boolean slowConsumer;
    private long lastAckTime;
    private AtomicLong consumedCount;
    
    public AbstractSubscription(final Broker broker, final ConnectionContext context, final ConsumerInfo info) throws InvalidSelectorException {
        this.destinations = new CopyOnWriteArrayList<Destination>();
        this.cursorMemoryHighWaterMark = 70;
        this.consumedCount = new AtomicLong();
        this.broker = broker;
        this.context = context;
        this.info = info;
        this.destinationFilter = DestinationFilter.parseFilter(info.getDestination());
        this.selectorExpression = parseSelector(info);
        this.lastAckTime = System.currentTimeMillis();
    }
    
    private static BooleanExpression parseSelector(final ConsumerInfo info) throws InvalidSelectorException {
        BooleanExpression rc = null;
        if (info.getSelector() != null) {
            rc = SelectorParser.parse(info.getSelector());
        }
        if (info.isNoLocal()) {
            if (rc == null) {
                rc = new NoLocalExpression(info.getConsumerId().getConnectionId());
            }
            else {
                rc = LogicExpression.createAND(new NoLocalExpression(info.getConsumerId().getConnectionId()), rc);
            }
        }
        if (info.getAdditionalPredicate() != null) {
            if (rc == null) {
                rc = info.getAdditionalPredicate();
            }
            else {
                rc = LogicExpression.createAND(info.getAdditionalPredicate(), rc);
            }
        }
        return rc;
    }
    
    @Override
    public synchronized void acknowledge(final ConnectionContext context, final MessageAck ack) throws Exception {
        this.lastAckTime = System.currentTimeMillis();
        this.consumedCount.incrementAndGet();
    }
    
    @Override
    public boolean matches(final MessageReference node, final MessageEvaluationContext context) throws IOException {
        final ConsumerId targetConsumerId = node.getTargetConsumerId();
        if (targetConsumerId != null && !targetConsumerId.equals(this.info.getConsumerId())) {
            return false;
        }
        try {
            return (this.selectorExpression == null || this.selectorExpression.matches(context)) && this.context.isAllowedToConsume(node);
        }
        catch (JMSException e) {
            AbstractSubscription.LOG.info("Selector failed to evaluate: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean isWildcard() {
        return this.destinationFilter.isWildcard();
    }
    
    @Override
    public boolean matches(final ActiveMQDestination destination) {
        return this.destinationFilter.matches(destination);
    }
    
    @Override
    public void add(final ConnectionContext context, final Destination destination) throws Exception {
        this.destinations.add(destination);
    }
    
    @Override
    public List<MessageReference> remove(final ConnectionContext context, final Destination destination) throws Exception {
        this.destinations.remove(destination);
        return (List<MessageReference>)Collections.EMPTY_LIST;
    }
    
    @Override
    public ConsumerInfo getConsumerInfo() {
        return this.info;
    }
    
    @Override
    public void gc() {
    }
    
    @Override
    public ConnectionContext getContext() {
        return this.context;
    }
    
    public ConsumerInfo getInfo() {
        return this.info;
    }
    
    public BooleanExpression getSelectorExpression() {
        return this.selectorExpression;
    }
    
    @Override
    public String getSelector() {
        return this.info.getSelector();
    }
    
    @Override
    public void setSelector(final String selector) throws InvalidSelectorException {
        final ConsumerInfo copy = this.info.copy();
        copy.setSelector(selector);
        final BooleanExpression newSelector = parseSelector(copy);
        this.info.setSelector(selector);
        this.selectorExpression = newSelector;
    }
    
    @Override
    public ObjectName getObjectName() {
        return this.objectName;
    }
    
    @Override
    public void setObjectName(final ObjectName objectName) {
        this.objectName = objectName;
    }
    
    @Override
    public int getPrefetchSize() {
        return this.info.getPrefetchSize();
    }
    
    public void setPrefetchSize(final int newSize) {
        this.info.setPrefetchSize(newSize);
    }
    
    @Override
    public boolean isRecoveryRequired() {
        return true;
    }
    
    @Override
    public boolean isSlowConsumer() {
        return this.slowConsumer;
    }
    
    public void setSlowConsumer(final boolean val) {
        this.slowConsumer = val;
    }
    
    @Override
    public boolean addRecoveredMessage(final ConnectionContext context, final MessageReference message) throws Exception {
        boolean result = false;
        final MessageEvaluationContext msgContext = context.getMessageEvaluationContext();
        try {
            final Destination regionDestination = (Destination)message.getRegionDestination();
            msgContext.setDestination(regionDestination.getActiveMQDestination());
            msgContext.setMessageReference(message);
            result = this.matches(message, msgContext);
            if (result) {
                this.doAddRecoveredMessage(message);
            }
        }
        finally {
            msgContext.clear();
        }
        return result;
    }
    
    @Override
    public ActiveMQDestination getActiveMQDestination() {
        return (this.info != null) ? this.info.getDestination() : null;
    }
    
    @Override
    public boolean isBrowser() {
        return this.info != null && this.info.isBrowser();
    }
    
    @Override
    public int getInFlightUsage() {
        if (this.info.getPrefetchSize() > 0) {
            return this.getInFlightSize() * 100 / this.info.getPrefetchSize();
        }
        return Integer.MAX_VALUE;
    }
    
    public void addDestination(final Destination destination) {
    }
    
    public void removeDestination(final Destination destination) {
    }
    
    @Override
    public int getCursorMemoryHighWaterMark() {
        return this.cursorMemoryHighWaterMark;
    }
    
    @Override
    public void setCursorMemoryHighWaterMark(final int cursorMemoryHighWaterMark) {
        this.cursorMemoryHighWaterMark = cursorMemoryHighWaterMark;
    }
    
    @Override
    public int countBeforeFull() {
        return this.getDispatchedQueueSize() - this.info.getPrefetchSize();
    }
    
    @Override
    public void unmatched(final MessageReference node) throws IOException {
    }
    
    protected void doAddRecoveredMessage(final MessageReference message) throws Exception {
        this.add(message);
    }
    
    @Override
    public long getTimeOfLastMessageAck() {
        return this.lastAckTime;
    }
    
    public void setTimeOfLastMessageAck(final long value) {
        this.lastAckTime = value;
    }
    
    @Override
    public long getConsumedCount() {
        return this.consumedCount.get();
    }
    
    @Override
    public void incrementConsumedCount() {
        this.consumedCount.incrementAndGet();
    }
    
    @Override
    public void resetConsumedCount() {
        this.consumedCount.set(0L);
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbstractSubscription.class);
    }
}
