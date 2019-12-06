// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.management;

import org.slf4j.LoggerFactory;
import org.apache.activemq.util.IndentPrinter;
import org.slf4j.Logger;

public class JMSEndpointStatsImpl extends StatsImpl
{
    private static final Logger LOG;
    protected CountStatisticImpl messageCount;
    protected CountStatisticImpl pendingMessageCount;
    protected CountStatisticImpl expiredMessageCount;
    protected TimeStatisticImpl messageWaitTime;
    protected TimeStatisticImpl messageRateTime;
    
    public JMSEndpointStatsImpl(final JMSSessionStatsImpl sessionStats) {
        this();
        this.setParent(this.messageCount, sessionStats.getMessageCount());
        this.setParent(this.pendingMessageCount, sessionStats.getPendingMessageCount());
        this.setParent(this.expiredMessageCount, sessionStats.getExpiredMessageCount());
        this.setParent(this.messageWaitTime, sessionStats.getMessageWaitTime());
        this.setParent(this.messageRateTime, sessionStats.getMessageRateTime());
    }
    
    public JMSEndpointStatsImpl() {
        this(new CountStatisticImpl("messageCount", "Number of messages processed"), new CountStatisticImpl("pendingMessageCount", "Number of pending messages"), new CountStatisticImpl("expiredMessageCount", "Number of expired messages"), new TimeStatisticImpl("messageWaitTime", "Time spent by a message before being delivered"), new TimeStatisticImpl("messageRateTime", "Time taken to process a message (thoughtput rate)"));
    }
    
    public JMSEndpointStatsImpl(final CountStatisticImpl messageCount, final CountStatisticImpl pendingMessageCount, final CountStatisticImpl expiredMessageCount, final TimeStatisticImpl messageWaitTime, final TimeStatisticImpl messageRateTime) {
        this.messageCount = messageCount;
        this.pendingMessageCount = pendingMessageCount;
        this.expiredMessageCount = expiredMessageCount;
        this.messageWaitTime = messageWaitTime;
        this.messageRateTime = messageRateTime;
        this.addStatistic("messageCount", messageCount);
        this.addStatistic("pendingMessageCount", pendingMessageCount);
        this.addStatistic("expiredMessageCount", expiredMessageCount);
        this.addStatistic("messageWaitTime", messageWaitTime);
        this.addStatistic("messageRateTime", messageRateTime);
    }
    
    @Override
    public synchronized void reset() {
        super.reset();
        this.messageCount.reset();
        this.messageRateTime.reset();
        this.pendingMessageCount.reset();
        this.expiredMessageCount.reset();
        this.messageWaitTime.reset();
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
    
    public TimeStatisticImpl getMessageRateTime() {
        return this.messageRateTime;
    }
    
    public TimeStatisticImpl getMessageWaitTime() {
        return this.messageWaitTime;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.messageCount);
        buffer.append(" ");
        buffer.append(this.messageRateTime);
        buffer.append(" ");
        buffer.append(this.pendingMessageCount);
        buffer.append(" ");
        buffer.append(this.expiredMessageCount);
        buffer.append(" ");
        buffer.append(this.messageWaitTime);
        return buffer.toString();
    }
    
    public void onMessage() {
        if (this.enabled) {
            final long start = this.messageCount.getLastSampleTime();
            this.messageCount.increment();
            final long end = this.messageCount.getLastSampleTime();
            this.messageRateTime.addTime(end - start);
        }
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        this.messageCount.setEnabled(enabled);
        this.messageRateTime.setEnabled(enabled);
        this.pendingMessageCount.setEnabled(enabled);
        this.expiredMessageCount.setEnabled(enabled);
        this.messageWaitTime.setEnabled(enabled);
    }
    
    public void dump(final IndentPrinter out) {
        out.printIndent();
        out.println(this.messageCount);
        out.printIndent();
        out.println(this.messageRateTime);
        out.printIndent();
        out.println(this.pendingMessageCount);
        out.printIndent();
        out.println(this.messageRateTime);
        out.printIndent();
        out.println(this.expiredMessageCount);
        out.printIndent();
        out.println(this.messageWaitTime);
    }
    
    protected void setParent(final CountStatisticImpl child, final CountStatisticImpl parent) {
        if (child instanceof CountStatisticImpl && parent instanceof CountStatisticImpl) {
            final CountStatisticImpl c = child;
            c.setParent(parent);
        }
        else {
            JMSEndpointStatsImpl.LOG.warn("Cannot associate endpoint counters with session level counters as they are not both CountStatisticImpl clases. Endpoint: " + child + " session: " + parent);
        }
    }
    
    protected void setParent(final TimeStatisticImpl child, final TimeStatisticImpl parent) {
        if (child instanceof TimeStatisticImpl && parent instanceof TimeStatisticImpl) {
            final TimeStatisticImpl c = child;
            c.setParent(parent);
        }
        else {
            JMSEndpointStatsImpl.LOG.warn("Cannot associate endpoint counters with session level counters as they are not both TimeStatisticImpl clases. Endpoint: " + child + " session: " + parent);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(JMSEndpointStatsImpl.class);
    }
}
