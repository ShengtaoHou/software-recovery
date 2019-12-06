// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.LoggerFactory;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.command.ConnectionError;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.ShutdownInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.ConsumerControl;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.TransactionInfo;
import java.util.Iterator;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.MessageId;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.apache.activemq.util.ByteArrayOutputStream;
import javax.jms.JMSException;
import org.apache.activemq.broker.BrokerContextAware;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.Command;
import java.io.IOException;
import java.util.HashMap;
import org.apache.activemq.command.ExceptionResponse;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.broker.BrokerContext;
import org.apache.activemq.util.FactoryFinder;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.command.LocalTransactionId;
import java.util.Map;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerId;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.util.IdGenerator;
import org.slf4j.Logger;

public class ProtocolConverter
{
    private static final Logger LOG;
    private static final IdGenerator CONNECTION_ID_GENERATOR;
    private static final String BROKER_VERSION;
    private static final StompFrame ping;
    private final ConnectionId connectionId;
    private final SessionId sessionId;
    private final ProducerId producerId;
    private final LongSequenceGenerator consumerIdGenerator;
    private final LongSequenceGenerator messageIdGenerator;
    private final LongSequenceGenerator transactionIdGenerator;
    private final LongSequenceGenerator tempDestinationGenerator;
    private final ConcurrentHashMap<Integer, ResponseHandler> resposeHandlers;
    private final ConcurrentHashMap<ConsumerId, StompSubscription> subscriptionsByConsumerId;
    private final ConcurrentHashMap<String, StompSubscription> subscriptions;
    private final ConcurrentHashMap<String, ActiveMQDestination> tempDestinations;
    private final ConcurrentHashMap<String, String> tempDestinationAmqToStompMap;
    private final Map<String, LocalTransactionId> transactions;
    private final StompTransport stompTransport;
    private final ConcurrentHashMap<String, AckEntry> pedingAcks;
    private final IdGenerator ACK_ID_GENERATOR;
    private final Object commnadIdMutex;
    private int lastCommandId;
    private final AtomicBoolean connected;
    private final FrameTranslator frameTranslator;
    private final FactoryFinder FRAME_TRANSLATOR_FINDER;
    private final BrokerContext brokerContext;
    private String version;
    private long hbReadInterval;
    private long hbWriteInterval;
    private float hbGracePeriodMultiplier;
    private String defaultHeartBeat;
    ConnectionInfo connectionInfo;
    
    public ProtocolConverter(final StompTransport stompTransport, final BrokerContext brokerContext) {
        this.connectionId = new ConnectionId(ProtocolConverter.CONNECTION_ID_GENERATOR.generateId());
        this.sessionId = new SessionId(this.connectionId, -1L);
        this.producerId = new ProducerId(this.sessionId, 1L);
        this.consumerIdGenerator = new LongSequenceGenerator();
        this.messageIdGenerator = new LongSequenceGenerator();
        this.transactionIdGenerator = new LongSequenceGenerator();
        this.tempDestinationGenerator = new LongSequenceGenerator();
        this.resposeHandlers = new ConcurrentHashMap<Integer, ResponseHandler>();
        this.subscriptionsByConsumerId = new ConcurrentHashMap<ConsumerId, StompSubscription>();
        this.subscriptions = new ConcurrentHashMap<String, StompSubscription>();
        this.tempDestinations = new ConcurrentHashMap<String, ActiveMQDestination>();
        this.tempDestinationAmqToStompMap = new ConcurrentHashMap<String, String>();
        this.transactions = new ConcurrentHashMap<String, LocalTransactionId>();
        this.pedingAcks = new ConcurrentHashMap<String, AckEntry>();
        this.ACK_ID_GENERATOR = new IdGenerator();
        this.commnadIdMutex = new Object();
        this.connected = new AtomicBoolean(false);
        this.frameTranslator = new LegacyFrameTranslator();
        this.FRAME_TRANSLATOR_FINDER = new FactoryFinder("META-INF/services/org/apache/activemq/transport/frametranslator/");
        this.version = "1.0";
        this.hbGracePeriodMultiplier = 1.0f;
        this.defaultHeartBeat = "0,0";
        this.connectionInfo = new ConnectionInfo();
        this.stompTransport = stompTransport;
        this.brokerContext = brokerContext;
    }
    
    protected int generateCommandId() {
        synchronized (this.commnadIdMutex) {
            return this.lastCommandId++;
        }
    }
    
    protected ResponseHandler createResponseHandler(final StompFrame command) {
        final String receiptId = command.getHeaders().get("receipt");
        if (receiptId != null) {
            return new ResponseHandler() {
                @Override
                public void onResponse(final ProtocolConverter converter, final Response response) throws IOException {
                    if (response.isException()) {
                        final Throwable exception = ((ExceptionResponse)response).getException();
                        ProtocolConverter.this.handleException(exception, command);
                    }
                    else {
                        final StompFrame sc = new StompFrame();
                        sc.setAction("RECEIPT");
                        sc.setHeaders(new HashMap<String, String>(1));
                        sc.getHeaders().put("receipt-id", receiptId);
                        ProtocolConverter.this.stompTransport.sendToStomp(sc);
                    }
                }
            };
        }
        return null;
    }
    
    protected void sendToActiveMQ(final Command command, final ResponseHandler handler) {
        command.setCommandId(this.generateCommandId());
        if (handler != null) {
            command.setResponseRequired(true);
            this.resposeHandlers.put(command.getCommandId(), handler);
        }
        this.stompTransport.sendToActiveMQ(command);
    }
    
    protected void sendToStomp(final StompFrame command) throws IOException {
        this.stompTransport.sendToStomp(command);
    }
    
    protected FrameTranslator findTranslator(final String header) {
        return this.findTranslator(header, null);
    }
    
    protected FrameTranslator findTranslator(final String header, final ActiveMQDestination destination) {
        FrameTranslator translator = this.frameTranslator;
        try {
            if (header != null) {
                translator = (FrameTranslator)this.FRAME_TRANSLATOR_FINDER.newInstance(header);
            }
            else if (destination != null && AdvisorySupport.isAdvisoryTopic(destination)) {
                translator = new JmsFrameTranslator();
            }
        }
        catch (Exception ex) {}
        if (translator instanceof BrokerContextAware) {
            ((BrokerContextAware)translator).setBrokerContext(this.brokerContext);
        }
        return translator;
    }
    
    public void onStompCommand(final StompFrame command) throws IOException, JMSException {
        try {
            if (command.getClass() == StompFrameError.class) {
                throw ((StompFrameError)command).getException();
            }
            final String action = command.getAction();
            if (action.startsWith("SEND")) {
                this.onStompSend(command);
            }
            else if (action.startsWith("ACK")) {
                this.onStompAck(command);
            }
            else if (action.startsWith("NACK")) {
                this.onStompNack(command);
            }
            else if (action.startsWith("BEGIN")) {
                this.onStompBegin(command);
            }
            else if (action.startsWith("COMMIT")) {
                this.onStompCommit(command);
            }
            else if (action.startsWith("ABORT")) {
                this.onStompAbort(command);
            }
            else if (action.startsWith("SUB")) {
                this.onStompSubscribe(command);
            }
            else if (action.startsWith("UNSUB")) {
                this.onStompUnsubscribe(command);
            }
            else if (action.startsWith("CONNECT") || action.startsWith("STOMP")) {
                this.onStompConnect(command);
            }
            else {
                if (!action.startsWith("DISCONNECT")) {
                    throw new ProtocolException("Unknown STOMP action: " + action);
                }
                this.onStompDisconnect(command);
            }
        }
        catch (ProtocolException e) {
            this.handleException(e, command);
            if (e.isFatal()) {
                this.getStompTransport().onException(e);
            }
        }
    }
    
    protected void handleException(final Throwable exception, final StompFrame command) throws IOException {
        ProtocolConverter.LOG.warn("Exception occurred processing: \n" + command + ": " + exception.toString());
        if (ProtocolConverter.LOG.isDebugEnabled()) {
            ProtocolConverter.LOG.debug("Exception detail", exception);
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter stream = new PrintWriter(new OutputStreamWriter(baos, "UTF-8"));
        exception.printStackTrace(stream);
        stream.close();
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("message", exception.getMessage());
        headers.put("content-type", "text/plain");
        if (command != null) {
            final String receiptId = command.getHeaders().get("receipt");
            if (receiptId != null) {
                headers.put("receipt-id", receiptId);
            }
        }
        final StompFrame errorMessage = new StompFrame("ERROR", headers, baos.toByteArray());
        this.sendToStomp(errorMessage);
    }
    
    protected void onStompSend(final StompFrame command) throws IOException, JMSException {
        this.checkConnected();
        final Map<String, String> headers = command.getHeaders();
        final String destination = headers.get("destination");
        if (destination == null) {
            throw new ProtocolException("SEND received without a Destination specified!");
        }
        final String stompTx = headers.get("transaction");
        headers.remove("transaction");
        final ActiveMQMessage message = this.convertMessage(command);
        message.setProducerId(this.producerId);
        final MessageId id = new MessageId(this.producerId, this.messageIdGenerator.getNextSequenceId());
        message.setMessageId(id);
        if (stompTx != null) {
            final TransactionId activemqTx = this.transactions.get(stompTx);
            if (activemqTx == null) {
                throw new ProtocolException("Invalid transaction id: " + stompTx);
            }
            message.setTransactionId(activemqTx);
        }
        message.onSend();
        this.sendToActiveMQ(message, this.createResponseHandler(command));
    }
    
    protected void onStompNack(final StompFrame command) throws ProtocolException {
        this.checkConnected();
        if (this.version.equals("1.0")) {
            throw new ProtocolException("NACK received but connection is in v1.0 mode.");
        }
        final Map<String, String> headers = command.getHeaders();
        final String subscriptionId = headers.get("subscription");
        if (subscriptionId == null && !this.version.equals("1.2")) {
            throw new ProtocolException("NACK received without a subscription id for acknowledge!");
        }
        String messageId = headers.get("message-id");
        if (messageId == null && !this.version.equals("1.2")) {
            throw new ProtocolException("NACK received without a message-id to acknowledge!");
        }
        final String ackId = headers.get("id");
        if (ackId == null && this.version.equals("1.2")) {
            throw new ProtocolException("NACK received without an ack header to acknowledge!");
        }
        TransactionId activemqTx = null;
        final String stompTx = headers.get("transaction");
        if (stompTx != null) {
            activemqTx = this.transactions.get(stompTx);
            if (activemqTx == null) {
                throw new ProtocolException("Invalid transaction id: " + stompTx);
            }
        }
        boolean nacked = false;
        if (ackId != null) {
            final AckEntry pendingAck = this.pedingAcks.get(ackId);
            if (pendingAck != null) {
                messageId = pendingAck.getMessageId();
                final MessageAck ack = pendingAck.onMessageNack(activemqTx);
                if (ack != null) {
                    this.sendToActiveMQ(ack, this.createResponseHandler(command));
                    nacked = true;
                }
            }
        }
        else if (subscriptionId != null) {
            final StompSubscription sub = this.subscriptions.get(subscriptionId);
            if (sub != null) {
                final MessageAck ack = sub.onStompMessageNack(messageId, activemqTx);
                if (ack != null) {
                    this.sendToActiveMQ(ack, this.createResponseHandler(command));
                    nacked = true;
                }
            }
        }
        if (!nacked) {
            throw new ProtocolException("Unexpected NACK received for message-id [" + messageId + "]");
        }
    }
    
    protected void onStompAck(final StompFrame command) throws ProtocolException {
        this.checkConnected();
        final Map<String, String> headers = command.getHeaders();
        String messageId = headers.get("message-id");
        if (messageId == null && !this.version.equals("1.2")) {
            throw new ProtocolException("ACK received without a message-id to acknowledge!");
        }
        final String subscriptionId = headers.get("subscription");
        if (subscriptionId == null && this.version.equals("1.1")) {
            throw new ProtocolException("ACK received without a subscription id for acknowledge!");
        }
        final String ackId = headers.get("id");
        if (ackId == null && this.version.equals("1.2")) {
            throw new ProtocolException("ACK received without a ack id for acknowledge!");
        }
        TransactionId activemqTx = null;
        final String stompTx = headers.get("transaction");
        if (stompTx != null) {
            activemqTx = this.transactions.get(stompTx);
            if (activemqTx == null) {
                throw new ProtocolException("Invalid transaction id: " + stompTx);
            }
        }
        boolean acked = false;
        if (ackId != null) {
            final AckEntry pendingAck = this.pedingAcks.get(ackId);
            if (pendingAck != null) {
                messageId = pendingAck.getMessageId();
                final MessageAck ack = pendingAck.onMessageAck(activemqTx);
                if (ack != null) {
                    this.sendToActiveMQ(ack, this.createResponseHandler(command));
                    acked = true;
                }
            }
        }
        else if (subscriptionId != null) {
            final StompSubscription sub = this.subscriptions.get(subscriptionId);
            if (sub != null) {
                final MessageAck ack = sub.onStompMessageAck(messageId, activemqTx);
                if (ack != null) {
                    this.sendToActiveMQ(ack, this.createResponseHandler(command));
                    acked = true;
                }
            }
        }
        else {
            for (final StompSubscription sub2 : this.subscriptionsByConsumerId.values()) {
                final MessageAck ack2 = sub2.onStompMessageAck(messageId, activemqTx);
                if (ack2 != null) {
                    this.sendToActiveMQ(ack2, this.createResponseHandler(command));
                    acked = true;
                    break;
                }
            }
        }
        if (!acked) {
            throw new ProtocolException("Unexpected ACK received for message-id [" + messageId + "]");
        }
    }
    
    protected void onStompBegin(final StompFrame command) throws ProtocolException {
        this.checkConnected();
        final Map<String, String> headers = command.getHeaders();
        final String stompTx = headers.get("transaction");
        if (!headers.containsKey("transaction")) {
            throw new ProtocolException("Must specify the transaction you are beginning");
        }
        if (this.transactions.get(stompTx) != null) {
            throw new ProtocolException("The transaction was already started: " + stompTx);
        }
        final LocalTransactionId activemqTx = new LocalTransactionId(this.connectionId, this.transactionIdGenerator.getNextSequenceId());
        this.transactions.put(stompTx, activemqTx);
        final TransactionInfo tx = new TransactionInfo();
        tx.setConnectionId(this.connectionId);
        tx.setTransactionId(activemqTx);
        tx.setType((byte)0);
        this.sendToActiveMQ(tx, this.createResponseHandler(command));
    }
    
    protected void onStompCommit(final StompFrame command) throws ProtocolException {
        this.checkConnected();
        final Map<String, String> headers = command.getHeaders();
        final String stompTx = headers.get("transaction");
        if (stompTx == null) {
            throw new ProtocolException("Must specify the transaction you are committing");
        }
        final TransactionId activemqTx = this.transactions.remove(stompTx);
        if (activemqTx == null) {
            throw new ProtocolException("Invalid transaction id: " + stompTx);
        }
        for (final StompSubscription sub : this.subscriptionsByConsumerId.values()) {
            sub.onStompCommit(activemqTx);
        }
        final TransactionInfo tx = new TransactionInfo();
        tx.setConnectionId(this.connectionId);
        tx.setTransactionId(activemqTx);
        tx.setType((byte)2);
        this.sendToActiveMQ(tx, this.createResponseHandler(command));
    }
    
    protected void onStompAbort(final StompFrame command) throws ProtocolException {
        this.checkConnected();
        final Map<String, String> headers = command.getHeaders();
        final String stompTx = headers.get("transaction");
        if (stompTx == null) {
            throw new ProtocolException("Must specify the transaction you are committing");
        }
        final TransactionId activemqTx = this.transactions.remove(stompTx);
        if (activemqTx == null) {
            throw new ProtocolException("Invalid transaction id: " + stompTx);
        }
        for (final StompSubscription sub : this.subscriptionsByConsumerId.values()) {
            try {
                sub.onStompAbort(activemqTx);
            }
            catch (Exception e) {
                throw new ProtocolException("Transaction abort failed", false, e);
            }
        }
        final TransactionInfo tx = new TransactionInfo();
        tx.setConnectionId(this.connectionId);
        tx.setTransactionId(activemqTx);
        tx.setType((byte)4);
        this.sendToActiveMQ(tx, this.createResponseHandler(command));
    }
    
    protected void onStompSubscribe(final StompFrame command) throws ProtocolException {
        this.checkConnected();
        final FrameTranslator translator = this.findTranslator(command.getHeaders().get("transformation"));
        final Map<String, String> headers = command.getHeaders();
        final String subscriptionId = headers.get("id");
        final String destination = headers.get("destination");
        if (this.version.equals("1.1") && subscriptionId == null) {
            throw new ProtocolException("SUBSCRIBE received without a subscription id!");
        }
        final ActiveMQDestination actualDest = translator.convertDestination(this, destination, true);
        if (actualDest == null) {
            throw new ProtocolException("Invalid 'null' Destination.");
        }
        final ConsumerId id = new ConsumerId(this.sessionId, this.consumerIdGenerator.getNextSequenceId());
        final ConsumerInfo consumerInfo = new ConsumerInfo(id);
        consumerInfo.setPrefetchSize(actualDest.isQueue() ? 1000 : (headers.containsKey("activemq.subscriptionName") ? 100 : 32767));
        consumerInfo.setDispatchAsync(true);
        final String browser = headers.get("browser");
        if (browser != null && browser.equals("true")) {
            if (this.version.equals("1.0")) {
                throw new ProtocolException("Queue Browser feature only valid for Stomp v1.1+ clients!");
            }
            consumerInfo.setBrowser(true);
            consumerInfo.setPrefetchSize(500);
        }
        final String selector = headers.remove("selector");
        if (selector != null) {
            consumerInfo.setSelector("convert_string_expressions:" + selector);
        }
        IntrospectionSupport.setProperties(consumerInfo, headers, "activemq.");
        if (actualDest.isQueue() && consumerInfo.getSubscriptionName() != null) {
            throw new ProtocolException("Invalid Subscription: cannot durably subscribe to a Queue destination!");
        }
        consumerInfo.setDestination(translator.convertDestination(this, destination, true));
        StompSubscription stompSubscription;
        if (!consumerInfo.isBrowser()) {
            stompSubscription = new StompSubscription(this, subscriptionId, consumerInfo, headers.get("transformation"));
        }
        else {
            stompSubscription = new StompQueueBrowserSubscription(this, subscriptionId, consumerInfo, headers.get("transformation"));
        }
        stompSubscription.setDestination(actualDest);
        final String ackMode = headers.get("ack");
        if ("client".equals(ackMode)) {
            stompSubscription.setAckMode("client");
        }
        else if ("client-individual".equals(ackMode)) {
            stompSubscription.setAckMode("client-individual");
        }
        else {
            stompSubscription.setAckMode("auto");
        }
        this.subscriptionsByConsumerId.put(id, stompSubscription);
        if (subscriptionId != null) {
            this.subscriptions.put(subscriptionId, stompSubscription);
        }
        final String receiptId = command.getHeaders().get("receipt");
        if (receiptId != null && consumerInfo.getPrefetchSize() > 0) {
            final StompFrame cmd = command;
            final int prefetch = consumerInfo.getPrefetchSize();
            consumerInfo.setPrefetchSize(0);
            final ResponseHandler handler = new ResponseHandler() {
                @Override
                public void onResponse(final ProtocolConverter converter, final Response response) throws IOException {
                    if (response.isException()) {
                        final Throwable exception = ((ExceptionResponse)response).getException();
                        ProtocolConverter.this.handleException(exception, cmd);
                    }
                    else {
                        final StompFrame sc = new StompFrame();
                        sc.setAction("RECEIPT");
                        sc.setHeaders(new HashMap<String, String>(1));
                        sc.getHeaders().put("receipt-id", receiptId);
                        ProtocolConverter.this.stompTransport.sendToStomp(sc);
                        final ConsumerControl control = new ConsumerControl();
                        control.setPrefetch(prefetch);
                        control.setDestination(actualDest);
                        control.setConsumerId(id);
                        ProtocolConverter.this.sendToActiveMQ(control, null);
                    }
                }
            };
            this.sendToActiveMQ(consumerInfo, handler);
        }
        else {
            this.sendToActiveMQ(consumerInfo, this.createResponseHandler(command));
        }
    }
    
    protected void onStompUnsubscribe(final StompFrame command) throws ProtocolException {
        this.checkConnected();
        final Map<String, String> headers = command.getHeaders();
        ActiveMQDestination destination = null;
        final Object o = headers.get("destination");
        if (o != null) {
            destination = this.findTranslator(command.getHeaders().get("transformation")).convertDestination(this, (String)o, true);
        }
        final String subscriptionId = headers.get("id");
        if (this.version.equals("1.1") && subscriptionId == null) {
            throw new ProtocolException("UNSUBSCRIBE received without a subscription id!");
        }
        if (subscriptionId == null && destination == null) {
            throw new ProtocolException("Must specify the subscriptionId or the destination you are unsubscribing from");
        }
        String clientId;
        final String durable = clientId = command.getHeaders().get("activemq.subscriptionName");
        if (this.version.equals("1.1")) {
            clientId = this.connectionInfo.getClientId();
        }
        if (durable != null) {
            final RemoveSubscriptionInfo info = new RemoveSubscriptionInfo();
            info.setClientId(clientId);
            info.setSubscriptionName(durable);
            info.setConnectionId(this.connectionId);
            this.sendToActiveMQ(info, this.createResponseHandler(command));
            return;
        }
        if (subscriptionId != null) {
            final StompSubscription sub = this.subscriptions.remove(subscriptionId);
            if (sub != null) {
                this.sendToActiveMQ(sub.getConsumerInfo().createRemoveCommand(), this.createResponseHandler(command));
                return;
            }
        }
        else {
            final Iterator<StompSubscription> iter = this.subscriptionsByConsumerId.values().iterator();
            while (iter.hasNext()) {
                final StompSubscription sub2 = iter.next();
                if (destination != null && destination.equals(sub2.getDestination())) {
                    this.sendToActiveMQ(sub2.getConsumerInfo().createRemoveCommand(), this.createResponseHandler(command));
                    iter.remove();
                    return;
                }
            }
        }
        throw new ProtocolException("No subscription matched.");
    }
    
    protected void onStompConnect(final StompFrame command) throws ProtocolException {
        if (this.connected.get()) {
            throw new ProtocolException("Already connected.");
        }
        final Map<String, String> headers = command.getHeaders();
        final String login = headers.get("login");
        final String passcode = headers.get("passcode");
        final String clientId = headers.get("client-id");
        String heartBeat = headers.get("heart-beat");
        if (heartBeat == null) {
            heartBeat = this.defaultHeartBeat;
        }
        this.version = StompCodec.detectVersion(headers);
        this.configureInactivityMonitor(heartBeat.trim());
        IntrospectionSupport.setProperties(this.connectionInfo, headers, "activemq.");
        this.connectionInfo.setConnectionId(this.connectionId);
        if (clientId != null) {
            this.connectionInfo.setClientId(clientId);
        }
        else {
            this.connectionInfo.setClientId("" + this.connectionInfo.getConnectionId().toString());
        }
        this.connectionInfo.setResponseRequired(true);
        this.connectionInfo.setUserName(login);
        this.connectionInfo.setPassword(passcode);
        this.connectionInfo.setTransportContext(command.getTransportContext());
        this.sendToActiveMQ(this.connectionInfo, new ResponseHandler() {
            @Override
            public void onResponse(final ProtocolConverter converter, final Response response) throws IOException {
                if (response.isException()) {
                    final Throwable exception = ((ExceptionResponse)response).getException();
                    ProtocolConverter.this.handleException(exception, command);
                    ProtocolConverter.this.getStompTransport().onException(IOExceptionSupport.create(exception));
                    return;
                }
                final SessionInfo sessionInfo = new SessionInfo(ProtocolConverter.this.sessionId);
                ProtocolConverter.this.sendToActiveMQ(sessionInfo, null);
                final ProducerInfo producerInfo = new ProducerInfo(ProtocolConverter.this.producerId);
                ProtocolConverter.this.sendToActiveMQ(producerInfo, new ResponseHandler() {
                    @Override
                    public void onResponse(final ProtocolConverter converter, final Response response) throws IOException {
                        if (response.isException()) {
                            final Throwable exception = ((ExceptionResponse)response).getException();
                            ProtocolConverter.this.handleException(exception, command);
                            ProtocolConverter.this.getStompTransport().onException(IOExceptionSupport.create(exception));
                        }
                        ProtocolConverter.this.connected.set(true);
                        final HashMap<String, String> responseHeaders = new HashMap<String, String>();
                        responseHeaders.put("session", ProtocolConverter.this.connectionInfo.getClientId());
                        String requestId = headers.get("request-id");
                        if (requestId == null) {
                            requestId = headers.get("receipt");
                        }
                        if (requestId != null) {
                            responseHeaders.put("response-id", requestId);
                            responseHeaders.put("receipt-id", requestId);
                        }
                        responseHeaders.put("version", ProtocolConverter.this.version);
                        responseHeaders.put("heart-beat", String.format("%d,%d", ProtocolConverter.this.hbWriteInterval, ProtocolConverter.this.hbReadInterval));
                        responseHeaders.put("server", "ActiveMQ/" + ProtocolConverter.BROKER_VERSION);
                        final StompFrame sc = new StompFrame();
                        sc.setAction("CONNECTED");
                        sc.setHeaders(responseHeaders);
                        ProtocolConverter.this.sendToStomp(sc);
                        final StompWireFormat format = ProtocolConverter.this.stompTransport.getWireFormat();
                        if (format != null) {
                            format.setStompVersion(ProtocolConverter.this.version);
                        }
                    }
                });
            }
        });
    }
    
    protected void onStompDisconnect(final StompFrame command) throws ProtocolException {
        if (this.connected.get()) {
            this.sendToActiveMQ(this.connectionInfo.createRemoveCommand(), this.createResponseHandler(command));
            this.sendToActiveMQ(new ShutdownInfo(), this.createResponseHandler(command));
            this.connected.set(false);
        }
    }
    
    protected void checkConnected() throws ProtocolException {
        if (!this.connected.get()) {
            throw new ProtocolException("Not connected.");
        }
    }
    
    public void onActiveMQCommand(final Command command) throws IOException, JMSException {
        if (command.isResponse()) {
            final Response response = (Response)command;
            final ResponseHandler rh = this.resposeHandlers.remove(response.getCorrelationId());
            if (rh != null) {
                rh.onResponse(this, response);
            }
            else if (response.isException()) {
                final Throwable exception = ((ExceptionResponse)response).getException();
                this.handleException(exception, null);
            }
        }
        else if (command.isMessageDispatch()) {
            final MessageDispatch md = (MessageDispatch)command;
            final StompSubscription sub = this.subscriptionsByConsumerId.get(md.getConsumerId());
            if (sub != null) {
                String ackId = null;
                if (this.version.equals("1.2") && sub.getAckMode() != "auto" && md.getMessage() != null) {
                    final AckEntry pendingAck = new AckEntry(md.getMessage().getMessageId().toString(), sub);
                    ackId = this.ACK_ID_GENERATOR.generateId();
                    this.pedingAcks.put(ackId, pendingAck);
                }
                try {
                    sub.onMessageDispatch(md, ackId);
                }
                catch (Exception ex) {
                    if (ackId != null) {
                        this.pedingAcks.remove(ackId);
                    }
                }
            }
        }
        else if (command.getDataStructureType() == 10) {
            this.stompTransport.sendToStomp(ProtocolConverter.ping);
        }
        else if (command.getDataStructureType() == 16) {
            final Throwable exception2 = ((ConnectionError)command).getException();
            this.handleException(exception2, null);
        }
    }
    
    public ActiveMQMessage convertMessage(final StompFrame command) throws IOException, JMSException {
        final ActiveMQMessage msg = this.findTranslator(command.getHeaders().get("transformation")).convertFrame(this, command);
        return msg;
    }
    
    public StompFrame convertMessage(final ActiveMQMessage message, final boolean ignoreTransformation) throws IOException, JMSException {
        if (ignoreTransformation) {
            return this.frameTranslator.convertMessage(this, message);
        }
        return this.findTranslator(message.getStringProperty("transformation"), message.getDestination()).convertMessage(this, message);
    }
    
    public StompTransport getStompTransport() {
        return this.stompTransport;
    }
    
    public ActiveMQDestination createTempDestination(final String name, final boolean topic) {
        ActiveMQDestination rc = this.tempDestinations.get(name);
        if (rc == null) {
            if (topic) {
                rc = new ActiveMQTempTopic(this.connectionId, this.tempDestinationGenerator.getNextSequenceId());
            }
            else {
                rc = new ActiveMQTempQueue(this.connectionId, this.tempDestinationGenerator.getNextSequenceId());
            }
            this.sendToActiveMQ(new DestinationInfo(this.connectionId, (byte)0, rc), null);
            this.tempDestinations.put(name, rc);
            this.tempDestinationAmqToStompMap.put(rc.getQualifiedName(), name);
        }
        return rc;
    }
    
    public String getCreatedTempDestinationName(final ActiveMQDestination destination) {
        return this.tempDestinationAmqToStompMap.get(destination.getQualifiedName());
    }
    
    public String getDefaultHeartBeat() {
        return this.defaultHeartBeat;
    }
    
    public void setDefaultHeartBeat(final String defaultHeartBeat) {
        this.defaultHeartBeat = defaultHeartBeat;
    }
    
    public float getHbGracePeriodMultiplier() {
        return this.hbGracePeriodMultiplier;
    }
    
    public void setHbGracePeriodMultiplier(final float hbGracePeriodMultiplier) {
        this.hbGracePeriodMultiplier = hbGracePeriodMultiplier;
    }
    
    protected void configureInactivityMonitor(final String heartBeatConfig) throws ProtocolException {
        final String[] keepAliveOpts = heartBeatConfig.split(",");
        if (keepAliveOpts == null || keepAliveOpts.length != 2) {
            throw new ProtocolException("Invalid heart-beat header:" + heartBeatConfig, true);
        }
        try {
            this.hbReadInterval = Long.parseLong(keepAliveOpts[0]);
            this.hbWriteInterval = Long.parseLong(keepAliveOpts[1]);
        }
        catch (NumberFormatException e) {
            throw new ProtocolException("Invalid heart-beat header:" + heartBeatConfig, true);
        }
        try {
            final StompInactivityMonitor monitor = this.stompTransport.getInactivityMonitor();
            monitor.setReadCheckTime((long)(this.hbReadInterval * this.hbGracePeriodMultiplier));
            monitor.setInitialDelayTime(Math.min(this.hbReadInterval, this.hbWriteInterval));
            monitor.setWriteCheckTime(this.hbWriteInterval);
            monitor.startMonitoring();
        }
        catch (Exception ex) {
            this.hbReadInterval = 0L;
            this.hbWriteInterval = 0L;
        }
        if (ProtocolConverter.LOG.isDebugEnabled()) {
            ProtocolConverter.LOG.debug("Stomp Connect heartbeat conf RW[" + this.hbReadInterval + "," + this.hbWriteInterval + "]");
        }
    }
    
    protected void sendReceipt(final StompFrame command) {
        final String receiptId = command.getHeaders().get("receipt");
        if (receiptId != null) {
            final StompFrame sc = new StompFrame();
            sc.setAction("RECEIPT");
            sc.setHeaders(new HashMap<String, String>(1));
            sc.getHeaders().put("receipt-id", receiptId);
            try {
                this.sendToStomp(sc);
            }
            catch (IOException e) {
                ProtocolConverter.LOG.warn("Could not send a receipt for " + command, e);
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ProtocolConverter.class);
        CONNECTION_ID_GENERATOR = new IdGenerator();
        ping = new StompFrame("KEEPALIVE");
        InputStream in = null;
        String version = "5.6.0";
        if ((in = ProtocolConverter.class.getResourceAsStream("/org/apache/activemq/version.txt")) != null) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            try {
                version = reader.readLine();
            }
            catch (Exception ex) {}
        }
        BROKER_VERSION = version;
    }
    
    private static class AckEntry
    {
        private final String messageId;
        private final StompSubscription subscription;
        
        public AckEntry(final String messageId, final StompSubscription subscription) {
            this.messageId = messageId;
            this.subscription = subscription;
        }
        
        public MessageAck onMessageAck(final TransactionId transactionId) {
            return this.subscription.onStompMessageAck(this.messageId, transactionId);
        }
        
        public MessageAck onMessageNack(final TransactionId transactionId) throws ProtocolException {
            return this.subscription.onStompMessageNack(this.messageId, transactionId);
        }
        
        public String getMessageId() {
            return this.messageId;
        }
        
        public StompSubscription getSubscription() {
            return this.subscription;
        }
    }
}
