// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.state;

import org.apache.activemq.command.ConsumerControl;
import org.apache.activemq.command.ConnectionControl;
import org.apache.activemq.command.ConnectionError;
import org.apache.activemq.command.ControlCommand;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.ProducerAck;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.FlushCommand;
import org.apache.activemq.command.ShutdownInfo;
import org.apache.activemq.command.KeepAliveInfo;
import org.apache.activemq.command.WireFormatInfo;
import org.apache.activemq.command.TransactionInfo;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.ConnectionInfo;

public interface CommandVisitor
{
    Response processAddConnection(final ConnectionInfo p0) throws Exception;
    
    Response processAddSession(final SessionInfo p0) throws Exception;
    
    Response processAddProducer(final ProducerInfo p0) throws Exception;
    
    Response processAddConsumer(final ConsumerInfo p0) throws Exception;
    
    Response processRemoveConnection(final ConnectionId p0, final long p1) throws Exception;
    
    Response processRemoveSession(final SessionId p0, final long p1) throws Exception;
    
    Response processRemoveProducer(final ProducerId p0) throws Exception;
    
    Response processRemoveConsumer(final ConsumerId p0, final long p1) throws Exception;
    
    Response processAddDestination(final DestinationInfo p0) throws Exception;
    
    Response processRemoveDestination(final DestinationInfo p0) throws Exception;
    
    Response processRemoveSubscription(final RemoveSubscriptionInfo p0) throws Exception;
    
    Response processMessage(final Message p0) throws Exception;
    
    Response processMessageAck(final MessageAck p0) throws Exception;
    
    Response processMessagePull(final MessagePull p0) throws Exception;
    
    Response processBeginTransaction(final TransactionInfo p0) throws Exception;
    
    Response processPrepareTransaction(final TransactionInfo p0) throws Exception;
    
    Response processCommitTransactionOnePhase(final TransactionInfo p0) throws Exception;
    
    Response processCommitTransactionTwoPhase(final TransactionInfo p0) throws Exception;
    
    Response processRollbackTransaction(final TransactionInfo p0) throws Exception;
    
    Response processWireFormat(final WireFormatInfo p0) throws Exception;
    
    Response processKeepAlive(final KeepAliveInfo p0) throws Exception;
    
    Response processShutdown(final ShutdownInfo p0) throws Exception;
    
    Response processFlush(final FlushCommand p0) throws Exception;
    
    Response processBrokerInfo(final BrokerInfo p0) throws Exception;
    
    Response processRecoverTransactions(final TransactionInfo p0) throws Exception;
    
    Response processForgetTransaction(final TransactionInfo p0) throws Exception;
    
    Response processEndTransaction(final TransactionInfo p0) throws Exception;
    
    Response processMessageDispatchNotification(final MessageDispatchNotification p0) throws Exception;
    
    Response processProducerAck(final ProducerAck p0) throws Exception;
    
    Response processMessageDispatch(final MessageDispatch p0) throws Exception;
    
    Response processControlCommand(final ControlCommand p0) throws Exception;
    
    Response processConnectionError(final ConnectionError p0) throws Exception;
    
    Response processConnectionControl(final ConnectionControl p0) throws Exception;
    
    Response processConsumerControl(final ConsumerControl p0) throws Exception;
}
