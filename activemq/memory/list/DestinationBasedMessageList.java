// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory.list;

import org.apache.activemq.command.Message;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.broker.region.MessageReference;
import java.util.HashMap;
import org.apache.activemq.memory.buffer.OrderBasedMessageBuffer;
import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.memory.buffer.MessageQueue;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Map;
import org.apache.activemq.memory.buffer.MessageBuffer;

public class DestinationBasedMessageList implements MessageList
{
    private MessageBuffer messageBuffer;
    private Map<ActiveMQDestination, MessageQueue> queueIndex;
    private DestinationMap subscriptionIndex;
    private Object lock;
    
    public DestinationBasedMessageList(final int maximumSize) {
        this(new OrderBasedMessageBuffer(maximumSize));
    }
    
    public DestinationBasedMessageList(final MessageBuffer buffer) {
        this.queueIndex = new HashMap<ActiveMQDestination, MessageQueue>();
        this.subscriptionIndex = new DestinationMap();
        this.lock = new Object();
        this.messageBuffer = buffer;
    }
    
    @Override
    public void add(final MessageReference node) {
        final ActiveMQMessage message = (ActiveMQMessage)node.getMessageHardRef();
        final ActiveMQDestination destination = message.getDestination();
        MessageQueue queue = null;
        synchronized (this.lock) {
            queue = this.queueIndex.get(destination);
            if (queue == null) {
                queue = this.messageBuffer.createMessageQueue();
                this.queueIndex.put(destination, queue);
                this.subscriptionIndex.put(destination, queue);
            }
        }
        queue.add(node);
    }
    
    public List<MessageReference> getMessages(final Subscription sub) {
        return this.getMessages(sub.getConsumerInfo().getDestination());
    }
    
    @Override
    public List<MessageReference> getMessages(final ActiveMQDestination destination) {
        Set set = null;
        synchronized (this.lock) {
            set = this.subscriptionIndex.get(destination);
        }
        final List<MessageReference> answer = new ArrayList<MessageReference>();
        for (final MessageQueue queue : set) {
            queue.appendMessages(answer);
        }
        return answer;
    }
    
    @Override
    public Message[] browse(final ActiveMQDestination destination) {
        final List<MessageReference> result = this.getMessages(destination);
        return result.toArray(new Message[result.size()]);
    }
    
    @Override
    public void clear() {
        this.messageBuffer.clear();
    }
}
