// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Message;
import java.io.IOException;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQDestination;

public abstract class AbstractMessageStore implements MessageStore
{
    public static final ListenableFuture<Object> FUTURE;
    protected final ActiveMQDestination destination;
    protected boolean prioritizedMessages;
    
    public AbstractMessageStore(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    @Override
    public void dispose(final ConnectionContext context) {
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
    }
    
    @Override
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    @Override
    public void setMemoryUsage(final MemoryUsage memoryUsage) {
    }
    
    @Override
    public void setBatch(final MessageId messageId) throws IOException, Exception {
    }
    
    @Override
    public boolean isEmpty() throws Exception {
        return this.getMessageCount() == 0;
    }
    
    @Override
    public void setPrioritizedMessages(final boolean prioritizedMessages) {
        this.prioritizedMessages = prioritizedMessages;
    }
    
    @Override
    public boolean isPrioritizedMessages() {
        return this.prioritizedMessages;
    }
    
    @Override
    public void addMessage(final ConnectionContext context, final Message message, final boolean canOptimizeHint) throws IOException {
        this.addMessage(context, message);
    }
    
    @Override
    public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message) throws IOException {
        this.addMessage(context, message);
        return AbstractMessageStore.FUTURE;
    }
    
    @Override
    public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message, final boolean canOptimizeHint) throws IOException {
        this.addMessage(context, message, canOptimizeHint);
        return AbstractMessageStore.FUTURE;
    }
    
    @Override
    public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message, final boolean canOptimizeHint) throws IOException {
        this.addMessage(context, message, canOptimizeHint);
        return AbstractMessageStore.FUTURE;
    }
    
    @Override
    public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message) throws IOException {
        this.addMessage(context, message);
        return new InlineListenableFuture();
    }
    
    @Override
    public void removeAsyncMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
        this.removeMessage(context, ack);
    }
    
    @Override
    public void updateMessage(final Message message) throws IOException {
        throw new IOException("update is not supported by: " + this);
    }
    
    static {
        FUTURE = new InlineListenableFuture();
    }
}
