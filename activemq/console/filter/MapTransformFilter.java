// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import java.lang.reflect.Array;
import java.util.Arrays;
import javax.management.openmbean.CompositeDataSupport;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.HashMap;
import org.apache.activemq.command.ActiveMQStreamMessage;
import java.util.Enumeration;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQBytesMessage;
import javax.jms.JMSException;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import javax.management.Attribute;
import javax.management.AttributeList;
import java.util.Iterator;
import java.util.Properties;
import javax.management.ObjectName;
import javax.management.ObjectInstance;
import java.lang.reflect.Method;
import java.util.Map;

public class MapTransformFilter extends ResultTransformFilter
{
    public MapTransformFilter(final QueryFilter next) {
        super(next);
    }
    
    @Override
    protected Object transformElement(final Object object) throws Exception {
        try {
            final Method method = this.getClass().getDeclaredMethod("transformToMap", object.getClass());
            return method.invoke(this, object);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    protected Map transformToMap(final ObjectInstance obj) {
        return this.transformToMap(obj.getObjectName());
    }
    
    protected Map transformToMap(final ObjectName objname) {
        final Properties props = new Properties();
        final Map objProps = objname.getKeyPropertyList();
        for (final Object key : objProps.keySet()) {
            final Object val = objProps.get(key);
            if (val != null) {
                props.setProperty(key.toString(), this.getDisplayString(val));
            }
        }
        return props;
    }
    
    protected Map transformToMap(final AttributeList list) {
        final Properties props = new Properties();
        for (final Attribute attrib : list) {
            if (attrib.getName().equals("Attribute:ObjectName:")) {
                props.putAll(this.transformToMap((ObjectName)attrib.getValue()));
            }
            else {
                if (attrib.getValue() == null) {
                    continue;
                }
                props.setProperty(attrib.getName(), this.getDisplayString(attrib.getValue()));
            }
        }
        return props;
    }
    
    protected Map transformToMap(final ActiveMQTextMessage msg) throws JMSException {
        final Properties props = new Properties();
        props.putAll(this.transformToMap((ActiveMQMessage)msg));
        if (msg.getText() != null) {
            props.setProperty("JMS_BODY_FIELD:JMSText", msg.getText());
        }
        return props;
    }
    
    protected Map transformToMap(final ActiveMQBytesMessage msg) throws JMSException {
        final Properties props = new Properties();
        props.putAll(this.transformToMap((ActiveMQMessage)msg));
        long bodyLength;
        int i;
        byte[] msgBody;
        for (bodyLength = msg.getBodyLength(), i = 0, i = 0; i < bodyLength / 2147483647L; ++i) {
            msgBody = new byte[Integer.MAX_VALUE];
            props.setProperty("JMS_BODY_FIELD:JMSBytes:" + (i + 1), new String(msgBody));
        }
        msgBody = new byte[(int)(bodyLength % 2147483647L)];
        props.setProperty("JMS_BODY_FIELD:JMSBytes:" + (i + 1), new String(msgBody));
        return props;
    }
    
    protected Map transformToMap(final ActiveMQObjectMessage msg) throws JMSException {
        final Properties props = new Properties();
        props.putAll(this.transformToMap((ActiveMQMessage)msg));
        if (msg.getObject() != null) {
            props.setProperty("JMS_BODY_FIELD:JMSObjectClass", msg.getObject().getClass().getName());
            props.setProperty("JMS_BODY_FIELD:JMSObjectString", this.getDisplayString(msg.getObject()));
        }
        return props;
    }
    
    protected Map transformToMap(final ActiveMQMapMessage msg) throws JMSException {
        final Properties props = new Properties();
        props.putAll(this.transformToMap((ActiveMQMessage)msg));
        final Enumeration e = msg.getMapNames();
        while (e.hasMoreElements()) {
            final String key = e.nextElement();
            final Object val = msg.getObject(key);
            if (val != null) {
                props.setProperty("JMS_BODY_FIELD:" + key, this.getDisplayString(val));
            }
        }
        return props;
    }
    
    protected Map transformToMap(final ActiveMQStreamMessage msg) throws JMSException {
        final Properties props = new Properties();
        props.putAll(this.transformToMap((ActiveMQMessage)msg));
        props.setProperty("JMS_BODY_FIELD:JMSStreamMessage", this.getDisplayString(msg));
        return props;
    }
    
    protected Map<String, String> transformToMap(final ActiveMQMessage msg) throws JMSException {
        final Map<String, String> props = new HashMap<String, String>();
        if (msg.getJMSCorrelationID() != null) {
            props.put("JMS_HEADER_FIELD:JMSCorrelationID", msg.getJMSCorrelationID());
        }
        props.put("JMS_HEADER_FIELD:JMSDeliveryMode", (msg.getJMSDeliveryMode() == 2) ? "persistent" : "non-persistent");
        if (msg.getJMSDestination() != null) {
            props.put("JMS_HEADER_FIELD:JMSDestination", ((ActiveMQDestination)msg.getJMSDestination()).getPhysicalName());
        }
        props.put("JMS_HEADER_FIELD:JMSExpiration", Long.toString(msg.getJMSExpiration()));
        props.put("JMS_HEADER_FIELD:JMSMessageID", msg.getJMSMessageID());
        props.put("JMS_HEADER_FIELD:JMSPriority", Integer.toString(msg.getJMSPriority()));
        props.put("JMS_HEADER_FIELD:JMSRedelivered", Boolean.toString(msg.getJMSRedelivered()));
        if (msg.getJMSReplyTo() != null) {
            props.put("JMS_HEADER_FIELD:JMSReplyTo", ((ActiveMQDestination)msg.getJMSReplyTo()).getPhysicalName());
        }
        props.put("JMS_HEADER_FIELD:JMSTimestamp", Long.toString(msg.getJMSTimestamp()));
        if (msg.getJMSType() != null) {
            props.put("JMS_HEADER_FIELD:JMSType", msg.getJMSType());
        }
        final Enumeration e = msg.getPropertyNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            if (msg.getObjectProperty(name) != null) {
                props.put("JMS_CUSTOM_FIELD:" + name, this.getDisplayString(msg.getObjectProperty(name)));
            }
        }
        return props;
    }
    
    protected Map transformToMap(final CompositeDataSupport data) {
        final Properties props = new Properties();
        final String typeName = data.getCompositeType().getTypeName();
        if (typeName.equals(ActiveMQTextMessage.class.getName())) {
            props.setProperty("JMS_BODY_FIELD:Text", data.get("Text").toString());
        }
        else if (typeName.equals(ActiveMQBytesMessage.class.getName())) {
            props.setProperty("JMS_BODY_FIELD:BodyLength", data.get("BodyLength").toString());
            props.setProperty("JMS_BODY_FIELD:BodyPreview", new String((byte[])data.get("BodyPreview")));
        }
        else if (typeName.equals(ActiveMQMapMessage.class.getName())) {
            final Map contentMap = (Map)data.get("ContentMap");
            for (final String key : contentMap.keySet()) {
                props.setProperty("JMS_BODY_FIELD:" + key, contentMap.get(key).toString());
            }
        }
        else if (!typeName.equals(ActiveMQObjectMessage.class.getName()) && !typeName.equals(ActiveMQStreamMessage.class.getName())) {
            if (!typeName.equals(ActiveMQMessage.class.getName())) {
                throw new IllegalArgumentException("Unrecognized composite data to transform. composite type: " + typeName);
            }
        }
        props.setProperty("JMS_HEADER_FIELD:JMSCorrelationID", "" + data.get("JMSCorrelationID"));
        props.setProperty("JMS_HEADER_FIELD:JMSDestination", "" + data.get("JMSDestination"));
        props.setProperty("JMS_HEADER_FIELD:JMSMessageID", "" + data.get("JMSMessageID"));
        props.setProperty("JMS_HEADER_FIELD:JMSReplyTo", "" + data.get("JMSReplyTo"));
        props.setProperty("JMS_HEADER_FIELD:JMSType", "" + data.get("JMSType"));
        props.setProperty("JMS_HEADER_FIELD:JMSDeliveryMode", "" + data.get("JMSDeliveryMode"));
        props.setProperty("JMS_HEADER_FIELD:JMSExpiration", "" + data.get("JMSExpiration"));
        props.setProperty("JMS_HEADER_FIELD:JMSPriority", "" + data.get("JMSPriority"));
        props.setProperty("JMS_HEADER_FIELD:JMSRedelivered", "" + data.get("JMSRedelivered"));
        props.setProperty("JMS_HEADER_FIELD:JMSTimestamp", "" + data.get("JMSTimestamp"));
        props.setProperty("JMS_CUSTOM_FIELD:Properties", "" + data.get("Properties"));
        return props;
    }
    
    protected String getDisplayString(Object obj) {
        if (null == obj) {
            return "null";
        }
        if (obj != null && obj.getClass().isArray()) {
            final Class type = obj.getClass().getComponentType();
            if (!type.isPrimitive()) {
                obj = Arrays.asList((Object[])obj);
            }
            else {
                final int len = Array.getLength(obj);
                if (0 == len) {
                    return "[]";
                }
                final StringBuilder bldr = new StringBuilder();
                bldr.append("[");
                for (int i = 0; i <= len; ++i) {
                    bldr.append(Array.get(obj, i));
                    if (i + 1 >= len) {
                        return bldr.append("]").toString();
                    }
                    bldr.append(",");
                }
            }
        }
        return obj.toString();
    }
}
