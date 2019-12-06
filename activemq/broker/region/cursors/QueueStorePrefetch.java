// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import org.slf4j.LoggerFactory;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.command.Message;
import java.io.IOException;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.store.MessageStore;
import org.slf4j.Logger;

class QueueStorePrefetch extends AbstractStoreCursor
{
    private static final Logger LOG;
    private final MessageStore store;
    private final Broker broker;
    
    public QueueStorePrefetch(final Queue queue, final Broker broker) {
        super(queue);
        this.store = queue.getMessageStore();
        this.broker = broker;
    }
    
    @Override
    public boolean recoverMessageReference(final MessageId messageReference) throws Exception {
        final Message msg = this.store.getMessage(messageReference);
        if (msg != null) {
            return this.recoverMessage(msg);
        }
        final String err = "Failed to retrieve message for id: " + messageReference;
        QueueStorePrefetch.LOG.error(err);
        throw new IOException(err);
    }
    
    @Override
    protected synchronized int getStoreSize() {
        try {
            final int result = this.store.getMessageCount();
            return result;
        }
        catch (IOException e) {
            QueueStorePrefetch.LOG.error("Failed to get message count", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected synchronized boolean isStoreEmpty() {
        try {
            return this.store.isEmpty();
        }
        catch (Exception e) {
            QueueStorePrefetch.LOG.error("Failed to get message count", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void resetBatch() {
        this.store.resetBatching();
    }
    
    @Override
    protected void setBatch(final MessageId messageId) throws Exception {
        this.store.setBatch(messageId);
        this.batchResetNeeded = false;
    }
    
    @Override
    protected void doFillBatch() throws Exception {
        this.hadSpace = this.hasSpace();
        if (!this.broker.getBrokerService().isPersistent() || this.hadSpace) {
            this.store.recoverNextMessages(this.maxBatchSize, this);
            this.dealWithDuplicates();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(QueueStorePrefetch.class);
    }
}
