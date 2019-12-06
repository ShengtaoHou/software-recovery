// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.util.HashMap;
import org.apache.activemq.state.CommandVisitor;
import javax.jms.MessageNotWriteableException;
import org.apache.activemq.filter.PropertyExpression;
import org.apache.activemq.util.TypeConversionSupport;
import org.apache.activemq.broker.scheduler.CronParser;
import org.apache.activemq.ActiveMQConnection;
import java.util.List;
import javax.jms.MessageFormatException;
import java.util.Iterator;
import org.fusesource.hawtbuf.UTF8Buffer;
import java.util.Collection;
import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;
import javax.jms.Destination;
import java.io.UnsupportedEncodingException;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.JMSExceptionSupport;
import javax.jms.JMSException;
import org.apache.activemq.util.Callback;
import java.util.Map;
import org.apache.activemq.ScheduledMessage;

public class ActiveMQMessage extends Message implements org.apache.activemq.Message, ScheduledMessage
{
    public static final byte DATA_STRUCTURE_TYPE = 23;
    public static final String DLQ_DELIVERY_FAILURE_CAUSE_PROPERTY = "dlqDeliveryFailureCause";
    public static final String BROKER_PATH_PROPERTY = "JMSActiveMQBrokerPath";
    private static final Map<String, PropertySetter> JMS_PROPERTY_SETERS;
    protected transient Callback acknowledgeCallback;
    
    @Override
    public byte getDataStructureType() {
        return 23;
    }
    
    @Override
    public Message copy() {
        final ActiveMQMessage copy = new ActiveMQMessage();
        this.copy(copy);
        return copy;
    }
    
    protected void copy(final ActiveMQMessage copy) {
        super.copy(copy);
        copy.acknowledgeCallback = this.acknowledgeCallback;
    }
    
    @Override
    public int hashCode() {
        final MessageId id = this.getMessageId();
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final ActiveMQMessage msg = (ActiveMQMessage)o;
        final MessageId oMsg = msg.getMessageId();
        final MessageId thisMsg = this.getMessageId();
        return thisMsg != null && oMsg != null && oMsg.equals(thisMsg);
    }
    
    @Override
    public void acknowledge() throws JMSException {
        if (this.acknowledgeCallback != null) {
            try {
                this.acknowledgeCallback.execute();
            }
            catch (JMSException e) {
                throw e;
            }
            catch (Throwable e2) {
                throw JMSExceptionSupport.create(e2);
            }
        }
    }
    
    @Override
    public void clearBody() throws JMSException {
        this.setContent(null);
        this.readOnlyBody = false;
    }
    
    @Override
    public String getJMSMessageID() {
        final MessageId messageId = this.getMessageId();
        if (messageId == null) {
            return null;
        }
        return messageId.toString();
    }
    
    @Override
    public void setJMSMessageID(final String value) throws JMSException {
        if (value != null) {
            try {
                final MessageId id = new MessageId(value);
                this.setMessageId(id);
            }
            catch (NumberFormatException e) {
                final MessageId id2 = new MessageId();
                id2.setTextView(value);
                this.setMessageId(id2);
            }
        }
        else {
            this.setMessageId(null);
        }
    }
    
    public void setJMSMessageID(final ProducerId producerId, final long producerSequenceId) throws JMSException {
        MessageId id = null;
        try {
            id = new MessageId(producerId, producerSequenceId);
            this.setMessageId(id);
        }
        catch (Throwable e) {
            throw JMSExceptionSupport.create("Invalid message id '" + id + "', reason: " + e.getMessage(), e);
        }
    }
    
    @Override
    public long getJMSTimestamp() {
        return this.getTimestamp();
    }
    
    @Override
    public void setJMSTimestamp(final long timestamp) {
        this.setTimestamp(timestamp);
    }
    
    @Override
    public String getJMSCorrelationID() {
        return this.getCorrelationId();
    }
    
    @Override
    public void setJMSCorrelationID(final String correlationId) {
        this.setCorrelationId(correlationId);
    }
    
    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return encodeString(this.getCorrelationId());
    }
    
    @Override
    public void setJMSCorrelationIDAsBytes(final byte[] correlationId) throws JMSException {
        this.setCorrelationId(decodeString(correlationId));
    }
    
    @Override
    public String getJMSXMimeType() {
        return "jms/message";
    }
    
    protected static String decodeString(final byte[] data) throws JMSException {
        try {
            if (data == null) {
                return null;
            }
            return new String(data, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new JMSException("Invalid UTF-8 encoding: " + e.getMessage());
        }
    }
    
    protected static byte[] encodeString(final String data) throws JMSException {
        try {
            if (data == null) {
                return null;
            }
            return data.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new JMSException("Invalid UTF-8 encoding: " + e.getMessage());
        }
    }
    
    @Override
    public Destination getJMSReplyTo() {
        return this.getReplyTo();
    }
    
    @Override
    public void setJMSReplyTo(final Destination destination) throws JMSException {
        this.setReplyTo(ActiveMQDestination.transform(destination));
    }
    
    @Override
    public Destination getJMSDestination() {
        return this.getDestination();
    }
    
    @Override
    public void setJMSDestination(final Destination destination) throws JMSException {
        this.setDestination(ActiveMQDestination.transform(destination));
    }
    
    @Override
    public int getJMSDeliveryMode() {
        return this.isPersistent() ? 2 : 1;
    }
    
    @Override
    public void setJMSDeliveryMode(final int mode) {
        this.setPersistent(mode == 2);
    }
    
    @Override
    public boolean getJMSRedelivered() {
        return this.isRedelivered();
    }
    
    @Override
    public void setJMSRedelivered(final boolean redelivered) {
        this.setRedelivered(redelivered);
    }
    
    @Override
    public String getJMSType() {
        return this.getType();
    }
    
    @Override
    public void setJMSType(final String type) {
        this.setType(type);
    }
    
    @Override
    public long getJMSExpiration() {
        return this.getExpiration();
    }
    
    @Override
    public void setJMSExpiration(final long expiration) {
        this.setExpiration(expiration);
    }
    
    @Override
    public int getJMSPriority() {
        return this.getPriority();
    }
    
    @Override
    public void setJMSPriority(final int priority) {
        this.setPriority((byte)priority);
    }
    
    @Override
    public void clearProperties() {
        super.clearProperties();
        this.readOnlyProperties = false;
    }
    
    @Override
    public boolean propertyExists(final String name) throws JMSException {
        try {
            return this.getProperties().containsKey(name) || this.getObjectProperty(name) != null;
        }
        catch (IOException e) {
            throw JMSExceptionSupport.create(e);
        }
    }
    
    @Override
    public Enumeration getPropertyNames() throws JMSException {
        try {
            final Vector<String> result = new Vector<String>(this.getProperties().keySet());
            if (this.getRedeliveryCounter() != 0) {
                result.add("JMSXDeliveryCount");
            }
            if (this.getGroupID() != null) {
                result.add("JMSXGroupID");
            }
            if (this.getGroupID() != null) {
                result.add("JMSXGroupSeq");
            }
            if (this.getUserID() != null) {
                result.add("JMSXUserID");
            }
            return result.elements();
        }
        catch (IOException e) {
            throw JMSExceptionSupport.create(e);
        }
    }
    
    public Enumeration getAllPropertyNames() throws JMSException {
        try {
            final Vector<String> result = new Vector<String>(this.getProperties().keySet());
            result.addAll(ActiveMQMessage.JMS_PROPERTY_SETERS.keySet());
            return result.elements();
        }
        catch (IOException e) {
            throw JMSExceptionSupport.create(e);
        }
    }
    
    @Override
    public void setObjectProperty(final String name, final Object value) throws JMSException {
        this.setObjectProperty(name, value, true);
    }
    
    public void setObjectProperty(final String name, Object value, final boolean checkReadOnly) throws JMSException {
        if (checkReadOnly) {
            this.checkReadOnlyProperties();
        }
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Property name cannot be empty or null");
        }
        if (value instanceof UTF8Buffer) {
            value = value.toString();
        }
        this.checkValidObject(value);
        value = this.convertScheduled(name, value);
        final PropertySetter setter = ActiveMQMessage.JMS_PROPERTY_SETERS.get(name);
        if (setter != null && value != null) {
            setter.set(this, value);
        }
        else {
            try {
                this.setProperty(name, value);
            }
            catch (IOException e) {
                throw JMSExceptionSupport.create(e);
            }
        }
    }
    
    public void setProperties(final Map<String, ?> properties) throws JMSException {
        for (final Map.Entry<String, ?> entry : properties.entrySet()) {
            this.setObjectProperty(entry.getKey(), entry.getValue());
        }
    }
    
    protected void checkValidObject(final Object value) throws MessageFormatException {
        boolean valid = value instanceof Boolean || value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long;
        valid = (valid || value instanceof Float || value instanceof Double || value instanceof Character || value instanceof String || value == null);
        if (!valid) {
            final ActiveMQConnection conn = this.getConnection();
            if (conn != null && !conn.isNestedMapAndListEnabled()) {
                throw new MessageFormatException("Only objectified primitive objects and String types are allowed but was: " + value + " type: " + value.getClass());
            }
            if (!(value instanceof Map) && !(value instanceof List)) {
                throw new MessageFormatException("Only objectified primitive objects, String, Map and List types are allowed but was: " + value + " type: " + value.getClass());
            }
        }
    }
    
    protected void checkValidScheduled(final String name, final Object value) throws MessageFormatException {
        if (("AMQ_SCHEDULED_DELAY".equals(name) || "AMQ_SCHEDULED_PERIOD".equals(name) || "AMQ_SCHEDULED_REPEAT".equals(name)) && !(value instanceof Long) && !(value instanceof Integer)) {
            throw new MessageFormatException(name + " should be long or int value");
        }
        if ("AMQ_SCHEDULED_CRON".equals(name)) {
            CronParser.validate(value.toString());
        }
    }
    
    protected Object convertScheduled(final String name, final Object value) throws MessageFormatException {
        Object result = value;
        if ("AMQ_SCHEDULED_DELAY".equals(name)) {
            result = TypeConversionSupport.convert(value, Long.class);
        }
        else if ("AMQ_SCHEDULED_PERIOD".equals(name)) {
            result = TypeConversionSupport.convert(value, Long.class);
        }
        else if ("AMQ_SCHEDULED_REPEAT".equals(name)) {
            result = TypeConversionSupport.convert(value, Integer.class);
        }
        return result;
    }
    
    @Override
    public Object getObjectProperty(final String name) throws JMSException {
        if (name == null) {
            throw new NullPointerException("Property name cannot be null");
        }
        final PropertyExpression expression = new PropertyExpression(name);
        return expression.evaluate(this);
    }
    
    @Override
    public boolean getBooleanProperty(final String name) throws JMSException {
        final Object value = this.getObjectProperty(name);
        if (value == null) {
            return false;
        }
        final Boolean rc = (Boolean)TypeConversionSupport.convert(value, Boolean.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a boolean");
        }
        return rc;
    }
    
    @Override
    public byte getByteProperty(final String name) throws JMSException {
        final Object value = this.getObjectProperty(name);
        if (value == null) {
            throw new NumberFormatException("property " + name + " was null");
        }
        final Byte rc = (Byte)TypeConversionSupport.convert(value, Byte.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a byte");
        }
        return rc;
    }
    
    @Override
    public short getShortProperty(final String name) throws JMSException {
        final Object value = this.getObjectProperty(name);
        if (value == null) {
            throw new NumberFormatException("property " + name + " was null");
        }
        final Short rc = (Short)TypeConversionSupport.convert(value, Short.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a short");
        }
        return rc;
    }
    
    @Override
    public int getIntProperty(final String name) throws JMSException {
        final Object value = this.getObjectProperty(name);
        if (value == null) {
            throw new NumberFormatException("property " + name + " was null");
        }
        final Integer rc = (Integer)TypeConversionSupport.convert(value, Integer.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as an integer");
        }
        return rc;
    }
    
    @Override
    public long getLongProperty(final String name) throws JMSException {
        final Object value = this.getObjectProperty(name);
        if (value == null) {
            throw new NumberFormatException("property " + name + " was null");
        }
        final Long rc = (Long)TypeConversionSupport.convert(value, Long.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a long");
        }
        return rc;
    }
    
    @Override
    public float getFloatProperty(final String name) throws JMSException {
        final Object value = this.getObjectProperty(name);
        if (value == null) {
            throw new NullPointerException("property " + name + " was null");
        }
        final Float rc = (Float)TypeConversionSupport.convert(value, Float.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a float");
        }
        return rc;
    }
    
    @Override
    public double getDoubleProperty(final String name) throws JMSException {
        final Object value = this.getObjectProperty(name);
        if (value == null) {
            throw new NullPointerException("property " + name + " was null");
        }
        final Double rc = (Double)TypeConversionSupport.convert(value, Double.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a double");
        }
        return rc;
    }
    
    @Override
    public String getStringProperty(final String name) throws JMSException {
        Object value = null;
        if (name.equals("JMSXUserID")) {
            value = this.getUserID();
            if (value == null) {
                value = this.getObjectProperty(name);
            }
        }
        else {
            value = this.getObjectProperty(name);
        }
        if (value == null) {
            return null;
        }
        final String rc = (String)TypeConversionSupport.convert(value, String.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a String");
        }
        return rc;
    }
    
    @Override
    public void setBooleanProperty(final String name, final boolean value) throws JMSException {
        this.setBooleanProperty(name, value, true);
    }
    
    public void setBooleanProperty(final String name, final boolean value, final boolean checkReadOnly) throws JMSException {
        this.setObjectProperty(name, value, checkReadOnly);
    }
    
    @Override
    public void setByteProperty(final String name, final byte value) throws JMSException {
        this.setObjectProperty(name, value);
    }
    
    @Override
    public void setShortProperty(final String name, final short value) throws JMSException {
        this.setObjectProperty(name, value);
    }
    
    @Override
    public void setIntProperty(final String name, final int value) throws JMSException {
        this.setObjectProperty(name, value);
    }
    
    @Override
    public void setLongProperty(final String name, final long value) throws JMSException {
        this.setObjectProperty(name, value);
    }
    
    @Override
    public void setFloatProperty(final String name, final float value) throws JMSException {
        this.setObjectProperty(name, new Float(value));
    }
    
    @Override
    public void setDoubleProperty(final String name, final double value) throws JMSException {
        this.setObjectProperty(name, new Double(value));
    }
    
    @Override
    public void setStringProperty(final String name, final String value) throws JMSException {
        this.setObjectProperty(name, value);
    }
    
    private void checkReadOnlyProperties() throws MessageNotWriteableException {
        if (this.readOnlyProperties) {
            throw new MessageNotWriteableException("Message properties are read-only");
        }
    }
    
    protected void checkReadOnlyBody() throws MessageNotWriteableException {
        if (this.readOnlyBody) {
            throw new MessageNotWriteableException("Message body is read-only");
        }
    }
    
    public Callback getAcknowledgeCallback() {
        return this.acknowledgeCallback;
    }
    
    public void setAcknowledgeCallback(final Callback acknowledgeCallback) {
        this.acknowledgeCallback = acknowledgeCallback;
    }
    
    public void onSend() throws JMSException {
        this.setReadOnlyBody(true);
        this.setReadOnlyProperties(true);
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processMessage(this);
    }
    
    @Override
    public void storeContent() {
    }
    
    @Override
    public void storeContentAndClear() {
        this.storeContent();
    }
    
    static {
        (JMS_PROPERTY_SETERS = new HashMap<String, PropertySetter>()).put("JMSXDeliveryCount", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                final Integer rc = (Integer)TypeConversionSupport.convert(value, Integer.class);
                if (rc == null) {
                    throw new MessageFormatException("Property JMSXDeliveryCount cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setRedeliveryCounter(rc - 1);
            }
        });
        ActiveMQMessage.JMS_PROPERTY_SETERS.put("JMSXGroupID", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                final String rc = (String)TypeConversionSupport.convert(value, String.class);
                if (rc == null) {
                    throw new MessageFormatException("Property JMSXGroupID cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setGroupID(rc);
            }
        });
        ActiveMQMessage.JMS_PROPERTY_SETERS.put("JMSXGroupSeq", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                final Integer rc = (Integer)TypeConversionSupport.convert(value, Integer.class);
                if (rc == null) {
                    throw new MessageFormatException("Property JMSXGroupSeq cannot be set from a " + value.getClass().getName() + ".");
                }
                message.setGroupSequence(rc);
            }
        });
        ActiveMQMessage.JMS_PROPERTY_SETERS.put("JMSCorrelationID", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                final String rc = (String)TypeConversionSupport.convert(value, String.class);
                if (rc == null) {
                    throw new MessageFormatException("Property JMSCorrelationID cannot be set from a " + value.getClass().getName() + ".");
                }
                ((ActiveMQMessage)message).setJMSCorrelationID(rc);
            }
        });
        ActiveMQMessage.JMS_PROPERTY_SETERS.put("JMSDeliveryMode", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                Integer rc = (Integer)TypeConversionSupport.convert(value, Integer.class);
                if (rc == null) {
                    final Boolean bool = (Boolean)TypeConversionSupport.convert(value, Boolean.class);
                    if (bool == null) {
                        throw new MessageFormatException("Property JMSDeliveryMode cannot be set from a " + value.getClass().getName() + ".");
                    }
                    rc = (bool ? 2 : 1);
                }
                ((ActiveMQMessage)message).setJMSDeliveryMode(rc);
            }
        });
        ActiveMQMessage.JMS_PROPERTY_SETERS.put("JMSExpiration", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                final Long rc = (Long)TypeConversionSupport.convert(value, Long.class);
                if (rc == null) {
                    throw new MessageFormatException("Property JMSExpiration cannot be set from a " + value.getClass().getName() + ".");
                }
                ((ActiveMQMessage)message).setJMSExpiration(rc);
            }
        });
        ActiveMQMessage.JMS_PROPERTY_SETERS.put("JMSPriority", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                final Integer rc = (Integer)TypeConversionSupport.convert(value, Integer.class);
                if (rc == null) {
                    throw new MessageFormatException("Property JMSPriority cannot be set from a " + value.getClass().getName() + ".");
                }
                ((ActiveMQMessage)message).setJMSPriority(rc);
            }
        });
        ActiveMQMessage.JMS_PROPERTY_SETERS.put("JMSRedelivered", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                final Boolean rc = (Boolean)TypeConversionSupport.convert(value, Boolean.class);
                if (rc == null) {
                    throw new MessageFormatException("Property JMSRedelivered cannot be set from a " + value.getClass().getName() + ".");
                }
                ((ActiveMQMessage)message).setJMSRedelivered(rc);
            }
        });
        ActiveMQMessage.JMS_PROPERTY_SETERS.put("JMSReplyTo", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                final ActiveMQDestination rc = (ActiveMQDestination)TypeConversionSupport.convert(value, ActiveMQDestination.class);
                if (rc == null) {
                    throw new MessageFormatException("Property JMSReplyTo cannot be set from a " + value.getClass().getName() + ".");
                }
                ((ActiveMQMessage)message).setReplyTo(rc);
            }
        });
        ActiveMQMessage.JMS_PROPERTY_SETERS.put("JMSTimestamp", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                final Long rc = (Long)TypeConversionSupport.convert(value, Long.class);
                if (rc == null) {
                    throw new MessageFormatException("Property JMSTimestamp cannot be set from a " + value.getClass().getName() + ".");
                }
                ((ActiveMQMessage)message).setJMSTimestamp(rc);
            }
        });
        ActiveMQMessage.JMS_PROPERTY_SETERS.put("JMSType", new PropertySetter() {
            @Override
            public void set(final Message message, final Object value) throws MessageFormatException {
                final String rc = (String)TypeConversionSupport.convert(value, String.class);
                if (rc == null) {
                    throw new MessageFormatException("Property JMSType cannot be set from a " + value.getClass().getName() + ".");
                }
                ((ActiveMQMessage)message).setJMSType(rc);
            }
        });
    }
    
    interface PropertySetter
    {
        void set(final Message p0, final Object p1) throws MessageFormatException;
    }
}
