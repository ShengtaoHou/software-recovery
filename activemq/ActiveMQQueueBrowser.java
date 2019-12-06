// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.Queue;
import javax.jms.Message;
import javax.jms.IllegalStateException;
import org.apache.activemq.command.MessageDispatch;
import javax.jms.MessageListener;
import javax.jms.JMSException;
import org.apache.activemq.selector.SelectorParser;
import javax.jms.InvalidDestinationException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Enumeration;
import javax.jms.QueueBrowser;

public class ActiveMQQueueBrowser implements QueueBrowser, Enumeration
{
    private final ActiveMQSession session;
    private final ActiveMQDestination destination;
    private final String selector;
    private ActiveMQMessageConsumer consumer;
    private boolean closed;
    private final ConsumerId consumerId;
    private final AtomicBoolean browseDone;
    private final boolean dispatchAsync;
    private Object semaphore;
    
    protected ActiveMQQueueBrowser(final ActiveMQSession session, final ConsumerId consumerId, final ActiveMQDestination destination, final String selector, final boolean dispatchAsync) throws JMSException {
        this.browseDone = new AtomicBoolean(true);
        this.semaphore = new Object();
        if (destination == null) {
            throw new InvalidDestinationException("Don't understand null destinations");
        }
        if (destination.getPhysicalName() == null) {
            throw new InvalidDestinationException("The destination object was not given a physical name.");
        }
        if (selector != null && selector.trim().length() != 0) {
            SelectorParser.parse(selector);
        }
        this.session = session;
        this.consumerId = consumerId;
        this.destination = destination;
        this.selector = selector;
        this.dispatchAsync = dispatchAsync;
    }
    
    private ActiveMQMessageConsumer createConsumer() throws JMSException {
        this.browseDone.set(false);
        final ActiveMQPrefetchPolicy prefetchPolicy = this.session.connection.getPrefetchPolicy();
        return new ActiveMQMessageConsumer(this.session, this.consumerId, this.destination, null, this.selector, prefetchPolicy.getQueueBrowserPrefetch(), prefetchPolicy.getMaximumPendingMessageLimit(), false, true, this.dispatchAsync, null) {
            @Override
            public void dispatch(final MessageDispatch md) {
                if (md.getMessage() == null) {
                    ActiveMQQueueBrowser.this.browseDone.set(true);
                }
                else {
                    super.dispatch(md);
                }
                ActiveMQQueueBrowser.this.notifyMessageAvailable();
            }
        };
    }
    
    private void destroyConsumer() {
        if (this.consumer == null) {
            return;
        }
        try {
            if (this.session.getTransacted() && this.session.getTransactionContext().isInLocalTransaction()) {
                this.session.commit();
            }
            this.consumer.close();
            this.consumer = null;
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Enumeration getEnumeration() throws JMSException {
        this.checkClosed();
        if (this.consumer == null) {
            this.consumer = this.createConsumer();
        }
        return this;
    }
    
    private void checkClosed() throws IllegalStateException {
        if (this.closed) {
            throw new IllegalStateException("The Consumer is closed");
        }
    }
    
    @Override
    public boolean hasMoreElements() {
        while (true) {
            synchronized (this) {
                if (this.consumer == null) {
                    return false;
                }
            }
            if (this.consumer.getMessageSize() > 0) {
                return true;
            }
            if (this.browseDone.get() || !this.session.isRunning()) {
                this.destroyConsumer();
                return false;
            }
            this.waitForMessage();
        }
    }
    
    @Override
    public Object nextElement() {
        while (true) {
            synchronized (this) {
                if (this.consumer == null) {
                    return null;
                }
            }
            try {
                final Message answer = this.consumer.receiveNoWait();
                if (answer != null) {
                    return answer;
                }
            }
            catch (JMSException e) {
                this.session.connection.onClientInternalException(e);
                return null;
            }
            if (this.browseDone.get() || !this.session.isRunning()) {
                break;
            }
            this.waitForMessage();
        }
        this.destroyConsumer();
        return null;
    }
    
    @Override
    public synchronized void close() throws JMSException {
        this.browseDone.set(true);
        this.destroyConsumer();
        this.closed = true;
    }
    
    @Override
    public Queue getQueue() throws JMSException {
        return (Queue)this.destination;
    }
    
    @Override
    public String getMessageSelector() throws JMSException {
        return this.selector;
    }
    
    protected void waitForMessage() {
        try {
            this.consumer.sendPullCommand(-1L);
            synchronized (this.semaphore) {
                this.semaphore.wait(2000L);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (JMSException ex) {}
    }
    
    protected void notifyMessageAvailable() {
        synchronized (this.semaphore) {
            this.semaphore.notifyAll();
        }
    }
    
    @Override
    public String toString() {
        return "ActiveMQQueueBrowser { value=" + this.consumerId + " }";
    }
}
