// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import org.slf4j.LoggerFactory;
import org.apache.activemq.thread.Scheduler;
import org.apache.activemq.command.ActiveMQTempDestination;
import java.util.Collections;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.ConsumerId;
import javax.jms.TopicPublisher;
import javax.jms.QueueSender;
import javax.jms.QueueReceiver;
import javax.jms.TemporaryTopic;
import javax.jms.TemporaryQueue;
import javax.jms.QueueBrowser;
import javax.jms.InvalidDestinationException;
import javax.jms.TopicSubscriber;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTempQueue;
import javax.jms.Queue;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.Topic;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Destination;
import javax.jms.TransactionRolledBackException;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.util.Callback;
import org.apache.activemq.command.MessageAck;
import java.util.Iterator;
import org.apache.activemq.command.RemoveInfo;
import org.apache.activemq.transaction.Synchronization;
import javax.jms.IllegalStateException;
import java.io.InputStream;
import org.apache.activemq.blob.BlobUploader;
import java.io.File;
import org.apache.activemq.blob.BlobDownloader;
import org.apache.activemq.command.ActiveMQBlobMessage;
import java.net.URL;
import org.apache.activemq.command.ActiveMQTextMessage;
import javax.jms.TextMessage;
import org.apache.activemq.command.ActiveMQStreamMessage;
import javax.jms.StreamMessage;
import java.io.Serializable;
import org.apache.activemq.command.ActiveMQObjectMessage;
import javax.jms.ObjectMessage;
import javax.jms.Message;
import org.apache.activemq.command.ActiveMQMapMessage;
import javax.jms.MapMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQBytesMessage;
import javax.jms.BytesMessage;
import org.apache.activemq.management.StatsImpl;
import javax.jms.JMSException;
import org.apache.activemq.command.Command;
import java.util.List;
import org.apache.activemq.command.SessionId;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.blob.BlobTransferPolicy;
import org.apache.activemq.management.JMSSessionStatsImpl;
import javax.jms.MessageListener;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.command.SessionInfo;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.apache.activemq.management.StatsCapable;
import javax.jms.TopicSession;
import javax.jms.QueueSession;
import javax.jms.Session;

public class ActiveMQSession implements Session, QueueSession, TopicSession, StatsCapable, ActiveMQDispatcher
{
    public static final int INDIVIDUAL_ACKNOWLEDGE = 4;
    public static final int MAX_ACK_CONSTANT = 4;
    private static final Logger LOG;
    private final ThreadPoolExecutor connectionExecutor;
    protected int acknowledgementMode;
    protected final ActiveMQConnection connection;
    protected final SessionInfo info;
    protected final LongSequenceGenerator consumerIdGenerator;
    protected final LongSequenceGenerator producerIdGenerator;
    protected final LongSequenceGenerator deliveryIdGenerator;
    protected final ActiveMQSessionExecutor executor;
    protected final AtomicBoolean started;
    protected final CopyOnWriteArrayList<ActiveMQMessageConsumer> consumers;
    protected final CopyOnWriteArrayList<ActiveMQMessageProducer> producers;
    protected boolean closed;
    private volatile boolean synchronizationRegistered;
    protected boolean asyncDispatch;
    protected boolean sessionAsyncDispatch;
    protected final boolean debug;
    protected Object sendMutex;
    private final AtomicBoolean clearInProgress;
    private MessageListener messageListener;
    private final JMSSessionStatsImpl stats;
    private TransactionContext transactionContext;
    private DeliveryListener deliveryListener;
    private MessageTransformer transformer;
    private BlobTransferPolicy blobTransferPolicy;
    private long lastDeliveredSequenceId;
    final AtomicInteger clearRequestsCounter;
    
    protected ActiveMQSession(final ActiveMQConnection connection, final SessionId sessionId, final int acknowledgeMode, final boolean asyncDispatch, final boolean sessionAsyncDispatch) throws JMSException {
        this.consumerIdGenerator = new LongSequenceGenerator();
        this.producerIdGenerator = new LongSequenceGenerator();
        this.deliveryIdGenerator = new LongSequenceGenerator();
        this.started = new AtomicBoolean(false);
        this.consumers = new CopyOnWriteArrayList<ActiveMQMessageConsumer>();
        this.producers = new CopyOnWriteArrayList<ActiveMQMessageProducer>();
        this.sendMutex = new Object();
        this.clearInProgress = new AtomicBoolean();
        this.clearRequestsCounter = new AtomicInteger(0);
        this.debug = ActiveMQSession.LOG.isDebugEnabled();
        this.connection = connection;
        this.acknowledgementMode = acknowledgeMode;
        this.asyncDispatch = asyncDispatch;
        this.sessionAsyncDispatch = sessionAsyncDispatch;
        this.info = new SessionInfo(connection.getConnectionInfo(), sessionId.getValue());
        this.setTransactionContext(new TransactionContext(connection));
        this.stats = new JMSSessionStatsImpl(this.producers, this.consumers);
        this.connection.asyncSendPacket(this.info);
        this.setTransformer(connection.getTransformer());
        this.setBlobTransferPolicy(connection.getBlobTransferPolicy());
        this.connectionExecutor = connection.getExecutor();
        this.executor = new ActiveMQSessionExecutor(this);
        connection.addSession(this);
        if (connection.isStarted()) {
            this.start();
        }
    }
    
    protected ActiveMQSession(final ActiveMQConnection connection, final SessionId sessionId, final int acknowledgeMode, final boolean asyncDispatch) throws JMSException {
        this(connection, sessionId, acknowledgeMode, asyncDispatch, true);
    }
    
    public void setTransactionContext(final TransactionContext transactionContext) {
        this.transactionContext = transactionContext;
    }
    
    public TransactionContext getTransactionContext() {
        return this.transactionContext;
    }
    
    @Override
    public StatsImpl getStats() {
        return this.stats;
    }
    
    public JMSSessionStatsImpl getSessionStats() {
        return this.stats;
    }
    
    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        final ActiveMQBytesMessage message = new ActiveMQBytesMessage();
        this.configureMessage(message);
        return message;
    }
    
    @Override
    public MapMessage createMapMessage() throws JMSException {
        final ActiveMQMapMessage message = new ActiveMQMapMessage();
        this.configureMessage(message);
        return message;
    }
    
    @Override
    public Message createMessage() throws JMSException {
        final ActiveMQMessage message = new ActiveMQMessage();
        this.configureMessage(message);
        return message;
    }
    
    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        final ActiveMQObjectMessage message = new ActiveMQObjectMessage();
        this.configureMessage(message);
        return message;
    }
    
    @Override
    public ObjectMessage createObjectMessage(final Serializable object) throws JMSException {
        final ActiveMQObjectMessage message = new ActiveMQObjectMessage();
        this.configureMessage(message);
        message.setObject(object);
        return message;
    }
    
    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        final ActiveMQStreamMessage message = new ActiveMQStreamMessage();
        this.configureMessage(message);
        return message;
    }
    
    @Override
    public TextMessage createTextMessage() throws JMSException {
        final ActiveMQTextMessage message = new ActiveMQTextMessage();
        this.configureMessage(message);
        return message;
    }
    
    @Override
    public TextMessage createTextMessage(final String text) throws JMSException {
        final ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setText(text);
        this.configureMessage(message);
        return message;
    }
    
    public BlobMessage createBlobMessage(final URL url) throws JMSException {
        return this.createBlobMessage(url, false);
    }
    
    public BlobMessage createBlobMessage(final URL url, final boolean deletedByBroker) throws JMSException {
        final ActiveMQBlobMessage message = new ActiveMQBlobMessage();
        this.configureMessage(message);
        message.setURL(url);
        message.setDeletedByBroker(deletedByBroker);
        message.setBlobDownloader(new BlobDownloader(this.getBlobTransferPolicy()));
        return message;
    }
    
    public BlobMessage createBlobMessage(final File file) throws JMSException {
        final ActiveMQBlobMessage message = new ActiveMQBlobMessage();
        this.configureMessage(message);
        message.setBlobUploader(new BlobUploader(this.getBlobTransferPolicy(), file));
        message.setBlobDownloader(new BlobDownloader(this.getBlobTransferPolicy()));
        message.setDeletedByBroker(true);
        message.setName(file.getName());
        return message;
    }
    
    public BlobMessage createBlobMessage(final InputStream in) throws JMSException {
        final ActiveMQBlobMessage message = new ActiveMQBlobMessage();
        this.configureMessage(message);
        message.setBlobUploader(new BlobUploader(this.getBlobTransferPolicy(), in));
        message.setBlobDownloader(new BlobDownloader(this.getBlobTransferPolicy()));
        message.setDeletedByBroker(true);
        return message;
    }
    
    @Override
    public boolean getTransacted() throws JMSException {
        this.checkClosed();
        return this.isTransacted();
    }
    
    @Override
    public int getAcknowledgeMode() throws JMSException {
        this.checkClosed();
        return this.acknowledgementMode;
    }
    
    @Override
    public void commit() throws JMSException {
        this.checkClosed();
        if (!this.getTransacted()) {
            throw new IllegalStateException("Not a transacted session");
        }
        if (ActiveMQSession.LOG.isDebugEnabled()) {
            ActiveMQSession.LOG.debug(this.getSessionId() + " Transaction Commit :" + this.transactionContext.getTransactionId());
        }
        this.transactionContext.commit();
    }
    
    @Override
    public void rollback() throws JMSException {
        this.checkClosed();
        if (!this.getTransacted()) {
            throw new IllegalStateException("Not a transacted session");
        }
        if (ActiveMQSession.LOG.isDebugEnabled()) {
            ActiveMQSession.LOG.debug(this.getSessionId() + " Transaction Rollback, txid:" + this.transactionContext.getTransactionId());
        }
        this.transactionContext.rollback();
    }
    
    @Override
    public void close() throws JMSException {
        if (!this.closed) {
            if (this.getTransactionContext().isInXATransaction()) {
                if (!this.synchronizationRegistered) {
                    this.synchronizationRegistered = true;
                    this.getTransactionContext().addSynchronization(new Synchronization() {
                        @Override
                        public void afterCommit() throws Exception {
                            ActiveMQSession.this.doClose();
                            ActiveMQSession.this.synchronizationRegistered = false;
                        }
                        
                        @Override
                        public void afterRollback() throws Exception {
                            ActiveMQSession.this.doClose();
                            ActiveMQSession.this.synchronizationRegistered = false;
                        }
                    });
                }
            }
            else {
                this.doClose();
            }
        }
    }
    
    private void doClose() throws JMSException {
        final boolean interrupted = Thread.interrupted();
        this.dispose();
        final RemoveInfo removeCommand = this.info.createRemoveCommand();
        removeCommand.setLastDeliveredSequenceId(this.lastDeliveredSequenceId);
        this.connection.asyncSendPacket(removeCommand);
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
    
    void clearMessagesInProgress(final AtomicInteger transportInterruptionProcessingComplete) {
        this.clearRequestsCounter.incrementAndGet();
        this.executor.clearMessagesInProgress();
        if (this.consumers.isEmpty()) {
            return;
        }
        if (this.clearInProgress.compareAndSet(false, true)) {
            for (final ActiveMQMessageConsumer consumer : this.consumers) {
                consumer.inProgressClearRequired();
                transportInterruptionProcessingComplete.incrementAndGet();
                try {
                    this.connection.getScheduler().executeAfterDelay(new Runnable() {
                        @Override
                        public void run() {
                            consumer.clearMessagesInProgress();
                        }
                    }, 0L);
                }
                catch (JMSException e) {
                    this.connection.onClientInternalException(e);
                }
            }
            try {
                this.connection.getScheduler().executeAfterDelay(new Runnable() {
                    @Override
                    public void run() {
                        ActiveMQSession.this.clearInProgress.set(false);
                    }
                }, 0L);
            }
            catch (JMSException e2) {
                this.connection.onClientInternalException(e2);
            }
        }
    }
    
    void deliverAcks() {
        for (final ActiveMQMessageConsumer consumer : this.consumers) {
            consumer.deliverAcks();
        }
    }
    
    public synchronized void dispose() throws JMSException {
        if (!this.closed) {
            try {
                this.executor.stop();
                for (final ActiveMQMessageConsumer consumer : this.consumers) {
                    consumer.setFailureError(this.connection.getFirstFailureError());
                    consumer.dispose();
                    this.lastDeliveredSequenceId = Math.max(this.lastDeliveredSequenceId, consumer.getLastDeliveredSequenceId());
                }
                this.consumers.clear();
                for (final ActiveMQMessageProducer producer : this.producers) {
                    producer.dispose();
                }
                this.producers.clear();
                try {
                    if (this.getTransactionContext().isInLocalTransaction()) {
                        this.rollback();
                    }
                }
                catch (JMSException ex) {}
            }
            finally {
                this.connection.removeSession(this);
                this.transactionContext = null;
                this.closed = true;
            }
        }
    }
    
    protected void configureMessage(final ActiveMQMessage message) throws IllegalStateException {
        this.checkClosed();
        message.setConnection(this.connection);
    }
    
    protected void checkClosed() throws IllegalStateException {
        if (this.closed) {
            throw new IllegalStateException("The Session is closed");
        }
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    @Override
    public void recover() throws JMSException {
        this.checkClosed();
        if (this.getTransacted()) {
            throw new IllegalStateException("This session is transacted");
        }
        for (final ActiveMQMessageConsumer c : this.consumers) {
            c.rollback();
        }
    }
    
    @Override
    public MessageListener getMessageListener() throws JMSException {
        this.checkClosed();
        return this.messageListener;
    }
    
    @Override
    public void setMessageListener(final MessageListener listener) throws JMSException {
        if (listener != null) {
            this.checkClosed();
        }
        if ((this.messageListener = listener) != null) {
            this.executor.setDispatchedBySessionPool(true);
        }
    }
    
    @Override
    public void run() {
        MessageDispatch messageDispatch;
        while ((messageDispatch = this.executor.dequeueNoWait()) != null) {
            final MessageDispatch md = messageDispatch;
            final ActiveMQMessage message = (ActiveMQMessage)md.getMessage();
            MessageAck earlyAck = null;
            if (message.isExpired()) {
                earlyAck = new MessageAck(md, (byte)6, 1);
            }
            else if (this.connection.isDuplicate(this, message)) {
                ActiveMQSession.LOG.debug("{} got duplicate: {}", this, message.getMessageId());
                earlyAck = new MessageAck(md, (byte)1, 1);
                earlyAck.setFirstMessageId(md.getMessage().getMessageId());
                earlyAck.setPoisonCause(new Throwable("Duplicate delivery to " + this));
            }
            if (earlyAck != null) {
                try {
                    this.asyncSendPacket(earlyAck);
                }
                catch (Throwable t) {
                    ActiveMQSession.LOG.error("error dispatching ack: {} ", earlyAck, t);
                    this.connection.onClientInternalException(t);
                }
            }
            else {
                if (this.isClientAcknowledge() || this.isIndividualAcknowledge()) {
                    message.setAcknowledgeCallback(new Callback() {
                        @Override
                        public void execute() throws Exception {
                        }
                    });
                }
                if (this.deliveryListener != null) {
                    this.deliveryListener.beforeDelivery(this, message);
                }
                md.setDeliverySequenceId(this.getNextDeliveryId());
                final MessageAck ack = new MessageAck(md, (byte)2, 1);
                try {
                    ack.setFirstMessageId(md.getMessage().getMessageId());
                    this.doStartTransaction();
                    ack.setTransactionId(this.getTransactionContext().getTransactionId());
                    if (ack.getTransactionId() != null) {
                        this.getTransactionContext().addSynchronization(new Synchronization() {
                            final int clearRequestCount = (ActiveMQSession.this.clearRequestsCounter.get() == Integer.MAX_VALUE) ? ActiveMQSession.this.clearRequestsCounter.incrementAndGet() : ActiveMQSession.this.clearRequestsCounter.get();
                            
                            @Override
                            public void beforeEnd() throws Exception {
                                if (ack.getTransactionId().isXATransaction() && !ActiveMQSession.this.connection.hasDispatcher(ack.getConsumerId())) {
                                    ActiveMQSession.LOG.debug("forcing rollback - {} consumer no longer active on {}", ack, ActiveMQSession.this.connection);
                                    throw new TransactionRolledBackException("consumer " + ack.getConsumerId() + " no longer active on " + ActiveMQSession.this.connection);
                                }
                                ActiveMQSession.LOG.trace("beforeEnd ack {}", ack);
                                ActiveMQSession.this.sendAck(ack);
                            }
                            
                            @Override
                            public void afterRollback() throws Exception {
                                ActiveMQSession.LOG.trace("rollback {}", ack, new Throwable("here"));
                                md.getMessage().onMessageRolledBack();
                                ActiveMQSession.this.connection.rollbackDuplicate(ActiveMQSession.this, md.getMessage());
                                if (ActiveMQSession.this.clearRequestsCounter.get() > this.clearRequestCount) {
                                    ActiveMQSession.LOG.debug("No redelivery of {} on rollback of {} due to failover of {}", md, ack.getTransactionId(), ActiveMQSession.this.connection.getTransport());
                                    return;
                                }
                                if (ack.getTransactionId().isXATransaction() && !ActiveMQSession.this.connection.hasDispatcher(ack.getConsumerId())) {
                                    ActiveMQSession.LOG.debug("No local redelivery of {} on rollback of {} because consumer is no longer active on {}", md, ack.getTransactionId(), ActiveMQSession.this.connection.getTransport());
                                    return;
                                }
                                final RedeliveryPolicy redeliveryPolicy = ActiveMQSession.this.connection.getRedeliveryPolicy();
                                final int redeliveryCounter = md.getMessage().getRedeliveryCounter();
                                if (redeliveryPolicy.getMaximumRedeliveries() != -1 && redeliveryCounter > redeliveryPolicy.getMaximumRedeliveries()) {
                                    final MessageAck ack = new MessageAck(md, (byte)1, 1);
                                    ack.setFirstMessageId(md.getMessage().getMessageId());
                                    ack.setPoisonCause(new Throwable("Exceeded ra redelivery policy limit:" + redeliveryPolicy));
                                    ActiveMQSession.this.asyncSendPacket(ack);
                                }
                                else {
                                    final MessageAck ack = new MessageAck(md, (byte)3, 1);
                                    ack.setFirstMessageId(md.getMessage().getMessageId());
                                    ActiveMQSession.this.asyncSendPacket(ack);
                                    long redeliveryDelay = redeliveryPolicy.getInitialRedeliveryDelay();
                                    for (int i = 0; i < redeliveryCounter; ++i) {
                                        redeliveryDelay = redeliveryPolicy.getNextRedeliveryDelay(redeliveryDelay);
                                    }
                                    ActiveMQSession.this.connection.getScheduler().executeAfterDelay(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((ActiveMQDispatcher)md.getConsumer()).dispatch(md);
                                        }
                                    }, redeliveryDelay);
                                }
                            }
                        });
                    }
                    ActiveMQSession.LOG.trace("{} onMessage({})", this, message.getMessageId());
                    this.messageListener.onMessage(message);
                }
                catch (Throwable e) {
                    ActiveMQSession.LOG.error("error dispatching message: ", e);
                    this.connection.onClientInternalException(e);
                    if (ack.getTransactionId() == null) {
                        try {
                            this.asyncSendPacket(ack);
                        }
                        catch (Throwable e) {
                            this.connection.onClientInternalException(e);
                        }
                    }
                }
                finally {
                    if (ack.getTransactionId() == null) {
                        try {
                            this.asyncSendPacket(ack);
                        }
                        catch (Throwable e2) {
                            this.connection.onClientInternalException(e2);
                        }
                    }
                }
                if (this.deliveryListener == null) {
                    continue;
                }
                this.deliveryListener.afterDelivery(this, message);
            }
        }
    }
    
    @Override
    public MessageProducer createProducer(final Destination destination) throws JMSException {
        this.checkClosed();
        if (destination instanceof CustomDestination) {
            final CustomDestination customDestination = (CustomDestination)destination;
            return customDestination.createProducer(this);
        }
        final int timeSendOut = this.connection.getSendTimeout();
        return new ActiveMQMessageProducer(this, this.getNextProducerId(), ActiveMQMessageTransformation.transformDestination(destination), timeSendOut);
    }
    
    @Override
    public MessageConsumer createConsumer(final Destination destination) throws JMSException {
        return this.createConsumer(destination, (String)null);
    }
    
    @Override
    public MessageConsumer createConsumer(final Destination destination, final String messageSelector) throws JMSException {
        return this.createConsumer(destination, messageSelector, false);
    }
    
    public MessageConsumer createConsumer(final Destination destination, final MessageListener messageListener) throws JMSException {
        return this.createConsumer(destination, null, messageListener);
    }
    
    public MessageConsumer createConsumer(final Destination destination, final String messageSelector, final MessageListener messageListener) throws JMSException {
        return this.createConsumer(destination, messageSelector, false, messageListener);
    }
    
    @Override
    public MessageConsumer createConsumer(final Destination destination, final String messageSelector, final boolean noLocal) throws JMSException {
        return this.createConsumer(destination, messageSelector, noLocal, null);
    }
    
    public MessageConsumer createConsumer(final Destination destination, final String messageSelector, final boolean noLocal, final MessageListener messageListener) throws JMSException {
        this.checkClosed();
        if (destination instanceof CustomDestination) {
            final CustomDestination customDestination = (CustomDestination)destination;
            return customDestination.createConsumer(this, messageSelector, noLocal);
        }
        final ActiveMQPrefetchPolicy prefetchPolicy = this.connection.getPrefetchPolicy();
        int prefetch = 0;
        if (destination instanceof Topic) {
            prefetch = prefetchPolicy.getTopicPrefetch();
        }
        else {
            prefetch = prefetchPolicy.getQueuePrefetch();
        }
        final ActiveMQDestination activemqDestination = ActiveMQMessageTransformation.transformDestination(destination);
        return new ActiveMQMessageConsumer(this, this.getNextConsumerId(), activemqDestination, null, messageSelector, prefetch, prefetchPolicy.getMaximumPendingMessageLimit(), noLocal, false, this.isAsyncDispatch(), messageListener);
    }
    
    @Override
    public Queue createQueue(final String queueName) throws JMSException {
        this.checkClosed();
        if (queueName.startsWith("ID:")) {
            return new ActiveMQTempQueue(queueName);
        }
        return new ActiveMQQueue(queueName);
    }
    
    @Override
    public Topic createTopic(final String topicName) throws JMSException {
        this.checkClosed();
        if (topicName.startsWith("ID:")) {
            return new ActiveMQTempTopic(topicName);
        }
        return new ActiveMQTopic(topicName);
    }
    
    @Override
    public TopicSubscriber createDurableSubscriber(final Topic topic, final String name) throws JMSException {
        this.checkClosed();
        return this.createDurableSubscriber(topic, name, null, false);
    }
    
    @Override
    public TopicSubscriber createDurableSubscriber(final Topic topic, final String name, final String messageSelector, final boolean noLocal) throws JMSException {
        this.checkClosed();
        if (topic == null) {
            throw new InvalidDestinationException("Topic cannot be null");
        }
        if (topic instanceof CustomDestination) {
            final CustomDestination customDestination = (CustomDestination)topic;
            return customDestination.createDurableSubscriber(this, name, messageSelector, noLocal);
        }
        this.connection.checkClientIDWasManuallySpecified();
        final ActiveMQPrefetchPolicy prefetchPolicy = this.connection.getPrefetchPolicy();
        final int prefetch = (this.isAutoAcknowledge() && this.connection.isOptimizedMessageDispatch()) ? prefetchPolicy.getOptimizeDurableTopicPrefetch() : prefetchPolicy.getDurableTopicPrefetch();
        final int maxPrendingLimit = prefetchPolicy.getMaximumPendingMessageLimit();
        return new ActiveMQTopicSubscriber(this, this.getNextConsumerId(), ActiveMQMessageTransformation.transformDestination(topic), name, messageSelector, prefetch, maxPrendingLimit, noLocal, false, this.asyncDispatch);
    }
    
    @Override
    public QueueBrowser createBrowser(final Queue queue) throws JMSException {
        this.checkClosed();
        return this.createBrowser(queue, null);
    }
    
    @Override
    public QueueBrowser createBrowser(final Queue queue, final String messageSelector) throws JMSException {
        this.checkClosed();
        return new ActiveMQQueueBrowser(this, this.getNextConsumerId(), ActiveMQMessageTransformation.transformDestination(queue), messageSelector, this.asyncDispatch);
    }
    
    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        this.checkClosed();
        return (TemporaryQueue)this.connection.createTempDestination(false);
    }
    
    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        this.checkClosed();
        return (TemporaryTopic)this.connection.createTempDestination(true);
    }
    
    @Override
    public QueueReceiver createReceiver(final Queue queue) throws JMSException {
        this.checkClosed();
        return this.createReceiver(queue, null);
    }
    
    @Override
    public QueueReceiver createReceiver(final Queue queue, final String messageSelector) throws JMSException {
        this.checkClosed();
        if (queue instanceof CustomDestination) {
            final CustomDestination customDestination = (CustomDestination)queue;
            return customDestination.createReceiver(this, messageSelector);
        }
        final ActiveMQPrefetchPolicy prefetchPolicy = this.connection.getPrefetchPolicy();
        return new ActiveMQQueueReceiver(this, this.getNextConsumerId(), ActiveMQMessageTransformation.transformDestination(queue), messageSelector, prefetchPolicy.getQueuePrefetch(), prefetchPolicy.getMaximumPendingMessageLimit(), this.asyncDispatch);
    }
    
    @Override
    public QueueSender createSender(final Queue queue) throws JMSException {
        this.checkClosed();
        if (queue instanceof CustomDestination) {
            final CustomDestination customDestination = (CustomDestination)queue;
            return customDestination.createSender(this);
        }
        final int timeSendOut = this.connection.getSendTimeout();
        return new ActiveMQQueueSender(this, ActiveMQMessageTransformation.transformDestination(queue), timeSendOut);
    }
    
    @Override
    public TopicSubscriber createSubscriber(final Topic topic) throws JMSException {
        this.checkClosed();
        return this.createSubscriber(topic, null, false);
    }
    
    @Override
    public TopicSubscriber createSubscriber(final Topic topic, final String messageSelector, final boolean noLocal) throws JMSException {
        this.checkClosed();
        if (topic instanceof CustomDestination) {
            final CustomDestination customDestination = (CustomDestination)topic;
            return customDestination.createSubscriber(this, messageSelector, noLocal);
        }
        final ActiveMQPrefetchPolicy prefetchPolicy = this.connection.getPrefetchPolicy();
        return new ActiveMQTopicSubscriber(this, this.getNextConsumerId(), ActiveMQMessageTransformation.transformDestination(topic), null, messageSelector, prefetchPolicy.getTopicPrefetch(), prefetchPolicy.getMaximumPendingMessageLimit(), noLocal, false, this.asyncDispatch);
    }
    
    @Override
    public TopicPublisher createPublisher(final Topic topic) throws JMSException {
        this.checkClosed();
        if (topic instanceof CustomDestination) {
            final CustomDestination customDestination = (CustomDestination)topic;
            return customDestination.createPublisher(this);
        }
        final int timeSendOut = this.connection.getSendTimeout();
        return new ActiveMQTopicPublisher(this, ActiveMQMessageTransformation.transformDestination(topic), timeSendOut);
    }
    
    @Override
    public void unsubscribe(final String name) throws JMSException {
        this.checkClosed();
        this.connection.unsubscribe(name);
    }
    
    @Override
    public void dispatch(final MessageDispatch messageDispatch) {
        try {
            this.executor.execute(messageDispatch);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.connection.onClientInternalException(e);
        }
    }
    
    public void acknowledge() throws JMSException {
        for (final ActiveMQMessageConsumer c : this.consumers) {
            c.acknowledge();
        }
    }
    
    protected void addConsumer(final ActiveMQMessageConsumer consumer) throws JMSException {
        this.consumers.add(consumer);
        if (consumer.isDurableSubscriber()) {
            this.stats.onCreateDurableSubscriber();
        }
        this.connection.addDispatcher(consumer.getConsumerId(), this);
    }
    
    protected void removeConsumer(final ActiveMQMessageConsumer consumer) {
        this.connection.removeDispatcher(consumer.getConsumerId());
        if (consumer.isDurableSubscriber()) {
            this.stats.onRemoveDurableSubscriber();
        }
        this.consumers.remove(consumer);
        this.connection.removeDispatcher(consumer);
    }
    
    protected void addProducer(final ActiveMQMessageProducer producer) throws JMSException {
        this.producers.add(producer);
        this.connection.addProducer(producer.getProducerInfo().getProducerId(), producer);
    }
    
    protected void removeProducer(final ActiveMQMessageProducer producer) {
        this.connection.removeProducer(producer.getProducerInfo().getProducerId());
        this.producers.remove(producer);
    }
    
    protected void start() throws JMSException {
        this.started.set(true);
        for (final ActiveMQMessageConsumer c : this.consumers) {
            c.start();
        }
        this.executor.start();
    }
    
    protected void stop() throws JMSException {
        for (final ActiveMQMessageConsumer c : this.consumers) {
            c.stop();
        }
        this.started.set(false);
        this.executor.stop();
    }
    
    protected SessionId getSessionId() {
        return this.info.getSessionId();
    }
    
    protected ConsumerId getNextConsumerId() {
        return new ConsumerId(this.info.getSessionId(), this.consumerIdGenerator.getNextSequenceId());
    }
    
    protected ProducerId getNextProducerId() {
        return new ProducerId(this.info.getSessionId(), this.producerIdGenerator.getNextSequenceId());
    }
    
    protected void send(final ActiveMQMessageProducer producer, final ActiveMQDestination destination, final Message message, final int deliveryMode, final int priority, final long timeToLive, final MemoryUsage producerWindow, final int sendTimeout, final AsyncCallback onComplete) throws JMSException {
        this.checkClosed();
        if (destination.isTemporary() && this.connection.isDeleted(destination)) {
            throw new InvalidDestinationException("Cannot publish to a deleted Destination: " + destination);
        }
        synchronized (this.sendMutex) {
            this.doStartTransaction();
            final TransactionId txid = this.transactionContext.getTransactionId();
            final long sequenceNumber = producer.getMessageSequence();
            message.setJMSDeliveryMode(deliveryMode);
            long expiration = 0L;
            if (!producer.getDisableMessageTimestamp()) {
                final long timeStamp = System.currentTimeMillis();
                message.setJMSTimestamp(timeStamp);
                if (timeToLive > 0L) {
                    expiration = timeToLive + timeStamp;
                }
            }
            message.setJMSExpiration(expiration);
            message.setJMSPriority(priority);
            message.setJMSRedelivered(false);
            ActiveMQMessage msg = ActiveMQMessageTransformation.transformMessage(message, this.connection);
            msg.setDestination(destination);
            msg.setMessageId(new MessageId(producer.getProducerInfo().getProducerId(), sequenceNumber));
            if (msg != message) {
                message.setJMSMessageID(msg.getMessageId().toString());
                message.setJMSDestination(destination);
            }
            msg.setBrokerPath(null);
            msg.setTransactionId(txid);
            if (this.connection.isCopyMessageOnSend()) {
                msg = (ActiveMQMessage)msg.copy();
            }
            msg.setConnection(this.connection);
            msg.onSend();
            msg.setProducerId(msg.getMessageId().getProducerId());
            if (ActiveMQSession.LOG.isTraceEnabled()) {
                ActiveMQSession.LOG.trace(this.getSessionId() + " sending message: " + msg);
            }
            if (onComplete == null && sendTimeout <= 0 && !msg.isResponseRequired() && !this.connection.isAlwaysSyncSend() && (!msg.isPersistent() || this.connection.isUseAsyncSend() || txid != null)) {
                this.connection.asyncSendPacket(msg);
                if (producerWindow != null) {
                    final int size = msg.getSize();
                    producerWindow.increaseUsage(size);
                }
            }
            else if (sendTimeout > 0 && onComplete == null) {
                this.connection.syncSendPacket(msg, sendTimeout);
            }
            else {
                this.connection.syncSendPacket(msg, onComplete);
            }
        }
    }
    
    protected void doStartTransaction() throws JMSException {
        if (this.getTransacted() && !this.transactionContext.isInXATransaction()) {
            this.transactionContext.begin();
        }
    }
    
    public boolean hasUncomsumedMessages() {
        return this.executor.hasUncomsumedMessages();
    }
    
    public boolean isTransacted() {
        return this.acknowledgementMode == 0 || this.transactionContext.isInXATransaction();
    }
    
    protected boolean isClientAcknowledge() {
        return this.acknowledgementMode == 2;
    }
    
    public boolean isAutoAcknowledge() {
        return this.acknowledgementMode == 1;
    }
    
    public boolean isDupsOkAcknowledge() {
        return this.acknowledgementMode == 3;
    }
    
    public boolean isIndividualAcknowledge() {
        return this.acknowledgementMode == 4;
    }
    
    public DeliveryListener getDeliveryListener() {
        return this.deliveryListener;
    }
    
    public void setDeliveryListener(final DeliveryListener deliveryListener) {
        this.deliveryListener = deliveryListener;
    }
    
    protected SessionInfo getSessionInfo() throws JMSException {
        final SessionInfo info = new SessionInfo(this.connection.getConnectionInfo(), this.getSessionId().getValue());
        return info;
    }
    
    public void asyncSendPacket(final Command command) throws JMSException {
        this.connection.asyncSendPacket(command);
    }
    
    public Response syncSendPacket(final Command command) throws JMSException {
        return this.connection.syncSendPacket(command);
    }
    
    public long getNextDeliveryId() {
        return this.deliveryIdGenerator.getNextSequenceId();
    }
    
    public void redispatch(final ActiveMQDispatcher dispatcher, final MessageDispatchChannel unconsumedMessages) throws JMSException {
        final List<MessageDispatch> c = unconsumedMessages.removeAll();
        for (final MessageDispatch md : c) {
            this.connection.rollbackDuplicate(dispatcher, md.getMessage());
        }
        Collections.reverse(c);
        for (final MessageDispatch md : c) {
            this.executor.executeFirst(md);
        }
    }
    
    public boolean isRunning() {
        return this.started.get();
    }
    
    public boolean isAsyncDispatch() {
        return this.asyncDispatch;
    }
    
    public void setAsyncDispatch(final boolean asyncDispatch) {
        this.asyncDispatch = asyncDispatch;
    }
    
    public boolean isSessionAsyncDispatch() {
        return this.sessionAsyncDispatch;
    }
    
    public void setSessionAsyncDispatch(final boolean sessionAsyncDispatch) {
        this.sessionAsyncDispatch = sessionAsyncDispatch;
    }
    
    public MessageTransformer getTransformer() {
        return this.transformer;
    }
    
    public ActiveMQConnection getConnection() {
        return this.connection;
    }
    
    public void setTransformer(final MessageTransformer transformer) {
        this.transformer = transformer;
    }
    
    public BlobTransferPolicy getBlobTransferPolicy() {
        return this.blobTransferPolicy;
    }
    
    public void setBlobTransferPolicy(final BlobTransferPolicy blobTransferPolicy) {
        this.blobTransferPolicy = blobTransferPolicy;
    }
    
    public List<MessageDispatch> getUnconsumedMessages() {
        return this.executor.getUnconsumedMessages();
    }
    
    @Override
    public String toString() {
        return "ActiveMQSession {id=" + this.info.getSessionId() + ",started=" + this.started.get() + "}";
    }
    
    public void checkMessageListener() throws JMSException {
        if (this.messageListener != null) {
            throw new IllegalStateException("Cannot synchronously receive a message when a MessageListener is set");
        }
        for (final ActiveMQMessageConsumer consumer : this.consumers) {
            if (consumer.hasMessageListener()) {
                throw new IllegalStateException("Cannot synchronously receive a message when a MessageListener is set");
            }
        }
    }
    
    protected void setOptimizeAcknowledge(final boolean value) {
        for (final ActiveMQMessageConsumer c : this.consumers) {
            c.setOptimizeAcknowledge(value);
        }
    }
    
    protected void setPrefetchSize(final ConsumerId id, final int prefetch) {
        for (final ActiveMQMessageConsumer c : this.consumers) {
            if (c.getConsumerId().equals(id)) {
                c.setPrefetchSize(prefetch);
                break;
            }
        }
    }
    
    protected void close(final ConsumerId id) {
        for (final ActiveMQMessageConsumer c : this.consumers) {
            if (c.getConsumerId().equals(id)) {
                try {
                    c.close();
                }
                catch (JMSException e) {
                    ActiveMQSession.LOG.warn("Exception closing consumer", e);
                }
                ActiveMQSession.LOG.warn("Closed consumer on Command, " + id);
                break;
            }
        }
    }
    
    public boolean isInUse(final ActiveMQTempDestination destination) {
        for (final ActiveMQMessageConsumer c : this.consumers) {
            if (c.isInUse(destination)) {
                return true;
            }
        }
        return false;
    }
    
    public long getLastDeliveredSequenceId() {
        return this.lastDeliveredSequenceId;
    }
    
    protected void sendAck(final MessageAck ack) throws JMSException {
        this.sendAck(ack, false);
    }
    
    protected void sendAck(final MessageAck ack, final boolean lazy) throws JMSException {
        if (lazy || this.connection.isSendAcksAsync() || this.getTransacted()) {
            this.asyncSendPacket(ack);
        }
        else {
            this.syncSendPacket(ack);
        }
    }
    
    protected Scheduler getScheduler() throws JMSException {
        return this.connection.getScheduler();
    }
    
    protected ThreadPoolExecutor getConnectionExecutor() {
        return this.connectionExecutor;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ActiveMQSession.class);
    }
    
    public interface DeliveryListener
    {
        void beforeDelivery(final ActiveMQSession p0, final Message p1);
        
        void afterDelivery(final ActiveMQSession p0, final Message p1);
    }
}
