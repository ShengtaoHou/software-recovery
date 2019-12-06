// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.filter.DestinationFilter;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.Broker;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.activemq.broker.region.SubscriptionRecovery;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.ConnectionContext;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.activemq.thread.Scheduler;

public class TimedSubscriptionRecoveryPolicy implements SubscriptionRecoveryPolicy
{
    private static final int GC_INTERVAL = 1000;
    private Scheduler scheduler;
    private final List<TimestampWrapper> buffer;
    private volatile long lastGCRun;
    private long recoverDuration;
    private final Runnable gcTask;
    
    public TimedSubscriptionRecoveryPolicy() {
        this.buffer = Collections.synchronizedList(new LinkedList<TimestampWrapper>());
        this.lastGCRun = System.currentTimeMillis();
        this.recoverDuration = 60000L;
        this.gcTask = new Runnable() {
            @Override
            public void run() {
                TimedSubscriptionRecoveryPolicy.this.gc();
            }
        };
    }
    
    @Override
    public SubscriptionRecoveryPolicy copy() {
        final TimedSubscriptionRecoveryPolicy rc = new TimedSubscriptionRecoveryPolicy();
        rc.setRecoverDuration(this.recoverDuration);
        return rc;
    }
    
    @Override
    public boolean add(final ConnectionContext context, final MessageReference message) throws Exception {
        this.buffer.add(new TimestampWrapper(message, this.lastGCRun));
        return true;
    }
    
    @Override
    public void recover(final ConnectionContext context, final Topic topic, final SubscriptionRecovery sub) throws Exception {
        final ArrayList<TimestampWrapper> copy = new ArrayList<TimestampWrapper>(this.buffer);
        if (!copy.isEmpty()) {
            for (final TimestampWrapper timestampWrapper : copy) {
                final MessageReference message = timestampWrapper.message;
                sub.addRecoveredMessage(context, message);
            }
        }
    }
    
    @Override
    public void setBroker(final Broker broker) {
        this.scheduler = broker.getScheduler();
    }
    
    @Override
    public void start() throws Exception {
        this.scheduler.executePeriodically(this.gcTask, 1000L);
    }
    
    @Override
    public void stop() throws Exception {
        this.scheduler.cancel(this.gcTask);
    }
    
    public void gc() {
        this.lastGCRun = System.currentTimeMillis();
        while (this.buffer.size() > 0) {
            final TimestampWrapper timestampWrapper = this.buffer.get(0);
            if (this.lastGCRun <= timestampWrapper.timestamp + this.recoverDuration) {
                break;
            }
            this.buffer.remove(0);
        }
    }
    
    public long getRecoverDuration() {
        return this.recoverDuration;
    }
    
    public void setRecoverDuration(final long recoverDuration) {
        this.recoverDuration = recoverDuration;
    }
    
    @Override
    public Message[] browse(final ActiveMQDestination destination) throws Exception {
        final List<Message> result = new ArrayList<Message>();
        final ArrayList<TimestampWrapper> copy = new ArrayList<TimestampWrapper>(this.buffer);
        final DestinationFilter filter = DestinationFilter.parseFilter(destination);
        for (final TimestampWrapper timestampWrapper : copy) {
            final MessageReference ref = timestampWrapper.message;
            final Message message = ref.getMessage();
            if (filter.matches(message.getDestination())) {
                result.add(message);
            }
        }
        return result.toArray(new Message[result.size()]);
    }
    
    static class TimestampWrapper
    {
        public MessageReference message;
        public long timestamp;
        
        public TimestampWrapper(final MessageReference message, final long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
