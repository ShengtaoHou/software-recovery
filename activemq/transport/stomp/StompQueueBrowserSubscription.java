// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.TransactionId;
import javax.jms.JMSException;
import java.io.IOException;
import javax.jms.Destination;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.ConsumerInfo;

public class StompQueueBrowserSubscription extends StompSubscription
{
    public StompQueueBrowserSubscription(final ProtocolConverter stompTransport, final String subscriptionId, final ConsumerInfo consumerInfo, final String transformation) {
        super(stompTransport, subscriptionId, consumerInfo, transformation);
    }
    
    @Override
    void onMessageDispatch(final MessageDispatch md, final String ackId) throws IOException, JMSException {
        if (md.getMessage() != null) {
            super.onMessageDispatch(md, ackId);
        }
        else {
            final StompFrame browseDone = new StompFrame("MESSAGE");
            browseDone.getHeaders().put("subscription", this.getSubscriptionId());
            browseDone.getHeaders().put("browser", "end");
            browseDone.getHeaders().put("destination", this.protocolConverter.findTranslator(null).convertDestination(this.protocolConverter, this.destination));
            browseDone.getHeaders().put("message-id", "0");
            this.protocolConverter.sendToStomp(browseDone);
        }
    }
    
    @Override
    public MessageAck onStompMessageNack(final String messageId, final TransactionId transactionId) throws ProtocolException {
        throw new ProtocolException("Cannot Nack a message on a Queue Browser Subscription.");
    }
}
