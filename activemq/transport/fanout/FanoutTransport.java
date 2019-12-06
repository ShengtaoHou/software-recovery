// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.fanout;

import org.apache.activemq.command.Response;
import org.apache.activemq.transport.DefaultTransportListener;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.FutureResponse;
import org.apache.activemq.transport.ResponseCallback;
import org.apache.activemq.command.Message;
import org.apache.activemq.util.IOExceptionSupport;
import java.io.IOException;
import org.apache.activemq.command.Command;
import org.apache.activemq.util.ServiceStopper;
import java.net.URI;
import java.util.Iterator;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.Service;
import org.apache.activemq.util.ServiceSupport;
import org.apache.activemq.transport.TransportFactory;
import java.io.InterruptedIOException;
import org.apache.activemq.thread.Task;
import java.util.ArrayList;
import org.apache.activemq.thread.TaskRunner;
import org.apache.activemq.thread.TaskRunnerFactory;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.state.ConnectionStateTracker;
import org.apache.activemq.transport.TransportListener;
import org.slf4j.Logger;
import org.apache.activemq.transport.CompositeTransport;

public class FanoutTransport implements CompositeTransport
{
    private static final Logger LOG;
    private TransportListener transportListener;
    private boolean disposed;
    private boolean connected;
    private final Object reconnectMutex;
    private final ConnectionStateTracker stateTracker;
    private final ConcurrentHashMap<Integer, RequestCounter> requestMap;
    private final TaskRunnerFactory reconnectTaskFactory;
    private final TaskRunner reconnectTask;
    private boolean started;
    private final ArrayList<FanoutTransportHandler> transports;
    private int connectedCount;
    private int minAckCount;
    private long initialReconnectDelay;
    private long maxReconnectDelay;
    private long backOffMultiplier;
    private final boolean useExponentialBackOff = true;
    private int maxReconnectAttempts;
    private Exception connectionFailure;
    private FanoutTransportHandler primary;
    private boolean fanOutQueues;
    
    public FanoutTransport() throws InterruptedIOException {
        this.reconnectMutex = new Object();
        this.stateTracker = new ConnectionStateTracker();
        this.requestMap = new ConcurrentHashMap<Integer, RequestCounter>();
        this.transports = new ArrayList<FanoutTransportHandler>();
        this.minAckCount = 2;
        this.initialReconnectDelay = 10L;
        this.maxReconnectDelay = 30000L;
        this.backOffMultiplier = 2L;
        this.fanOutQueues = false;
        (this.reconnectTaskFactory = new TaskRunnerFactory()).init();
        this.reconnectTask = this.reconnectTaskFactory.createTaskRunner(new Task() {
            @Override
            public boolean iterate() {
                return FanoutTransport.this.doConnect();
            }
        }, "ActiveMQ Fanout Worker: " + System.identityHashCode(this));
    }
    
    private boolean doConnect() {
        long closestReconnectDate = 0L;
        synchronized (this.reconnectMutex) {
            if (this.disposed || this.connectionFailure != null) {
                this.reconnectMutex.notifyAll();
            }
            if (this.transports.size() == this.connectedCount || this.disposed || this.connectionFailure != null) {
                return false;
            }
            if (!this.transports.isEmpty()) {
                final Iterator<FanoutTransportHandler> iter = this.transports.iterator();
                int i = 0;
                while (iter.hasNext() && !this.disposed) {
                    final long now = System.currentTimeMillis();
                    final FanoutTransportHandler fanoutHandler = iter.next();
                    if (fanoutHandler.transport == null) {
                        if (fanoutHandler.reconnectDate != 0L && fanoutHandler.reconnectDate > now) {
                            if (closestReconnectDate == 0L || fanoutHandler.reconnectDate < closestReconnectDate) {
                                closestReconnectDate = fanoutHandler.reconnectDate;
                            }
                        }
                        else {
                            final URI uri = fanoutHandler.uri;
                            try {
                                FanoutTransport.LOG.debug("Stopped: " + this);
                                FanoutTransport.LOG.debug("Attempting connect to: " + uri);
                                final Transport t = TransportFactory.compositeConnect(uri);
                                fanoutHandler.transport = t;
                                t.setTransportListener(fanoutHandler);
                                if (this.started) {
                                    this.restoreTransport(fanoutHandler);
                                }
                                FanoutTransport.LOG.debug("Connection established");
                                fanoutHandler.reconnectDelay = this.initialReconnectDelay;
                                fanoutHandler.connectFailures = 0;
                                if (this.primary == null) {
                                    this.primary = fanoutHandler;
                                }
                                ++this.connectedCount;
                            }
                            catch (Exception e) {
                                FanoutTransport.LOG.debug("Connect fail to: " + uri + ", reason: " + e);
                                if (fanoutHandler.transport != null) {
                                    ServiceSupport.dispose(fanoutHandler.transport);
                                    fanoutHandler.transport = null;
                                }
                                if (this.maxReconnectAttempts > 0 && ++fanoutHandler.connectFailures >= this.maxReconnectAttempts) {
                                    FanoutTransport.LOG.error("Failed to connect to transport after: " + fanoutHandler.connectFailures + " attempt(s)");
                                    this.connectionFailure = e;
                                    this.reconnectMutex.notifyAll();
                                    return false;
                                }
                                final FanoutTransportHandler fanoutTransportHandler = fanoutHandler;
                                fanoutTransportHandler.reconnectDelay *= this.backOffMultiplier;
                                if (fanoutHandler.reconnectDelay > this.maxReconnectDelay) {
                                    fanoutHandler.reconnectDelay = this.maxReconnectDelay;
                                }
                                fanoutHandler.reconnectDate = now + fanoutHandler.reconnectDelay;
                                if (closestReconnectDate == 0L || fanoutHandler.reconnectDate < closestReconnectDate) {
                                    closestReconnectDate = fanoutHandler.reconnectDate;
                                }
                            }
                        }
                    }
                    ++i;
                }
                if (this.transports.size() == this.connectedCount || this.disposed) {
                    this.reconnectMutex.notifyAll();
                    return false;
                }
            }
        }
        try {
            final long reconnectDelay = closestReconnectDate - System.currentTimeMillis();
            if (reconnectDelay > 0L) {
                FanoutTransport.LOG.debug("Waiting " + reconnectDelay + " ms before attempting connection. ");
                Thread.sleep(reconnectDelay);
            }
        }
        catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
        }
        return true;
    }
    
    @Override
    public void start() throws Exception {
        synchronized (this.reconnectMutex) {
            FanoutTransport.LOG.debug("Started.");
            if (this.started) {
                return;
            }
            this.started = true;
            for (final FanoutTransportHandler th : this.transports) {
                if (th.transport != null) {
                    this.restoreTransport(th);
                }
            }
            this.connected = true;
        }
    }
    
    @Override
    public void stop() throws Exception {
        try {
            synchronized (this.reconnectMutex) {
                final ServiceStopper ss = new ServiceStopper();
                if (!this.started) {
                    return;
                }
                this.started = false;
                this.disposed = true;
                this.connected = false;
                for (final FanoutTransportHandler th : this.transports) {
                    if (th.transport != null) {
                        ss.stop(th.transport);
                    }
                }
                FanoutTransport.LOG.debug("Stopped: " + this);
                ss.throwFirstException();
            }
        }
        finally {
            this.reconnectTask.shutdown();
            this.reconnectTaskFactory.shutdownNow();
        }
    }
    
    public int getMinAckCount() {
        return this.minAckCount;
    }
    
    public void setMinAckCount(final int minAckCount) {
        this.minAckCount = minAckCount;
    }
    
    public long getInitialReconnectDelay() {
        return this.initialReconnectDelay;
    }
    
    public void setInitialReconnectDelay(final long initialReconnectDelay) {
        this.initialReconnectDelay = initialReconnectDelay;
    }
    
    public long getMaxReconnectDelay() {
        return this.maxReconnectDelay;
    }
    
    public void setMaxReconnectDelay(final long maxReconnectDelay) {
        this.maxReconnectDelay = maxReconnectDelay;
    }
    
    public long getReconnectDelayExponent() {
        return this.backOffMultiplier;
    }
    
    public void setReconnectDelayExponent(final long reconnectDelayExponent) {
        this.backOffMultiplier = reconnectDelayExponent;
    }
    
    public int getMaxReconnectAttempts() {
        return this.maxReconnectAttempts;
    }
    
    public void setMaxReconnectAttempts(final int maxReconnectAttempts) {
        this.maxReconnectAttempts = maxReconnectAttempts;
    }
    
    @Override
    public void oneway(final Object o) throws IOException {
        final Command command = (Command)o;
        try {
            synchronized (this.reconnectMutex) {
                while (this.connectedCount < this.minAckCount && !this.disposed && this.connectionFailure == null) {
                    FanoutTransport.LOG.debug("Waiting for at least " + this.minAckCount + " transports to be connected.");
                    this.reconnectMutex.wait(1000L);
                }
                if (this.connectedCount < this.minAckCount) {
                    Exception error;
                    if (this.disposed) {
                        error = new IOException("Transport disposed.");
                    }
                    else if (this.connectionFailure != null) {
                        error = this.connectionFailure;
                    }
                    else {
                        error = new IOException("Unexpected failure.");
                    }
                    if (error instanceof IOException) {
                        throw (IOException)error;
                    }
                    throw IOExceptionSupport.create(error);
                }
                else {
                    final boolean fanout = this.isFanoutCommand(command);
                    if (this.stateTracker.track(command) == null && command.isResponseRequired()) {
                        final int size = fanout ? this.minAckCount : 1;
                        this.requestMap.put(new Integer(command.getCommandId()), new RequestCounter(command, size));
                    }
                    if (fanout) {
                        for (final FanoutTransportHandler th : this.transports) {
                            if (th.transport != null) {
                                try {
                                    th.transport.oneway(command);
                                }
                                catch (IOException e) {
                                    FanoutTransport.LOG.debug("Send attempt: failed.");
                                    th.onException(e);
                                }
                            }
                        }
                    }
                    else {
                        try {
                            this.primary.transport.oneway(command);
                        }
                        catch (IOException e2) {
                            FanoutTransport.LOG.debug("Send attempt: failed.");
                            this.primary.onException(e2);
                        }
                    }
                }
            }
        }
        catch (InterruptedException e3) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
        }
    }
    
    private boolean isFanoutCommand(final Command command) {
        if (command.isMessage()) {
            return this.fanOutQueues || ((Message)command).getDestination().isTopic();
        }
        return command.getDataStructureType() != 5 && command.getDataStructureType() != 12;
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
    
    public void reconnect() {
        FanoutTransport.LOG.debug("Waking up reconnect task");
        try {
            this.reconnectTask.wakeup();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public TransportListener getTransportListener() {
        return this.transportListener;
    }
    
    @Override
    public void setTransportListener(final TransportListener commandListener) {
        this.transportListener = commandListener;
    }
    
    @Override
    public <T> T narrow(final Class<T> target) {
        if (target.isAssignableFrom(this.getClass())) {
            return target.cast(this);
        }
        synchronized (this.reconnectMutex) {
            for (final FanoutTransportHandler th : this.transports) {
                if (th.transport != null) {
                    final T rc = th.transport.narrow(target);
                    if (rc != null) {
                        return rc;
                    }
                    continue;
                }
            }
        }
        return null;
    }
    
    protected void restoreTransport(final FanoutTransportHandler th) throws Exception, IOException {
        th.transport.start();
        this.stateTracker.setRestoreConsumers(th.transport == this.primary);
        this.stateTracker.restore(th.transport);
        for (final RequestCounter rc : this.requestMap.values()) {
            th.transport.oneway(rc.command);
        }
    }
    
    @Override
    public void add(final boolean reblance, final URI[] uris) {
        synchronized (this.reconnectMutex) {
            for (int i = 0; i < uris.length; ++i) {
                final URI uri = uris[i];
                boolean match = false;
                for (final FanoutTransportHandler th : this.transports) {
                    if (th.uri.equals(uri)) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    final FanoutTransportHandler th2 = new FanoutTransportHandler(uri);
                    this.transports.add(th2);
                    this.reconnect();
                }
            }
        }
    }
    
    @Override
    public void remove(final boolean rebalance, final URI[] uris) {
        synchronized (this.reconnectMutex) {
            for (int i = 0; i < uris.length; ++i) {
                final URI uri = uris[i];
                final Iterator<FanoutTransportHandler> iter = this.transports.iterator();
                while (iter.hasNext()) {
                    final FanoutTransportHandler th = iter.next();
                    if (th.uri.equals(uri)) {
                        if (th.transport != null) {
                            ServiceSupport.dispose(th.transport);
                            --this.connectedCount;
                        }
                        iter.remove();
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void reconnect(final URI uri) throws IOException {
        this.add(true, new URI[] { uri });
    }
    
    @Override
    public boolean isReconnectSupported() {
        return true;
    }
    
    @Override
    public boolean isUpdateURIsSupported() {
        return true;
    }
    
    @Override
    public void updateURIs(final boolean reblance, final URI[] uris) throws IOException {
        this.add(reblance, uris);
    }
    
    @Override
    public String getRemoteAddress() {
        if (this.primary != null && this.primary.transport != null) {
            return this.primary.transport.getRemoteAddress();
        }
        return null;
    }
    
    protected void transportListenerOnCommand(final Command command) {
        if (this.transportListener != null) {
            this.transportListener.onCommand(command);
        }
    }
    
    @Override
    public boolean isFaultTolerant() {
        return true;
    }
    
    public boolean isFanOutQueues() {
        return this.fanOutQueues;
    }
    
    public void setFanOutQueues(final boolean fanOutQueues) {
        this.fanOutQueues = fanOutQueues;
    }
    
    @Override
    public boolean isDisposed() {
        return this.disposed;
    }
    
    @Override
    public boolean isConnected() {
        return this.connected;
    }
    
    @Override
    public int getReceiveCounter() {
        int rc = 0;
        synchronized (this.reconnectMutex) {
            for (final FanoutTransportHandler th : this.transports) {
                if (th.transport != null) {
                    rc += th.transport.getReceiveCounter();
                }
            }
        }
        return rc;
    }
    
    static {
        LOG = LoggerFactory.getLogger(FanoutTransport.class);
    }
    
    static class RequestCounter
    {
        final Command command;
        final AtomicInteger ackCount;
        
        RequestCounter(final Command command, final int count) {
            this.command = command;
            this.ackCount = new AtomicInteger(count);
        }
        
        @Override
        public String toString() {
            return this.command.getCommandId() + "=" + this.ackCount.get();
        }
    }
    
    class FanoutTransportHandler extends DefaultTransportListener
    {
        private final URI uri;
        private Transport transport;
        private int connectFailures;
        private long reconnectDelay;
        private long reconnectDate;
        
        public FanoutTransportHandler(final URI uri) {
            this.reconnectDelay = FanoutTransport.this.initialReconnectDelay;
            this.uri = uri;
        }
        
        @Override
        public void onCommand(final Object o) {
            final Command command = (Command)o;
            if (command.isResponse()) {
                final Integer id = new Integer(((Response)command).getCorrelationId());
                final RequestCounter rc = FanoutTransport.this.requestMap.get(id);
                if (rc != null) {
                    if (rc.ackCount.decrementAndGet() <= 0) {
                        FanoutTransport.this.requestMap.remove(id);
                        FanoutTransport.this.transportListenerOnCommand(command);
                    }
                }
                else {
                    FanoutTransport.this.transportListenerOnCommand(command);
                }
            }
            else {
                FanoutTransport.this.transportListenerOnCommand(command);
            }
        }
        
        @Override
        public void onException(final IOException error) {
            try {
                synchronized (FanoutTransport.this.reconnectMutex) {
                    if (this.transport == null || !this.transport.isConnected()) {
                        return;
                    }
                    FanoutTransport.LOG.debug("Transport failed, starting up reconnect task", error);
                    ServiceSupport.dispose(this.transport);
                    this.transport = null;
                    FanoutTransport.this.connectedCount--;
                    if (FanoutTransport.this.primary == this) {
                        FanoutTransport.this.primary = null;
                    }
                    FanoutTransport.this.reconnectTask.wakeup();
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (FanoutTransport.this.transportListener != null) {
                    FanoutTransport.this.transportListener.onException(new InterruptedIOException());
                }
            }
        }
    }
}
