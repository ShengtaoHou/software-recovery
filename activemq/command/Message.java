// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.util.zip.DeflaterOutputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.util.MarshallingSupport;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import java.util.Collections;
import java.io.IOException;
import org.fusesource.hawtbuf.UTF8Buffer;
import java.util.HashMap;
import javax.jms.JMSException;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.ActiveMQConnection;
import java.util.Map;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.broker.region.MessageReference;

public abstract class Message extends BaseCommand implements MarshallAware, MessageReference
{
    public static final String ORIGINAL_EXPIRATION = "originalExpiration";
    public static final int DEFAULT_MINIMUM_MESSAGE_SIZE = 1024;
    protected MessageId messageId;
    protected ActiveMQDestination originalDestination;
    protected TransactionId originalTransactionId;
    protected ProducerId producerId;
    protected ActiveMQDestination destination;
    protected TransactionId transactionId;
    protected long expiration;
    protected long timestamp;
    protected long arrival;
    protected long brokerInTime;
    protected long brokerOutTime;
    protected String correlationId;
    protected ActiveMQDestination replyTo;
    protected boolean persistent;
    protected String type;
    protected byte priority;
    protected String groupID;
    protected int groupSequence;
    protected ConsumerId targetConsumerId;
    protected boolean compressed;
    protected String userID;
    protected ByteSequence content;
    protected ByteSequence marshalledProperties;
    protected DataStructure dataStructure;
    protected int redeliveryCounter;
    protected int size;
    protected Map<String, Object> properties;
    protected boolean readOnlyProperties;
    protected boolean readOnlyBody;
    protected transient boolean recievedByDFBridge;
    protected boolean droppable;
    protected boolean jmsXGroupFirstForConsumer;
    private transient short referenceCount;
    private transient ActiveMQConnection connection;
    transient MessageDestination regionDestination;
    transient MemoryUsage memoryUsage;
    private BrokerId[] brokerPath;
    private BrokerId[] cluster;
    
    public abstract Message copy();
    
    public abstract void clearBody() throws JMSException;
    
    public abstract void storeContent();
    
    public abstract void storeContentAndClear();
    
    public void clearMarshalledState() throws JMSException {
        this.properties = null;
    }
    
    protected void copy(final Message copy) {
        super.copy(copy);
        copy.producerId = this.producerId;
        copy.transactionId = this.transactionId;
        copy.destination = this.destination;
        copy.messageId = ((this.messageId != null) ? this.messageId.copy() : null);
        copy.originalDestination = this.originalDestination;
        copy.originalTransactionId = this.originalTransactionId;
        copy.expiration = this.expiration;
        copy.timestamp = this.timestamp;
        copy.correlationId = this.correlationId;
        copy.replyTo = this.replyTo;
        copy.persistent = this.persistent;
        copy.redeliveryCounter = this.redeliveryCounter;
        copy.type = this.type;
        copy.priority = this.priority;
        copy.size = this.size;
        copy.groupID = this.groupID;
        copy.userID = this.userID;
        copy.groupSequence = this.groupSequence;
        if (this.properties != null) {
            (copy.properties = new HashMap<String, Object>(this.properties)).remove("originalExpiration");
        }
        else {
            copy.properties = this.properties;
        }
        copy.content = this.content;
        copy.marshalledProperties = this.marshalledProperties;
        copy.dataStructure = this.dataStructure;
        copy.readOnlyProperties = this.readOnlyProperties;
        copy.readOnlyBody = this.readOnlyBody;
        copy.compressed = this.compressed;
        copy.recievedByDFBridge = this.recievedByDFBridge;
        copy.arrival = this.arrival;
        copy.connection = this.connection;
        copy.regionDestination = this.regionDestination;
        copy.brokerInTime = this.brokerInTime;
        copy.brokerOutTime = this.brokerOutTime;
        copy.memoryUsage = this.memoryUsage;
        copy.brokerPath = this.brokerPath;
        copy.jmsXGroupFirstForConsumer = this.jmsXGroupFirstForConsumer;
    }
    
    public Object getProperty(final String name) throws IOException {
        if (this.properties == null) {
            if (this.marshalledProperties == null) {
                return null;
            }
            this.properties = this.unmarsallProperties(this.marshalledProperties);
        }
        Object result = this.properties.get(name);
        if (result instanceof UTF8Buffer) {
            result = result.toString();
        }
        return result;
    }
    
    public Map<String, Object> getProperties() throws IOException {
        if (this.properties == null) {
            if (this.marshalledProperties == null) {
                return (Map<String, Object>)Collections.EMPTY_MAP;
            }
            this.properties = this.unmarsallProperties(this.marshalledProperties);
        }
        return Collections.unmodifiableMap((Map<? extends String, ?>)this.properties);
    }
    
    public void clearProperties() {
        this.marshalledProperties = null;
        this.properties = null;
    }
    
    public void setProperty(final String name, final Object value) throws IOException {
        this.lazyCreateProperties();
        this.properties.put(name, value);
    }
    
    public void removeProperty(final String name) throws IOException {
        this.lazyCreateProperties();
        this.properties.remove(name);
    }
    
    protected void lazyCreateProperties() throws IOException {
        if (this.properties == null) {
            if (this.marshalledProperties == null) {
                this.properties = new HashMap<String, Object>();
            }
            else {
                this.properties = this.unmarsallProperties(this.marshalledProperties);
                this.marshalledProperties = null;
            }
        }
        else {
            this.marshalledProperties = null;
        }
    }
    
    private Map<String, Object> unmarsallProperties(final ByteSequence marshalledProperties) throws IOException {
        return MarshallingSupport.unmarshalPrimitiveMap(new DataInputStream(new ByteArrayInputStream(marshalledProperties)));
    }
    
    @Override
    public void beforeMarshall(final WireFormat wireFormat) throws IOException {
        if (this.marshalledProperties == null && this.properties != null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream os = new DataOutputStream(baos);
            MarshallingSupport.marshalPrimitiveMap(this.properties, os);
            os.close();
            this.marshalledProperties = baos.toByteSequence();
        }
    }
    
    @Override
    public void afterMarshall(final WireFormat wireFormat) throws IOException {
    }
    
    @Override
    public void beforeUnmarshall(final WireFormat wireFormat) throws IOException {
    }
    
    @Override
    public void afterUnmarshall(final WireFormat wireFormat) throws IOException {
    }
    
    public ProducerId getProducerId() {
        return this.producerId;
    }
    
    public void setProducerId(final ProducerId producerId) {
        this.producerId = producerId;
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public TransactionId getTransactionId() {
        return this.transactionId;
    }
    
    public void setTransactionId(final TransactionId transactionId) {
        this.transactionId = transactionId;
    }
    
    public boolean isInTransaction() {
        return this.transactionId != null;
    }
    
    public ActiveMQDestination getOriginalDestination() {
        return this.originalDestination;
    }
    
    public void setOriginalDestination(final ActiveMQDestination destination) {
        this.originalDestination = destination;
    }
    
    @Override
    public MessageId getMessageId() {
        return this.messageId;
    }
    
    public void setMessageId(final MessageId messageId) {
        this.messageId = messageId;
    }
    
    public TransactionId getOriginalTransactionId() {
        return this.originalTransactionId;
    }
    
    public void setOriginalTransactionId(final TransactionId transactionId) {
        this.originalTransactionId = transactionId;
    }
    
    @Override
    public String getGroupID() {
        return this.groupID;
    }
    
    public void setGroupID(final String groupID) {
        this.groupID = groupID;
    }
    
    @Override
    public int getGroupSequence() {
        return this.groupSequence;
    }
    
    public void setGroupSequence(final int groupSequence) {
        this.groupSequence = groupSequence;
    }
    
    public String getCorrelationId() {
        return this.correlationId;
    }
    
    public void setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
    }
    
    @Override
    public boolean isPersistent() {
        return this.persistent;
    }
    
    public void setPersistent(final boolean deliveryMode) {
        this.persistent = deliveryMode;
    }
    
    @Override
    public long getExpiration() {
        return this.expiration;
    }
    
    public void setExpiration(final long expiration) {
        this.expiration = expiration;
    }
    
    public byte getPriority() {
        return this.priority;
    }
    
    public void setPriority(final byte priority) {
        if (priority < 0) {
            this.priority = 0;
        }
        else if (priority > 9) {
            this.priority = 9;
        }
        else {
            this.priority = priority;
        }
    }
    
    public ActiveMQDestination getReplyTo() {
        return this.replyTo;
    }
    
    public void setReplyTo(final ActiveMQDestination replyTo) {
        this.replyTo = replyTo;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public ByteSequence getContent() {
        return this.content;
    }
    
    public void setContent(final ByteSequence content) {
        this.content = content;
    }
    
    public ByteSequence getMarshalledProperties() {
        return this.marshalledProperties;
    }
    
    public void setMarshalledProperties(final ByteSequence marshalledProperties) {
        this.marshalledProperties = marshalledProperties;
    }
    
    public DataStructure getDataStructure() {
        return this.dataStructure;
    }
    
    public void setDataStructure(final DataStructure data) {
        this.dataStructure = data;
    }
    
    @Override
    public ConsumerId getTargetConsumerId() {
        return this.targetConsumerId;
    }
    
    public void setTargetConsumerId(final ConsumerId targetConsumerId) {
        this.targetConsumerId = targetConsumerId;
    }
    
    @Override
    public boolean isExpired() {
        final long expireTime = this.getExpiration();
        return expireTime > 0L && System.currentTimeMillis() > expireTime;
    }
    
    @Override
    public boolean isAdvisory() {
        return this.type != null && this.type.equals("Advisory");
    }
    
    public boolean isCompressed() {
        return this.compressed;
    }
    
    public void setCompressed(final boolean compressed) {
        this.compressed = compressed;
    }
    
    public boolean isRedelivered() {
        return this.redeliveryCounter > 0;
    }
    
    public void setRedelivered(final boolean redelivered) {
        if (redelivered) {
            if (!this.isRedelivered()) {
                this.setRedeliveryCounter(1);
            }
        }
        else if (this.isRedelivered()) {
            this.setRedeliveryCounter(0);
        }
    }
    
    @Override
    public void incrementRedeliveryCounter() {
        ++this.redeliveryCounter;
    }
    
    @Override
    public int getRedeliveryCounter() {
        return this.redeliveryCounter;
    }
    
    public void setRedeliveryCounter(final int deliveryCounter) {
        this.redeliveryCounter = deliveryCounter;
    }
    
    public BrokerId[] getBrokerPath() {
        return this.brokerPath;
    }
    
    public void setBrokerPath(final BrokerId[] brokerPath) {
        this.brokerPath = brokerPath;
    }
    
    public boolean isReadOnlyProperties() {
        return this.readOnlyProperties;
    }
    
    public void setReadOnlyProperties(final boolean readOnlyProperties) {
        this.readOnlyProperties = readOnlyProperties;
    }
    
    public boolean isReadOnlyBody() {
        return this.readOnlyBody;
    }
    
    public void setReadOnlyBody(final boolean readOnlyBody) {
        this.readOnlyBody = readOnlyBody;
    }
    
    public ActiveMQConnection getConnection() {
        return this.connection;
    }
    
    public void setConnection(final ActiveMQConnection connection) {
        this.connection = connection;
    }
    
    public long getArrival() {
        return this.arrival;
    }
    
    public void setArrival(final long arrival) {
        this.arrival = arrival;
    }
    
    public String getUserID() {
        return this.userID;
    }
    
    public void setUserID(final String jmsxUserID) {
        this.userID = jmsxUserID;
    }
    
    @Override
    public int getReferenceCount() {
        return this.referenceCount;
    }
    
    @Override
    public Message getMessageHardRef() {
        return this;
    }
    
    @Override
    public Message getMessage() {
        return this;
    }
    
    public void setRegionDestination(final MessageDestination destination) {
        this.regionDestination = destination;
        if (this.memoryUsage == null) {
            this.memoryUsage = destination.getMemoryUsage();
        }
    }
    
    @Override
    public MessageDestination getRegionDestination() {
        return this.regionDestination;
    }
    
    public MemoryUsage getMemoryUsage() {
        return this.memoryUsage;
    }
    
    public void setMemoryUsage(final MemoryUsage usage) {
        this.memoryUsage = usage;
    }
    
    @Override
    public boolean isMarshallAware() {
        return true;
    }
    
    @Override
    public int incrementReferenceCount() {
        final int rc;
        final int size;
        synchronized (this) {
            final short referenceCount = (short)(this.referenceCount + 1);
            this.referenceCount = referenceCount;
            rc = referenceCount;
            size = this.getSize();
        }
        if (rc == 1 && this.getMemoryUsage() != null) {
            this.getMemoryUsage().increaseUsage(size);
        }
        return rc;
    }
    
    @Override
    public int decrementReferenceCount() {
        final int rc;
        final int size;
        synchronized (this) {
            final short referenceCount = (short)(this.referenceCount - 1);
            this.referenceCount = referenceCount;
            rc = referenceCount;
            size = this.getSize();
        }
        if (rc == 0 && this.getMemoryUsage() != null) {
            this.getMemoryUsage().decreaseUsage(size);
        }
        return rc;
    }
    
    @Override
    public int getSize() {
        final int minimumMessageSize = this.getMinimumMessageSize();
        if (this.size < minimumMessageSize || this.size == 0) {
            this.size = minimumMessageSize;
            if (this.marshalledProperties != null) {
                this.size += this.marshalledProperties.getLength();
            }
            if (this.content != null) {
                this.size += this.content.getLength();
            }
        }
        return this.size;
    }
    
    protected int getMinimumMessageSize() {
        int result = 1024;
        final MessageDestination dest = this.regionDestination;
        if (dest != null) {
            result = dest.getMinimumMessageSize();
        }
        return result;
    }
    
    public boolean isRecievedByDFBridge() {
        return this.recievedByDFBridge;
    }
    
    public void setRecievedByDFBridge(final boolean recievedByDFBridge) {
        this.recievedByDFBridge = recievedByDFBridge;
    }
    
    public void onMessageRolledBack() {
        this.incrementRedeliveryCounter();
    }
    
    public boolean isDroppable() {
        return this.droppable;
    }
    
    public void setDroppable(final boolean droppable) {
        this.droppable = droppable;
    }
    
    public BrokerId[] getCluster() {
        return this.cluster;
    }
    
    public void setCluster(final BrokerId[] cluster) {
        this.cluster = cluster;
    }
    
    @Override
    public boolean isMessage() {
        return true;
    }
    
    public long getBrokerInTime() {
        return this.brokerInTime;
    }
    
    public void setBrokerInTime(final long brokerInTime) {
        this.brokerInTime = brokerInTime;
    }
    
    public long getBrokerOutTime() {
        return this.brokerOutTime;
    }
    
    public void setBrokerOutTime(final long brokerOutTime) {
        this.brokerOutTime = brokerOutTime;
    }
    
    @Override
    public boolean isDropped() {
        return false;
    }
    
    public boolean isJMSXGroupFirstForConsumer() {
        return this.jmsXGroupFirstForConsumer;
    }
    
    public void setJMSXGroupFirstForConsumer(final boolean val) {
        this.jmsXGroupFirstForConsumer = val;
    }
    
    public void compress() throws IOException {
        if (!this.isCompressed()) {
            this.storeContent();
            if (!this.isCompressed() && this.getContent() != null) {
                this.doCompress();
            }
        }
    }
    
    protected void doCompress() throws IOException {
        this.compressed = true;
        final ByteSequence bytes = this.getContent();
        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        final OutputStream os = new DeflaterOutputStream(bytesOut);
        os.write(bytes.data, bytes.offset, bytes.length);
        os.close();
        this.setContent(bytesOut.toByteSequence());
    }
    
    @Override
    public String toString() {
        return this.toString(null);
    }
    
    @Override
    public String toString(final Map<String, Object> overrideFields) {
        try {
            this.getProperties();
        }
        catch (IOException ex) {}
        return super.toString(overrideFields);
    }
    
    public interface MessageDestination
    {
        int getMinimumMessageSize();
        
        MemoryUsage getMemoryUsage();
    }
}
