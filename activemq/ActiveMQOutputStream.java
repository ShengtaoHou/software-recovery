// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.ActiveMQBytesMessage;
import java.io.IOException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.command.ActiveMQMessage;
import javax.jms.JMSException;
import org.apache.activemq.command.Command;
import org.apache.activemq.util.IntrospectionSupport;
import javax.jms.InvalidDestinationException;
import java.util.HashMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.ProducerInfo;
import java.util.Map;
import org.slf4j.Logger;
import java.io.OutputStream;

@Deprecated
public class ActiveMQOutputStream extends OutputStream implements Disposable
{
    private static final Logger LOG;
    protected int count;
    final byte[] buffer;
    private final ActiveMQConnection connection;
    private final Map<String, Object> properties;
    private final ProducerInfo info;
    private long messageSequence;
    private boolean closed;
    private final int deliveryMode;
    private final int priority;
    private final long timeToLive;
    private boolean alwaysSyncSend;
    private boolean addPropertiesOnFirstMsgOnly;
    public static final String AMQ_STREAM_CHUNK_SIZE = "AMQ_STREAM_CHUNK_SIZE";
    
    public ActiveMQOutputStream(final ActiveMQConnection connection, final ProducerId producerId, final ActiveMQDestination destination, final Map<String, Object> properties, final int deliveryMode, final int priority, final long timeToLive) throws JMSException {
        this.alwaysSyncSend = false;
        this.addPropertiesOnFirstMsgOnly = false;
        this.connection = connection;
        this.deliveryMode = deliveryMode;
        this.priority = priority;
        this.timeToLive = timeToLive;
        this.properties = ((properties == null) ? null : new HashMap<String, Object>(properties));
        Integer chunkSize = (this.properties == null) ? null : this.properties.get("AMQ_STREAM_CHUNK_SIZE");
        if (chunkSize == null) {
            chunkSize = 65536;
        }
        else {
            if (chunkSize < 1) {
                throw new IllegalArgumentException("Chunk size must be greater then 0");
            }
            chunkSize *= 1024;
        }
        this.buffer = new byte[(int)chunkSize];
        if (destination == null) {
            throw new InvalidDestinationException("Don't understand null destinations");
        }
        this.info = new ProducerInfo(producerId);
        if (destination.getOptions() != null) {
            final Map<String, String> options = new HashMap<String, String>(destination.getOptions());
            IntrospectionSupport.setProperties(this, options, "producer.");
            IntrospectionSupport.setProperties(this.info, options, "producer.");
            if (options.size() > 0) {
                final String msg = "There are " + options.size() + " producer options that couldn't be set on the producer. Check the options are spelled correctly. Unknown parameters=[" + options + "]. This producer cannot be started.";
                ActiveMQOutputStream.LOG.warn(msg);
                throw new ConfigurationException(msg);
            }
        }
        this.info.setDestination(destination);
        this.connection.addOutputStream(this);
        this.connection.asyncSendPacket(this.info);
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.flushBuffer();
            try {
                this.send(new ActiveMQMessage(), true);
                this.dispose();
                this.connection.asyncSendPacket(this.info.createRemoveCommand());
            }
            catch (JMSException e) {
                IOExceptionSupport.create(e);
            }
        }
    }
    
    @Override
    public void dispose() {
        if (!this.closed) {
            this.connection.removeOutputStream(this);
            this.closed = true;
        }
    }
    
    @Override
    public synchronized void write(final int b) throws IOException {
        this.buffer[this.count++] = (byte)b;
        if (this.count == this.buffer.length) {
            this.flushBuffer();
        }
    }
    
    @Override
    public synchronized void write(final byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            final int max = Math.min(len, this.buffer.length - this.count);
            System.arraycopy(b, off, this.buffer, this.count, max);
            len -= max;
            this.count += max;
            off += max;
            if (this.count == this.buffer.length) {
                this.flushBuffer();
            }
        }
    }
    
    @Override
    public synchronized void flush() throws IOException {
        this.flushBuffer();
    }
    
    private void flushBuffer() throws IOException {
        if (this.count != 0) {
            try {
                final ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
                msg.writeBytes(this.buffer, 0, this.count);
                this.send(msg, false);
            }
            catch (JMSException e) {
                throw IOExceptionSupport.create(e);
            }
            this.count = 0;
        }
    }
    
    private void send(final ActiveMQMessage msg, final boolean eosMessage) throws JMSException {
        if (this.properties != null && (this.messageSequence == 0L || !this.addPropertiesOnFirstMsgOnly)) {
            for (final String key : this.properties.keySet()) {
                final Object value = this.properties.get(key);
                msg.setObjectProperty(key, value);
            }
        }
        msg.setType("org.apache.activemq.Stream");
        msg.setGroupID(this.info.getProducerId().toString());
        if (eosMessage) {
            msg.setGroupSequence(-1);
        }
        else {
            msg.setGroupSequence((int)this.messageSequence);
        }
        final MessageId id = new MessageId(this.info.getProducerId(), this.messageSequence++);
        this.connection.send(this.info.getDestination(), msg, id, this.deliveryMode, this.priority, this.timeToLive, !eosMessage && !this.isAlwaysSyncSend());
    }
    
    @Override
    public String toString() {
        return "ActiveMQOutputStream { producerId=" + this.info.getProducerId() + " }";
    }
    
    public boolean isAlwaysSyncSend() {
        return this.alwaysSyncSend;
    }
    
    public void setAlwaysSyncSend(final boolean alwaysSyncSend) {
        this.alwaysSyncSend = alwaysSyncSend;
    }
    
    public boolean isAddPropertiesOnFirstMsgOnly() {
        return this.addPropertiesOnFirstMsgOnly;
    }
    
    public void setAddPropertiesOnFirstMsgOnly(final boolean propertiesOnFirstMsgOnly) {
        this.addPropertiesOnFirstMsgOnly = propertiesOnFirstMsgOnly;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ActiveMQOutputStream.class);
    }
}
