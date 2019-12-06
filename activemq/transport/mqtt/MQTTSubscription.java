// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import org.fusesource.mqtt.codec.PUBLISH;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageDispatch;
import org.fusesource.mqtt.client.QoS;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerInfo;

class MQTTSubscription
{
    private final MQTTProtocolConverter protocolConverter;
    private final ConsumerInfo consumerInfo;
    private ActiveMQDestination destination;
    private final QoS qos;
    
    public MQTTSubscription(final MQTTProtocolConverter protocolConverter, final QoS qos, final ConsumerInfo consumerInfo) {
        this.protocolConverter = protocolConverter;
        this.consumerInfo = consumerInfo;
        this.qos = qos;
    }
    
    MessageAck createMessageAck(final MessageDispatch md) {
        return new MessageAck(md, (byte)2, 1);
    }
    
    PUBLISH createPublish(final ActiveMQMessage message) throws DataFormatException, IOException, JMSException {
        final PUBLISH publish = this.protocolConverter.convertMessage(message);
        if (publish.qos().ordinal() > this.qos.ordinal()) {
            publish.qos(this.qos);
        }
        switch (publish.qos()) {
            case AT_LEAST_ONCE:
            case EXACTLY_ONCE: {
                this.protocolConverter.getPacketIdGenerator().setPacketId(this.protocolConverter.getClientId(), this, message, publish);
                break;
            }
        }
        return publish;
    }
    
    public boolean expectAck(final PUBLISH publish) {
        QoS publishQoS = publish.qos();
        if (publishQoS.compareTo((Enum)this.qos) > 0) {
            publishQoS = this.qos;
        }
        return !publishQoS.equals((Object)QoS.AT_MOST_ONCE);
    }
    
    public ConsumerInfo getConsumerInfo() {
        return this.consumerInfo;
    }
    
    public QoS qos() {
        return this.qos;
    }
}
