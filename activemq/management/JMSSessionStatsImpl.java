// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

import org.apache.activemq.util.IndentPrinter;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQMessageProducer;
import java.util.List;

public class JMSSessionStatsImpl extends StatsImpl
{
    private List producers;
    private List consumers;
    private CountStatisticImpl messageCount;
    private CountStatisticImpl pendingMessageCount;
    private CountStatisticImpl expiredMessageCount;
    private TimeStatisticImpl messageWaitTime;
    private CountStatisticImpl durableSubscriptionCount;
    private TimeStatisticImpl messageRateTime;
    
    public JMSSessionStatsImpl(final List producers, final List consumers) {
        this.producers = producers;
        this.consumers = consumers;
        this.messageCount = new CountStatisticImpl("messageCount", "Number of messages exchanged");
        this.pendingMessageCount = new CountStatisticImpl("pendingMessageCount", "Number of pending messages");
        this.expiredMessageCount = new CountStatisticImpl("expiredMessageCount", "Number of expired messages");
        this.messageWaitTime = new TimeStatisticImpl("messageWaitTime", "Time spent by a message before being delivered");
        this.durableSubscriptionCount = new CountStatisticImpl("durableSubscriptionCount", "The number of durable subscriptions");
        this.messageWaitTime = new TimeStatisticImpl("messageWaitTime", "Time spent by a message before being delivered");
        this.messageRateTime = new TimeStatisticImpl("messageRateTime", "Time taken to process a message (thoughtput rate)");
        this.addStatistic("messageCount", this.messageCount);
        this.addStatistic("pendingMessageCount", this.pendingMessageCount);
        this.addStatistic("expiredMessageCount", this.expiredMessageCount);
        this.addStatistic("messageWaitTime", this.messageWaitTime);
        this.addStatistic("durableSubscriptionCount", this.durableSubscriptionCount);
        this.addStatistic("messageRateTime", this.messageRateTime);
    }
    
    public JMSProducerStatsImpl[] getProducers() {
        final Object[] producerArray = this.producers.toArray();
        final int size = producerArray.length;
        final JMSProducerStatsImpl[] answer = new JMSProducerStatsImpl[size];
        for (int i = 0; i < size; ++i) {
            final ActiveMQMessageProducer producer = (ActiveMQMessageProducer)producerArray[i];
            answer[i] = producer.getProducerStats();
        }
        return answer;
    }
    
    public JMSConsumerStatsImpl[] getConsumers() {
        final Object[] consumerArray = this.consumers.toArray();
        final int size = consumerArray.length;
        final JMSConsumerStatsImpl[] answer = new JMSConsumerStatsImpl[size];
        for (int i = 0; i < size; ++i) {
            final ActiveMQMessageConsumer consumer = (ActiveMQMessageConsumer)consumerArray[i];
            answer[i] = consumer.getConsumerStats();
        }
        return answer;
    }
    
    @Override
    public void reset() {
        super.reset();
        final JMSConsumerStatsImpl[] cstats = this.getConsumers();
        for (int size = cstats.length, i = 0; i < size; ++i) {
            cstats[i].reset();
        }
        final JMSProducerStatsImpl[] pstats = this.getProducers();
        for (int size = pstats.length, j = 0; j < size; ++j) {
            pstats[j].reset();
        }
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        final JMSConsumerStatsImpl[] cstats = this.getConsumers();
        for (int size = cstats.length, i = 0; i < size; ++i) {
            cstats[i].setEnabled(enabled);
        }
        final JMSProducerStatsImpl[] pstats = this.getProducers();
        for (int size = pstats.length, j = 0; j < size; ++j) {
            pstats[j].setEnabled(enabled);
        }
    }
    
    public CountStatisticImpl getMessageCount() {
        return this.messageCount;
    }
    
    public CountStatisticImpl getPendingMessageCount() {
        return this.pendingMessageCount;
    }
    
    public CountStatisticImpl getExpiredMessageCount() {
        return this.expiredMessageCount;
    }
    
    public TimeStatisticImpl getMessageWaitTime() {
        return this.messageWaitTime;
    }
    
    public CountStatisticImpl getDurableSubscriptionCount() {
        return this.durableSubscriptionCount;
    }
    
    public TimeStatisticImpl getMessageRateTime() {
        return this.messageRateTime;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(" ");
        buffer.append(this.messageCount);
        buffer.append(" ");
        buffer.append(this.messageRateTime);
        buffer.append(" ");
        buffer.append(this.pendingMessageCount);
        buffer.append(" ");
        buffer.append(this.expiredMessageCount);
        buffer.append(" ");
        buffer.append(this.messageWaitTime);
        buffer.append(" ");
        buffer.append(this.durableSubscriptionCount);
        buffer.append(" producers{ ");
        final JMSProducerStatsImpl[] producerArray = this.getProducers();
        for (int i = 0; i < producerArray.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(Integer.toString(i));
            buffer.append(" = ");
            buffer.append(producerArray[i]);
        }
        buffer.append(" } consumers{ ");
        final JMSConsumerStatsImpl[] consumerArray = this.getConsumers();
        for (int j = 0; j < consumerArray.length; ++j) {
            if (j > 0) {
                buffer.append(", ");
            }
            buffer.append(Integer.toString(j));
            buffer.append(" = ");
            buffer.append(consumerArray[j]);
        }
        buffer.append(" }");
        return buffer.toString();
    }
    
    public void dump(final IndentPrinter out) {
        out.printIndent();
        out.println(this.messageCount);
        out.printIndent();
        out.println(this.messageRateTime);
        out.printIndent();
        out.println(this.pendingMessageCount);
        out.printIndent();
        out.println(this.expiredMessageCount);
        out.printIndent();
        out.println(this.messageWaitTime);
        out.printIndent();
        out.println(this.durableSubscriptionCount);
        out.println();
        out.printIndent();
        out.println("producers {");
        out.incrementIndent();
        final JMSProducerStatsImpl[] producerArray = this.getProducers();
        for (int i = 0; i < producerArray.length; ++i) {
            final JMSProducerStatsImpl producer = producerArray[i];
            producer.dump(out);
        }
        out.decrementIndent();
        out.printIndent();
        out.println("}");
        out.printIndent();
        out.println("consumers {");
        out.incrementIndent();
        final JMSConsumerStatsImpl[] consumerArray = this.getConsumers();
        for (int j = 0; j < consumerArray.length; ++j) {
            final JMSConsumerStatsImpl consumer = consumerArray[j];
            consumer.dump(out);
        }
        out.decrementIndent();
        out.printIndent();
        out.println("}");
    }
    
    public void onCreateDurableSubscriber() {
        this.durableSubscriptionCount.increment();
    }
    
    public void onRemoveDurableSubscriber() {
        this.durableSubscriptionCount.decrement();
    }
}
