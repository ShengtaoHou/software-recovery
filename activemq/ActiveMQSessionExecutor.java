// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import org.slf4j.LoggerFactory;
import java.util.List;
import javax.jms.JMSException;
import org.apache.activemq.util.JMSExceptionSupport;
import org.apache.activemq.command.ConsumerId;
import java.util.Iterator;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.thread.TaskRunner;
import org.slf4j.Logger;
import org.apache.activemq.thread.Task;

public class ActiveMQSessionExecutor implements Task
{
    private static final Logger LOG;
    private final ActiveMQSession session;
    private final MessageDispatchChannel messageQueue;
    private boolean dispatchedBySessionPool;
    private volatile TaskRunner taskRunner;
    private boolean startedOrWarnedThatNotStarted;
    
    ActiveMQSessionExecutor(final ActiveMQSession session) {
        this.session = session;
        if (this.session.connection != null && this.session.connection.isMessagePrioritySupported()) {
            this.messageQueue = new SimplePriorityMessageDispatchChannel();
        }
        else {
            this.messageQueue = new FifoMessageDispatchChannel();
        }
    }
    
    void setDispatchedBySessionPool(final boolean value) {
        this.dispatchedBySessionPool = value;
        this.wakeup();
    }
    
    void execute(final MessageDispatch message) throws InterruptedException {
        if (!this.startedOrWarnedThatNotStarted) {
            final ActiveMQConnection connection = this.session.connection;
            final long aboutUnstartedConnectionTimeout = connection.getWarnAboutUnstartedConnectionTimeout();
            if (connection.isStarted() || aboutUnstartedConnectionTimeout < 0L) {
                this.startedOrWarnedThatNotStarted = true;
            }
            else {
                final long elapsedTime = System.currentTimeMillis() - connection.getTimeCreated();
                if (elapsedTime > aboutUnstartedConnectionTimeout) {
                    ActiveMQSessionExecutor.LOG.warn("Received a message on a connection which is not yet started. Have you forgotten to call Connection.start()? Connection: " + connection + " Received: " + message);
                    this.startedOrWarnedThatNotStarted = true;
                }
            }
        }
        if (!this.session.isSessionAsyncDispatch() && !this.dispatchedBySessionPool) {
            this.dispatch(message);
        }
        else {
            this.messageQueue.enqueue(message);
            this.wakeup();
        }
    }
    
    public void wakeup() {
        if (!this.dispatchedBySessionPool) {
            if (this.session.isSessionAsyncDispatch()) {
                try {
                    TaskRunner taskRunner = this.taskRunner;
                    if (taskRunner == null) {
                        synchronized (this) {
                            if (this.taskRunner == null) {
                                if (!this.isRunning()) {
                                    return;
                                }
                                this.taskRunner = this.session.connection.getSessionTaskRunner().createTaskRunner(this, "ActiveMQ Session: " + this.session.getSessionId());
                            }
                            taskRunner = this.taskRunner;
                        }
                    }
                    taskRunner.wakeup();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            else {
                while (this.iterate()) {}
            }
        }
    }
    
    void executeFirst(final MessageDispatch message) {
        this.messageQueue.enqueueFirst(message);
        this.wakeup();
    }
    
    public boolean hasUncomsumedMessages() {
        return !this.messageQueue.isClosed() && this.messageQueue.isRunning() && !this.messageQueue.isEmpty();
    }
    
    void dispatch(final MessageDispatch message) {
        for (final ActiveMQMessageConsumer consumer : this.session.consumers) {
            final ConsumerId consumerId = message.getConsumerId();
            if (consumerId.equals(consumer.getConsumerId())) {
                consumer.dispatch(message);
                break;
            }
        }
    }
    
    synchronized void start() {
        if (!this.messageQueue.isRunning()) {
            this.messageQueue.start();
            if (this.hasUncomsumedMessages()) {
                this.wakeup();
            }
        }
    }
    
    void stop() throws JMSException {
        try {
            if (this.messageQueue.isRunning()) {
                synchronized (this) {
                    this.messageQueue.stop();
                    if (this.taskRunner != null) {
                        this.taskRunner.shutdown();
                        this.taskRunner = null;
                    }
                }
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw JMSExceptionSupport.create(e);
        }
    }
    
    boolean isRunning() {
        return this.messageQueue.isRunning();
    }
    
    void close() {
        this.messageQueue.close();
    }
    
    void clear() {
        this.messageQueue.clear();
    }
    
    MessageDispatch dequeueNoWait() {
        return this.messageQueue.dequeueNoWait();
    }
    
    protected void clearMessagesInProgress() {
        this.messageQueue.clear();
    }
    
    public boolean isEmpty() {
        return this.messageQueue.isEmpty();
    }
    
    @Override
    public boolean iterate() {
        for (final ActiveMQMessageConsumer consumer : this.session.consumers) {
            if (consumer.iterate()) {
                return true;
            }
        }
        final MessageDispatch message = this.messageQueue.dequeueNoWait();
        if (message == null) {
            return false;
        }
        this.dispatch(message);
        return !this.messageQueue.isEmpty();
    }
    
    List<MessageDispatch> getUnconsumedMessages() {
        return this.messageQueue.removeAll();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ActiveMQSessionExecutor.class);
    }
}
