// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.Message;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.activemq.state.ProducerState;
import org.apache.activemq.broker.region.Region;
import org.apache.activemq.broker.region.Destination;
import org.slf4j.Logger;

public class ProducerBrokerExchange
{
    private static final Logger LOG;
    private ConnectionContext connectionContext;
    private Destination regionDestination;
    private Region region;
    private ProducerState producerState;
    private boolean mutable;
    private AtomicLong lastSendSequenceNumber;
    private boolean auditProducerSequenceIds;
    private boolean isNetworkProducer;
    private BrokerService brokerService;
    private final FlowControlInfo flowControlInfo;
    
    public ProducerBrokerExchange() {
        this.mutable = true;
        this.lastSendSequenceNumber = new AtomicLong(-1L);
        this.flowControlInfo = new FlowControlInfo();
    }
    
    public ProducerBrokerExchange copy() {
        final ProducerBrokerExchange rc = new ProducerBrokerExchange();
        rc.connectionContext = this.connectionContext.copy();
        rc.regionDestination = this.regionDestination;
        rc.region = this.region;
        rc.producerState = this.producerState;
        rc.mutable = this.mutable;
        return rc;
    }
    
    public ConnectionContext getConnectionContext() {
        return this.connectionContext;
    }
    
    public void setConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }
    
    public boolean isMutable() {
        return this.mutable;
    }
    
    public void setMutable(final boolean mutable) {
        this.mutable = mutable;
    }
    
    public Destination getRegionDestination() {
        return this.regionDestination;
    }
    
    public void setRegionDestination(final Destination regionDestination) {
        this.regionDestination = regionDestination;
    }
    
    public Region getRegion() {
        return this.region;
    }
    
    public void setRegion(final Region region) {
        this.region = region;
    }
    
    public ProducerState getProducerState() {
        return this.producerState;
    }
    
    public void setProducerState(final ProducerState producerState) {
        this.producerState = producerState;
    }
    
    public boolean canDispatch(final Message messageSend) {
        boolean canDispatch = true;
        if (this.auditProducerSequenceIds && messageSend.isPersistent()) {
            final long producerSequenceId = messageSend.getMessageId().getProducerSequenceId();
            if (this.isNetworkProducer) {
                final long lastStoredForMessageProducer = this.getStoredSequenceIdForMessage(messageSend.getMessageId());
                if (producerSequenceId <= lastStoredForMessageProducer) {
                    canDispatch = false;
                    ProducerBrokerExchange.LOG.warn("suppressing duplicate message send [{}] from network producer with producerSequence [{}] less than last stored: {}", ProducerBrokerExchange.LOG.isTraceEnabled() ? messageSend : messageSend.getMessageId(), producerSequenceId, lastStoredForMessageProducer);
                }
            }
            else if (producerSequenceId <= this.lastSendSequenceNumber.get()) {
                canDispatch = false;
                if (messageSend.isInTransaction()) {
                    ProducerBrokerExchange.LOG.warn("suppressing duplicated message send [{}] with producerSequenceId [{}] <= last stored: {}", ProducerBrokerExchange.LOG.isTraceEnabled() ? messageSend : messageSend.getMessageId(), producerSequenceId, this.lastSendSequenceNumber);
                }
                else {
                    ProducerBrokerExchange.LOG.debug("suppressing duplicated message send [{}] with producerSequenceId [{}] <= last stored: {}", ProducerBrokerExchange.LOG.isTraceEnabled() ? messageSend : messageSend.getMessageId(), producerSequenceId, this.lastSendSequenceNumber);
                }
            }
            else {
                this.lastSendSequenceNumber.set(producerSequenceId);
            }
        }
        return canDispatch;
    }
    
    private long getStoredSequenceIdForMessage(final MessageId messageId) {
        try {
            return this.brokerService.getPersistenceAdapter().getLastProducerSequenceId(messageId.getProducerId());
        }
        catch (IOException ignored) {
            ProducerBrokerExchange.LOG.debug("Failed to determine last producer sequence id for: {}", messageId, ignored);
            return -1L;
        }
    }
    
    public void setLastStoredSequenceId(final long l) {
        this.auditProducerSequenceIds = true;
        if (this.connectionContext.isNetworkConnection()) {
            this.brokerService = this.connectionContext.getBroker().getBrokerService();
            this.isNetworkProducer = true;
        }
        this.lastSendSequenceNumber.set(l);
        ProducerBrokerExchange.LOG.debug("last stored sequence id set: {}", (Object)l);
    }
    
    public void incrementSend() {
        this.flowControlInfo.incrementSend();
    }
    
    public void blockingOnFlowControl(final boolean blockingOnFlowControl) {
        this.flowControlInfo.setBlockingOnFlowControl(blockingOnFlowControl);
    }
    
    public void incrementTimeBlocked(final Destination destination, final long timeBlocked) {
        this.flowControlInfo.incrementTimeBlocked(timeBlocked);
    }
    
    public boolean isBlockedForFlowControl() {
        return this.flowControlInfo.isBlockingOnFlowControl();
    }
    
    public void resetFlowControl() {
        this.flowControlInfo.reset();
    }
    
    public long getTotalTimeBlocked() {
        return this.flowControlInfo.getTotalTimeBlocked();
    }
    
    public int getPercentageBlocked() {
        final double value = (double)(this.flowControlInfo.getSendsBlocked() / this.flowControlInfo.getTotalSends());
        return (int)value * 100;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ProducerBrokerExchange.class);
    }
    
    public static class FlowControlInfo
    {
        private AtomicBoolean blockingOnFlowControl;
        private AtomicLong totalSends;
        private AtomicLong sendsBlocked;
        private AtomicLong totalTimeBlocked;
        
        public FlowControlInfo() {
            this.blockingOnFlowControl = new AtomicBoolean();
            this.totalSends = new AtomicLong();
            this.sendsBlocked = new AtomicLong();
            this.totalTimeBlocked = new AtomicLong();
        }
        
        public boolean isBlockingOnFlowControl() {
            return this.blockingOnFlowControl.get();
        }
        
        public void setBlockingOnFlowControl(final boolean blockingOnFlowControl) {
            this.blockingOnFlowControl.set(blockingOnFlowControl);
            if (blockingOnFlowControl) {
                this.incrementSendBlocked();
            }
        }
        
        public long getTotalSends() {
            return this.totalSends.get();
        }
        
        public void incrementSend() {
            this.totalSends.incrementAndGet();
        }
        
        public long getSendsBlocked() {
            return this.sendsBlocked.get();
        }
        
        public void incrementSendBlocked() {
            this.sendsBlocked.incrementAndGet();
        }
        
        public long getTotalTimeBlocked() {
            return this.totalTimeBlocked.get();
        }
        
        public void incrementTimeBlocked(final long time) {
            this.totalTimeBlocked.addAndGet(time);
        }
        
        public void reset() {
            this.blockingOnFlowControl.set(false);
            this.totalSends.set(0L);
            this.sendsBlocked.set(0L);
            this.totalTimeBlocked.set(0L);
        }
    }
}
