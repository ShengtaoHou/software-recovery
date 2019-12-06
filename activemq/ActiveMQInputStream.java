// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import org.apache.activemq.command.ActiveMQBytesMessage;
import java.util.Collections;
import javax.jms.IllegalStateException;
import org.apache.activemq.util.JMSExceptionSupport;
import org.apache.activemq.command.ActiveMQMessage;
import java.io.IOException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.command.MessageAck;
import javax.jms.JMSException;
import org.apache.activemq.command.Command;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.HashMap;
import org.apache.activemq.selector.SelectorParser;
import javax.jms.InvalidDestinationException;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ProducerId;
import java.util.Map;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.ConsumerInfo;
import java.io.InputStream;

@Deprecated
public class ActiveMQInputStream extends InputStream implements ActiveMQDispatcher
{
    private final ActiveMQConnection connection;
    private final ConsumerInfo info;
    private final MessageDispatchChannel unconsumedMessages;
    private int deliveredCounter;
    private MessageDispatch lastDelivered;
    private boolean eosReached;
    private byte[] buffer;
    private int pos;
    private Map<String, Object> jmsProperties;
    private ProducerId producerId;
    private long nextSequenceId;
    private final long timeout;
    private boolean firstReceived;
    
    public ActiveMQInputStream(final ActiveMQConnection connection, final ConsumerId consumerId, final ActiveMQDestination dest, String selector, final boolean noLocal, final String name, final int prefetch, final long timeout) throws JMSException {
        this.unconsumedMessages = new FifoMessageDispatchChannel();
        this.connection = connection;
        if (dest == null) {
            throw new InvalidDestinationException("Don't understand null destinations");
        }
        if (dest.isTemporary()) {
            final String physicalName = dest.getPhysicalName();
            if (physicalName == null) {
                throw new IllegalArgumentException("Physical name of Destination should be valid: " + dest);
            }
            final String connectionID = connection.getConnectionInfo().getConnectionId().getValue();
            if (physicalName.indexOf(connectionID) < 0) {
                throw new InvalidDestinationException("Cannot use a Temporary destination from another Connection");
            }
            if (connection.isDeleted(dest)) {
                throw new InvalidDestinationException("Cannot use a Temporary destination that has been deleted");
            }
        }
        if (timeout < -1L) {
            throw new IllegalArgumentException("Timeout must be >= -1");
        }
        this.timeout = timeout;
        (this.info = new ConsumerInfo(consumerId)).setSubscriptionName(name);
        if (selector != null && selector.trim().length() != 0) {
            selector = "JMSType='org.apache.activemq.Stream' AND ( " + selector + " ) ";
        }
        else {
            selector = "JMSType='org.apache.activemq.Stream'";
        }
        SelectorParser.parse(selector);
        this.info.setSelector(selector);
        this.info.setPrefetchSize(prefetch);
        this.info.setNoLocal(noLocal);
        this.info.setBrowser(false);
        this.info.setDispatchAsync(false);
        if (dest.getOptions() != null) {
            final Map<String, String> options = new HashMap<String, String>(dest.getOptions());
            IntrospectionSupport.setProperties(this.info, options, "consumer.");
        }
        this.info.setDestination(dest);
        this.connection.addInputStream(this);
        this.connection.addDispatcher(this.info.getConsumerId(), this);
        this.connection.syncSendPacket(this.info);
        this.unconsumedMessages.start();
    }
    
    @Override
    public void close() throws IOException {
        if (!this.unconsumedMessages.isClosed()) {
            try {
                if (this.lastDelivered != null) {
                    final MessageAck ack = new MessageAck(this.lastDelivered, (byte)2, this.deliveredCounter);
                    this.connection.asyncSendPacket(ack);
                }
                this.dispose();
                this.connection.syncSendPacket(this.info.createRemoveCommand());
            }
            catch (JMSException e) {
                throw IOExceptionSupport.create(e);
            }
        }
    }
    
    public void dispose() {
        if (!this.unconsumedMessages.isClosed()) {
            this.unconsumedMessages.close();
            this.connection.removeDispatcher(this.info.getConsumerId());
            this.connection.removeInputStream(this);
        }
    }
    
    public Map<String, Object> getJMSProperties() throws IOException {
        if (this.jmsProperties == null) {
            this.fillBuffer();
        }
        return this.jmsProperties;
    }
    
    public ActiveMQMessage receive() throws JMSException, ReadTimeoutException {
        this.checkClosed();
        MessageDispatch md;
        try {
            if (this.firstReceived || this.timeout == -1L) {
                md = this.unconsumedMessages.dequeue(-1L);
                this.firstReceived = true;
            }
            else {
                md = this.unconsumedMessages.dequeue(this.timeout);
                if (md == null) {
                    throw new ReadTimeoutException();
                }
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw JMSExceptionSupport.create(e);
        }
        if (md == null || this.unconsumedMessages.isClosed() || md.getMessage().isExpired()) {
            return null;
        }
        ++this.deliveredCounter;
        if (0.75 * this.info.getPrefetchSize() <= this.deliveredCounter) {
            final MessageAck ack = new MessageAck(md, (byte)2, this.deliveredCounter);
            this.connection.asyncSendPacket(ack);
            this.deliveredCounter = 0;
            this.lastDelivered = null;
        }
        else {
            this.lastDelivered = md;
        }
        return (ActiveMQMessage)md.getMessage();
    }
    
    protected void checkClosed() throws IllegalStateException {
        if (this.unconsumedMessages.isClosed()) {
            throw new IllegalStateException("The Consumer is closed");
        }
    }
    
    @Override
    public int read() throws IOException {
        this.fillBuffer();
        if (this.eosReached || this.buffer.length == 0) {
            return -1;
        }
        return this.buffer[this.pos++] & 0xFF;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        this.fillBuffer();
        if (this.eosReached || this.buffer.length == 0) {
            return -1;
        }
        final int max = Math.min(len, this.buffer.length - this.pos);
        System.arraycopy(this.buffer, this.pos, b, off, max);
        this.pos += max;
        return max;
    }
    
    private void fillBuffer() throws IOException {
        if (this.eosReached || (this.buffer != null && this.buffer.length > this.pos)) {
            return;
        }
        Label_0027: {
            break Label_0027;
            try {
                ActiveMQMessage m;
                while (true) {
                    m = this.receive();
                    if (m != null && m.getDataStructureType() == 24) {
                        final long producerSequenceId = m.getMessageId().getProducerSequenceId();
                        if (this.producerId == null) {
                            if (producerSequenceId != 0L) {
                                continue;
                            }
                            ++this.nextSequenceId;
                            this.producerId = m.getMessageId().getProducerId();
                            break;
                        }
                        else {
                            if (!m.getMessageId().getProducerId().equals(this.producerId)) {
                                throw new IOException("Received an unexpected message: invalid producer: " + m);
                            }
                            if (producerSequenceId != this.nextSequenceId++) {
                                throw new IOException("Received an unexpected message: expected ID: " + (this.nextSequenceId - 1L) + " but was: " + producerSequenceId + " for message: " + m);
                            }
                            break;
                        }
                    }
                    else {
                        this.eosReached = true;
                        if (this.jmsProperties == null) {
                            this.jmsProperties = Collections.emptyMap();
                        }
                        return;
                    }
                }
                final ActiveMQBytesMessage bm = (ActiveMQBytesMessage)m;
                bm.readBytes(this.buffer = new byte[(int)bm.getBodyLength()]);
                this.pos = 0;
                if (this.jmsProperties == null) {
                    this.jmsProperties = Collections.unmodifiableMap((Map<? extends String, ?>)new HashMap<String, Object>(bm.getProperties()));
                }
            }
            catch (JMSException e) {
                this.eosReached = true;
                if (this.jmsProperties == null) {
                    this.jmsProperties = Collections.emptyMap();
                }
                throw IOExceptionSupport.create(e);
            }
        }
    }
    
    @Override
    public void dispatch(final MessageDispatch md) {
        this.unconsumedMessages.enqueue(md);
    }
    
    @Override
    public String toString() {
        return "ActiveMQInputStream { value=" + this.info.getConsumerId() + ", producerId=" + this.producerId + " }";
    }
    
    public class ReadTimeoutException extends IOException
    {
        private static final long serialVersionUID = -3217758894326719909L;
    }
}
