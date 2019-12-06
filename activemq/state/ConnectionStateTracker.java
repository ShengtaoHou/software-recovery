// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.state;

import org.apache.activemq.command.IntegerResponse;
import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ConsumerControl;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.command.ConsumerInfo;
import javax.jms.TransactionRolledBackException;
import org.apache.activemq.command.ExceptionResponse;
import org.apache.activemq.command.TransactionInfo;
import java.util.Vector;
import java.util.Iterator;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.util.IOExceptionSupport;
import java.io.IOException;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.command.Message;
import java.util.LinkedHashMap;
import org.apache.activemq.command.Command;
import java.util.Map;
import org.apache.activemq.command.ConnectionId;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class ConnectionStateTracker extends CommandVisitorAdapter
{
    private static final Logger LOG;
    private static final Tracked TRACKED_RESPONSE_MARKER;
    private static final int MESSAGE_PULL_SIZE = 400;
    protected final ConcurrentHashMap<ConnectionId, ConnectionState> connectionStates;
    private boolean trackTransactions;
    private boolean restoreSessions;
    private boolean restoreConsumers;
    private boolean restoreProducers;
    private boolean restoreTransaction;
    private boolean trackMessages;
    private boolean trackTransactionProducers;
    private int maxCacheSize;
    private long currentCacheSize;
    private final Map<Object, Command> messageCache;
    
    public ConnectionStateTracker() {
        this.connectionStates = new ConcurrentHashMap<ConnectionId, ConnectionState>();
        this.restoreSessions = true;
        this.restoreConsumers = true;
        this.restoreProducers = true;
        this.restoreTransaction = true;
        this.trackMessages = true;
        this.trackTransactionProducers = true;
        this.maxCacheSize = 131072;
        this.messageCache = new LinkedHashMap<Object, Command>() {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<Object, Command> eldest) {
                final boolean result = ConnectionStateTracker.this.currentCacheSize > ConnectionStateTracker.this.maxCacheSize;
                if (result) {
                    if (eldest.getValue() instanceof Message) {
                        ConnectionStateTracker.this.currentCacheSize -= eldest.getValue().getSize();
                    }
                    else if (eldest.getValue() instanceof MessagePull) {
                        ConnectionStateTracker.this.currentCacheSize -= 400L;
                    }
                    if (ConnectionStateTracker.LOG.isTraceEnabled()) {
                        ConnectionStateTracker.LOG.trace("removing tracked message: " + eldest.getKey());
                    }
                }
                return result;
            }
        };
    }
    
    public Tracked track(final Command command) throws IOException {
        try {
            return (Tracked)command.visit(this);
        }
        catch (IOException e) {
            throw e;
        }
        catch (Throwable e2) {
            throw IOExceptionSupport.create(e2);
        }
    }
    
    public void trackBack(final Command command) {
        if (command != null) {
            if (this.trackMessages && command.isMessage()) {
                final Message message = (Message)command;
                if (message.getTransactionId() == null) {
                    this.currentCacheSize += message.getSize();
                }
            }
            else if (command instanceof MessagePull && ((MessagePull)command).isTracked()) {
                this.currentCacheSize += 400L;
            }
        }
    }
    
    public void restore(final Transport transport) throws IOException {
        for (final ConnectionState connectionState : this.connectionStates.values()) {
            connectionState.getInfo().setFailoverReconnect(true);
            if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                ConnectionStateTracker.LOG.debug("conn: " + connectionState.getInfo().getConnectionId());
            }
            transport.oneway(connectionState.getInfo());
            this.restoreTempDestinations(transport, connectionState);
            if (this.restoreSessions) {
                this.restoreSessions(transport, connectionState);
            }
            if (this.restoreTransaction) {
                this.restoreTransactions(transport, connectionState);
            }
        }
        for (final Command msg : this.messageCache.values()) {
            if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                ConnectionStateTracker.LOG.debug("command: " + (msg.isMessage() ? ((Message)msg).getMessageId() : msg));
            }
            transport.oneway(msg);
        }
    }
    
    private void restoreTransactions(final Transport transport, final ConnectionState connectionState) throws IOException {
        final Vector<TransactionInfo> toRollback = new Vector<TransactionInfo>();
        for (final TransactionState transactionState : connectionState.getTransactionStates()) {
            if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                ConnectionStateTracker.LOG.debug("tx: " + transactionState.getId());
            }
            if (!transactionState.getCommands().isEmpty()) {
                final Command lastCommand = transactionState.getCommands().get(transactionState.getCommands().size() - 1);
                if (lastCommand instanceof TransactionInfo) {
                    final TransactionInfo transactionInfo = (TransactionInfo)lastCommand;
                    if (transactionInfo.getType() == 2) {
                        if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                            ConnectionStateTracker.LOG.debug("rolling back potentially completed tx: " + transactionState.getId());
                        }
                        toRollback.add(transactionInfo);
                        continue;
                    }
                }
            }
            for (final ProducerState producerState : transactionState.getProducerStates().values()) {
                if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                    ConnectionStateTracker.LOG.debug("tx replay producer :" + producerState.getInfo());
                }
                transport.oneway(producerState.getInfo());
            }
            for (final Command command : transactionState.getCommands()) {
                if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                    ConnectionStateTracker.LOG.debug("tx replay: " + command);
                }
                transport.oneway(command);
            }
            for (final ProducerState producerState : transactionState.getProducerStates().values()) {
                if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                    ConnectionStateTracker.LOG.debug("tx remove replayed producer :" + producerState.getInfo());
                }
                transport.oneway(producerState.getInfo().createRemoveCommand());
            }
        }
        for (final TransactionInfo command2 : toRollback) {
            final ExceptionResponse response = new ExceptionResponse();
            response.setException(new TransactionRolledBackException("Transaction completion in doubt due to failover. Forcing rollback of " + command2.getTransactionId()));
            response.setCorrelationId(command2.getCommandId());
            transport.getTransportListener().onCommand(response);
        }
    }
    
    protected void restoreSessions(final Transport transport, final ConnectionState connectionState) throws IOException {
        for (final SessionState sessionState : connectionState.getSessionStates()) {
            if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                ConnectionStateTracker.LOG.debug("session: " + sessionState.getInfo().getSessionId());
            }
            transport.oneway(sessionState.getInfo());
            if (this.restoreProducers) {
                this.restoreProducers(transport, sessionState);
            }
            if (this.restoreConsumers) {
                this.restoreConsumers(transport, sessionState);
            }
        }
    }
    
    protected void restoreConsumers(final Transport transport, final SessionState sessionState) throws IOException {
        final ConnectionState connectionState = this.connectionStates.get(sessionState.getInfo().getSessionId().getParentId());
        final boolean connectionInterruptionProcessingComplete = connectionState.isConnectionInterruptProcessingComplete();
        for (final ConsumerState consumerState : sessionState.getConsumerStates()) {
            ConsumerInfo infoToSend = consumerState.getInfo();
            if (!connectionInterruptionProcessingComplete && infoToSend.getPrefetchSize() > 0) {
                infoToSend = consumerState.getInfo().copy();
                connectionState.getRecoveringPullConsumers().put(infoToSend.getConsumerId(), consumerState.getInfo());
                infoToSend.setPrefetchSize(0);
                if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                    ConnectionStateTracker.LOG.debug("restore consumer: " + infoToSend.getConsumerId() + " in pull mode pending recovery, overriding prefetch: " + consumerState.getInfo().getPrefetchSize());
                }
            }
            if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                ConnectionStateTracker.LOG.debug("consumer: " + infoToSend.getConsumerId());
            }
            transport.oneway(infoToSend);
        }
    }
    
    protected void restoreProducers(final Transport transport, final SessionState sessionState) throws IOException {
        for (final ProducerState producerState : sessionState.getProducerStates()) {
            if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                ConnectionStateTracker.LOG.debug("producer: " + producerState.getInfo().getProducerId());
            }
            transport.oneway(producerState.getInfo());
        }
    }
    
    protected void restoreTempDestinations(final Transport transport, final ConnectionState connectionState) throws IOException {
        for (final DestinationInfo info : connectionState.getTempDestinations()) {
            transport.oneway(info);
            if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                ConnectionStateTracker.LOG.debug("tempDest: " + info.getDestination());
            }
        }
    }
    
    @Override
    public Response processAddDestination(final DestinationInfo info) {
        if (info != null) {
            final ConnectionState cs = this.connectionStates.get(info.getConnectionId());
            if (cs != null && info.getDestination().isTemporary()) {
                cs.addTempDestination(info);
            }
        }
        return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
    }
    
    @Override
    public Response processRemoveDestination(final DestinationInfo info) {
        if (info != null) {
            final ConnectionState cs = this.connectionStates.get(info.getConnectionId());
            if (cs != null && info.getDestination().isTemporary()) {
                cs.removeTempDestination(info.getDestination());
            }
        }
        return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
    }
    
    @Override
    public Response processAddProducer(final ProducerInfo info) {
        if (info != null && info.getProducerId() != null) {
            final SessionId sessionId = info.getProducerId().getParentId();
            if (sessionId != null) {
                final ConnectionId connectionId = sessionId.getParentId();
                if (connectionId != null) {
                    final ConnectionState cs = this.connectionStates.get(connectionId);
                    if (cs != null) {
                        final SessionState ss = cs.getSessionState(sessionId);
                        if (ss != null) {
                            ss.addProducer(info);
                        }
                    }
                }
            }
        }
        return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
    }
    
    @Override
    public Response processRemoveProducer(final ProducerId id) {
        if (id != null) {
            final SessionId sessionId = id.getParentId();
            if (sessionId != null) {
                final ConnectionId connectionId = sessionId.getParentId();
                if (connectionId != null) {
                    final ConnectionState cs = this.connectionStates.get(connectionId);
                    if (cs != null) {
                        final SessionState ss = cs.getSessionState(sessionId);
                        if (ss != null) {
                            ss.removeProducer(id);
                        }
                    }
                }
            }
        }
        return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
    }
    
    @Override
    public Response processAddConsumer(final ConsumerInfo info) {
        if (info != null) {
            final SessionId sessionId = info.getConsumerId().getParentId();
            if (sessionId != null) {
                final ConnectionId connectionId = sessionId.getParentId();
                if (connectionId != null) {
                    final ConnectionState cs = this.connectionStates.get(connectionId);
                    if (cs != null) {
                        final SessionState ss = cs.getSessionState(sessionId);
                        if (ss != null) {
                            ss.addConsumer(info);
                        }
                    }
                }
            }
        }
        return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
    }
    
    @Override
    public Response processRemoveConsumer(final ConsumerId id, final long lastDeliveredSequenceId) {
        if (id != null) {
            final SessionId sessionId = id.getParentId();
            if (sessionId != null) {
                final ConnectionId connectionId = sessionId.getParentId();
                if (connectionId != null) {
                    final ConnectionState cs = this.connectionStates.get(connectionId);
                    if (cs != null) {
                        final SessionState ss = cs.getSessionState(sessionId);
                        if (ss != null) {
                            ss.removeConsumer(id);
                        }
                        cs.getRecoveringPullConsumers().remove(id);
                    }
                }
            }
        }
        return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
    }
    
    @Override
    public Response processAddSession(final SessionInfo info) {
        if (info != null) {
            final ConnectionId connectionId = info.getSessionId().getParentId();
            if (connectionId != null) {
                final ConnectionState cs = this.connectionStates.get(connectionId);
                if (cs != null) {
                    cs.addSession(info);
                }
            }
        }
        return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
    }
    
    @Override
    public Response processRemoveSession(final SessionId id, final long lastDeliveredSequenceId) {
        if (id != null) {
            final ConnectionId connectionId = id.getParentId();
            if (connectionId != null) {
                final ConnectionState cs = this.connectionStates.get(connectionId);
                if (cs != null) {
                    cs.removeSession(id);
                }
            }
        }
        return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
    }
    
    @Override
    public Response processAddConnection(final ConnectionInfo info) {
        if (info != null) {
            this.connectionStates.put(info.getConnectionId(), new ConnectionState(info));
        }
        return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
    }
    
    @Override
    public Response processRemoveConnection(final ConnectionId id, final long lastDeliveredSequenceId) throws Exception {
        if (id != null) {
            this.connectionStates.remove(id);
        }
        return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
    }
    
    @Override
    public Response processMessage(final Message send) throws Exception {
        if (send != null) {
            if (this.trackTransactions && send.getTransactionId() != null) {
                final ProducerId producerId = send.getProducerId();
                final ConnectionId connectionId = producerId.getParentId().getParentId();
                if (connectionId != null) {
                    final ConnectionState cs = this.connectionStates.get(connectionId);
                    if (cs != null) {
                        final TransactionState transactionState = cs.getTransactionState(send.getTransactionId());
                        if (transactionState != null) {
                            transactionState.addCommand(send);
                            if (this.trackTransactionProducers) {
                                final SessionState ss = cs.getSessionState(producerId.getParentId());
                                final ProducerState producerState = ss.getProducerState(producerId);
                                producerState.setTransactionState(transactionState);
                            }
                        }
                    }
                }
                return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
            }
            if (this.trackMessages) {
                this.messageCache.put(send.getMessageId(), send);
            }
        }
        return null;
    }
    
    @Override
    public Response processBeginTransaction(final TransactionInfo info) {
        if (this.trackTransactions && info != null && info.getTransactionId() != null) {
            final ConnectionId connectionId = info.getConnectionId();
            if (connectionId != null) {
                final ConnectionState cs = this.connectionStates.get(connectionId);
                if (cs != null) {
                    cs.addTransactionState(info.getTransactionId());
                    final TransactionState state = cs.getTransactionState(info.getTransactionId());
                    state.addCommand(info);
                }
            }
            return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
        }
        return null;
    }
    
    @Override
    public Response processPrepareTransaction(final TransactionInfo info) throws Exception {
        if (this.trackTransactions && info != null && info.getTransactionId() != null) {
            final ConnectionId connectionId = info.getConnectionId();
            if (connectionId != null) {
                final ConnectionState cs = this.connectionStates.get(connectionId);
                if (cs != null) {
                    final TransactionState transactionState = cs.getTransactionState(info.getTransactionId());
                    if (transactionState != null) {
                        transactionState.addCommand(info);
                        return new Tracked(new PrepareReadonlyTransactionAction(info));
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Response processCommitTransactionOnePhase(final TransactionInfo info) throws Exception {
        if (this.trackTransactions && info != null && info.getTransactionId() != null) {
            final ConnectionId connectionId = info.getConnectionId();
            if (connectionId != null) {
                final ConnectionState cs = this.connectionStates.get(connectionId);
                if (cs != null) {
                    final TransactionState transactionState = cs.getTransactionState(info.getTransactionId());
                    if (transactionState != null) {
                        transactionState.addCommand(info);
                        return new Tracked(new RemoveTransactionAction(info));
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Response processCommitTransactionTwoPhase(final TransactionInfo info) throws Exception {
        if (this.trackTransactions && info != null && info.getTransactionId() != null) {
            final ConnectionId connectionId = info.getConnectionId();
            if (connectionId != null) {
                final ConnectionState cs = this.connectionStates.get(connectionId);
                if (cs != null) {
                    final TransactionState transactionState = cs.getTransactionState(info.getTransactionId());
                    if (transactionState != null) {
                        transactionState.addCommand(info);
                        return new Tracked(new RemoveTransactionAction(info));
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Response processRollbackTransaction(final TransactionInfo info) throws Exception {
        if (this.trackTransactions && info != null && info.getTransactionId() != null) {
            final ConnectionId connectionId = info.getConnectionId();
            if (connectionId != null) {
                final ConnectionState cs = this.connectionStates.get(connectionId);
                if (cs != null) {
                    final TransactionState transactionState = cs.getTransactionState(info.getTransactionId());
                    if (transactionState != null) {
                        transactionState.addCommand(info);
                        return new Tracked(new RemoveTransactionAction(info));
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Response processEndTransaction(final TransactionInfo info) throws Exception {
        if (this.trackTransactions && info != null && info.getTransactionId() != null) {
            final ConnectionId connectionId = info.getConnectionId();
            if (connectionId != null) {
                final ConnectionState cs = this.connectionStates.get(connectionId);
                if (cs != null) {
                    final TransactionState transactionState = cs.getTransactionState(info.getTransactionId());
                    if (transactionState != null) {
                        transactionState.addCommand(info);
                    }
                }
            }
            return ConnectionStateTracker.TRACKED_RESPONSE_MARKER;
        }
        return null;
    }
    
    @Override
    public Response processMessagePull(final MessagePull pull) throws Exception {
        if (pull != null) {
            final String id = pull.getDestination() + "::" + pull.getConsumerId();
            if (this.messageCache.put(id.intern(), pull) == null) {
                pull.setTracked(true);
            }
        }
        return null;
    }
    
    public boolean isRestoreConsumers() {
        return this.restoreConsumers;
    }
    
    public void setRestoreConsumers(final boolean restoreConsumers) {
        this.restoreConsumers = restoreConsumers;
    }
    
    public boolean isRestoreProducers() {
        return this.restoreProducers;
    }
    
    public void setRestoreProducers(final boolean restoreProducers) {
        this.restoreProducers = restoreProducers;
    }
    
    public boolean isRestoreSessions() {
        return this.restoreSessions;
    }
    
    public void setRestoreSessions(final boolean restoreSessions) {
        this.restoreSessions = restoreSessions;
    }
    
    public boolean isTrackTransactions() {
        return this.trackTransactions;
    }
    
    public void setTrackTransactions(final boolean trackTransactions) {
        this.trackTransactions = trackTransactions;
    }
    
    public boolean isTrackTransactionProducers() {
        return this.trackTransactionProducers;
    }
    
    public void setTrackTransactionProducers(final boolean trackTransactionProducers) {
        this.trackTransactionProducers = trackTransactionProducers;
    }
    
    public boolean isRestoreTransaction() {
        return this.restoreTransaction;
    }
    
    public void setRestoreTransaction(final boolean restoreTransaction) {
        this.restoreTransaction = restoreTransaction;
    }
    
    public boolean isTrackMessages() {
        return this.trackMessages;
    }
    
    public void setTrackMessages(final boolean trackMessages) {
        this.trackMessages = trackMessages;
    }
    
    public int getMaxCacheSize() {
        return this.maxCacheSize;
    }
    
    public void setMaxCacheSize(final int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
    
    public long getCurrentCacheSize() {
        return this.currentCacheSize;
    }
    
    public void connectionInterruptProcessingComplete(final Transport transport, final ConnectionId connectionId) {
        final ConnectionState connectionState = this.connectionStates.get(connectionId);
        if (connectionState != null) {
            connectionState.setConnectionInterruptProcessingComplete(true);
            final Map<ConsumerId, ConsumerInfo> stalledConsumers = connectionState.getRecoveringPullConsumers();
            for (final Map.Entry<ConsumerId, ConsumerInfo> entry : stalledConsumers.entrySet()) {
                final ConsumerControl control = new ConsumerControl();
                control.setConsumerId(entry.getKey());
                control.setPrefetch(entry.getValue().getPrefetchSize());
                control.setDestination(entry.getValue().getDestination());
                try {
                    if (ConnectionStateTracker.LOG.isDebugEnabled()) {
                        ConnectionStateTracker.LOG.debug("restored recovering consumer: " + control.getConsumerId() + " with: " + control.getPrefetch());
                    }
                    transport.oneway(control);
                }
                catch (Exception ex) {
                    if (!ConnectionStateTracker.LOG.isDebugEnabled()) {
                        continue;
                    }
                    ConnectionStateTracker.LOG.debug("Failed to submit control for consumer: " + control.getConsumerId() + " with: " + control.getPrefetch(), ex);
                }
            }
            stalledConsumers.clear();
        }
    }
    
    public void transportInterrupted(final ConnectionId connectionId) {
        final ConnectionState connectionState = this.connectionStates.get(connectionId);
        if (connectionState != null) {
            connectionState.setConnectionInterruptProcessingComplete(false);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ConnectionStateTracker.class);
        TRACKED_RESPONSE_MARKER = new Tracked(null);
    }
    
    private class RemoveTransactionAction implements ResponseHandler
    {
        private final TransactionInfo info;
        
        public RemoveTransactionAction(final TransactionInfo info) {
            this.info = info;
        }
        
        @Override
        public void onResponse(final Command response) {
            final ConnectionId connectionId = this.info.getConnectionId();
            final ConnectionState cs = ConnectionStateTracker.this.connectionStates.get(connectionId);
            if (cs != null) {
                cs.removeTransactionState(this.info.getTransactionId());
            }
        }
    }
    
    private class PrepareReadonlyTransactionAction extends RemoveTransactionAction
    {
        public PrepareReadonlyTransactionAction(final TransactionInfo info) {
            super(info);
        }
        
        @Override
        public void onResponse(final Command command) {
            if (command instanceof IntegerResponse) {
                final IntegerResponse response = (IntegerResponse)command;
                if (3 == response.getResult()) {
                    super.onResponse(command);
                }
            }
        }
    }
}
