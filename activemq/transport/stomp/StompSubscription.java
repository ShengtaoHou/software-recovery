// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.util.Iterator;
import java.util.Map;
import org.apache.activemq.command.TransactionId;
import javax.jms.JMSException;
import java.io.IOException;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.Command;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.LinkedList;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.MessageId;
import java.util.LinkedHashMap;
import org.apache.activemq.command.ConsumerInfo;

public class StompSubscription
{
    public static final String AUTO_ACK = "auto";
    public static final String CLIENT_ACK = "client";
    public static final String INDIVIDUAL_ACK = "client-individual";
    protected final ProtocolConverter protocolConverter;
    protected final String subscriptionId;
    protected final ConsumerInfo consumerInfo;
    protected final LinkedHashMap<MessageId, MessageDispatch> dispatchedMessage;
    protected final LinkedList<MessageDispatch> unconsumedMessage;
    protected String ackMode;
    protected ActiveMQDestination destination;
    protected String transformation;
    
    public StompSubscription(final ProtocolConverter stompTransport, final String subscriptionId, final ConsumerInfo consumerInfo, final String transformation) {
        this.dispatchedMessage = new LinkedHashMap<MessageId, MessageDispatch>();
        this.unconsumedMessage = new LinkedList<MessageDispatch>();
        this.ackMode = "auto";
        this.protocolConverter = stompTransport;
        this.subscriptionId = subscriptionId;
        this.consumerInfo = consumerInfo;
        this.transformation = transformation;
    }
    
    void onMessageDispatch(final MessageDispatch md, final String ackId) throws IOException, JMSException {
        final ActiveMQMessage message = (ActiveMQMessage)md.getMessage();
        if (this.ackMode == "client") {
            synchronized (this) {
                this.dispatchedMessage.put(message.getMessageId(), md);
            }
        }
        else if (this.ackMode == "client-individual") {
            synchronized (this) {
                this.dispatchedMessage.put(message.getMessageId(), md);
            }
        }
        else if (this.ackMode == "auto") {
            final MessageAck ack = new MessageAck(md, (byte)2, 1);
            this.protocolConverter.getStompTransport().sendToActiveMQ(ack);
        }
        boolean ignoreTransformation = false;
        if (this.transformation != null && !(message instanceof ActiveMQBytesMessage)) {
            message.setReadOnlyProperties(false);
            message.setStringProperty("transformation", this.transformation);
        }
        else if (message.getStringProperty("transformation") != null) {
            ignoreTransformation = true;
        }
        final StompFrame command = this.protocolConverter.convertMessage(message, ignoreTransformation);
        command.setAction("MESSAGE");
        if (this.subscriptionId != null) {
            command.getHeaders().put("subscription", this.subscriptionId);
        }
        if (ackId != null) {
            command.getHeaders().put("ack", ackId);
        }
        this.protocolConverter.getStompTransport().sendToStomp(command);
    }
    
    synchronized void onStompAbort(final TransactionId transactionId) {
        this.unconsumedMessage.clear();
    }
    
    void onStompCommit(final TransactionId transactionId) {
        MessageAck ack = null;
        synchronized (this) {
            final Iterator<?> iter = this.dispatchedMessage.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry entry = (Map.Entry)iter.next();
                final MessageDispatch msg = entry.getValue();
                if (this.unconsumedMessage.contains(msg)) {
                    iter.remove();
                }
            }
            if (!this.unconsumedMessage.isEmpty()) {
                ack = new MessageAck(this.unconsumedMessage.getLast(), (byte)2, this.unconsumedMessage.size());
                this.unconsumedMessage.clear();
            }
        }
        if (ack != null) {
            this.protocolConverter.getStompTransport().sendToActiveMQ(ack);
        }
    }
    
    synchronized MessageAck onStompMessageAck(final String messageId, final TransactionId transactionId) {
        final MessageId msgId = new MessageId(messageId);
        if (!this.dispatchedMessage.containsKey(msgId)) {
            return null;
        }
        final MessageAck ack = new MessageAck();
        ack.setDestination(this.consumerInfo.getDestination());
        ack.setConsumerId(this.consumerInfo.getConsumerId());
        if (this.ackMode == "client") {
            if (transactionId == null) {
                ack.setAckType((byte)2);
            }
            else {
                ack.setAckType((byte)0);
            }
            int count = 0;
            final Iterator<?> iter = this.dispatchedMessage.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry entry = (Map.Entry)iter.next();
                final MessageId id = entry.getKey();
                final MessageDispatch msg = entry.getValue();
                if (transactionId != null) {
                    if (!this.unconsumedMessage.contains(msg)) {
                        this.unconsumedMessage.add(msg);
                        ++count;
                    }
                }
                else {
                    iter.remove();
                    ++count;
                }
                if (id.equals(msgId)) {
                    ack.setLastMessageId(id);
                    break;
                }
            }
            ack.setMessageCount(count);
            if (transactionId != null) {
                ack.setTransactionId(transactionId);
            }
        }
        else if (this.ackMode == "client-individual") {
            ack.setAckType((byte)4);
            ack.setMessageID(msgId);
            if (transactionId != null) {
                this.unconsumedMessage.add(this.dispatchedMessage.get(msgId));
                ack.setTransactionId(transactionId);
            }
            this.dispatchedMessage.remove(msgId);
        }
        return ack;
    }
    
    public MessageAck onStompMessageNack(final String messageId, final TransactionId transactionId) throws ProtocolException {
        final MessageId msgId = new MessageId(messageId);
        if (!this.dispatchedMessage.containsKey(msgId)) {
            return null;
        }
        final MessageAck ack = new MessageAck();
        ack.setDestination(this.consumerInfo.getDestination());
        ack.setConsumerId(this.consumerInfo.getConsumerId());
        ack.setAckType((byte)1);
        ack.setMessageID(msgId);
        if (transactionId != null) {
            this.unconsumedMessage.add(this.dispatchedMessage.get(msgId));
            ack.setTransactionId(transactionId);
        }
        this.dispatchedMessage.remove(msgId);
        return ack;
    }
    
    public String getAckMode() {
        return this.ackMode;
    }
    
    public void setAckMode(final String ackMode) {
        this.ackMode = ackMode;
    }
    
    public String getSubscriptionId() {
        return this.subscriptionId;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public ConsumerInfo getConsumerInfo() {
        return this.consumerInfo;
    }
}
