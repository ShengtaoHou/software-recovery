// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import javax.jms.JMSException;
import javax.management.openmbean.ArrayType;
import java.util.Iterator;
import java.util.Set;
import org.fusesource.hawtbuf.UTF8Buffer;
import javax.management.openmbean.TabularDataSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularType;
import java.util.ArrayList;
import javax.management.openmbean.OpenType;
import java.util.List;
import org.apache.activemq.command.ActiveMQBlobMessage;
import org.apache.activemq.broker.region.policy.SlowConsumerEntry;
import org.apache.activemq.broker.scheduler.Job;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQStreamMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMessage;
import java.util.HashMap;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import java.util.Map;

public final class OpenTypeSupport
{
    private static final Map<Class, AbstractOpenTypeFactory> OPEN_TYPE_FACTORIES;
    
    private OpenTypeSupport() {
    }
    
    public static OpenTypeFactory getFactory(final Class<?> clazz) throws OpenDataException {
        return OpenTypeSupport.OPEN_TYPE_FACTORIES.get(clazz);
    }
    
    public static CompositeData convert(final Object message) throws OpenDataException {
        final OpenTypeFactory f = getFactory(message.getClass());
        if (f == null) {
            throw new OpenDataException("Cannot create a CompositeData for type: " + message.getClass().getName());
        }
        final CompositeType ct = f.getCompositeType();
        final Map<String, Object> fields = f.getFields(message);
        return new CompositeDataSupport(ct, fields);
    }
    
    static {
        (OPEN_TYPE_FACTORIES = new HashMap<Class, AbstractOpenTypeFactory>()).put(ActiveMQMessage.class, new MessageOpenTypeFactory());
        OpenTypeSupport.OPEN_TYPE_FACTORIES.put(ActiveMQBytesMessage.class, new ByteMessageOpenTypeFactory());
        OpenTypeSupport.OPEN_TYPE_FACTORIES.put(ActiveMQMapMessage.class, new MapMessageOpenTypeFactory());
        OpenTypeSupport.OPEN_TYPE_FACTORIES.put(ActiveMQObjectMessage.class, new ObjectMessageOpenTypeFactory());
        OpenTypeSupport.OPEN_TYPE_FACTORIES.put(ActiveMQStreamMessage.class, new StreamMessageOpenTypeFactory());
        OpenTypeSupport.OPEN_TYPE_FACTORIES.put(ActiveMQTextMessage.class, new TextMessageOpenTypeFactory());
        OpenTypeSupport.OPEN_TYPE_FACTORIES.put(Job.class, new JobOpenTypeFactory());
        OpenTypeSupport.OPEN_TYPE_FACTORIES.put(SlowConsumerEntry.class, new SlowConsumerEntryOpenTypeFactory());
        OpenTypeSupport.OPEN_TYPE_FACTORIES.put(ActiveMQBlobMessage.class, new ActiveMQBlobMessageOpenTypeFactory());
        OpenTypeSupport.OPEN_TYPE_FACTORIES.put(HealthStatus.class, new HealthStatusOpenTypeFactory());
    }
    
    public abstract static class AbstractOpenTypeFactory implements OpenTypeFactory
    {
        private CompositeType compositeType;
        private final List<String> itemNamesList;
        private final List<String> itemDescriptionsList;
        private final List<OpenType> itemTypesList;
        
        public AbstractOpenTypeFactory() {
            this.itemNamesList = new ArrayList<String>();
            this.itemDescriptionsList = new ArrayList<String>();
            this.itemTypesList = new ArrayList<OpenType>();
        }
        
        @Override
        public CompositeType getCompositeType() throws OpenDataException {
            if (this.compositeType == null) {
                this.init();
                this.compositeType = this.createCompositeType();
            }
            return this.compositeType;
        }
        
        protected void init() throws OpenDataException {
        }
        
        protected CompositeType createCompositeType() throws OpenDataException {
            final String[] itemNames = this.itemNamesList.toArray(new String[this.itemNamesList.size()]);
            final String[] itemDescriptions = this.itemDescriptionsList.toArray(new String[this.itemDescriptionsList.size()]);
            final OpenType[] itemTypes = this.itemTypesList.toArray(new OpenType[this.itemTypesList.size()]);
            return new CompositeType(this.getTypeName(), this.getDescription(), itemNames, itemDescriptions, itemTypes);
        }
        
        protected abstract String getTypeName();
        
        protected void addItem(final String name, final String description, final OpenType type) {
            this.itemNamesList.add(name);
            this.itemDescriptionsList.add(description);
            this.itemTypesList.add(type);
        }
        
        protected String getDescription() {
            return this.getTypeName();
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final Map<String, Object> rc = new HashMap<String, Object>();
            return rc;
        }
    }
    
    static class MessageOpenTypeFactory extends AbstractOpenTypeFactory
    {
        protected TabularType stringPropertyTabularType;
        protected TabularType booleanPropertyTabularType;
        protected TabularType bytePropertyTabularType;
        protected TabularType shortPropertyTabularType;
        protected TabularType intPropertyTabularType;
        protected TabularType longPropertyTabularType;
        protected TabularType floatPropertyTabularType;
        protected TabularType doublePropertyTabularType;
        
        @Override
        protected String getTypeName() {
            return ActiveMQMessage.class.getName();
        }
        
        @Override
        protected void init() throws OpenDataException {
            super.init();
            this.addItem("JMSCorrelationID", "JMSCorrelationID", SimpleType.STRING);
            this.addItem("JMSDestination", "JMSDestination", SimpleType.STRING);
            this.addItem("JMSMessageID", "JMSMessageID", SimpleType.STRING);
            this.addItem("JMSReplyTo", "JMSReplyTo", SimpleType.STRING);
            this.addItem("JMSType", "JMSType", SimpleType.STRING);
            this.addItem("JMSDeliveryMode", "JMSDeliveryMode", SimpleType.STRING);
            this.addItem("JMSExpiration", "JMSExpiration", SimpleType.LONG);
            this.addItem("JMSPriority", "JMSPriority", SimpleType.INTEGER);
            this.addItem("JMSRedelivered", "JMSRedelivered", SimpleType.BOOLEAN);
            this.addItem("JMSTimestamp", "JMSTimestamp", SimpleType.DATE);
            this.addItem("JMSXGroupID", "Message Group ID", SimpleType.STRING);
            this.addItem("JMSXGroupSeq", "Message Group Sequence Number", SimpleType.INTEGER);
            this.addItem("JMSXUserID", "The user that sent the message", SimpleType.STRING);
            this.addItem("BrokerPath", "Brokers traversed", SimpleType.STRING);
            this.addItem("OriginalDestination", "Original Destination Before Senting To DLQ", SimpleType.STRING);
            this.addItem("PropertiesText", "User Properties Text", SimpleType.STRING);
            this.stringPropertyTabularType = this.createTabularType(String.class, SimpleType.STRING);
            this.booleanPropertyTabularType = this.createTabularType(Boolean.class, SimpleType.BOOLEAN);
            this.bytePropertyTabularType = this.createTabularType(Byte.class, SimpleType.BYTE);
            this.shortPropertyTabularType = this.createTabularType(Short.class, SimpleType.SHORT);
            this.intPropertyTabularType = this.createTabularType(Integer.class, SimpleType.INTEGER);
            this.longPropertyTabularType = this.createTabularType(Long.class, SimpleType.LONG);
            this.floatPropertyTabularType = this.createTabularType(Float.class, SimpleType.FLOAT);
            this.doublePropertyTabularType = this.createTabularType(Double.class, SimpleType.DOUBLE);
            this.addItem("StringProperties", "User String Properties", this.stringPropertyTabularType);
            this.addItem("BooleanProperties", "User Boolean Properties", this.booleanPropertyTabularType);
            this.addItem("ByteProperties", "User Byte Properties", this.bytePropertyTabularType);
            this.addItem("ShortProperties", "User Short Properties", this.shortPropertyTabularType);
            this.addItem("IntProperties", "User Integer Properties", this.intPropertyTabularType);
            this.addItem("LongProperties", "User Long Properties", this.longPropertyTabularType);
            this.addItem("FloatProperties", "User Float Properties", this.floatPropertyTabularType);
            this.addItem("DoubleProperties", "User Double Properties", this.doublePropertyTabularType);
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final ActiveMQMessage m = (ActiveMQMessage)o;
            final Map<String, Object> rc = super.getFields(o);
            rc.put("JMSCorrelationID", m.getJMSCorrelationID());
            rc.put("JMSDestination", "" + m.getJMSDestination());
            rc.put("JMSMessageID", m.getJMSMessageID());
            rc.put("JMSReplyTo", this.toString(m.getJMSReplyTo()));
            rc.put("JMSType", m.getJMSType());
            rc.put("JMSDeliveryMode", (m.getJMSDeliveryMode() == 2) ? "PERSISTENT" : "NON-PERSISTENT");
            rc.put("JMSExpiration", m.getJMSExpiration());
            rc.put("JMSPriority", m.getJMSPriority());
            rc.put("JMSRedelivered", m.getJMSRedelivered());
            rc.put("JMSTimestamp", new Date(m.getJMSTimestamp()));
            rc.put("JMSXGroupID", m.getGroupID());
            rc.put("JMSXGroupSeq", m.getGroupSequence());
            rc.put("JMSXUserID", m.getUserID());
            rc.put("BrokerPath", Arrays.toString(m.getBrokerPath()));
            rc.put("OriginalDestination", this.toString(m.getOriginalDestination()));
            try {
                rc.put("PropertiesText", "" + m.getProperties());
            }
            catch (IOException e) {
                rc.put("PropertiesText", "");
            }
            try {
                rc.put("StringProperties", this.createTabularData(m, this.stringPropertyTabularType, String.class));
            }
            catch (IOException e) {
                rc.put("StringProperties", new TabularDataSupport(this.stringPropertyTabularType));
            }
            try {
                rc.put("BooleanProperties", this.createTabularData(m, this.booleanPropertyTabularType, Boolean.class));
            }
            catch (IOException e) {
                rc.put("BooleanProperties", new TabularDataSupport(this.booleanPropertyTabularType));
            }
            try {
                rc.put("ByteProperties", this.createTabularData(m, this.bytePropertyTabularType, Byte.class));
            }
            catch (IOException e) {
                rc.put("ByteProperties", new TabularDataSupport(this.bytePropertyTabularType));
            }
            try {
                rc.put("ShortProperties", this.createTabularData(m, this.shortPropertyTabularType, Short.class));
            }
            catch (IOException e) {
                rc.put("ShortProperties", new TabularDataSupport(this.shortPropertyTabularType));
            }
            try {
                rc.put("IntProperties", this.createTabularData(m, this.intPropertyTabularType, Integer.class));
            }
            catch (IOException e) {
                rc.put("IntProperties", new TabularDataSupport(this.intPropertyTabularType));
            }
            try {
                rc.put("LongProperties", this.createTabularData(m, this.longPropertyTabularType, Long.class));
            }
            catch (IOException e) {
                rc.put("LongProperties", new TabularDataSupport(this.longPropertyTabularType));
            }
            try {
                rc.put("FloatProperties", this.createTabularData(m, this.floatPropertyTabularType, Float.class));
            }
            catch (IOException e) {
                rc.put("FloatProperties", new TabularDataSupport(this.floatPropertyTabularType));
            }
            try {
                rc.put("DoubleProperties", this.createTabularData(m, this.doublePropertyTabularType, Double.class));
            }
            catch (IOException e) {
                rc.put("DoubleProperties", new TabularDataSupport(this.doublePropertyTabularType));
            }
            return rc;
        }
        
        protected String toString(final Object value) {
            if (value == null) {
                return null;
            }
            return value.toString();
        }
        
        protected <T> TabularType createTabularType(final Class<T> type, final OpenType openType) throws OpenDataException {
            final String typeName = "java.util.Map<java.lang.String, " + type.getName() + ">";
            final String[] keyValue = { "key", "value" };
            final OpenType[] openTypes = { SimpleType.STRING, openType };
            final CompositeType rowType = new CompositeType(typeName, typeName, keyValue, keyValue, openTypes);
            return new TabularType(typeName, typeName, rowType, new String[] { "key" });
        }
        
        protected TabularDataSupport createTabularData(final ActiveMQMessage m, final TabularType type, final Class valueType) throws IOException, OpenDataException {
            final TabularDataSupport answer = new TabularDataSupport(type);
            final Set<Map.Entry<String, Object>> entries = m.getProperties().entrySet();
            for (final Map.Entry<String, Object> entry : entries) {
                final Object value = entry.getValue();
                if (value instanceof UTF8Buffer && valueType.equals(String.class)) {
                    final String actual = value.toString();
                    final CompositeDataSupport compositeData = this.createTabularRowValue(type, entry.getKey(), actual);
                    answer.put(compositeData);
                }
                if (valueType.isInstance(value)) {
                    final CompositeDataSupport compositeData2 = this.createTabularRowValue(type, entry.getKey(), value);
                    answer.put(compositeData2);
                }
            }
            return answer;
        }
        
        protected CompositeDataSupport createTabularRowValue(final TabularType type, final String key, final Object value) throws OpenDataException {
            final Map<String, Object> fields = new HashMap<String, Object>();
            fields.put("key", key);
            fields.put("value", value);
            return new CompositeDataSupport(type.getRowType(), fields);
        }
    }
    
    static class ByteMessageOpenTypeFactory extends MessageOpenTypeFactory
    {
        @Override
        protected String getTypeName() {
            return ActiveMQBytesMessage.class.getName();
        }
        
        @Override
        protected void init() throws OpenDataException {
            super.init();
            this.addItem("BodyLength", "Body length", SimpleType.LONG);
            this.addItem("BodyPreview", "Body preview", new ArrayType(1, SimpleType.BYTE));
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final ActiveMQBytesMessage m = (ActiveMQBytesMessage)o;
            m.setReadOnlyBody(true);
            final Map<String, Object> rc = super.getFields(o);
            long length = 0L;
            try {
                length = m.getBodyLength();
                rc.put("BodyLength", length);
            }
            catch (JMSException e) {
                rc.put("BodyLength", 0L);
            }
            try {
                final byte[] preview = new byte[(int)Math.min(length, 255L)];
                m.readBytes(preview);
                m.reset();
                final Byte[] data = new Byte[preview.length];
                for (int i = 0; i < data.length; ++i) {
                    data[i] = new Byte(preview[i]);
                }
                rc.put("BodyPreview", data);
            }
            catch (JMSException e) {
                rc.put("BodyPreview", new Byte[0]);
            }
            return rc;
        }
    }
    
    static class MapMessageOpenTypeFactory extends MessageOpenTypeFactory
    {
        @Override
        protected String getTypeName() {
            return ActiveMQMapMessage.class.getName();
        }
        
        @Override
        protected void init() throws OpenDataException {
            super.init();
            this.addItem("ContentMap", "Content map", SimpleType.STRING);
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final ActiveMQMapMessage m = (ActiveMQMapMessage)o;
            final Map<String, Object> rc = super.getFields(o);
            try {
                rc.put("ContentMap", "" + m.getContentMap());
            }
            catch (JMSException e) {
                rc.put("ContentMap", "");
            }
            return rc;
        }
    }
    
    static class ObjectMessageOpenTypeFactory extends MessageOpenTypeFactory
    {
        @Override
        protected String getTypeName() {
            return ActiveMQObjectMessage.class.getName();
        }
        
        @Override
        protected void init() throws OpenDataException {
            super.init();
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final Map<String, Object> rc = super.getFields(o);
            return rc;
        }
    }
    
    static class StreamMessageOpenTypeFactory extends MessageOpenTypeFactory
    {
        @Override
        protected String getTypeName() {
            return ActiveMQStreamMessage.class.getName();
        }
        
        @Override
        protected void init() throws OpenDataException {
            super.init();
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final Map<String, Object> rc = super.getFields(o);
            return rc;
        }
    }
    
    static class TextMessageOpenTypeFactory extends MessageOpenTypeFactory
    {
        @Override
        protected String getTypeName() {
            return ActiveMQTextMessage.class.getName();
        }
        
        @Override
        protected void init() throws OpenDataException {
            super.init();
            this.addItem("Text", "Text", SimpleType.STRING);
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final ActiveMQTextMessage m = (ActiveMQTextMessage)o;
            final Map<String, Object> rc = super.getFields(o);
            try {
                rc.put("Text", "" + m.getText());
            }
            catch (JMSException e) {
                rc.put("Text", "");
            }
            return rc;
        }
    }
    
    static class JobOpenTypeFactory extends AbstractOpenTypeFactory
    {
        @Override
        protected String getTypeName() {
            return Job.class.getName();
        }
        
        @Override
        protected void init() throws OpenDataException {
            super.init();
            this.addItem("jobId", "jobId", SimpleType.STRING);
            this.addItem("cronEntry", "Cron entry", SimpleType.STRING);
            this.addItem("start", "start time", SimpleType.STRING);
            this.addItem("delay", "initial delay", SimpleType.LONG);
            this.addItem("next", "next time", SimpleType.STRING);
            this.addItem("period", "period between jobs", SimpleType.LONG);
            this.addItem("repeat", "number of times to repeat", SimpleType.INTEGER);
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final Job job = (Job)o;
            final Map<String, Object> rc = super.getFields(o);
            rc.put("jobId", job.getJobId());
            rc.put("cronEntry", "" + job.getCronEntry());
            rc.put("start", job.getStartTime());
            rc.put("delay", job.getDelay());
            rc.put("next", job.getNextExecutionTime());
            rc.put("period", job.getPeriod());
            rc.put("repeat", job.getRepeat());
            return rc;
        }
    }
    
    static class ActiveMQBlobMessageOpenTypeFactory extends MessageOpenTypeFactory
    {
        @Override
        protected String getTypeName() {
            return ActiveMQBlobMessage.class.getName();
        }
        
        @Override
        protected void init() throws OpenDataException {
            super.init();
            this.addItem("Url", "Body Url", SimpleType.STRING);
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final ActiveMQBlobMessage m = (ActiveMQBlobMessage)o;
            final Map<String, Object> rc = super.getFields(o);
            try {
                rc.put("Url", "" + m.getURL().toString());
            }
            catch (JMSException e) {
                rc.put("Url", "");
            }
            return rc;
        }
    }
    
    static class SlowConsumerEntryOpenTypeFactory extends AbstractOpenTypeFactory
    {
        @Override
        protected String getTypeName() {
            return SlowConsumerEntry.class.getName();
        }
        
        @Override
        protected void init() throws OpenDataException {
            super.init();
            this.addItem("subscription", "the subscription view", SimpleType.OBJECTNAME);
            this.addItem("slowCount", "number of times deemed slow", SimpleType.INTEGER);
            this.addItem("markCount", "number of periods remaining slow", SimpleType.INTEGER);
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final SlowConsumerEntry entry = (SlowConsumerEntry)o;
            final Map<String, Object> rc = super.getFields(o);
            rc.put("subscription", entry.getSubscription());
            rc.put("slowCount", entry.getSlowCount());
            rc.put("markCount", entry.getMarkCount());
            return rc;
        }
    }
    
    static class HealthStatusOpenTypeFactory extends AbstractOpenTypeFactory
    {
        @Override
        protected String getTypeName() {
            return HealthStatus.class.getName();
        }
        
        @Override
        protected void init() throws OpenDataException {
            super.init();
            this.addItem("healthId", "health check id", SimpleType.STRING);
            this.addItem("level", "severity", SimpleType.STRING);
            this.addItem("message", "severity", SimpleType.STRING);
            this.addItem("resource", "event resource", SimpleType.STRING);
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) throws OpenDataException {
            final HealthStatus event = (HealthStatus)o;
            final Map<String, Object> rc = super.getFields(o);
            rc.put("healthId", event.getHealthId());
            rc.put("level", event.getLevel());
            rc.put("message", event.getMessage());
            rc.put("resource", event.getResource());
            return rc;
        }
    }
    
    public interface OpenTypeFactory
    {
        CompositeType getCompositeType() throws OpenDataException;
        
        Map<String, Object> getFields(final Object p0) throws OpenDataException;
    }
}
