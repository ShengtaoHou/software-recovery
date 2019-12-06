// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.util.Enumeration;
import java.net.MalformedURLException;
import org.apache.activemq.blob.BlobDownloader;
import org.apache.activemq.command.ActiveMQBlobMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import javax.jms.TextMessage;
import org.apache.activemq.command.ActiveMQStreamMessage;
import javax.jms.StreamMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import javax.jms.ObjectMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import javax.jms.MapMessage;
import javax.jms.MessageEOFException;
import org.apache.activemq.command.ActiveMQBytesMessage;
import javax.jms.BytesMessage;
import org.apache.activemq.command.ActiveMQMessage;
import javax.jms.Message;
import javax.jms.JMSException;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTempTopic;
import javax.jms.Topic;
import javax.jms.TemporaryTopic;
import org.apache.activemq.command.ActiveMQTempQueue;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.Destination;

public final class ActiveMQMessageTransformation
{
    private ActiveMQMessageTransformation() {
    }
    
    public static ActiveMQDestination transformDestination(final Destination destination) throws JMSException {
        ActiveMQDestination activeMQDestination = null;
        if (destination != null) {
            if (destination instanceof ActiveMQDestination) {
                return (ActiveMQDestination)destination;
            }
            if (destination instanceof TemporaryQueue) {
                activeMQDestination = new ActiveMQTempQueue(((Queue)destination).getQueueName());
            }
            else if (destination instanceof TemporaryTopic) {
                activeMQDestination = new ActiveMQTempTopic(((Topic)destination).getTopicName());
            }
            else if (destination instanceof Queue) {
                activeMQDestination = new ActiveMQQueue(((Queue)destination).getQueueName());
            }
            else if (destination instanceof Topic) {
                activeMQDestination = new ActiveMQTopic(((Topic)destination).getTopicName());
            }
        }
        return activeMQDestination;
    }
    
    public static ActiveMQMessage transformMessage(final Message message, final ActiveMQConnection connection) throws JMSException {
        if (message instanceof ActiveMQMessage) {
            return (ActiveMQMessage)message;
        }
        ActiveMQMessage activeMessage = null;
        if (message instanceof BytesMessage) {
            final BytesMessage bytesMsg = (BytesMessage)message;
            bytesMsg.reset();
            final ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
            msg.setConnection(connection);
            try {
                while (true) {
                    msg.writeByte(bytesMsg.readByte());
                }
            }
            catch (MessageEOFException ex) {}
            catch (JMSException ex2) {}
            activeMessage = msg;
        }
        else if (message instanceof MapMessage) {
            final MapMessage mapMsg = (MapMessage)message;
            final ActiveMQMapMessage msg2 = new ActiveMQMapMessage();
            msg2.setConnection(connection);
            final Enumeration iter = mapMsg.getMapNames();
            while (iter.hasMoreElements()) {
                final String name = iter.nextElement().toString();
                msg2.setObject(name, mapMsg.getObject(name));
            }
            activeMessage = msg2;
        }
        else if (message instanceof ObjectMessage) {
            final ObjectMessage objMsg = (ObjectMessage)message;
            final ActiveMQObjectMessage msg3 = new ActiveMQObjectMessage();
            msg3.setConnection(connection);
            msg3.setObject(objMsg.getObject());
            msg3.storeContent();
            activeMessage = msg3;
        }
        else if (message instanceof StreamMessage) {
            final StreamMessage streamMessage = (StreamMessage)message;
            streamMessage.reset();
            final ActiveMQStreamMessage msg4 = new ActiveMQStreamMessage();
            msg4.setConnection(connection);
            Object obj = null;
            try {
                while ((obj = streamMessage.readObject()) != null) {
                    msg4.writeObject(obj);
                }
            }
            catch (MessageEOFException ex3) {}
            catch (JMSException ex4) {}
            activeMessage = msg4;
        }
        else if (message instanceof TextMessage) {
            final TextMessage textMsg = (TextMessage)message;
            final ActiveMQTextMessage msg5 = new ActiveMQTextMessage();
            msg5.setConnection(connection);
            msg5.setText(textMsg.getText());
            activeMessage = msg5;
        }
        else if (message instanceof BlobMessage) {
            final BlobMessage blobMessage = (BlobMessage)message;
            final ActiveMQBlobMessage msg6 = new ActiveMQBlobMessage();
            msg6.setConnection(connection);
            if (connection != null) {
                msg6.setBlobDownloader(new BlobDownloader(connection.getBlobTransferPolicy()));
            }
            try {
                msg6.setURL(blobMessage.getURL());
            }
            catch (MalformedURLException ex5) {}
            activeMessage = msg6;
        }
        else {
            activeMessage = new ActiveMQMessage();
            activeMessage.setConnection(connection);
        }
        copyProperties(message, activeMessage);
        return activeMessage;
    }
    
    public static void copyProperties(final Message fromMessage, final Message toMessage) throws JMSException {
        toMessage.setJMSMessageID(fromMessage.getJMSMessageID());
        toMessage.setJMSCorrelationID(fromMessage.getJMSCorrelationID());
        toMessage.setJMSReplyTo(transformDestination(fromMessage.getJMSReplyTo()));
        toMessage.setJMSDestination(transformDestination(fromMessage.getJMSDestination()));
        toMessage.setJMSDeliveryMode(fromMessage.getJMSDeliveryMode());
        toMessage.setJMSRedelivered(fromMessage.getJMSRedelivered());
        toMessage.setJMSType(fromMessage.getJMSType());
        toMessage.setJMSExpiration(fromMessage.getJMSExpiration());
        toMessage.setJMSPriority(fromMessage.getJMSPriority());
        toMessage.setJMSTimestamp(fromMessage.getJMSTimestamp());
        final Enumeration propertyNames = fromMessage.getPropertyNames();
        while (propertyNames.hasMoreElements()) {
            final String name = propertyNames.nextElement().toString();
            final Object obj = fromMessage.getObjectProperty(name);
            toMessage.setObjectProperty(name, obj);
        }
    }
}
