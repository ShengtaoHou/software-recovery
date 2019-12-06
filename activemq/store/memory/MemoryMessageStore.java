// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.memory;

import java.util.Iterator;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.command.MessageAck;
import java.io.IOException;
import org.apache.activemq.broker.ConnectionContext;
import java.util.Collections;
import java.util.LinkedHashMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageId;
import java.util.Map;
import org.apache.activemq.store.AbstractMessageStore;

public class MemoryMessageStore extends AbstractMessageStore
{
    protected final Map<MessageId, Message> messageTable;
    protected MessageId lastBatchId;
    
    public MemoryMessageStore(final ActiveMQDestination destination) {
        this(destination, new LinkedHashMap<MessageId, Message>());
    }
    
    public MemoryMessageStore(final ActiveMQDestination destination, final Map<MessageId, Message> messageTable) {
        super(destination);
        this.messageTable = Collections.synchronizedMap(messageTable);
    }
    
    @Override
    public synchronized void addMessage(final ConnectionContext context, final Message message) throws IOException {
        synchronized (this.messageTable) {
            this.messageTable.put(message.getMessageId(), message);
        }
        message.incrementReferenceCount();
    }
    
    @Override
    public Message getMessage(final MessageId identity) throws IOException {
        return this.messageTable.get(identity);
    }
    
    @Override
    public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
        this.removeMessage(ack.getLastMessageId());
    }
    
    public void removeMessage(final MessageId msgId) throws IOException {
        synchronized (this.messageTable) {
            final Message removed = this.messageTable.remove(msgId);
            if (removed != null) {
                removed.decrementReferenceCount();
            }
            if ((this.lastBatchId != null && this.lastBatchId.equals(msgId)) || this.messageTable.isEmpty()) {
                this.lastBatchId = null;
            }
        }
    }
    
    @Override
    public void recover(final MessageRecoveryListener listener) throws Exception {
        synchronized (this.messageTable) {
            for (final Object msg : this.messageTable.values()) {
                if (msg.getClass() == MessageId.class) {
                    listener.recoverMessageReference((MessageId)msg);
                }
                else {
                    listener.recoverMessage((Message)msg);
                }
            }
        }
    }
    
    @Override
    public void removeAllMessages(final ConnectionContext context) throws IOException {
        synchronized (this.messageTable) {
            this.messageTable.clear();
        }
    }
    
    public void delete() {
        synchronized (this.messageTable) {
            this.messageTable.clear();
        }
    }
    
    @Override
    public int getMessageCount() {
        return this.messageTable.size();
    }
    
    @Override
    public void recoverNextMessages(final int maxReturned, final MessageRecoveryListener listener) throws Exception {
        synchronized (this.messageTable) {
            boolean pastLackBatch = this.lastBatchId == null;
            int count = 0;
            for (final Map.Entry entry : this.messageTable.entrySet()) {
                if (pastLackBatch) {
                    ++count;
                    final Object msg = entry.getValue();
                    this.lastBatchId = entry.getKey();
                    if (msg.getClass() == MessageId.class) {
                        listener.recoverMessageReference((MessageId)msg);
                    }
                    else {
                        listener.recoverMessage((Message)msg);
                    }
                }
                else {
                    pastLackBatch = entry.getKey().equals(this.lastBatchId);
                }
            }
        }
    }
    
    @Override
    public void resetBatching() {
        this.lastBatchId = null;
    }
    
    @Override
    public void setBatch(final MessageId messageId) {
        this.lastBatchId = messageId;
    }
    
    @Override
    public void updateMessage(final Message message) {
        synchronized (this.messageTable) {
            this.messageTable.put(message.getMessageId(), message);
        }
    }
}
