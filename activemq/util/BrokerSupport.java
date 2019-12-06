// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.apache.activemq.security.SecurityContext;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.state.ProducerState;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ConnectionContext;

public final class BrokerSupport
{
    private BrokerSupport() {
    }
    
    public static void resendNoCopy(final ConnectionContext context, final Message originalMessage, final ActiveMQDestination deadLetterDestination) throws Exception {
        doResend(context, originalMessage, deadLetterDestination, false);
    }
    
    public static void resend(final ConnectionContext context, final Message originalMessage, final ActiveMQDestination deadLetterDestination) throws Exception {
        doResend(context, originalMessage, deadLetterDestination, true);
    }
    
    public static void doResend(final ConnectionContext context, final Message originalMessage, final ActiveMQDestination deadLetterDestination, final boolean copy) throws Exception {
        final Message message = copy ? originalMessage.copy() : originalMessage;
        message.setOriginalDestination(message.getDestination());
        message.setOriginalTransactionId(message.getTransactionId());
        message.setDestination(deadLetterDestination);
        message.setTransactionId(null);
        message.setMemoryUsage(null);
        message.setRedeliveryCounter(0);
        final boolean originalFlowControl = context.isProducerFlowControl();
        try {
            context.setProducerFlowControl(false);
            final ProducerInfo info = new ProducerInfo();
            final ProducerState state = new ProducerState(info);
            final ProducerBrokerExchange producerExchange = new ProducerBrokerExchange();
            producerExchange.setProducerState(state);
            producerExchange.setMutable(true);
            producerExchange.setConnectionContext(context);
            context.getBroker().send(producerExchange, message);
        }
        finally {
            context.setProducerFlowControl(originalFlowControl);
        }
    }
    
    public static ConnectionContext getConnectionContext(final Broker broker) {
        ConnectionContext adminConnectionContext = broker.getAdminConnectionContext();
        if (adminConnectionContext == null) {
            adminConnectionContext = createAdminConnectionContext(broker);
            broker.setAdminConnectionContext(adminConnectionContext);
        }
        return adminConnectionContext;
    }
    
    protected static ConnectionContext createAdminConnectionContext(final Broker broker) {
        final ConnectionContext context = new ConnectionContext();
        context.setBroker(broker);
        context.setSecurityContext(SecurityContext.BROKER_SECURITY_CONTEXT);
        return context;
    }
}
