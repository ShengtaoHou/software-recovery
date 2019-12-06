// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.slf4j.LoggerFactory;
import java.net.URISyntaxException;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.broker.region.policy.SlowConsumerStrategy;
import org.apache.activemq.broker.region.policy.AbortSlowConsumerStrategy;
import javax.management.MalformedObjectNameException;
import java.io.IOException;
import org.apache.activemq.broker.region.Subscription;
import javax.management.ObjectName;
import java.util.Iterator;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Connection;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.Collections;
import java.util.HashMap;
import javax.management.openmbean.CompositeType;
import java.util.Map;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import org.apache.activemq.command.ActiveMQMessage;
import javax.management.openmbean.TabularData;
import java.util.List;
import org.apache.activemq.filter.BooleanExpression;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.selector.SelectorParser;
import org.apache.activemq.filter.MessageEvaluationContext;
import java.util.ArrayList;
import javax.management.openmbean.OpenDataException;
import javax.jms.InvalidSelectorException;
import javax.management.openmbean.CompositeData;
import org.apache.activemq.broker.region.Destination;
import org.slf4j.Logger;

public class DestinationView implements DestinationViewMBean
{
    private static final Logger LOG;
    protected final Destination destination;
    protected final ManagedRegionBroker broker;
    
    public DestinationView(final ManagedRegionBroker broker, final Destination destination) {
        this.broker = broker;
        this.destination = destination;
    }
    
    public void gc() {
        this.destination.gc();
    }
    
    @Override
    public String getName() {
        return this.destination.getName();
    }
    
    @Override
    public void resetStatistics() {
        this.destination.getDestinationStatistics().reset();
    }
    
    @Override
    public long getEnqueueCount() {
        return this.destination.getDestinationStatistics().getEnqueues().getCount();
    }
    
    @Override
    public long getDequeueCount() {
        return this.destination.getDestinationStatistics().getDequeues().getCount();
    }
    
    @Override
    public long getDispatchCount() {
        return this.destination.getDestinationStatistics().getDispatched().getCount();
    }
    
    @Override
    public long getInFlightCount() {
        return this.destination.getDestinationStatistics().getInflight().getCount();
    }
    
    @Override
    public long getExpiredCount() {
        return this.destination.getDestinationStatistics().getExpired().getCount();
    }
    
    @Override
    public long getConsumerCount() {
        return this.destination.getDestinationStatistics().getConsumers().getCount();
    }
    
    @Override
    public long getQueueSize() {
        return this.destination.getDestinationStatistics().getMessages().getCount();
    }
    
    public long getMessagesCached() {
        return this.destination.getDestinationStatistics().getMessagesCached().getCount();
    }
    
    @Override
    public int getMemoryPercentUsage() {
        return this.destination.getMemoryUsage().getPercentUsage();
    }
    
    @Override
    public long getMemoryUsageByteCount() {
        return this.destination.getMemoryUsage().getUsage();
    }
    
    @Override
    public long getMemoryLimit() {
        return this.destination.getMemoryUsage().getLimit();
    }
    
    @Override
    public void setMemoryLimit(final long limit) {
        this.destination.getMemoryUsage().setLimit(limit);
    }
    
    @Override
    public double getAverageEnqueueTime() {
        return this.destination.getDestinationStatistics().getProcessTime().getAverageTime();
    }
    
    @Override
    public long getMaxEnqueueTime() {
        return this.destination.getDestinationStatistics().getProcessTime().getMaxTime();
    }
    
    @Override
    public long getMinEnqueueTime() {
        return this.destination.getDestinationStatistics().getProcessTime().getMinTime();
    }
    
    @Override
    public double getAverageMessageSize() {
        return this.destination.getDestinationStatistics().getMessageSize().getAverageSize();
    }
    
    @Override
    public long getMaxMessageSize() {
        return this.destination.getDestinationStatistics().getMessageSize().getMaxSize();
    }
    
    @Override
    public long getMinMessageSize() {
        return this.destination.getDestinationStatistics().getMessageSize().getMinSize();
    }
    
    @Override
    public boolean isPrioritizedMessages() {
        return this.destination.isPrioritizedMessages();
    }
    
    @Override
    public CompositeData[] browse() throws OpenDataException {
        try {
            return this.browse(null);
        }
        catch (InvalidSelectorException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public CompositeData[] browse(final String selector) throws OpenDataException, InvalidSelectorException {
        final Message[] messages = this.destination.browse();
        final ArrayList<CompositeData> c = new ArrayList<CompositeData>();
        final MessageEvaluationContext ctx = new MessageEvaluationContext();
        ctx.setDestination(this.destination.getActiveMQDestination());
        final BooleanExpression selectorExpression = (selector == null) ? null : SelectorParser.parse(selector);
        for (int i = 0; i < messages.length; ++i) {
            try {
                if (selectorExpression == null) {
                    c.add(OpenTypeSupport.convert(messages[i]));
                }
                else {
                    ctx.setMessageReference(messages[i]);
                    if (selectorExpression.matches(ctx)) {
                        c.add(OpenTypeSupport.convert(messages[i]));
                    }
                }
            }
            catch (Throwable e) {
                System.out.println(e);
                e.printStackTrace();
                DestinationView.LOG.warn("exception browsing destination", e);
            }
        }
        final CompositeData[] rc = new CompositeData[c.size()];
        c.toArray(rc);
        return rc;
    }
    
    @Override
    public List<Object> browseMessages() throws InvalidSelectorException {
        return this.browseMessages(null);
    }
    
    @Override
    public List<Object> browseMessages(final String selector) throws InvalidSelectorException {
        final Message[] messages = this.destination.browse();
        final ArrayList<Object> answer = new ArrayList<Object>();
        final MessageEvaluationContext ctx = new MessageEvaluationContext();
        ctx.setDestination(this.destination.getActiveMQDestination());
        final BooleanExpression selectorExpression = (selector == null) ? null : SelectorParser.parse(selector);
        for (int i = 0; i < messages.length; ++i) {
            try {
                final Message message = messages[i];
                message.setReadOnlyBody(true);
                if (selectorExpression == null) {
                    answer.add(message);
                }
                else {
                    ctx.setMessageReference(message);
                    if (selectorExpression.matches(ctx)) {
                        answer.add(message);
                    }
                }
            }
            catch (Throwable e) {
                DestinationView.LOG.warn("exception browsing destination", e);
            }
        }
        return answer;
    }
    
    @Override
    public TabularData browseAsTable() throws OpenDataException {
        try {
            return this.browseAsTable(null);
        }
        catch (InvalidSelectorException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public TabularData browseAsTable(final String selector) throws OpenDataException, InvalidSelectorException {
        final OpenTypeSupport.OpenTypeFactory factory = OpenTypeSupport.getFactory(ActiveMQMessage.class);
        final Message[] messages = this.destination.browse();
        final CompositeType ct = factory.getCompositeType();
        final TabularType tt = new TabularType("MessageList", "MessageList", ct, new String[] { "JMSMessageID" });
        final TabularDataSupport rc = new TabularDataSupport(tt);
        final MessageEvaluationContext ctx = new MessageEvaluationContext();
        ctx.setDestination(this.destination.getActiveMQDestination());
        final BooleanExpression selectorExpression = (selector == null) ? null : SelectorParser.parse(selector);
        for (int i = 0; i < messages.length; ++i) {
            try {
                if (selectorExpression == null) {
                    rc.put(new CompositeDataSupport(ct, factory.getFields(messages[i])));
                }
                else {
                    ctx.setMessageReference(messages[i]);
                    if (selectorExpression.matches(ctx)) {
                        rc.put(new CompositeDataSupport(ct, factory.getFields(messages[i])));
                    }
                }
            }
            catch (Throwable e) {
                DestinationView.LOG.warn("exception browsing destination", e);
            }
        }
        return rc;
    }
    
    @Override
    public String sendTextMessageWithProperties(final String properties) throws Exception {
        final String[] kvs = properties.split(",");
        final Map<String, String> props = new HashMap<String, String>();
        for (final String kv : kvs) {
            final String[] it = kv.split("=");
            if (it.length == 2) {
                props.put(it[0], it[1]);
            }
        }
        return this.sendTextMessage(props, props.remove("body"), props.remove("username"), props.remove("password"));
    }
    
    @Override
    public String sendTextMessage(final String body) throws Exception {
        return this.sendTextMessage(Collections.EMPTY_MAP, body);
    }
    
    @Override
    public String sendTextMessage(final Map headers, final String body) throws Exception {
        return this.sendTextMessage(headers, body, null, null);
    }
    
    @Override
    public String sendTextMessage(final String body, final String user, final String password) throws Exception {
        return this.sendTextMessage(Collections.EMPTY_MAP, body, user, password);
    }
    
    @Override
    public String sendTextMessage(final Map<String, String> headers, final String body, final String userName, final String password) throws Exception {
        final String brokerUrl = "vm://" + this.broker.getBrokerName();
        final ActiveMQDestination dest = this.destination.getActiveMQDestination();
        final ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = null;
        try {
            connection = cf.createConnection(userName, password);
            final Session session = connection.createSession(false, 1);
            final MessageProducer producer = session.createProducer(dest);
            final ActiveMQTextMessage msg = (ActiveMQTextMessage)session.createTextMessage(body);
            for (final Map.Entry entry : headers.entrySet()) {
                msg.setObjectProperty(entry.getKey(), entry.getValue());
            }
            producer.setDeliveryMode(msg.getJMSDeliveryMode());
            producer.setPriority(msg.getPriority());
            long ttl = 0L;
            if (msg.getExpiration() != 0L) {
                ttl = msg.getExpiration() - System.currentTimeMillis();
            }
            else {
                final String timeToLive = headers.get("timeToLive");
                if (timeToLive != null) {
                    ttl = Integer.valueOf(timeToLive);
                }
            }
            producer.setTimeToLive((ttl > 0L) ? ttl : 0L);
            producer.send(msg);
            return msg.getJMSMessageID();
        }
        finally {
            connection.close();
        }
    }
    
    @Override
    public int getMaxAuditDepth() {
        return this.destination.getMaxAuditDepth();
    }
    
    @Override
    public int getMaxProducersToAudit() {
        return this.destination.getMaxProducersToAudit();
    }
    
    public boolean isEnableAudit() {
        return this.destination.isEnableAudit();
    }
    
    public void setEnableAudit(final boolean enableAudit) {
        this.destination.setEnableAudit(enableAudit);
    }
    
    @Override
    public void setMaxAuditDepth(final int maxAuditDepth) {
        this.destination.setMaxAuditDepth(maxAuditDepth);
    }
    
    @Override
    public void setMaxProducersToAudit(final int maxProducersToAudit) {
        this.destination.setMaxProducersToAudit(maxProducersToAudit);
    }
    
    @Override
    public float getMemoryUsagePortion() {
        return this.destination.getMemoryUsage().getUsagePortion();
    }
    
    @Override
    public long getProducerCount() {
        return this.destination.getDestinationStatistics().getProducers().getCount();
    }
    
    @Override
    public boolean isProducerFlowControl() {
        return this.destination.isProducerFlowControl();
    }
    
    @Override
    public void setMemoryUsagePortion(final float value) {
        this.destination.getMemoryUsage().setUsagePortion(value);
    }
    
    @Override
    public void setProducerFlowControl(final boolean producerFlowControl) {
        this.destination.setProducerFlowControl(producerFlowControl);
    }
    
    @Override
    public boolean isAlwaysRetroactive() {
        return this.destination.isAlwaysRetroactive();
    }
    
    @Override
    public void setAlwaysRetroactive(final boolean alwaysRetroactive) {
        this.destination.setAlwaysRetroactive(alwaysRetroactive);
    }
    
    @Override
    public void setBlockedProducerWarningInterval(final long blockedProducerWarningInterval) {
        this.destination.setBlockedProducerWarningInterval(blockedProducerWarningInterval);
    }
    
    @Override
    public long getBlockedProducerWarningInterval() {
        return this.destination.getBlockedProducerWarningInterval();
    }
    
    @Override
    public int getMaxPageSize() {
        return this.destination.getMaxPageSize();
    }
    
    @Override
    public void setMaxPageSize(final int pageSize) {
        this.destination.setMaxPageSize(pageSize);
    }
    
    @Override
    public boolean isUseCache() {
        return this.destination.isUseCache();
    }
    
    @Override
    public void setUseCache(final boolean value) {
        this.destination.setUseCache(value);
    }
    
    @Override
    public ObjectName[] getSubscriptions() throws IOException, MalformedObjectNameException {
        final List<Subscription> subscriptions = this.destination.getConsumers();
        final ObjectName[] answer = new ObjectName[subscriptions.size()];
        final ObjectName brokerObjectName = this.broker.getBrokerService().getBrokerObjectName();
        int index = 0;
        for (final Subscription subscription : subscriptions) {
            final String connectionClientId = subscription.getContext().getClientId();
            answer[index++] = BrokerMBeanSupport.createSubscriptionName(brokerObjectName, connectionClientId, subscription.getConsumerInfo());
        }
        return answer;
    }
    
    @Override
    public ObjectName getSlowConsumerStrategy() throws IOException, MalformedObjectNameException {
        ObjectName result = null;
        final SlowConsumerStrategy strategy = this.destination.getSlowConsumerStrategy();
        if (strategy != null && strategy instanceof AbortSlowConsumerStrategy) {
            result = this.broker.registerSlowConsumerStrategy((AbortSlowConsumerStrategy)strategy);
        }
        return result;
    }
    
    @Override
    public String getOptions() {
        final Map<String, String> options = this.destination.getActiveMQDestination().getOptions();
        String optionsString = "";
        try {
            if (options != null) {
                optionsString = URISupport.createQueryString(options);
            }
        }
        catch (URISyntaxException ex) {}
        return optionsString;
    }
    
    @Override
    public boolean isDLQ() {
        return this.destination.isDLQ();
    }
    
    @Override
    public long getBlockedSends() {
        return this.destination.getDestinationStatistics().getBlockedSends().getCount();
    }
    
    @Override
    public double getAverageBlockedTime() {
        return this.destination.getDestinationStatistics().getBlockedTime().getAverageTime();
    }
    
    @Override
    public long getTotalBlockedTime() {
        return this.destination.getDestinationStatistics().getBlockedTime().getTotalTime();
    }
    
    static {
        LOG = LoggerFactory.getLogger(DestinationViewMBean.class);
    }
}
