// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.vm;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.FutureResponse;
import org.apache.activemq.transport.ResponseCallback;
import org.apache.activemq.command.ShutdownInfo;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.io.InterruptedIOException;
import java.io.IOException;
import org.apache.activemq.transport.TransportDisposedIOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.thread.TaskRunner;
import org.apache.activemq.thread.TaskRunnerFactory;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.URI;
import org.apache.activemq.transport.TransportListener;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.apache.activemq.thread.Task;
import org.apache.activemq.transport.Transport;

public class VMTransport implements Transport, Task
{
    protected static final Logger LOG;
    private static final AtomicLong NEXT_ID;
    protected VMTransport peer;
    protected TransportListener transportListener;
    protected boolean marshal;
    protected boolean network;
    protected boolean async;
    protected int asyncQueueDepth;
    protected final URI location;
    protected final long id;
    private LinkedBlockingQueue<Object> messageQueue;
    private TaskRunnerFactory taskRunnerFactory;
    private TaskRunner taskRunner;
    protected final AtomicBoolean started;
    protected final AtomicBoolean disposed;
    private volatile int receiveCounter;
    
    public VMTransport(final URI location) {
        this.async = true;
        this.asyncQueueDepth = 2000;
        this.started = new AtomicBoolean();
        this.disposed = new AtomicBoolean();
        this.location = location;
        this.id = VMTransport.NEXT_ID.getAndIncrement();
    }
    
    public void setPeer(final VMTransport peer) {
        this.peer = peer;
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        if (this.disposed.get()) {
            throw new TransportDisposedIOException("Transport disposed.");
        }
        if (this.peer == null) {
            throw new IOException("Peer not connected.");
        }
        try {
            if (this.peer.disposed.get()) {
                throw new TransportDisposedIOException("Peer (" + this.peer.toString() + ") disposed.");
            }
            if (this.peer.async || !this.peer.started.get()) {
                this.peer.getMessageQueue().put(command);
                this.peer.wakeup();
                return;
            }
        }
        catch (InterruptedException e) {
            final InterruptedIOException iioe = new InterruptedIOException(e.getMessage());
            iioe.initCause(e);
            throw iioe;
        }
        this.dispatch(this.peer, this.peer.messageQueue, command);
    }
    
    public void dispatch(final VMTransport transport, final BlockingQueue<Object> pending, final Object command) {
        final TransportListener transportListener = transport.getTransportListener();
        if (transportListener != null) {
            synchronized (transport.started) {
                while (pending != null && !pending.isEmpty() && !transport.isDisposed()) {
                    this.doDispatch(transport, transportListener, pending.poll());
                }
                transport.messageQueue = null;
                if (command != null && !this.disposed.get() && !transport.isDisposed()) {
                    this.doDispatch(transport, transportListener, command);
                }
            }
        }
    }
    
    public void doDispatch(final VMTransport transport, final TransportListener transportListener, final Object command) {
        ++transport.receiveCounter;
        transportListener.onCommand(command);
    }
    
    @Override
    public void start() throws Exception {
        if (this.transportListener == null) {
            throw new IOException("TransportListener not set.");
        }
        if (!this.async) {
            synchronized (this.started) {
                if (this.started.compareAndSet(false, true)) {
                    final LinkedBlockingQueue<Object> mq = this.getMessageQueue();
                    Object command;
                    while ((command = mq.poll()) != null && !this.disposed.get()) {
                        ++this.receiveCounter;
                        this.doDispatch(this, this.transportListener, command);
                    }
                }
            }
        }
        else if (this.started.compareAndSet(false, true)) {
            this.wakeup();
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (this.disposed.compareAndSet(false, true)) {
            TaskRunner tr = this.taskRunner;
            final LinkedBlockingQueue<Object> mq = this.messageQueue;
            this.taskRunner = null;
            this.messageQueue = null;
            if (mq != null) {
                mq.clear();
            }
            if (tr != null) {
                try {
                    tr.shutdown(TimeUnit.SECONDS.toMillis(1L));
                }
                catch (Exception ex) {}
                tr = null;
            }
            try {
                this.peer.transportListener.onCommand(new ShutdownInfo());
            }
            catch (Exception ex2) {}
            try {
                this.peer.transportListener.onException(new TransportDisposedIOException("peer (" + this + ") stopped."));
            }
            catch (Exception ex3) {}
            if (this.taskRunnerFactory != null) {
                this.taskRunnerFactory.shutdownNow();
                this.taskRunnerFactory = null;
            }
        }
    }
    
    protected void wakeup() {
        if (this.async && this.started.get()) {
            try {
                this.getTaskRunner().wakeup();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            catch (TransportDisposedIOException ex) {}
        }
    }
    
    @Override
    public boolean iterate() {
        final TransportListener tl = this.transportListener;
        LinkedBlockingQueue<Object> mq;
        try {
            mq = this.getMessageQueue();
        }
        catch (TransportDisposedIOException e) {
            return false;
        }
        final Object command = mq.poll();
        if (command != null && !this.disposed.get()) {
            tl.onCommand(command);
            return !mq.isEmpty() && !this.disposed.get();
        }
        if (this.disposed.get()) {
            mq.clear();
        }
        return false;
    }
    
    @Override
    public void setTransportListener(final TransportListener commandListener) {
        this.transportListener = commandListener;
    }
    
    public void setMessageQueue(final LinkedBlockingQueue<Object> asyncQueue) {
        synchronized (this) {
            if (this.messageQueue == null) {
                this.messageQueue = asyncQueue;
            }
        }
    }
    
    public LinkedBlockingQueue<Object> getMessageQueue() throws TransportDisposedIOException {
        LinkedBlockingQueue<Object> result = this.messageQueue;
        if (result == null) {
            synchronized (this) {
                result = this.messageQueue;
                if (result == null) {
                    if (this.disposed.get()) {
                        throw new TransportDisposedIOException("The Transport has been disposed");
                    }
                    result = (this.messageQueue = new LinkedBlockingQueue<Object>(this.asyncQueueDepth));
                }
            }
        }
        return result;
    }
    
    protected TaskRunner getTaskRunner() throws TransportDisposedIOException {
        TaskRunner result = this.taskRunner;
        if (result == null) {
            synchronized (this) {
                result = this.taskRunner;
                if (result == null) {
                    if (this.disposed.get()) {
                        throw new TransportDisposedIOException("The Transport has been disposed");
                    }
                    final String name = "ActiveMQ VMTransport: " + this.toString();
                    if (this.taskRunnerFactory == null) {
                        (this.taskRunnerFactory = new TaskRunnerFactory(name)).init();
                    }
                    result = (this.taskRunner = this.taskRunnerFactory.createTaskRunner(this, name));
                }
            }
        }
        return result;
    }
    
    @Override
    public FutureResponse asyncRequest(final Object command, final ResponseCallback responseCallback) throws IOException {
        throw new AssertionError((Object)"Unsupported Method");
    }
    
    @Override
    public Object request(final Object command) throws IOException {
        throw new AssertionError((Object)"Unsupported Method");
    }
    
    @Override
    public Object request(final Object command, final int timeout) throws IOException {
        throw new AssertionError((Object)"Unsupported Method");
    }
    
    @Override
    public TransportListener getTransportListener() {
        return this.transportListener;
    }
    
    @Override
    public <T> T narrow(final Class<T> target) {
        if (target.isAssignableFrom(this.getClass())) {
            return target.cast(this);
        }
        return null;
    }
    
    public boolean isMarshal() {
        return this.marshal;
    }
    
    public void setMarshal(final boolean marshal) {
        this.marshal = marshal;
    }
    
    public boolean isNetwork() {
        return this.network;
    }
    
    public void setNetwork(final boolean network) {
        this.network = network;
    }
    
    @Override
    public String toString() {
        return this.location + "#" + this.id;
    }
    
    @Override
    public String getRemoteAddress() {
        if (this.peer != null) {
            return this.peer.toString();
        }
        return null;
    }
    
    public boolean isAsync() {
        return this.async;
    }
    
    public void setAsync(final boolean async) {
        this.async = async;
    }
    
    public int getAsyncQueueDepth() {
        return this.asyncQueueDepth;
    }
    
    public void setAsyncQueueDepth(final int asyncQueueDepth) {
        this.asyncQueueDepth = asyncQueueDepth;
    }
    
    @Override
    public boolean isFaultTolerant() {
        return false;
    }
    
    @Override
    public boolean isDisposed() {
        return this.disposed.get();
    }
    
    @Override
    public boolean isConnected() {
        return !this.disposed.get();
    }
    
    @Override
    public void reconnect(final URI uri) throws IOException {
        throw new IOException("Transport reconnect is not supported");
    }
    
    @Override
    public boolean isReconnectSupported() {
        return false;
    }
    
    @Override
    public boolean isUpdateURIsSupported() {
        return false;
    }
    
    @Override
    public void updateURIs(final boolean reblance, final URI[] uris) throws IOException {
        throw new IOException("URI update feature not supported");
    }
    
    @Override
    public int getReceiveCounter() {
        return this.receiveCounter;
    }
    
    static {
        LOG = LoggerFactory.getLogger(VMTransport.class);
        NEXT_ID = new AtomicLong(0L);
    }
}
