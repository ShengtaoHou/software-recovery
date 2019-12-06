// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.Destination;
import java.io.IOException;
import org.apache.activemq.util.ByteSequence;
import java.util.HashMap;
import javax.jms.JMSException;
import java.util.Map;
import org.apache.activemq.command.ActiveMQBytesMessage;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQMessage;

public class LegacyFrameTranslator implements FrameTranslator
{
    @Override
    public ActiveMQMessage convertFrame(final ProtocolConverter converter, final StompFrame command) throws JMSException, ProtocolException {
        final Map<?, ?> headers = command.getHeaders();
        ActiveMQMessage msg;
        if (headers.containsKey("amq-msg-type")) {
            final String intendedType = (String)headers.get("amq-msg-type");
            if (intendedType.equalsIgnoreCase("text")) {
                final ActiveMQTextMessage text = new ActiveMQTextMessage();
                try {
                    final ByteArrayOutputStream bytes = new ByteArrayOutputStream(command.getContent().length + 4);
                    final DataOutputStream data = new DataOutputStream(bytes);
                    data.writeInt(command.getContent().length);
                    data.write(command.getContent());
                    text.setContent(bytes.toByteSequence());
                    data.close();
                }
                catch (Throwable e) {
                    throw new ProtocolException("Text could not bet set: " + e, false, e);
                }
                msg = text;
            }
            else {
                if (!intendedType.equalsIgnoreCase("bytes")) {
                    throw new ProtocolException("Unsupported message type '" + intendedType + "'", false);
                }
                final ActiveMQBytesMessage byteMessage = new ActiveMQBytesMessage();
                byteMessage.writeBytes(command.getContent());
                msg = byteMessage;
            }
        }
        else if (headers.containsKey("content-length")) {
            headers.remove("content-length");
            final ActiveMQBytesMessage bm = new ActiveMQBytesMessage();
            bm.writeBytes(command.getContent());
            msg = bm;
        }
        else {
            final ActiveMQTextMessage text2 = new ActiveMQTextMessage();
            try {
                final ByteArrayOutputStream bytes2 = new ByteArrayOutputStream(command.getContent().length + 4);
                final DataOutputStream data2 = new DataOutputStream(bytes2);
                data2.writeInt(command.getContent().length);
                data2.write(command.getContent());
                text2.setContent(bytes2.toByteSequence());
                data2.close();
            }
            catch (Throwable e2) {
                throw new ProtocolException("Text could not bet set: " + e2, false, e2);
            }
            msg = text2;
        }
        Helper.copyStandardHeadersFromFrameToMessage(converter, command, msg, this);
        return msg;
    }
    
    @Override
    public StompFrame convertMessage(final ProtocolConverter converter, final ActiveMQMessage message) throws IOException, JMSException {
        final StompFrame command = new StompFrame();
        command.setAction("MESSAGE");
        final Map<String, String> headers = new HashMap<String, String>(25);
        command.setHeaders(headers);
        Helper.copyStandardHeadersFromMessageToFrame(converter, message, command, this);
        if (message.getDataStructureType() == 28) {
            if (!message.isCompressed() && message.getContent() != null) {
                final ByteSequence msgContent = message.getContent();
                if (msgContent.getLength() > 4) {
                    final byte[] content = new byte[msgContent.getLength() - 4];
                    System.arraycopy(msgContent.data, 4, content, 0, content.length);
                    command.setContent(content);
                }
            }
            else {
                final ActiveMQTextMessage msg = (ActiveMQTextMessage)message.copy();
                final String messageText = msg.getText();
                if (messageText != null) {
                    command.setContent(msg.getText().getBytes("UTF-8"));
                }
            }
        }
        else if (message.getDataStructureType() == 24) {
            final ActiveMQBytesMessage msg2 = (ActiveMQBytesMessage)message.copy();
            msg2.setReadOnlyBody(true);
            final byte[] data = new byte[(int)msg2.getBodyLength()];
            msg2.readBytes(data);
            headers.put("content-length", Integer.toString(data.length));
            command.setContent(data);
        }
        return command;
    }
    
    @Override
    public String convertDestination(final ProtocolConverter converter, final Destination d) {
        if (d == null) {
            return null;
        }
        final ActiveMQDestination activeMQDestination = (ActiveMQDestination)d;
        final String physicalName = activeMQDestination.getPhysicalName();
        final String rc = converter.getCreatedTempDestinationName(activeMQDestination);
        if (rc != null) {
            return rc;
        }
        final StringBuilder buffer = new StringBuilder();
        if (activeMQDestination.isQueue()) {
            if (activeMQDestination.isTemporary()) {
                buffer.append("/remote-temp-queue/");
            }
            else {
                buffer.append("/queue/");
            }
        }
        else if (activeMQDestination.isTemporary()) {
            buffer.append("/remote-temp-topic/");
        }
        else {
            buffer.append("/topic/");
        }
        buffer.append(physicalName);
        return buffer.toString();
    }
    
    @Override
    public ActiveMQDestination convertDestination(final ProtocolConverter converter, String name, final boolean forceFallback) throws ProtocolException {
        if (name == null) {
            return null;
        }
        final String originalName = name;
        name = name.trim();
        if (name.startsWith("/queue/")) {
            final String qName = name.substring("/queue/".length(), name.length());
            return ActiveMQDestination.createDestination(qName, (byte)1);
        }
        if (name.startsWith("/topic/")) {
            final String tName = name.substring("/topic/".length(), name.length());
            return ActiveMQDestination.createDestination(tName, (byte)2);
        }
        if (name.startsWith("/remote-temp-queue/")) {
            final String tName = name.substring("/remote-temp-queue/".length(), name.length());
            return ActiveMQDestination.createDestination(tName, (byte)5);
        }
        if (name.startsWith("/remote-temp-topic/")) {
            final String tName = name.substring("/remote-temp-topic/".length(), name.length());
            return ActiveMQDestination.createDestination(tName, (byte)6);
        }
        if (name.startsWith("/temp-queue/")) {
            return converter.createTempDestination(name, false);
        }
        if (name.startsWith("/temp-topic/")) {
            return converter.createTempDestination(name, true);
        }
        if (forceFallback) {
            try {
                final ActiveMQDestination fallback = ActiveMQDestination.getUnresolvableDestinationTransformer().transform(originalName);
                if (fallback != null) {
                    return fallback;
                }
            }
            catch (JMSException e) {
                throw new ProtocolException("Illegal destination name: [" + originalName + "] -- ActiveMQ STOMP destinations must begin with one of: /queue/ /topic/ /temp-queue/ /temp-topic/", false, e);
            }
        }
        throw new ProtocolException("Illegal destination name: [" + originalName + "] -- ActiveMQ STOMP destinations must begin with one of: /queue/ /topic/ /temp-queue/ /temp-topic/");
    }
}
