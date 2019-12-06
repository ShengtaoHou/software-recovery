// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.state;

import org.apache.activemq.command.ConsumerControl;
import org.apache.activemq.command.ConnectionError;
import org.apache.activemq.command.ConnectionControl;
import org.apache.activemq.command.ControlCommand;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.WireFormatInfo;
import org.apache.activemq.command.ShutdownInfo;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.command.ProducerAck;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.KeepAliveInfo;
import org.apache.activemq.command.FlushCommand;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.TransactionInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.ConnectionInfo;

public class CommandVisitorAdapter implements CommandVisitor
{
    @Override
    public Response processAddConnection(final ConnectionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processAddConsumer(final ConsumerInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processAddDestination(final DestinationInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processAddProducer(final ProducerInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processAddSession(final SessionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processBeginTransaction(final TransactionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processBrokerInfo(final BrokerInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processCommitTransactionOnePhase(final TransactionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processCommitTransactionTwoPhase(final TransactionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processEndTransaction(final TransactionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processFlush(final FlushCommand command) throws Exception {
        return null;
    }
    
    @Override
    public Response processForgetTransaction(final TransactionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processKeepAlive(final KeepAliveInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processMessage(final Message send) throws Exception {
        return null;
    }
    
    @Override
    public Response processMessageAck(final MessageAck ack) throws Exception {
        return null;
    }
    
    @Override
    public Response processMessageDispatchNotification(final MessageDispatchNotification notification) throws Exception {
        return null;
    }
    
    @Override
    public Response processMessagePull(final MessagePull pull) throws Exception {
        return null;
    }
    
    @Override
    public Response processPrepareTransaction(final TransactionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processProducerAck(final ProducerAck ack) throws Exception {
        return null;
    }
    
    @Override
    public Response processRecoverTransactions(final TransactionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processRemoveConnection(final ConnectionId id, final long lastDeliveredSequenceId) throws Exception {
        return null;
    }
    
    @Override
    public Response processRemoveConsumer(final ConsumerId id, final long lastDeliveredSequenceId) throws Exception {
        return null;
    }
    
    @Override
    public Response processRemoveDestination(final DestinationInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processRemoveProducer(final ProducerId id) throws Exception {
        return null;
    }
    
    @Override
    public Response processRemoveSession(final SessionId id, final long lastDeliveredSequenceId) throws Exception {
        return null;
    }
    
    @Override
    public Response processRemoveSubscription(final RemoveSubscriptionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processRollbackTransaction(final TransactionInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processShutdown(final ShutdownInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processWireFormat(final WireFormatInfo info) throws Exception {
        return null;
    }
    
    @Override
    public Response processMessageDispatch(final MessageDispatch dispatch) throws Exception {
        return null;
    }
    
    @Override
    public Response processControlCommand(final ControlCommand command) throws Exception {
        return null;
    }
    
    @Override
    public Response processConnectionControl(final ConnectionControl control) throws Exception {
        return null;
    }
    
    @Override
    public Response processConnectionError(final ConnectionError error) throws Exception {
        return null;
    }
    
    @Override
    public Response processConsumerControl(final ConsumerControl control) throws Exception {
        return null;
    }
}
