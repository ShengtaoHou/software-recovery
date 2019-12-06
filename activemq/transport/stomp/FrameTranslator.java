// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.Destination;
import java.io.IOException;
import javax.jms.JMSException;
import org.apache.activemq.command.ActiveMQMessage;

public interface FrameTranslator
{
    ActiveMQMessage convertFrame(final ProtocolConverter p0, final StompFrame p1) throws JMSException, ProtocolException;
    
    StompFrame convertMessage(final ProtocolConverter p0, final ActiveMQMessage p1) throws IOException, JMSException;
    
    String convertDestination(final ProtocolConverter p0, final Destination p1);
    
    ActiveMQDestination convertDestination(final ProtocolConverter p0, final String p1, final boolean p2) throws ProtocolException;
    
    public static final class Helper
    {
        private Helper() {
        }
        
        public static void copyStandardHeadersFromMessageToFrame(final ProtocolConverter converter, final ActiveMQMessage message, final StompFrame command, final FrameTranslator ft) throws IOException {
            final Map<String, String> headers = command.getHeaders();
            headers.put("destination", ft.convertDestination(converter, message.getDestination()));
            headers.put("message-id", message.getJMSMessageID());
            if (message.getJMSCorrelationID() != null) {
                headers.put("correlation-id", message.getJMSCorrelationID());
            }
            headers.put("expires", "" + message.getJMSExpiration());
            if (message.getJMSRedelivered()) {
                headers.put("redelivered", "true");
            }
            headers.put("priority", "" + message.getJMSPriority());
            if (message.getJMSReplyTo() != null) {
                headers.put("reply-to", ft.convertDestination(converter, message.getJMSReplyTo()));
            }
            headers.put("timestamp", "" + message.getJMSTimestamp());
            if (message.getJMSType() != null) {
                headers.put("type", message.getJMSType());
            }
            if (message.getUserID() != null) {
                headers.put("JMSXUserID", message.getUserID());
            }
            if (message.getOriginalDestination() != null) {
                headers.put("original-destination", ft.convertDestination(converter, message.getOriginalDestination()));
            }
            if (message.isPersistent()) {
                headers.put("persistent", "true");
            }
            final Map<String, Object> properties = message.getProperties();
            if (properties != null) {
                for (final Map.Entry<String, Object> prop : properties.entrySet()) {
                    headers.put(prop.getKey(), "" + prop.getValue());
                }
            }
        }
        
        public static void copyStandardHeadersFromFrameToMessage(final ProtocolConverter converter, final StompFrame command, final ActiveMQMessage msg, final FrameTranslator ft) throws ProtocolException, JMSException {
            final Map<String, String> headers = new HashMap<String, String>(command.getHeaders());
            final String destination = headers.remove("destination");
            msg.setDestination(ft.convertDestination(converter, destination, true));
            msg.setJMSCorrelationID(headers.remove("correlation-id"));
            Object o = headers.remove("expires");
            if (o != null) {
                msg.setJMSExpiration(Long.parseLong((String)o));
            }
            o = headers.remove("timestamp");
            if (o != null) {
                msg.setJMSTimestamp(Long.parseLong((String)o));
            }
            else {
                msg.setJMSTimestamp(System.currentTimeMillis());
            }
            o = headers.remove("priority");
            if (o != null) {
                msg.setJMSPriority(Integer.parseInt((String)o));
            }
            else {
                msg.setJMSPriority(4);
            }
            o = headers.remove("type");
            if (o != null) {
                msg.setJMSType((String)o);
            }
            o = headers.remove("reply-to");
            if (o != null) {
                try {
                    final ActiveMQDestination dest = ft.convertDestination(converter, (String)o, false);
                    msg.setJMSReplyTo(dest);
                }
                catch (ProtocolException pe) {
                    msg.setStringProperty("reply-to", (String)o);
                }
            }
            o = headers.remove("persistent");
            if (o != null) {
                msg.setPersistent("true".equals(o));
            }
            headers.remove("receipt");
            headers.remove("message-id");
            headers.remove("redelivered");
            headers.remove("subscription");
            headers.remove("JMSXUserID");
            msg.setProperties(headers);
        }
    }
}
