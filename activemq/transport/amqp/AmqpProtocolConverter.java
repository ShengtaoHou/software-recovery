// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import org.apache.qpid.proton.amqp.messaging.Modified;
import org.apache.qpid.proton.amqp.messaging.Released;
import org.apache.activemq.command.MessageAck;
import java.io.UnsupportedEncodingException;
import org.apache.qpid.proton.amqp.transport.SenderSettleMode;
import java.util.HashSet;
import java.util.LinkedList;
import org.apache.qpid.proton.amqp.transaction.TransactionalState;
import org.apache.activemq.command.MessageId;
import javax.jms.Destination;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.qpid.proton.jms.EncodedMessage;
import org.apache.activemq.util.LongSequenceGenerator;
import org.fusesource.hawtbuf.ByteArrayOutputStream;
import org.apache.activemq.command.SessionId;
import org.slf4j.LoggerFactory;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.command.ActiveMQTempQueue;
import java.util.Map;
import org.apache.qpid.proton.amqp.messaging.TerminusDurability;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import javax.jms.InvalidSelectorException;
import org.apache.activemq.selector.SelectorParser;
import org.apache.qpid.proton.amqp.DescribedType;
import org.apache.qpid.proton.amqp.messaging.Source;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.ProducerId;
import org.apache.qpid.proton.amqp.messaging.Target;
import org.apache.qpid.proton.amqp.transaction.Coordinator;
import org.apache.qpid.proton.jms.AMQPRawInboundTransformer;
import org.apache.qpid.proton.jms.AMQPNativeInboundTransformer;
import org.apache.qpid.proton.jms.JMSMappingInboundTransformer;
import org.apache.qpid.proton.engine.Sender;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.util.IOExceptionSupport;
import javax.jms.InvalidClientIDException;
import org.apache.qpid.proton.amqp.transport.ErrorCondition;
import org.apache.qpid.proton.amqp.transport.AmqpError;
import org.apache.activemq.command.ConnectionError;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.ShutdownInfo;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.RemoveInfo;
import org.apache.qpid.proton.engine.Session;
import org.apache.qpid.proton.engine.EndpointState;
import org.apache.qpid.proton.engine.Link;
import org.apache.qpid.proton.engine.Event;
import java.nio.ByteBuffer;
import org.apache.qpid.proton.framing.TransportFrame;
import org.apache.qpid.proton.engine.impl.ProtocolTracer;
import org.apache.qpid.proton.engine.impl.TransportImpl;
import org.apache.qpid.proton.jms.JMSVendor;
import org.apache.qpid.proton.jms.AutoOutboundTransformer;
import java.util.Iterator;
import org.apache.qpid.proton.message.Message;
import java.io.IOException;
import org.apache.qpid.proton.amqp.messaging.Accepted;
import org.apache.qpid.proton.amqp.messaging.Rejected;
import org.apache.activemq.command.ExceptionResponse;
import org.apache.activemq.command.Response;
import org.apache.qpid.proton.amqp.transaction.Discharge;
import org.apache.qpid.proton.amqp.transport.DeliveryState;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.transaction.Declared;
import org.apache.activemq.command.Command;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.TransactionInfo;
import org.apache.activemq.command.LocalTransactionId;
import org.apache.qpid.proton.amqp.transaction.Declare;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.fusesource.hawtbuf.Buffer;
import org.apache.qpid.proton.engine.Delivery;
import org.apache.qpid.proton.engine.Receiver;
import org.apache.qpid.proton.engine.impl.CollectorImpl;
import org.apache.qpid.proton.engine.impl.EngineFactoryImpl;
import org.apache.activemq.command.ConsumerId;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.qpid.proton.jms.OutboundTransformer;
import java.util.HashMap;
import org.apache.qpid.proton.jms.InboundTransformer;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.util.IdGenerator;
import org.apache.qpid.proton.engine.Sasl;
import org.apache.qpid.proton.engine.Collector;
import org.apache.qpid.proton.engine.Connection;
import org.apache.qpid.proton.engine.Transport;
import org.apache.qpid.proton.engine.EngineFactory;
import org.apache.qpid.proton.message.MessageFactory;
import org.apache.qpid.proton.ProtonFactoryLoader;
import org.apache.qpid.proton.amqp.Symbol;
import org.slf4j.Logger;

class AmqpProtocolConverter implements IAmqpProtocolConverter
{
    private static final Logger TRACE_FRAMES;
    private static final Logger LOG;
    private static final byte[] EMPTY_BYTE_ARRAY;
    private final AmqpTransport amqpTransport;
    private static final Symbol COPY;
    private static final Symbol JMS_SELECTOR;
    private static final Symbol NO_LOCAL;
    private static final Symbol DURABLE_SUBSCRIPTION_ENDED;
    private static final ProtonFactoryLoader<MessageFactory> messageFactoryLoader;
    protected int prefetch;
    protected EngineFactory engineFactory;
    protected Transport protonTransport;
    protected Connection protonConnection;
    protected MessageFactory messageFactory;
    protected Collector eventCollector;
    Sasl sasl;
    boolean closing;
    boolean closedSocket;
    private static final IdGenerator CONNECTION_ID_GENERATOR;
    private final ConnectionId connectionId;
    private final ConnectionInfo connectionInfo;
    private long nextSessionId;
    private long nextTempDestinationId;
    InboundTransformer inboundTransformer;
    long nextTransactionId;
    HashMap<Long, Transaction> transactions;
    AmqpDeliveryListener coordinatorContext;
    OutboundTransformer outboundTransformer;
    private final ConcurrentHashMap<ConsumerId, ConsumerContext> subscriptionsByConsumerId;
    private final Object commnadIdMutex;
    private int lastCommandId;
    private final ConcurrentHashMap<Integer, ResponseHandler> resposeHandlers;
    
    public AmqpProtocolConverter(final AmqpTransport transport) {
        this.prefetch = 100;
        this.engineFactory = (EngineFactory)new EngineFactoryImpl();
        this.protonTransport = this.engineFactory.createTransport();
        this.protonConnection = this.engineFactory.createConnection();
        this.messageFactory = (MessageFactory)AmqpProtocolConverter.messageFactoryLoader.loadFactory();
        this.eventCollector = (Collector)new CollectorImpl();
        this.closing = false;
        this.closedSocket = false;
        this.connectionId = new ConnectionId(AmqpProtocolConverter.CONNECTION_ID_GENERATOR.generateId());
        this.connectionInfo = new ConnectionInfo();
        this.nextSessionId = 0L;
        this.nextTempDestinationId = 0L;
        this.nextTransactionId = 1L;
        this.transactions = new HashMap<Long, Transaction>();
        this.coordinatorContext = new BaseProducerContext() {
            @Override
            protected void onMessage(final Receiver receiver, final Delivery delivery, final Buffer buffer) throws Exception {
                final Message msg = AmqpProtocolConverter.this.messageFactory.createMessage();
                int offset = buffer.offset;
                int decoded;
                for (int len = buffer.length; len > 0; len -= decoded) {
                    decoded = msg.decode(buffer.data, offset, len);
                    assert decoded > 0 : "Make progress decoding the message";
                    offset += decoded;
                }
                final Object action = ((AmqpValue)msg.getBody()).getValue();
                AmqpProtocolConverter.LOG.debug("COORDINATOR received: {}, [{}]", action, buffer);
                if (action instanceof Declare) {
                    final Declare declare = (Declare)action;
                    if (declare.getGlobalId() != null) {
                        throw new Exception("don't know how to handle a declare /w a set GlobalId");
                    }
                    final long txid = AmqpProtocolConverter.this.nextTransactionId++;
                    final TransactionInfo txinfo = new TransactionInfo(AmqpProtocolConverter.this.connectionId, new LocalTransactionId(AmqpProtocolConverter.this.connectionId, txid), (byte)0);
                    AmqpProtocolConverter.this.sendToActiveMQ(txinfo, null);
                    AmqpProtocolConverter.LOG.trace("started transaction {}", (Object)txid);
                    final Declared declared = new Declared();
                    declared.setTxnId(new Binary(AmqpProtocolConverter.this.toBytes(txid)));
                    delivery.disposition((DeliveryState)declared);
                    delivery.settle();
                }
                else {
                    if (!(action instanceof Discharge)) {
                        throw new Exception("Expected coordinator message type: " + action.getClass());
                    }
                    final Discharge discharge = (Discharge)action;
                    final long txid = AmqpProtocolConverter.this.toLong(discharge.getTxnId());
                    byte operation;
                    if (discharge.getFail()) {
                        AmqpProtocolConverter.LOG.trace("rollback transaction {}", (Object)txid);
                        operation = 4;
                    }
                    else {
                        AmqpProtocolConverter.LOG.trace("commit transaction {}", (Object)txid);
                        operation = 2;
                    }
                    final AmqpSessionContext context = (AmqpSessionContext)receiver.getSession().getContext();
                    for (final ConsumerContext consumer : context.consumers.values()) {
                        if (operation == 4) {
                            consumer.doRollback();
                        }
                        else {
                            consumer.doCommit();
                        }
                    }
                    final TransactionInfo txinfo2 = new TransactionInfo(AmqpProtocolConverter.this.connectionId, new LocalTransactionId(AmqpProtocolConverter.this.connectionId, txid), operation);
                    AmqpProtocolConverter.this.sendToActiveMQ(txinfo2, new ResponseHandler() {
                        @Override
                        public void onResponse(final IAmqpProtocolConverter converter, final Response response) throws IOException {
                            if (response.isException()) {
                                final ExceptionResponse er = (ExceptionResponse)response;
                                final Rejected rejected = new Rejected();
                                rejected.setError(AmqpProtocolConverter.this.createErrorCondition("failed", er.getException().getMessage()));
                                delivery.disposition((DeliveryState)rejected);
                            }
                            else {
                                delivery.disposition((DeliveryState)Accepted.getInstance());
                            }
                            AmqpProtocolConverter.LOG.debug("TX: {} settling {}", (Object)operation, action);
                            delivery.settle();
                            AmqpProtocolConverter.this.pumpProtonToSocket();
                        }
                    });
                    for (final ConsumerContext consumer2 : context.consumers.values()) {
                        if (operation == 4) {
                            consumer2.pumpOutbound();
                        }
                    }
                }
            }
        };
        this.outboundTransformer = (OutboundTransformer)new AutoOutboundTransformer((JMSVendor)ActiveMQJMSVendor.INSTANCE);
        this.subscriptionsByConsumerId = new ConcurrentHashMap<ConsumerId, ConsumerContext>();
        this.commnadIdMutex = new Object();
        this.resposeHandlers = new ConcurrentHashMap<Integer, ResponseHandler>();
        this.amqpTransport = transport;
        final int maxFrameSize = 1048576;
        this.protonTransport.setMaxFrameSize(maxFrameSize);
        this.protonTransport.bind(this.protonConnection);
        this.protonConnection.collect(this.eventCollector);
        this.updateTracer();
    }
    
    @Override
    public void updateTracer() {
        if (this.amqpTransport.isTrace()) {
            ((TransportImpl)this.protonTransport).setProtocolTracer((ProtocolTracer)new ProtocolTracer() {
                public void receivedFrame(final TransportFrame transportFrame) {
                    AmqpProtocolConverter.TRACE_FRAMES.trace("{} | RECV: {}", AmqpProtocolConverter.this.amqpTransport.getRemoteAddress(), transportFrame.getBody());
                }
                
                public void sentFrame(final TransportFrame transportFrame) {
                    AmqpProtocolConverter.TRACE_FRAMES.trace("{} | SENT: {}", AmqpProtocolConverter.this.amqpTransport.getRemoteAddress(), transportFrame.getBody());
                }
            });
        }
    }
    
    void pumpProtonToSocket() {
        try {
            boolean done = false;
            while (!done) {
                final ByteBuffer toWrite = this.protonTransport.getOutputBuffer();
                if (toWrite != null && toWrite.hasRemaining()) {
                    AmqpProtocolConverter.LOG.trace("Sending {} bytes out", (Object)toWrite.limit());
                    this.amqpTransport.sendToAmqp(toWrite);
                    this.protonTransport.outputConsumed();
                }
                else {
                    done = true;
                }
            }
        }
        catch (IOException e) {
            this.amqpTransport.onException(e);
        }
    }
    
    @Override
    public void onAMQPData(final Object command) throws Exception {
        Buffer frame;
        if (command.getClass() == AmqpHeader.class) {
            final AmqpHeader header = (AmqpHeader)command;
            switch (header.getProtocolId()) {
                case 3: {
                    (this.sasl = this.protonTransport.sasl()).setMechanisms(new String[] { "ANONYMOUS", "PLAIN" });
                    this.sasl.server();
                    break;
                }
            }
            frame = header.getBuffer();
        }
        else {
            frame = (Buffer)command;
        }
        this.onFrame(frame);
    }
    
    public void onFrame(final Buffer frame) throws Exception {
        while (frame.length > 0) {
            try {
                final int count = this.protonTransport.input(frame.data, frame.offset, frame.length);
                frame.moveHead(count);
            }
            catch (Throwable e) {
                this.handleException(new AmqpProtocolException("Could not decode AMQP frame: " + frame, true, e));
                return;
            }
            try {
                if (this.sasl != null && this.sasl.getRemoteMechanisms().length > 0) {
                    if ("PLAIN".equals(this.sasl.getRemoteMechanisms()[0])) {
                        final byte[] data = new byte[this.sasl.pending()];
                        this.sasl.recv(data, 0, data.length);
                        final Buffer[] parts = new Buffer(data).split((byte)0);
                        if (parts.length > 0) {
                            this.connectionInfo.setUserName(parts[0].utf8().toString());
                        }
                        if (parts.length > 1) {
                            this.connectionInfo.setPassword(parts[1].utf8().toString());
                        }
                        this.sasl.done(Sasl.SaslOutcome.PN_SASL_OK);
                        this.amqpTransport.getWireFormat().magicRead = false;
                        this.sasl = null;
                        AmqpProtocolConverter.LOG.debug("SASL [PLAIN] Handshake complete.");
                    }
                    else if ("ANONYMOUS".equals(this.sasl.getRemoteMechanisms()[0])) {
                        this.sasl.done(Sasl.SaslOutcome.PN_SASL_OK);
                        this.amqpTransport.getWireFormat().magicRead = false;
                        this.sasl = null;
                        AmqpProtocolConverter.LOG.debug("SASL [ANONYMOUS] Handshake complete.");
                    }
                }
                Event event = null;
                while ((event = this.eventCollector.peek()) != null) {
                    switch (event.getType()) {
                        case CONNECTION_REMOTE_STATE: {
                            this.processConnectionEvent(event.getConnection());
                            break;
                        }
                        case SESSION_REMOTE_STATE: {
                            this.processSessionEvent(event.getSession());
                            break;
                        }
                        case LINK_REMOTE_STATE: {
                            this.processLinkEvent(event.getLink());
                            break;
                        }
                        case LINK_FLOW: {
                            final Link link = event.getLink();
                            ((AmqpDeliveryListener)link.getContext()).drainCheck();
                            break;
                        }
                        case DELIVERY: {
                            this.processDelivery(event.getDelivery());
                            break;
                        }
                    }
                    this.eventCollector.pop();
                }
            }
            catch (Throwable e) {
                this.handleException(new AmqpProtocolException("Could not process AMQP commands", true, e));
            }
            this.pumpProtonToSocket();
        }
    }
    
    protected void processConnectionEvent(final Connection connection) throws Exception {
        final EndpointState remoteState = connection.getRemoteState();
        if (remoteState == EndpointState.ACTIVE) {
            this.onConnectionOpen();
        }
        else if (remoteState == EndpointState.CLOSED) {
            this.doClose();
        }
    }
    
    protected void processLinkEvent(final Link link) throws Exception {
        final EndpointState remoteState = link.getRemoteState();
        if (remoteState == EndpointState.ACTIVE) {
            this.onLinkOpen(link);
        }
        else if (remoteState == EndpointState.CLOSED) {
            ((AmqpDeliveryListener)link.getContext()).onClose();
            link.close();
        }
    }
    
    protected void processSessionEvent(final Session session) throws Exception {
        final EndpointState remoteState = session.getRemoteState();
        if (remoteState == EndpointState.ACTIVE) {
            this.onSessionOpen(session);
        }
        else if (remoteState == EndpointState.CLOSED) {
            this.onSessionClose(session);
        }
    }
    
    protected void processDelivery(final Delivery delivery) throws Exception {
        if (!delivery.isPartial()) {
            final AmqpDeliveryListener listener = (AmqpDeliveryListener)delivery.getLink().getContext();
            if (listener != null) {
                listener.onDelivery(delivery);
            }
        }
    }
    
    private void doClose() {
        if (!this.closing) {
            this.closing = true;
            this.sendToActiveMQ(new RemoveInfo(this.connectionId), new ResponseHandler() {
                @Override
                public void onResponse(final IAmqpProtocolConverter converter, final Response response) throws IOException {
                    AmqpProtocolConverter.this.protonConnection.close();
                    if (!AmqpProtocolConverter.this.closedSocket) {
                        AmqpProtocolConverter.this.pumpProtonToSocket();
                    }
                }
            });
            this.sendToActiveMQ(new ShutdownInfo(), null);
        }
    }
    
    @Override
    public void onAMQPException(final IOException error) {
        this.closedSocket = true;
        if (!this.closing) {
            this.amqpTransport.sendToActiveMQ(error);
        }
        else {
            try {
                this.amqpTransport.stop();
            }
            catch (Exception ex) {}
        }
    }
    
    @Override
    public void onActiveMQCommand(final Command command) throws Exception {
        if (command.isResponse()) {
            final Response response = (Response)command;
            final ResponseHandler rh = this.resposeHandlers.remove(response.getCorrelationId());
            if (rh != null) {
                rh.onResponse(this, response);
            }
            else if (response.isException()) {
                final Throwable exception = ((ExceptionResponse)response).getException();
                this.handleException(exception);
            }
        }
        else if (command.isMessageDispatch()) {
            final MessageDispatch md = (MessageDispatch)command;
            final ConsumerContext consumerContext = this.subscriptionsByConsumerId.get(md.getConsumerId());
            if (consumerContext != null) {
                if (md.getMessage() != null) {
                    AmqpProtocolConverter.LOG.trace("Dispatching MessageId: {} to consumer", md.getMessage().getMessageId());
                }
                else {
                    AmqpProtocolConverter.LOG.trace("Dispatching End of Browse Command to consumer {}", md.getConsumerId());
                }
                consumerContext.onMessageDispatch(md);
                if (md.getMessage() != null) {
                    AmqpProtocolConverter.LOG.trace("Finished Dispatch of MessageId: {} to consumer", md.getMessage().getMessageId());
                }
            }
        }
        else if (command.getDataStructureType() == 16) {
            final Throwable exception2 = ((ConnectionError)command).getException();
            this.handleException(exception2);
        }
        else if (!command.isBrokerInfo()) {
            AmqpProtocolConverter.LOG.debug("Do not know how to process ActiveMQ Command {}", command);
        }
    }
    
    private void onConnectionOpen() throws AmqpProtocolException {
        this.connectionInfo.setResponseRequired(true);
        this.connectionInfo.setConnectionId(this.connectionId);
        final String clientId = this.protonConnection.getRemoteContainer();
        if (clientId != null && !clientId.isEmpty()) {
            this.connectionInfo.setClientId(clientId);
        }
        this.connectionInfo.setTransportContext(this.amqpTransport.getPeerCertificates());
        this.sendToActiveMQ(this.connectionInfo, new ResponseHandler() {
            @Override
            public void onResponse(final IAmqpProtocolConverter converter, final Response response) throws IOException {
                AmqpProtocolConverter.this.protonConnection.open();
                AmqpProtocolConverter.this.pumpProtonToSocket();
                if (response.isException()) {
                    final Throwable exception = ((ExceptionResponse)response).getException();
                    if (exception instanceof SecurityException) {
                        AmqpProtocolConverter.this.protonConnection.setCondition(new ErrorCondition(AmqpError.UNAUTHORIZED_ACCESS, exception.getMessage()));
                    }
                    else if (exception instanceof InvalidClientIDException) {
                        AmqpProtocolConverter.this.protonConnection.setCondition(new ErrorCondition(AmqpError.INVALID_FIELD, exception.getMessage()));
                    }
                    else {
                        AmqpProtocolConverter.this.protonConnection.setCondition(new ErrorCondition(AmqpError.ILLEGAL_STATE, exception.getMessage()));
                    }
                    AmqpProtocolConverter.this.protonConnection.close();
                    AmqpProtocolConverter.this.pumpProtonToSocket();
                    AmqpProtocolConverter.this.amqpTransport.onException(IOExceptionSupport.create(exception));
                }
            }
        });
    }
    
    private void onSessionOpen(final Session session) {
        final AmqpSessionContext sessionContext = new AmqpSessionContext(this.connectionId, this.nextSessionId++);
        session.setContext((Object)sessionContext);
        this.sendToActiveMQ(new SessionInfo(sessionContext.sessionId), null);
        session.setIncomingCapacity(Integer.MAX_VALUE);
        session.open();
    }
    
    private void onSessionClose(final Session session) {
        final AmqpSessionContext sessionContext = (AmqpSessionContext)session.getContext();
        if (sessionContext != null) {
            AmqpProtocolConverter.LOG.trace("Session {} closed", sessionContext.sessionId);
            this.sendToActiveMQ(new RemoveInfo(sessionContext.sessionId), null);
            session.setContext((Object)null);
        }
        session.close();
    }
    
    private void onLinkOpen(final Link link) {
        link.setSource(link.getRemoteSource());
        link.setTarget(link.getRemoteTarget());
        final AmqpSessionContext sessionContext = (AmqpSessionContext)link.getSession().getContext();
        if (link instanceof Receiver) {
            this.onReceiverOpen((Receiver)link, sessionContext);
        }
        else {
            this.onSenderOpen((Sender)link, sessionContext);
        }
    }
    
    protected InboundTransformer getInboundTransformer() {
        if (this.inboundTransformer == null) {
            final String transformer = this.amqpTransport.getTransformer();
            if (transformer.equals("jms")) {
                this.inboundTransformer = (InboundTransformer)new JMSMappingInboundTransformer((JMSVendor)ActiveMQJMSVendor.INSTANCE);
            }
            else if (transformer.equals("native")) {
                this.inboundTransformer = (InboundTransformer)new AMQPNativeInboundTransformer((JMSVendor)ActiveMQJMSVendor.INSTANCE);
            }
            else if (transformer.equals("raw")) {
                this.inboundTransformer = (InboundTransformer)new AMQPRawInboundTransformer((JMSVendor)ActiveMQJMSVendor.INSTANCE);
            }
            else {
                AmqpProtocolConverter.LOG.warn("Unknown transformer type {} using native one instead", transformer);
                this.inboundTransformer = (InboundTransformer)new AMQPNativeInboundTransformer((JMSVendor)ActiveMQJMSVendor.INSTANCE);
            }
        }
        return this.inboundTransformer;
    }
    
    public byte[] toBytes(final long value) {
        final Buffer buffer = new Buffer(8);
        buffer.bigEndianEditor().writeLong(value);
        return buffer.data;
    }
    
    private long toLong(final Binary value) {
        final Buffer buffer = new Buffer(value.getArray(), value.getArrayOffset(), value.getLength());
        return buffer.bigEndianEditor().readLong();
    }
    
    void onReceiverOpen(final Receiver receiver, final AmqpSessionContext sessionContext) {
        final org.apache.qpid.proton.amqp.transport.Target remoteTarget = receiver.getRemoteTarget();
        try {
            if (remoteTarget instanceof Coordinator) {
                this.pumpProtonToSocket();
                receiver.setContext((Object)this.coordinatorContext);
                receiver.flow(this.prefetch);
                receiver.open();
                this.pumpProtonToSocket();
            }
            else {
                final Target target = (Target)remoteTarget;
                final ProducerId producerId = new ProducerId(sessionContext.sessionId, sessionContext.nextProducerId++);
                ActiveMQDestination dest;
                if (target.getDynamic()) {
                    dest = this.createTempQueue();
                    final Target actualTarget = new Target();
                    actualTarget.setAddress(dest.getQualifiedName());
                    actualTarget.setDynamic(true);
                    receiver.setTarget((org.apache.qpid.proton.amqp.transport.Target)actualTarget);
                }
                else {
                    dest = this.createDestination(remoteTarget);
                }
                final ProducerContext producerContext = new ProducerContext(producerId, dest);
                receiver.setContext((Object)producerContext);
                receiver.flow(this.prefetch);
                final ProducerInfo producerInfo = new ProducerInfo(producerId);
                producerInfo.setDestination(dest);
                this.sendToActiveMQ(producerInfo, new ResponseHandler() {
                    @Override
                    public void onResponse(final IAmqpProtocolConverter converter, final Response response) throws IOException {
                        if (response.isException()) {
                            receiver.setTarget((org.apache.qpid.proton.amqp.transport.Target)null);
                            final Throwable exception = ((ExceptionResponse)response).getException();
                            if (exception instanceof SecurityException) {
                                receiver.setCondition(new ErrorCondition(AmqpError.UNAUTHORIZED_ACCESS, exception.getMessage()));
                            }
                            else {
                                receiver.setCondition(new ErrorCondition(AmqpError.INTERNAL_ERROR, exception.getMessage()));
                            }
                            receiver.close();
                        }
                        else {
                            receiver.open();
                        }
                        AmqpProtocolConverter.this.pumpProtonToSocket();
                    }
                });
            }
        }
        catch (AmqpProtocolException exception) {
            receiver.setTarget((org.apache.qpid.proton.amqp.transport.Target)null);
            receiver.setCondition(new ErrorCondition(Symbol.getSymbol(exception.getSymbolicName()), exception.getMessage()));
            receiver.close();
        }
    }
    
    private ActiveMQDestination createDestination(final Object terminus) throws AmqpProtocolException {
        if (terminus == null) {
            return null;
        }
        if (terminus instanceof Source) {
            final Source source = (Source)terminus;
            if (source.getAddress() == null || source.getAddress().length() == 0) {
                throw new AmqpProtocolException("amqp:invalid-field", "source address not set");
            }
            return ActiveMQDestination.createDestination(source.getAddress(), (byte)1);
        }
        else if (terminus instanceof Target) {
            final Target target = (Target)terminus;
            if (target.getAddress() == null || target.getAddress().length() == 0) {
                throw new AmqpProtocolException("amqp:invalid-field", "target address not set");
            }
            return ActiveMQDestination.createDestination(target.getAddress(), (byte)1);
        }
        else {
            if (terminus instanceof Coordinator) {
                return null;
            }
            throw new RuntimeException("Unexpected terminus type: " + terminus);
        }
    }
    
    void onSenderOpen(final Sender sender, final AmqpSessionContext sessionContext) {
        Source source = (Source)sender.getRemoteSource();
        try {
            final ConsumerId id = new ConsumerId(sessionContext.sessionId, sessionContext.nextConsumerId++);
            final ConsumerContext consumerContext = new ConsumerContext(id, sender);
            sender.setContext((Object)consumerContext);
            String selector = null;
            if (source != null) {
                final Map filter = source.getFilter();
                if (filter != null) {
                    final DescribedType value = filter.get(AmqpProtocolConverter.JMS_SELECTOR);
                    if (value != null) {
                        selector = value.getDescribed().toString();
                        try {
                            SelectorParser.parse(selector);
                        }
                        catch (InvalidSelectorException e) {
                            sender.setSource((org.apache.qpid.proton.amqp.transport.Source)null);
                            sender.setCondition(new ErrorCondition(AmqpError.INVALID_FIELD, e.getMessage()));
                            sender.close();
                            consumerContext.closed = true;
                            return;
                        }
                    }
                }
            }
            if (source == null) {
                source = new Source();
                source.setAddress("");
                source.setCapabilities(new Symbol[] { AmqpProtocolConverter.DURABLE_SUBSCRIPTION_ENDED });
                sender.setSource((org.apache.qpid.proton.amqp.transport.Source)source);
                final RemoveSubscriptionInfo rsi = new RemoveSubscriptionInfo();
                rsi.setConnectionId(this.connectionId);
                rsi.setSubscriptionName(sender.getName());
                rsi.setClientId(this.connectionInfo.getClientId());
                consumerContext.closed = true;
                this.sendToActiveMQ(rsi, new ResponseHandler() {
                    @Override
                    public void onResponse(final IAmqpProtocolConverter converter, final Response response) throws IOException {
                        if (response.isException()) {
                            sender.setSource((org.apache.qpid.proton.amqp.transport.Source)null);
                            final Throwable exception = ((ExceptionResponse)response).getException();
                            if (exception instanceof SecurityException) {
                                sender.setCondition(new ErrorCondition(AmqpError.UNAUTHORIZED_ACCESS, exception.getMessage()));
                            }
                            else {
                                sender.setCondition(new ErrorCondition(AmqpError.INTERNAL_ERROR, exception.getMessage()));
                            }
                        }
                        sender.open();
                        AmqpProtocolConverter.this.pumpProtonToSocket();
                    }
                });
                return;
            }
            if (contains(source.getCapabilities(), AmqpProtocolConverter.DURABLE_SUBSCRIPTION_ENDED)) {
                consumerContext.closed = true;
                sender.close();
                this.pumpProtonToSocket();
                return;
            }
            ActiveMQDestination dest;
            if (source.getDynamic()) {
                dest = this.createTempQueue();
                source = new Source();
                source.setAddress(dest.getQualifiedName());
                source.setDynamic(true);
                sender.setSource((org.apache.qpid.proton.amqp.transport.Source)source);
            }
            else {
                dest = this.createDestination(source);
            }
            this.subscriptionsByConsumerId.put(id, consumerContext);
            final ConsumerInfo consumerInfo = new ConsumerInfo(id);
            (consumerContext.info = consumerInfo).setSelector(selector);
            consumerInfo.setNoRangeAcks(true);
            consumerInfo.setDestination(dest);
            consumerInfo.setPrefetchSize(100);
            consumerInfo.setDispatchAsync(true);
            if (source.getDistributionMode() == AmqpProtocolConverter.COPY && dest.isQueue()) {
                consumerInfo.setBrowser(true);
            }
            if (TerminusDurability.UNSETTLED_STATE.equals((Object)source.getDurable()) && dest.isTopic()) {
                consumerInfo.setSubscriptionName(sender.getName());
            }
            final Map filter2 = source.getFilter();
            if (filter2 != null) {
                final DescribedType value2 = filter2.get(AmqpProtocolConverter.NO_LOCAL);
                if (value2 != null) {
                    consumerInfo.setNoLocal(true);
                }
            }
            this.sendToActiveMQ(consumerInfo, new ResponseHandler() {
                @Override
                public void onResponse(final IAmqpProtocolConverter converter, final Response response) throws IOException {
                    if (response.isException()) {
                        sender.setSource((org.apache.qpid.proton.amqp.transport.Source)null);
                        final Throwable exception = ((ExceptionResponse)response).getException();
                        if (exception instanceof SecurityException) {
                            sender.setCondition(new ErrorCondition(AmqpError.UNAUTHORIZED_ACCESS, exception.getMessage()));
                        }
                        else if (exception instanceof InvalidSelectorException) {
                            sender.setCondition(new ErrorCondition(AmqpError.INVALID_FIELD, exception.getMessage()));
                        }
                        else {
                            sender.setCondition(new ErrorCondition(AmqpError.INTERNAL_ERROR, exception.getMessage()));
                        }
                        AmqpProtocolConverter.this.subscriptionsByConsumerId.remove(id);
                        sender.close();
                    }
                    else {
                        sessionContext.consumers.put(id, consumerContext);
                        sender.open();
                    }
                    AmqpProtocolConverter.this.pumpProtonToSocket();
                }
            });
        }
        catch (AmqpProtocolException e2) {
            sender.setSource((org.apache.qpid.proton.amqp.transport.Source)null);
            sender.setCondition(new ErrorCondition(Symbol.getSymbol(e2.getSymbolicName()), e2.getMessage()));
            sender.close();
        }
    }
    
    private static boolean contains(final Symbol[] haystack, final Symbol needle) {
        if (haystack != null) {
            for (final Symbol capability : haystack) {
                if (capability == needle) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private ActiveMQDestination createTempQueue() {
        final ActiveMQDestination rc = new ActiveMQTempQueue(this.connectionId, this.nextTempDestinationId++);
        final DestinationInfo info = new DestinationInfo();
        info.setConnectionId(this.connectionId);
        info.setOperationType((byte)0);
        info.setDestination(rc);
        this.sendToActiveMQ(info, null);
        return rc;
    }
    
    int generateCommandId() {
        synchronized (this.commnadIdMutex) {
            return this.lastCommandId++;
        }
    }
    
    void sendToActiveMQ(final Command command, final ResponseHandler handler) {
        command.setCommandId(this.generateCommandId());
        if (handler != null) {
            command.setResponseRequired(true);
            this.resposeHandlers.put(command.getCommandId(), handler);
        }
        this.amqpTransport.sendToActiveMQ(command);
    }
    
    void handleException(final Throwable exception) {
        exception.printStackTrace();
        AmqpProtocolConverter.LOG.debug("Exception detail", exception);
        try {
            this.amqpTransport.stop();
        }
        catch (Throwable e) {
            AmqpProtocolConverter.LOG.error("Failed to stop AMQP Transport ", e);
        }
    }
    
    ErrorCondition createErrorCondition(final String name) {
        return this.createErrorCondition(name, "");
    }
    
    ErrorCondition createErrorCondition(final String name, final String description) {
        final ErrorCondition condition = new ErrorCondition();
        condition.setCondition(Symbol.valueOf(name));
        condition.setDescription(description);
        return condition;
    }
    
    static {
        TRACE_FRAMES = AmqpTransportFilter.TRACE_FRAMES;
        LOG = LoggerFactory.getLogger(AmqpProtocolConverter.class);
        EMPTY_BYTE_ARRAY = new byte[0];
        COPY = Symbol.getSymbol("copy");
        JMS_SELECTOR = Symbol.valueOf("jms-selector");
        NO_LOCAL = Symbol.valueOf("no-local");
        DURABLE_SUBSCRIPTION_ENDED = Symbol.getSymbol("DURABLE_SUBSCRIPTION_ENDED");
        messageFactoryLoader = new ProtonFactoryLoader((Class)MessageFactory.class);
        CONNECTION_ID_GENERATOR = new IdGenerator();
    }
    
    static class AmqpSessionContext
    {
        private final SessionId sessionId;
        long nextProducerId;
        long nextConsumerId;
        final Map<ConsumerId, ConsumerContext> consumers;
        
        public AmqpSessionContext(final ConnectionId connectionId, final long id) {
            this.nextProducerId = 0L;
            this.nextConsumerId = 0L;
            this.consumers = new HashMap<ConsumerId, ConsumerContext>();
            this.sessionId = new SessionId(connectionId, id);
        }
    }
    
    abstract static class AmqpDeliveryListener
    {
        public abstract void onDelivery(final Delivery p0) throws Exception;
        
        public void onClose() throws Exception {
        }
        
        public void drainCheck() {
        }
        
        abstract void doCommit() throws Exception;
        
        abstract void doRollback() throws Exception;
    }
    
    abstract class BaseProducerContext extends AmqpDeliveryListener
    {
        ByteArrayOutputStream current;
        private final byte[] recvBuffer;
        
        BaseProducerContext() {
            this.current = new ByteArrayOutputStream();
            this.recvBuffer = new byte[8192];
        }
        
        @Override
        public void onDelivery(final Delivery delivery) throws Exception {
            final Receiver receiver = (Receiver)delivery.getLink();
            if (!delivery.isReadable()) {
                AmqpProtocolConverter.LOG.debug("Delivery was not readable!");
                return;
            }
            if (this.current == null) {
                this.current = new ByteArrayOutputStream();
            }
            int count;
            while ((count = receiver.recv(this.recvBuffer, 0, this.recvBuffer.length)) > 0) {
                this.current.write(this.recvBuffer, 0, count);
            }
            if (count == 0) {
                return;
            }
            receiver.advance();
            final Buffer buffer = this.current.toBuffer();
            this.current = null;
            this.onMessage(receiver, delivery, buffer);
        }
        
        @Override
        void doCommit() throws Exception {
        }
        
        @Override
        void doRollback() throws Exception {
        }
        
        protected abstract void onMessage(final Receiver p0, final Delivery p1, final Buffer p2) throws Exception;
    }
    
    class ProducerContext extends BaseProducerContext
    {
        private final ProducerId producerId;
        private final LongSequenceGenerator messageIdGenerator;
        private final ActiveMQDestination destination;
        private boolean closed;
        
        public ProducerContext(final ProducerId producerId, final ActiveMQDestination destination) {
            this.messageIdGenerator = new LongSequenceGenerator();
            this.producerId = producerId;
            this.destination = destination;
        }
        
        @Override
        protected void onMessage(final Receiver receiver, final Delivery delivery, final Buffer buffer) throws Exception {
            if (!this.closed) {
                final EncodedMessage em = new EncodedMessage((long)delivery.getMessageFormat(), buffer.data, buffer.offset, buffer.length);
                final ActiveMQMessage message = (ActiveMQMessage)AmqpProtocolConverter.this.getInboundTransformer().transform(em);
                if (message.getJMSReplyTo() != null && message.getJMSReplyTo() instanceof ActiveMQTempTopic) {
                    final ActiveMQTempTopic tempTopic = (ActiveMQTempTopic)message.getJMSReplyTo();
                    message.setJMSReplyTo(new ActiveMQTempQueue(tempTopic.getPhysicalName()));
                }
                this.current = null;
                if (this.destination != null) {
                    message.setJMSDestination(this.destination);
                }
                message.setProducerId(this.producerId);
                final MessageId messageId = new MessageId(this.producerId, this.messageIdGenerator.getNextSequenceId());
                final MessageId amqpMessageId = message.getMessageId();
                if (amqpMessageId != null) {
                    if (amqpMessageId.getTextView() != null) {
                        messageId.setTextView(amqpMessageId.getTextView());
                    }
                    else {
                        messageId.setTextView(amqpMessageId.toString());
                    }
                }
                message.setMessageId(messageId);
                AmqpProtocolConverter.LOG.trace("Inbound Message:{} from Producer:{}", message.getMessageId(), this.producerId + ":" + messageId.getProducerSequenceId());
                final DeliveryState remoteState = delivery.getRemoteState();
                if (remoteState != null && remoteState instanceof TransactionalState) {
                    final TransactionalState s = (TransactionalState)remoteState;
                    final long txid = AmqpProtocolConverter.this.toLong(s.getTxnId());
                    message.setTransactionId(new LocalTransactionId(AmqpProtocolConverter.this.connectionId, txid));
                }
                if (message.getExpiration() != 0L && message.getTimestamp() == 0L) {
                    message.setTimestamp(System.currentTimeMillis());
                    message.setExpiration(message.getTimestamp() + message.getExpiration());
                }
                message.onSend();
                if (!delivery.remotelySettled()) {
                    AmqpProtocolConverter.this.sendToActiveMQ(message, new ResponseHandler() {
                        @Override
                        public void onResponse(final IAmqpProtocolConverter converter, final Response response) throws IOException {
                            if (response.isException()) {
                                final ExceptionResponse er = (ExceptionResponse)response;
                                final Rejected rejected = new Rejected();
                                final ErrorCondition condition = new ErrorCondition();
                                condition.setCondition(Symbol.valueOf("failed"));
                                condition.setDescription(er.getException().getMessage());
                                rejected.setError(condition);
                                delivery.disposition((DeliveryState)rejected);
                            }
                            else {
                                if (receiver.getCredit() <= AmqpProtocolConverter.this.prefetch * 0.2) {
                                    AmqpProtocolConverter.LOG.trace("Sending more credit ({}) to producer: {}", (Object)(AmqpProtocolConverter.this.prefetch - receiver.getCredit()), ProducerContext.this.producerId);
                                    receiver.flow(AmqpProtocolConverter.this.prefetch - receiver.getCredit());
                                }
                                delivery.disposition((DeliveryState)Accepted.getInstance());
                                delivery.settle();
                            }
                            AmqpProtocolConverter.this.pumpProtonToSocket();
                        }
                    });
                }
                else {
                    if (receiver.getCredit() <= AmqpProtocolConverter.this.prefetch * 0.2) {
                        AmqpProtocolConverter.LOG.trace("Sending more credit ({}) to producer: {}", (Object)(AmqpProtocolConverter.this.prefetch - receiver.getCredit()), this.producerId);
                        receiver.flow(AmqpProtocolConverter.this.prefetch - receiver.getCredit());
                        AmqpProtocolConverter.this.pumpProtonToSocket();
                    }
                    AmqpProtocolConverter.this.sendToActiveMQ(message, null);
                }
            }
        }
        
        @Override
        public void onClose() throws Exception {
            if (!this.closed) {
                AmqpProtocolConverter.this.sendToActiveMQ(new RemoveInfo(this.producerId), null);
            }
        }
    }
    
    class Transaction
    {
    }
    
    class ConsumerContext extends AmqpDeliveryListener
    {
        private final ConsumerId consumerId;
        private final Sender sender;
        private final boolean presettle;
        private boolean closed;
        public ConsumerInfo info;
        private boolean endOfBrowse;
        protected LinkedList<MessageDispatch> dispatchedInTx;
        long nextTagId;
        HashSet<byte[]> tagCache;
        LinkedList<MessageDispatch> outbound;
        Buffer currentBuffer;
        Delivery currentDelivery;
        final String MESSAGE_FORMAT_KEY;
        
        public ConsumerContext(final ConsumerId consumerId, final Sender sender) {
            this.endOfBrowse = false;
            this.dispatchedInTx = new LinkedList<MessageDispatch>();
            this.nextTagId = 0L;
            this.tagCache = new HashSet<byte[]>();
            this.outbound = new LinkedList<MessageDispatch>();
            this.MESSAGE_FORMAT_KEY = AmqpProtocolConverter.this.outboundTransformer.getPrefixVendor() + "MESSAGE_FORMAT";
            this.consumerId = consumerId;
            this.sender = sender;
            this.presettle = (sender.getRemoteSenderSettleMode() == SenderSettleMode.SETTLED);
        }
        
        byte[] nextTag() {
            byte[] rc;
            if (this.tagCache != null && !this.tagCache.isEmpty()) {
                final Iterator<byte[]> iterator = this.tagCache.iterator();
                rc = iterator.next();
                iterator.remove();
            }
            else {
                try {
                    rc = Long.toHexString(this.nextTagId++).getBytes("UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            return rc;
        }
        
        void checkinTag(final byte[] data) {
            if (this.tagCache.size() < 1024) {
                this.tagCache.add(data);
            }
        }
        
        @Override
        public void onClose() throws Exception {
            if (!this.closed) {
                this.closed = true;
                final AmqpSessionContext session = (AmqpSessionContext)this.sender.getSession().getContext();
                if (session != null) {
                    session.consumers.remove(this.info.getConsumerId());
                }
                AmqpProtocolConverter.this.sendToActiveMQ(new RemoveInfo(this.consumerId), null);
            }
        }
        
        public void onMessageDispatch(final MessageDispatch md) throws Exception {
            if (!this.closed) {
                synchronized (this.outbound) {
                    this.outbound.addLast(md);
                }
                this.pumpOutbound();
                AmqpProtocolConverter.this.pumpProtonToSocket();
            }
        }
        
        public void pumpOutbound() throws Exception {
            while (!this.closed) {
                while (this.currentBuffer != null) {
                    final int sent = this.sender.send(this.currentBuffer.data, this.currentBuffer.offset, this.currentBuffer.length);
                    if (sent <= 0) {
                        return;
                    }
                    this.currentBuffer.moveHead(sent);
                    if (this.currentBuffer.length != 0) {
                        continue;
                    }
                    if (this.presettle) {
                        this.settle(this.currentDelivery, 4);
                    }
                    else {
                        this.sender.advance();
                    }
                    this.currentBuffer = null;
                    this.currentDelivery = null;
                }
                if (this.outbound.isEmpty()) {
                    return;
                }
                final MessageDispatch md = this.outbound.removeFirst();
                try {
                    ActiveMQMessage temp = null;
                    if (md.getMessage() != null) {
                        if (md.getDestination().isTopic()) {
                            synchronized (md.getMessage()) {
                                temp = (ActiveMQMessage)md.getMessage().copy();
                            }
                        }
                        else {
                            temp = (ActiveMQMessage)md.getMessage();
                        }
                        if (!temp.getProperties().containsKey(this.MESSAGE_FORMAT_KEY)) {
                            temp.setProperty(this.MESSAGE_FORMAT_KEY, 0);
                        }
                    }
                    final ActiveMQMessage jms = temp;
                    if (jms == null) {
                        this.endOfBrowse = true;
                        this.drainCheck();
                    }
                    else {
                        jms.setRedeliveryCounter(md.getRedeliveryCounter());
                        jms.setReadOnlyBody(true);
                        final EncodedMessage amqp = AmqpProtocolConverter.this.outboundTransformer.transform((javax.jms.Message)jms);
                        if (amqp == null || amqp.getLength() <= 0) {
                            continue;
                        }
                        this.currentBuffer = new Buffer(amqp.getArray(), amqp.getArrayOffset(), amqp.getLength());
                        if (this.presettle) {
                            this.currentDelivery = this.sender.delivery(AmqpProtocolConverter.EMPTY_BYTE_ARRAY, 0, 0);
                        }
                        else {
                            final byte[] tag = this.nextTag();
                            this.currentDelivery = this.sender.delivery(tag, 0, tag.length);
                        }
                        this.currentDelivery.setContext((Object)md);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void settle(final Delivery delivery, final int ackType) throws Exception {
            final byte[] tag = delivery.getTag();
            if (tag != null && tag.length > 0 && delivery.remotelySettled()) {
                this.checkinTag(tag);
            }
            if (ackType == -1) {
                delivery.settle();
                this.onMessageDispatch((MessageDispatch)delivery.getContext());
            }
            else {
                final MessageDispatch md = (MessageDispatch)delivery.getContext();
                final MessageAck ack = new MessageAck();
                ack.setConsumerId(this.consumerId);
                ack.setFirstMessageId(md.getMessage().getMessageId());
                ack.setLastMessageId(md.getMessage().getMessageId());
                ack.setMessageCount(1);
                ack.setAckType((byte)ackType);
                ack.setDestination(md.getDestination());
                final DeliveryState remoteState = delivery.getRemoteState();
                if (remoteState != null && remoteState instanceof TransactionalState) {
                    final TransactionalState s = (TransactionalState)remoteState;
                    final long txid = AmqpProtocolConverter.this.toLong(s.getTxnId());
                    final LocalTransactionId localTxId = new LocalTransactionId(AmqpProtocolConverter.this.connectionId, txid);
                    ack.setTransactionId(localTxId);
                    md.getMessage().setTransactionId(localTxId);
                    this.dispatchedInTx.addFirst(md);
                }
                AmqpProtocolConverter.LOG.trace("Sending Ack to ActiveMQ: {}", ack);
                AmqpProtocolConverter.this.sendToActiveMQ(ack, new ResponseHandler() {
                    @Override
                    public void onResponse(final IAmqpProtocolConverter converter, final Response response) throws IOException {
                        if (response.isException()) {
                            if (response.isException()) {
                                final Throwable exception = ((ExceptionResponse)response).getException();
                                exception.printStackTrace();
                                ConsumerContext.this.sender.close();
                            }
                        }
                        else {
                            delivery.settle();
                        }
                        AmqpProtocolConverter.this.pumpProtonToSocket();
                    }
                });
            }
        }
        
        @Override
        public void drainCheck() {
            if (this.info.isBrowser() && !this.endOfBrowse) {
                return;
            }
            if (this.outbound.isEmpty()) {
                this.sender.drained();
            }
        }
        
        @Override
        public void onDelivery(final Delivery delivery) throws Exception {
            final MessageDispatch md = (MessageDispatch)delivery.getContext();
            DeliveryState state = delivery.getRemoteState();
            if (state instanceof TransactionalState) {
                final TransactionalState txState = (TransactionalState)state;
                if (txState.getOutcome() instanceof DeliveryState) {
                    AmqpProtocolConverter.LOG.trace("onDelivery: TX delivery state = {}", state);
                    state = (DeliveryState)txState.getOutcome();
                    if (state instanceof Accepted) {
                        if (!delivery.remotelySettled()) {
                            delivery.disposition((DeliveryState)new Accepted());
                        }
                        this.settle(delivery, 0);
                    }
                }
            }
            else if (state instanceof Accepted) {
                AmqpProtocolConverter.LOG.trace("onDelivery: accepted state = {}", state);
                if (!delivery.remotelySettled()) {
                    delivery.disposition((DeliveryState)new Accepted());
                }
                this.settle(delivery, 4);
            }
            else if (state instanceof Rejected) {
                md.setRedeliveryCounter(md.getRedeliveryCounter() + 1);
                AmqpProtocolConverter.LOG.trace("onDelivery: Rejected state = {}, delivery count now {}", state, md.getRedeliveryCounter());
                this.settle(delivery, -1);
            }
            else if (state instanceof Released) {
                AmqpProtocolConverter.LOG.trace("onDelivery: Released state = {}", state);
                this.settle(delivery, -1);
            }
            else if (state instanceof Modified) {
                final Modified modified = (Modified)state;
                if (modified.getDeliveryFailed()) {
                    md.setRedeliveryCounter(md.getRedeliveryCounter() + 1);
                }
                AmqpProtocolConverter.LOG.trace("onDelivery: Modified state = {}, delivery count now {}", state, md.getRedeliveryCounter());
                byte ackType = -1;
                final Boolean undeliverableHere = modified.getUndeliverableHere();
                if (undeliverableHere != null && undeliverableHere) {
                    ackType = 1;
                }
                this.settle(delivery, ackType);
            }
            this.pumpOutbound();
        }
        
        @Override
        void doCommit() throws Exception {
            if (!this.dispatchedInTx.isEmpty()) {
                final MessageDispatch md = this.dispatchedInTx.getFirst();
                final MessageAck pendingTxAck = new MessageAck(md, (byte)2, this.dispatchedInTx.size());
                pendingTxAck.setTransactionId(md.getMessage().getTransactionId());
                pendingTxAck.setFirstMessageId(this.dispatchedInTx.getLast().getMessage().getMessageId());
                AmqpProtocolConverter.LOG.trace("Sending commit Ack to ActiveMQ: {}", pendingTxAck);
                this.dispatchedInTx.clear();
                AmqpProtocolConverter.this.sendToActiveMQ(pendingTxAck, new ResponseHandler() {
                    @Override
                    public void onResponse(final IAmqpProtocolConverter converter, final Response response) throws IOException {
                        if (response.isException() && response.isException()) {
                            final Throwable exception = ((ExceptionResponse)response).getException();
                            exception.printStackTrace();
                            ConsumerContext.this.sender.close();
                        }
                        AmqpProtocolConverter.this.pumpProtonToSocket();
                    }
                });
            }
        }
        
        @Override
        void doRollback() throws Exception {
            synchronized (this.outbound) {
                AmqpProtocolConverter.LOG.trace("Rolling back {} messages for redelivery. ", (Object)this.dispatchedInTx.size());
                for (final MessageDispatch md : this.dispatchedInTx) {
                    md.setRedeliveryCounter(md.getRedeliveryCounter() + 1);
                    md.getMessage().setTransactionId(null);
                    this.outbound.addFirst(md);
                }
                this.dispatchedInTx.clear();
            }
        }
    }
}
