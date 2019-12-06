// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTempTopic;
import javax.jms.TemporaryTopic;
import org.apache.activemq.command.ActiveMQTempQueue;
import javax.jms.TemporaryQueue;
import org.apache.activemq.command.ActiveMQTopic;
import javax.jms.Topic;
import org.apache.activemq.command.ActiveMQQueue;
import javax.jms.Queue;
import javax.jms.Destination;
import org.apache.activemq.command.ActiveMQMapMessage;
import javax.jms.MapMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import javax.jms.ObjectMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import javax.jms.TextMessage;
import org.apache.activemq.command.ActiveMQMessage;
import javax.jms.Message;
import org.apache.activemq.command.ActiveMQStreamMessage;
import javax.jms.StreamMessage;
import org.apache.activemq.command.ActiveMQBytesMessage;
import javax.jms.BytesMessage;
import org.apache.qpid.proton.jms.JMSVendor;

public class ActiveMQJMSVendor extends JMSVendor
{
    public static final ActiveMQJMSVendor INSTANCE;
    
    private ActiveMQJMSVendor() {
    }
    
    public BytesMessage createBytesMessage() {
        return new ActiveMQBytesMessage();
    }
    
    public StreamMessage createStreamMessage() {
        return new ActiveMQStreamMessage();
    }
    
    public Message createMessage() {
        return new ActiveMQMessage();
    }
    
    public TextMessage createTextMessage() {
        return new ActiveMQTextMessage();
    }
    
    public ObjectMessage createObjectMessage() {
        return new ActiveMQObjectMessage();
    }
    
    public MapMessage createMapMessage() {
        return new ActiveMQMapMessage();
    }
    
    public Destination createDestination(final String name) {
        return super.createDestination(name, (Class)Destination.class);
    }
    
    public <T extends Destination> T createDestination(final String name, final Class<T> kind) {
        final String destinationName = name.substring(name.lastIndexOf("://") + 3);
        if (kind == Queue.class) {
            return kind.cast(new ActiveMQQueue(destinationName));
        }
        if (kind == Topic.class) {
            return kind.cast(new ActiveMQTopic(destinationName));
        }
        if (kind == TemporaryQueue.class) {
            return kind.cast(new ActiveMQTempQueue(destinationName));
        }
        if (kind == TemporaryTopic.class) {
            return kind.cast(new ActiveMQTempTopic(destinationName));
        }
        return kind.cast(ActiveMQDestination.createDestination(name, (byte)1));
    }
    
    public void setJMSXUserID(final Message msg, final String value) {
        ((ActiveMQMessage)msg).setUserID(value);
    }
    
    public void setJMSXGroupID(final Message msg, final String value) {
        ((ActiveMQMessage)msg).setGroupID(value);
    }
    
    public void setJMSXGroupSequence(final Message msg, final int value) {
        ((ActiveMQMessage)msg).setGroupSequence(value);
    }
    
    public void setJMSXDeliveryCount(final Message msg, final long value) {
        ((ActiveMQMessage)msg).setRedeliveryCounter((int)value);
    }
    
    public String toAddress(final Destination dest) {
        return ((ActiveMQDestination)dest).getQualifiedName();
    }
    
    static {
        INSTANCE = new ActiveMQJMSVendor();
    }
}
