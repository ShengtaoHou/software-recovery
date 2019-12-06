// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import org.slf4j.LoggerFactory;
import javax.jms.QueueReceiver;
import javax.jms.TopicSubscriber;
import javax.jms.Destination;
import javax.transaction.xa.XAResource;
import javax.jms.Topic;
import javax.jms.TextMessage;
import javax.jms.TemporaryTopic;
import javax.jms.TemporaryQueue;
import javax.jms.StreamMessage;
import javax.jms.Queue;
import java.io.Serializable;
import javax.jms.ObjectMessage;
import javax.jms.Message;
import javax.jms.MapMessage;
import javax.jms.BytesMessage;
import java.util.Iterator;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.QueueSender;
import javax.jms.TopicPublisher;
import javax.jms.MessageProducer;
import javax.jms.QueueBrowser;
import javax.jms.MessageConsumer;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.pool.KeyedObjectPool;
import org.slf4j.Logger;
import javax.jms.XASession;
import javax.jms.QueueSession;
import javax.jms.TopicSession;
import javax.jms.Session;

public class PooledSession implements Session, TopicSession, QueueSession, XASession
{
    private static final transient Logger LOG;
    private final SessionKey key;
    private final KeyedObjectPool<SessionKey, PooledSession> sessionPool;
    private final CopyOnWriteArrayList<MessageConsumer> consumers;
    private final CopyOnWriteArrayList<QueueBrowser> browsers;
    private final CopyOnWriteArrayList<PooledSessionEventListener> sessionEventListeners;
    private MessageProducer producer;
    private TopicPublisher publisher;
    private QueueSender sender;
    private Session session;
    private boolean transactional;
    private boolean ignoreClose;
    private boolean isXa;
    private boolean useAnonymousProducers;
    
    public PooledSession(final SessionKey key, final Session session, final KeyedObjectPool<SessionKey, PooledSession> sessionPool, final boolean transactional, final boolean anonymous) {
        this.consumers = new CopyOnWriteArrayList<MessageConsumer>();
        this.browsers = new CopyOnWriteArrayList<QueueBrowser>();
        this.sessionEventListeners = new CopyOnWriteArrayList<PooledSessionEventListener>();
        this.transactional = true;
        this.useAnonymousProducers = true;
        this.key = key;
        this.session = session;
        this.sessionPool = sessionPool;
        this.transactional = transactional;
        this.useAnonymousProducers = anonymous;
    }
    
    public void addSessionEventListener(final PooledSessionEventListener listener) {
        if (!this.sessionEventListeners.contains(listener)) {
            this.sessionEventListeners.add(listener);
        }
    }
    
    protected boolean isIgnoreClose() {
        return this.ignoreClose;
    }
    
    protected void setIgnoreClose(final boolean ignoreClose) {
        this.ignoreClose = ignoreClose;
    }
    
    @Override
    public void close() throws JMSException {
        if (!this.ignoreClose) {
            boolean invalidate = false;
            try {
                this.getInternalSession().setMessageListener(null);
                for (final MessageConsumer consumer : this.consumers) {
                    consumer.close();
                }
                for (final QueueBrowser browser : this.browsers) {
                    browser.close();
                }
                if (this.transactional && !this.isXa) {
                    try {
                        this.getInternalSession().rollback();
                    }
                    catch (JMSException e) {
                        invalidate = true;
                        PooledSession.LOG.warn("Caught exception trying rollback() when putting session back into the pool, will invalidate. " + e, e);
                    }
                }
            }
            catch (JMSException ex) {
                invalidate = true;
                PooledSession.LOG.warn("Caught exception trying close() when putting session back into the pool, will invalidate. " + ex, ex);
            }
            finally {
                this.consumers.clear();
                this.browsers.clear();
                for (final PooledSessionEventListener listener : this.sessionEventListeners) {
                    listener.onSessionClosed(this);
                }
                this.sessionEventListeners.clear();
            }
            if (invalidate) {
                if (this.session != null) {
                    try {
                        this.session.close();
                    }
                    catch (JMSException e2) {
                        PooledSession.LOG.trace("Ignoring exception on close as discarding session: " + e2, e2);
                    }
                    this.session = null;
                }
                try {
                    this.sessionPool.invalidateObject((Object)this.key, (Object)this);
                }
                catch (Exception e3) {
                    PooledSession.LOG.trace("Ignoring exception on invalidateObject as discarding session: " + e3, e3);
                }
            }
            else {
                try {
                    this.sessionPool.returnObject((Object)this.key, (Object)this);
                }
                catch (Exception e3) {
                    final IllegalStateException illegalStateException = new IllegalStateException(e3.toString());
                    illegalStateException.initCause(e3);
                    throw illegalStateException;
                }
            }
        }
    }
    
    @Override
    public void commit() throws JMSException {
        this.getInternalSession().commit();
    }
    
    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        return this.getInternalSession().createBytesMessage();
    }
    
    @Override
    public MapMessage createMapMessage() throws JMSException {
        return this.getInternalSession().createMapMessage();
    }
    
    @Override
    public Message createMessage() throws JMSException {
        return this.getInternalSession().createMessage();
    }
    
    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        return this.getInternalSession().createObjectMessage();
    }
    
    @Override
    public ObjectMessage createObjectMessage(final Serializable serializable) throws JMSException {
        return this.getInternalSession().createObjectMessage(serializable);
    }
    
    @Override
    public Queue createQueue(final String s) throws JMSException {
        return this.getInternalSession().createQueue(s);
    }
    
    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        return this.getInternalSession().createStreamMessage();
    }
    
    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        final TemporaryQueue result = this.getInternalSession().createTemporaryQueue();
        for (final PooledSessionEventListener listener : this.sessionEventListeners) {
            listener.onTemporaryQueueCreate(result);
        }
        return result;
    }
    
    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        final TemporaryTopic result = this.getInternalSession().createTemporaryTopic();
        for (final PooledSessionEventListener listener : this.sessionEventListeners) {
            listener.onTemporaryTopicCreate(result);
        }
        return result;
    }
    
    @Override
    public void unsubscribe(final String s) throws JMSException {
        this.getInternalSession().unsubscribe(s);
    }
    
    @Override
    public TextMessage createTextMessage() throws JMSException {
        return this.getInternalSession().createTextMessage();
    }
    
    @Override
    public TextMessage createTextMessage(final String s) throws JMSException {
        return this.getInternalSession().createTextMessage(s);
    }
    
    @Override
    public Topic createTopic(final String s) throws JMSException {
        return this.getInternalSession().createTopic(s);
    }
    
    @Override
    public int getAcknowledgeMode() throws JMSException {
        return this.getInternalSession().getAcknowledgeMode();
    }
    
    @Override
    public boolean getTransacted() throws JMSException {
        return this.getInternalSession().getTransacted();
    }
    
    @Override
    public void recover() throws JMSException {
        this.getInternalSession().recover();
    }
    
    @Override
    public void rollback() throws JMSException {
        this.getInternalSession().rollback();
    }
    
    @Override
    public XAResource getXAResource() {
        if (this.session instanceof XASession) {
            return ((XASession)this.session).getXAResource();
        }
        return null;
    }
    
    @Override
    public Session getSession() {
        return this;
    }
    
    @Override
    public void run() {
        if (this.session != null) {
            this.session.run();
        }
    }
    
    @Override
    public QueueBrowser createBrowser(final Queue queue) throws JMSException {
        return this.addQueueBrowser(this.getInternalSession().createBrowser(queue));
    }
    
    @Override
    public QueueBrowser createBrowser(final Queue queue, final String selector) throws JMSException {
        return this.addQueueBrowser(this.getInternalSession().createBrowser(queue, selector));
    }
    
    @Override
    public MessageConsumer createConsumer(final Destination destination) throws JMSException {
        return this.addConsumer(this.getInternalSession().createConsumer(destination));
    }
    
    @Override
    public MessageConsumer createConsumer(final Destination destination, final String selector) throws JMSException {
        return this.addConsumer(this.getInternalSession().createConsumer(destination, selector));
    }
    
    @Override
    public MessageConsumer createConsumer(final Destination destination, final String selector, final boolean noLocal) throws JMSException {
        return this.addConsumer(this.getInternalSession().createConsumer(destination, selector, noLocal));
    }
    
    @Override
    public TopicSubscriber createDurableSubscriber(final Topic topic, final String selector) throws JMSException {
        return this.addTopicSubscriber(this.getInternalSession().createDurableSubscriber(topic, selector));
    }
    
    @Override
    public TopicSubscriber createDurableSubscriber(final Topic topic, final String name, final String selector, final boolean noLocal) throws JMSException {
        return this.addTopicSubscriber(this.getInternalSession().createDurableSubscriber(topic, name, selector, noLocal));
    }
    
    @Override
    public MessageListener getMessageListener() throws JMSException {
        return this.getInternalSession().getMessageListener();
    }
    
    @Override
    public void setMessageListener(final MessageListener messageListener) throws JMSException {
        this.getInternalSession().setMessageListener(messageListener);
    }
    
    @Override
    public TopicSubscriber createSubscriber(final Topic topic) throws JMSException {
        return this.addTopicSubscriber(((TopicSession)this.getInternalSession()).createSubscriber(topic));
    }
    
    @Override
    public TopicSubscriber createSubscriber(final Topic topic, final String selector, final boolean local) throws JMSException {
        return this.addTopicSubscriber(((TopicSession)this.getInternalSession()).createSubscriber(topic, selector, local));
    }
    
    @Override
    public QueueReceiver createReceiver(final Queue queue) throws JMSException {
        return this.addQueueReceiver(((QueueSession)this.getInternalSession()).createReceiver(queue));
    }
    
    @Override
    public QueueReceiver createReceiver(final Queue queue, final String selector) throws JMSException {
        return this.addQueueReceiver(((QueueSession)this.getInternalSession()).createReceiver(queue, selector));
    }
    
    @Override
    public MessageProducer createProducer(final Destination destination) throws JMSException {
        return new PooledProducer(this.getMessageProducer(destination), destination);
    }
    
    @Override
    public QueueSender createSender(final Queue queue) throws JMSException {
        return new PooledQueueSender(this.getQueueSender(queue), queue);
    }
    
    @Override
    public TopicPublisher createPublisher(final Topic topic) throws JMSException {
        return new PooledTopicPublisher(this.getTopicPublisher(topic), topic);
    }
    
    public Session getInternalSession() throws java.lang.IllegalStateException {
        if (this.session == null) {
            throw new java.lang.IllegalStateException("The session has already been closed");
        }
        return this.session;
    }
    
    public MessageProducer getMessageProducer() throws JMSException {
        return this.getMessageProducer(null);
    }
    
    public MessageProducer getMessageProducer(final Destination destination) throws JMSException {
        MessageProducer result = null;
        if (this.useAnonymousProducers) {
            if (this.producer == null) {
                synchronized (this) {
                    if (this.producer == null) {
                        this.producer = this.getInternalSession().createProducer(null);
                    }
                }
            }
            result = this.producer;
        }
        else {
            result = this.getInternalSession().createProducer(destination);
        }
        return result;
    }
    
    public QueueSender getQueueSender() throws JMSException {
        return this.getQueueSender(null);
    }
    
    public QueueSender getQueueSender(final Queue destination) throws JMSException {
        QueueSender result = null;
        if (this.useAnonymousProducers) {
            if (this.sender == null) {
                synchronized (this) {
                    if (this.sender == null) {
                        this.sender = ((QueueSession)this.getInternalSession()).createSender(null);
                    }
                }
            }
            result = this.sender;
        }
        else {
            result = ((QueueSession)this.getInternalSession()).createSender(destination);
        }
        return result;
    }
    
    public TopicPublisher getTopicPublisher() throws JMSException {
        return this.getTopicPublisher(null);
    }
    
    public TopicPublisher getTopicPublisher(final Topic destination) throws JMSException {
        TopicPublisher result = null;
        if (this.useAnonymousProducers) {
            if (this.publisher == null) {
                synchronized (this) {
                    if (this.publisher == null) {
                        this.publisher = ((TopicSession)this.getInternalSession()).createPublisher(null);
                    }
                }
            }
            result = this.publisher;
        }
        else {
            result = ((TopicSession)this.getInternalSession()).createPublisher(destination);
        }
        return result;
    }
    
    private QueueBrowser addQueueBrowser(final QueueBrowser browser) {
        this.browsers.add(browser);
        return browser;
    }
    
    private MessageConsumer addConsumer(final MessageConsumer consumer) {
        this.consumers.add(consumer);
        return new PooledMessageConsumer(this, consumer);
    }
    
    private TopicSubscriber addTopicSubscriber(final TopicSubscriber subscriber) {
        this.consumers.add(subscriber);
        return subscriber;
    }
    
    private QueueReceiver addQueueReceiver(final QueueReceiver receiver) {
        this.consumers.add(receiver);
        return receiver;
    }
    
    public void setIsXa(final boolean isXa) {
        this.isXa = isXa;
    }
    
    @Override
    public String toString() {
        return "PooledSession { " + this.session + " }";
    }
    
    protected void onConsumerClose(final MessageConsumer consumer) {
        this.consumers.remove(consumer);
    }
    
    static {
        LOG = LoggerFactory.getLogger(PooledSession.class);
    }
}
