// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import java.util.ArrayList;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQMessageTransformation;
import javax.jms.Destination;
import org.apache.activemq.command.ActiveMQTopic;

public final class AdvisorySupport
{
    public static final String ADVISORY_TOPIC_PREFIX = "ActiveMQ.Advisory.";
    public static final ActiveMQTopic CONNECTION_ADVISORY_TOPIC;
    public static final ActiveMQTopic QUEUE_ADVISORY_TOPIC;
    public static final ActiveMQTopic TOPIC_ADVISORY_TOPIC;
    public static final ActiveMQTopic TEMP_QUEUE_ADVISORY_TOPIC;
    public static final ActiveMQTopic TEMP_TOPIC_ADVISORY_TOPIC;
    public static final String PRODUCER_ADVISORY_TOPIC_PREFIX = "ActiveMQ.Advisory.Producer.";
    public static final String QUEUE_PRODUCER_ADVISORY_TOPIC_PREFIX = "ActiveMQ.Advisory.Producer.Queue.";
    public static final String TOPIC_PRODUCER_ADVISORY_TOPIC_PREFIX = "ActiveMQ.Advisory.Producer.Topic.";
    public static final String CONSUMER_ADVISORY_TOPIC_PREFIX = "ActiveMQ.Advisory.Consumer.";
    public static final String QUEUE_CONSUMER_ADVISORY_TOPIC_PREFIX = "ActiveMQ.Advisory.Consumer.Queue.";
    public static final String TOPIC_CONSUMER_ADVISORY_TOPIC_PREFIX = "ActiveMQ.Advisory.Consumer.Topic.";
    public static final String EXPIRED_TOPIC_MESSAGES_TOPIC_PREFIX = "ActiveMQ.Advisory.Expired.Topic.";
    public static final String EXPIRED_QUEUE_MESSAGES_TOPIC_PREFIX = "ActiveMQ.Advisory.Expired.Queue.";
    public static final String NO_TOPIC_CONSUMERS_TOPIC_PREFIX = "ActiveMQ.Advisory.NoConsumer.Topic.";
    public static final String NO_QUEUE_CONSUMERS_TOPIC_PREFIX = "ActiveMQ.Advisory.NoConsumer.Queue.";
    public static final String SLOW_CONSUMER_TOPIC_PREFIX = "ActiveMQ.Advisory.SlowConsumer.";
    public static final String FAST_PRODUCER_TOPIC_PREFIX = "ActiveMQ.Advisory.FastProducer.";
    public static final String MESSAGE_DISCAREDED_TOPIC_PREFIX = "ActiveMQ.Advisory.MessageDiscarded.";
    public static final String FULL_TOPIC_PREFIX = "ActiveMQ.Advisory.FULL.";
    public static final String MESSAGE_DELIVERED_TOPIC_PREFIX = "ActiveMQ.Advisory.MessageDelivered.";
    public static final String MESSAGE_CONSUMED_TOPIC_PREFIX = "ActiveMQ.Advisory.MessageConsumed.";
    public static final String MESSAGE_DLQ_TOPIC_PREFIX = "ActiveMQ.Advisory.MessageDLQd.";
    public static final String MASTER_BROKER_TOPIC_PREFIX = "ActiveMQ.Advisory.MasterBroker";
    public static final String NETWORK_BRIDGE_TOPIC_PREFIX = "ActiveMQ.Advisory.NetworkBridge";
    public static final String NETWORK_BRIDGE_FORWARD_FAILURE_TOPIC_PREFIX = "ActiveMQ.Advisory.NetworkBridge.ForwardFailure";
    public static final String AGENT_TOPIC = "ActiveMQ.Agent";
    public static final String ADIVSORY_MESSAGE_TYPE = "Advisory";
    public static final String MSG_PROPERTY_ORIGIN_BROKER_ID = "originBrokerId";
    public static final String MSG_PROPERTY_ORIGIN_BROKER_NAME = "originBrokerName";
    public static final String MSG_PROPERTY_ORIGIN_BROKER_URL = "originBrokerURL";
    public static final String MSG_PROPERTY_USAGE_NAME = "usageName";
    public static final String MSG_PROPERTY_CONSUMER_ID = "consumerId";
    public static final String MSG_PROPERTY_PRODUCER_ID = "producerId";
    public static final String MSG_PROPERTY_MESSAGE_ID = "orignalMessageId";
    public static final String MSG_PROPERTY_DESTINATION = "orignalDestination";
    public static final String MSG_PROPERTY_CONSUMER_COUNT = "consumerCount";
    public static final String MSG_PROPERTY_DISCARDED_COUNT = "discardedCount";
    public static final ActiveMQTopic ALL_DESTINATIONS_COMPOSITE_ADVISORY_TOPIC;
    public static final ActiveMQTopic TEMP_DESTINATION_COMPOSITE_ADVISORY_TOPIC;
    private static final ActiveMQTopic AGENT_TOPIC_DESTINATION;
    
    private AdvisorySupport() {
    }
    
    public static ActiveMQTopic getConnectionAdvisoryTopic() {
        return AdvisorySupport.CONNECTION_ADVISORY_TOPIC;
    }
    
    public static ActiveMQTopic[] getAllDestinationAdvisoryTopics(final Destination destination) throws JMSException {
        return getAllDestinationAdvisoryTopics(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic[] getAllDestinationAdvisoryTopics(final ActiveMQDestination destination) throws JMSException {
        final ArrayList<ActiveMQTopic> result = new ArrayList<ActiveMQTopic>();
        result.add(getConsumerAdvisoryTopic(destination));
        result.add(getProducerAdvisoryTopic(destination));
        result.add(getExpiredMessageTopic(destination));
        result.add(getNoConsumersAdvisoryTopic(destination));
        result.add(getSlowConsumerAdvisoryTopic(destination));
        result.add(getFastProducerAdvisoryTopic(destination));
        result.add(getMessageDiscardedAdvisoryTopic(destination));
        result.add(getMessageDeliveredAdvisoryTopic(destination));
        result.add(getMessageConsumedAdvisoryTopic(destination));
        result.add(getMessageDLQdAdvisoryTopic(destination));
        result.add(getFullAdvisoryTopic(destination));
        return result.toArray(new ActiveMQTopic[0]);
    }
    
    public static ActiveMQTopic getConsumerAdvisoryTopic(final Destination destination) throws JMSException {
        return getConsumerAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getConsumerAdvisoryTopic(final ActiveMQDestination destination) {
        String prefix;
        if (destination.isQueue()) {
            prefix = "ActiveMQ.Advisory.Consumer.Queue.";
        }
        else {
            prefix = "ActiveMQ.Advisory.Consumer.Topic.";
        }
        return getAdvisoryTopic(destination, prefix, true);
    }
    
    public static ActiveMQTopic getProducerAdvisoryTopic(final Destination destination) throws JMSException {
        return getProducerAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getProducerAdvisoryTopic(final ActiveMQDestination destination) {
        String prefix;
        if (destination.isQueue()) {
            prefix = "ActiveMQ.Advisory.Producer.Queue.";
        }
        else {
            prefix = "ActiveMQ.Advisory.Producer.Topic.";
        }
        return getAdvisoryTopic(destination, prefix, false);
    }
    
    private static ActiveMQTopic getAdvisoryTopic(final ActiveMQDestination destination, final String prefix, final boolean consumerTopics) {
        return new ActiveMQTopic(prefix + destination.getPhysicalName().replaceAll(",", "&sbquo;"));
    }
    
    public static ActiveMQTopic getExpiredMessageTopic(final Destination destination) throws JMSException {
        return getExpiredMessageTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getExpiredMessageTopic(final ActiveMQDestination destination) {
        if (destination.isQueue()) {
            return getExpiredQueueMessageAdvisoryTopic(destination);
        }
        return getExpiredTopicMessageAdvisoryTopic(destination);
    }
    
    public static ActiveMQTopic getExpiredTopicMessageAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.Expired.Topic." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getExpiredQueueMessageAdvisoryTopic(final Destination destination) throws JMSException {
        return getExpiredQueueMessageAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getExpiredQueueMessageAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.Expired.Queue." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getNoConsumersAdvisoryTopic(final Destination destination) throws JMSException {
        return getExpiredMessageTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getNoConsumersAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isQueue()) {
            return getNoQueueConsumersAdvisoryTopic(destination);
        }
        return getNoTopicConsumersAdvisoryTopic(destination);
    }
    
    public static ActiveMQTopic getNoTopicConsumersAdvisoryTopic(final Destination destination) throws JMSException {
        return getNoTopicConsumersAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getNoTopicConsumersAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.NoConsumer.Topic." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getNoQueueConsumersAdvisoryTopic(final Destination destination) throws JMSException {
        return getNoQueueConsumersAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getNoQueueConsumersAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.NoConsumer.Queue." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getSlowConsumerAdvisoryTopic(final Destination destination) throws JMSException {
        return getSlowConsumerAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getSlowConsumerAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.SlowConsumer." + destination.getDestinationTypeAsString() + "." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getFastProducerAdvisoryTopic(final Destination destination) throws JMSException {
        return getFastProducerAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getFastProducerAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.FastProducer." + destination.getDestinationTypeAsString() + "." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getMessageDiscardedAdvisoryTopic(final Destination destination) throws JMSException {
        return getMessageDiscardedAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getMessageDiscardedAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.MessageDiscarded." + destination.getDestinationTypeAsString() + "." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getMessageDeliveredAdvisoryTopic(final Destination destination) throws JMSException {
        return getMessageDeliveredAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getMessageDeliveredAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.MessageDelivered." + destination.getDestinationTypeAsString() + "." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getMessageConsumedAdvisoryTopic(final Destination destination) throws JMSException {
        return getMessageConsumedAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getMessageConsumedAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.MessageConsumed." + destination.getDestinationTypeAsString() + "." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getMessageDLQdAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.MessageDLQd." + destination.getDestinationTypeAsString() + "." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getMasterBrokerAdvisoryTopic() {
        return new ActiveMQTopic("ActiveMQ.Advisory.MasterBroker");
    }
    
    public static ActiveMQTopic getNetworkBridgeAdvisoryTopic() {
        return new ActiveMQTopic("ActiveMQ.Advisory.NetworkBridge");
    }
    
    public static ActiveMQTopic getFullAdvisoryTopic(final Destination destination) throws JMSException {
        return getFullAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getFullAdvisoryTopic(final ActiveMQDestination destination) {
        final String name = "ActiveMQ.Advisory.FULL." + destination.getDestinationTypeAsString() + "." + destination.getPhysicalName();
        return new ActiveMQTopic(name);
    }
    
    public static ActiveMQTopic getDestinationAdvisoryTopic(final Destination destination) throws JMSException {
        return getDestinationAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static ActiveMQTopic getDestinationAdvisoryTopic(final ActiveMQDestination destination) {
        switch (destination.getDestinationType()) {
            case 1: {
                return AdvisorySupport.QUEUE_ADVISORY_TOPIC;
            }
            case 2: {
                return AdvisorySupport.TOPIC_ADVISORY_TOPIC;
            }
            case 5: {
                return AdvisorySupport.TEMP_QUEUE_ADVISORY_TOPIC;
            }
            case 6: {
                return AdvisorySupport.TEMP_TOPIC_ADVISORY_TOPIC;
            }
            default: {
                throw new RuntimeException("Unknown destination type: " + destination.getDestinationType());
            }
        }
    }
    
    public static boolean isDestinationAdvisoryTopic(final Destination destination) throws JMSException {
        return isDestinationAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isTempDestinationAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (!isTempDestinationAdvisoryTopic(compositeDestinations[i])) {
                    return false;
                }
            }
            return true;
        }
        return destination.equals(AdvisorySupport.TEMP_QUEUE_ADVISORY_TOPIC) || destination.equals(AdvisorySupport.TEMP_TOPIC_ADVISORY_TOPIC);
    }
    
    public static boolean isDestinationAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isDestinationAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.equals(AdvisorySupport.TEMP_QUEUE_ADVISORY_TOPIC) || destination.equals(AdvisorySupport.TEMP_TOPIC_ADVISORY_TOPIC) || destination.equals(AdvisorySupport.QUEUE_ADVISORY_TOPIC) || destination.equals(AdvisorySupport.TOPIC_ADVISORY_TOPIC);
    }
    
    public static boolean isAdvisoryTopic(final Destination destination) throws JMSException {
        return isAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination == null) {
            return false;
        }
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.");
    }
    
    public static boolean isConnectionAdvisoryTopic(final Destination destination) throws JMSException {
        return isConnectionAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isConnectionAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isConnectionAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.equals(AdvisorySupport.CONNECTION_ADVISORY_TOPIC);
    }
    
    public static boolean isProducerAdvisoryTopic(final Destination destination) throws JMSException {
        return isProducerAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isProducerAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isProducerAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.Producer.");
    }
    
    public static boolean isConsumerAdvisoryTopic(final Destination destination) throws JMSException {
        return isConsumerAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isConsumerAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isConsumerAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.Consumer.");
    }
    
    public static boolean isSlowConsumerAdvisoryTopic(final Destination destination) throws JMSException {
        return isSlowConsumerAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isSlowConsumerAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isSlowConsumerAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.SlowConsumer.");
    }
    
    public static boolean isFastProducerAdvisoryTopic(final Destination destination) throws JMSException {
        return isFastProducerAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isFastProducerAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isFastProducerAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.FastProducer.");
    }
    
    public static boolean isMessageConsumedAdvisoryTopic(final Destination destination) throws JMSException {
        return isMessageConsumedAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isMessageConsumedAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isMessageConsumedAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.MessageConsumed.");
    }
    
    public static boolean isMasterBrokerAdvisoryTopic(final Destination destination) throws JMSException {
        return isMasterBrokerAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isMasterBrokerAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isMasterBrokerAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.MasterBroker");
    }
    
    public static boolean isMessageDeliveredAdvisoryTopic(final Destination destination) throws JMSException {
        return isMessageDeliveredAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isMessageDeliveredAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isMessageDeliveredAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.MessageDelivered.");
    }
    
    public static boolean isMessageDiscardedAdvisoryTopic(final Destination destination) throws JMSException {
        return isMessageDiscardedAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isMessageDiscardedAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isMessageDiscardedAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.MessageDiscarded.");
    }
    
    public static boolean isFullAdvisoryTopic(final Destination destination) throws JMSException {
        return isFullAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isFullAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isFullAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.FULL.");
    }
    
    public static boolean isNetworkBridgeAdvisoryTopic(final Destination destination) throws JMSException {
        return isNetworkBridgeAdvisoryTopic(ActiveMQMessageTransformation.transformDestination(destination));
    }
    
    public static boolean isNetworkBridgeAdvisoryTopic(final ActiveMQDestination destination) {
        if (destination.isComposite()) {
            final ActiveMQDestination[] compositeDestinations = destination.getCompositeDestinations();
            for (int i = 0; i < compositeDestinations.length; ++i) {
                if (isNetworkBridgeAdvisoryTopic(compositeDestinations[i])) {
                    return true;
                }
            }
            return false;
        }
        return destination.isTopic() && destination.getPhysicalName().startsWith("ActiveMQ.Advisory.NetworkBridge");
    }
    
    public static Destination getAgentDestination() {
        return AdvisorySupport.AGENT_TOPIC_DESTINATION;
    }
    
    public static ActiveMQTopic getNetworkBridgeForwardFailureAdvisoryTopic() {
        return new ActiveMQTopic("ActiveMQ.Advisory.NetworkBridge.ForwardFailure");
    }
    
    static {
        CONNECTION_ADVISORY_TOPIC = new ActiveMQTopic("ActiveMQ.Advisory.Connection");
        QUEUE_ADVISORY_TOPIC = new ActiveMQTopic("ActiveMQ.Advisory.Queue");
        TOPIC_ADVISORY_TOPIC = new ActiveMQTopic("ActiveMQ.Advisory.Topic");
        TEMP_QUEUE_ADVISORY_TOPIC = new ActiveMQTopic("ActiveMQ.Advisory.TempQueue");
        TEMP_TOPIC_ADVISORY_TOPIC = new ActiveMQTopic("ActiveMQ.Advisory.TempTopic");
        ALL_DESTINATIONS_COMPOSITE_ADVISORY_TOPIC = new ActiveMQTopic(AdvisorySupport.TOPIC_ADVISORY_TOPIC.getPhysicalName() + "," + AdvisorySupport.QUEUE_ADVISORY_TOPIC.getPhysicalName() + "," + AdvisorySupport.TEMP_QUEUE_ADVISORY_TOPIC.getPhysicalName() + "," + AdvisorySupport.TEMP_TOPIC_ADVISORY_TOPIC.getPhysicalName());
        TEMP_DESTINATION_COMPOSITE_ADVISORY_TOPIC = new ActiveMQTopic(AdvisorySupport.TEMP_QUEUE_ADVISORY_TOPIC.getPhysicalName() + "," + AdvisorySupport.TEMP_TOPIC_ADVISORY_TOPIC.getPhysicalName());
        AGENT_TOPIC_DESTINATION = new ActiveMQTopic("ActiveMQ.Agent");
    }
}
