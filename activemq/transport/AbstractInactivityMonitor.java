// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import org.apache.activemq.util.ThreadPoolUtils;
import java.util.TimerTask;
import org.apache.activemq.command.WireFormatInfo;
import java.util.concurrent.RejectedExecutionException;
import java.io.IOException;
import org.apache.activemq.command.KeepAliveInfo;
import java.util.concurrent.ThreadFactory;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.thread.SchedulerTimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Timer;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;

public abstract class AbstractInactivityMonitor extends TransportFilter
{
    private static final Logger LOG;
    private static ThreadPoolExecutor ASYNC_TASKS;
    private static int CHECKER_COUNTER;
    private static long DEFAULT_CHECK_TIME_MILLS;
    private static Timer READ_CHECK_TIMER;
    private static Timer WRITE_CHECK_TIMER;
    private final AtomicBoolean monitorStarted;
    private final AtomicBoolean commandSent;
    private final AtomicBoolean inSend;
    private final AtomicBoolean failed;
    private final AtomicBoolean commandReceived;
    private final AtomicBoolean inReceive;
    private final AtomicInteger lastReceiveCounter;
    private final ReentrantReadWriteLock sendLock;
    private SchedulerTimerTask writeCheckerTask;
    private SchedulerTimerTask readCheckerTask;
    private long readCheckTime;
    private long writeCheckTime;
    private long initialDelayTime;
    private boolean useKeepAlive;
    private boolean keepAliveResponseRequired;
    protected WireFormat wireFormat;
    private final Runnable readChecker;
    private final Runnable writeChecker;
    private final ThreadFactory factory;
    
    private boolean allowReadCheck(final long elapsed) {
        return elapsed > this.readCheckTime * 9L / 10L;
    }
    
    public AbstractInactivityMonitor(final Transport next, final WireFormat wireFormat) {
        super(next);
        this.monitorStarted = new AtomicBoolean(false);
        this.commandSent = new AtomicBoolean(false);
        this.inSend = new AtomicBoolean(false);
        this.failed = new AtomicBoolean(false);
        this.commandReceived = new AtomicBoolean(true);
        this.inReceive = new AtomicBoolean(false);
        this.lastReceiveCounter = new AtomicInteger(0);
        this.sendLock = new ReentrantReadWriteLock();
        this.readCheckTime = AbstractInactivityMonitor.DEFAULT_CHECK_TIME_MILLS;
        this.writeCheckTime = AbstractInactivityMonitor.DEFAULT_CHECK_TIME_MILLS;
        this.initialDelayTime = AbstractInactivityMonitor.DEFAULT_CHECK_TIME_MILLS;
        this.useKeepAlive = true;
        this.readChecker = new Runnable() {
            long lastRunTime;
            
            @Override
            public void run() {
                final long now = System.currentTimeMillis();
                final long elapsed = now - this.lastRunTime;
                if (this.lastRunTime != 0L) {
                    AbstractInactivityMonitor.LOG.debug("{}ms elapsed since last read check.", (Object)elapsed);
                }
                if (!AbstractInactivityMonitor.this.allowReadCheck(elapsed)) {
                    AbstractInactivityMonitor.LOG.debug("Aborting read check...Not enough time elapsed since last read check.");
                    return;
                }
                this.lastRunTime = now;
                AbstractInactivityMonitor.this.readCheck();
            }
            
            @Override
            public String toString() {
                return "ReadChecker";
            }
        };
        this.writeChecker = new Runnable() {
            long lastRunTime;
            
            @Override
            public void run() {
                final long now = System.currentTimeMillis();
                if (this.lastRunTime != 0L) {
                    AbstractInactivityMonitor.LOG.debug("{}: {}ms elapsed since last write check.", this, now - this.lastRunTime);
                }
                this.lastRunTime = now;
                AbstractInactivityMonitor.this.writeCheck();
            }
            
            @Override
            public String toString() {
                return "WriteChecker";
            }
        };
        this.factory = new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                final Thread thread = new Thread(runnable, "ActiveMQ InactivityMonitor Worker");
                thread.setDaemon(true);
                return thread;
            }
        };
        this.wireFormat = wireFormat;
    }
    
    @Override
    public void start() throws Exception {
        this.next.start();
        this.startMonitorThreads();
    }
    
    @Override
    public void stop() throws Exception {
        this.stopMonitorThreads();
        this.next.stop();
    }
    
    final void writeCheck() {
        if (this.inSend.get()) {
            AbstractInactivityMonitor.LOG.trace("Send in progress. Skipping write check.");
            return;
        }
        if (!this.commandSent.get() && this.useKeepAlive && this.monitorStarted.get() && !AbstractInactivityMonitor.ASYNC_TASKS.isTerminating() && !AbstractInactivityMonitor.ASYNC_TASKS.isTerminated()) {
            AbstractInactivityMonitor.LOG.trace("{} no message sent since last write check, sending a KeepAliveInfo", this);
            try {
                AbstractInactivityMonitor.ASYNC_TASKS.execute(new Runnable() {
                    @Override
                    public void run() {
                        AbstractInactivityMonitor.LOG.debug("Running {}", this);
                        if (AbstractInactivityMonitor.this.monitorStarted.get()) {
                            try {
                                if (AbstractInactivityMonitor.this.sendLock.writeLock().tryLock()) {
                                    final KeepAliveInfo info = new KeepAliveInfo();
                                    info.setResponseRequired(AbstractInactivityMonitor.this.keepAliveResponseRequired);
                                    AbstractInactivityMonitor.this.doOnewaySend(info);
                                }
                            }
                            catch (IOException e) {
                                AbstractInactivityMonitor.this.onException(e);
                            }
                            finally {
                                if (AbstractInactivityMonitor.this.sendLock.writeLock().isHeldByCurrentThread()) {
                                    AbstractInactivityMonitor.this.sendLock.writeLock().unlock();
                                }
                            }
                        }
                    }
                    
                    @Override
                    public String toString() {
                        return "WriteCheck[" + AbstractInactivityMonitor.this.getRemoteAddress() + "]";
                    }
                });
            }
            catch (RejectedExecutionException ex) {
                if (!AbstractInactivityMonitor.ASYNC_TASKS.isTerminating() && !AbstractInactivityMonitor.ASYNC_TASKS.isTerminated()) {
                    AbstractInactivityMonitor.LOG.error("Async write check was rejected from the executor: ", ex);
                    throw ex;
                }
            }
        }
        else {
            AbstractInactivityMonitor.LOG.trace("{} message sent since last write check, resetting flag.", this);
        }
        this.commandSent.set(false);
    }
    
    final void readCheck() {
        final int currentCounter = this.next.getReceiveCounter();
        final int previousCounter = this.lastReceiveCounter.getAndSet(currentCounter);
        if (this.inReceive.get() || currentCounter != previousCounter) {
            AbstractInactivityMonitor.LOG.trace("A receive is in progress, skipping read check.");
            return;
        }
        if (!this.commandReceived.get() && this.monitorStarted.get() && !AbstractInactivityMonitor.ASYNC_TASKS.isTerminating() && !AbstractInactivityMonitor.ASYNC_TASKS.isTerminated()) {
            AbstractInactivityMonitor.LOG.debug("No message received since last read check for {}. Throwing InactivityIOException.", this);
            try {
                AbstractInactivityMonitor.ASYNC_TASKS.execute(new Runnable() {
                    @Override
                    public void run() {
                        AbstractInactivityMonitor.LOG.debug("Running {}", this);
                        AbstractInactivityMonitor.this.onException(new InactivityIOException("Channel was inactive for too (>" + AbstractInactivityMonitor.this.readCheckTime + ") long: " + AbstractInactivityMonitor.this.next.getRemoteAddress()));
                    }
                    
                    @Override
                    public String toString() {
                        return "ReadCheck[" + AbstractInactivityMonitor.this.getRemoteAddress() + "]";
                    }
                });
            }
            catch (RejectedExecutionException ex) {
                if (!AbstractInactivityMonitor.ASYNC_TASKS.isTerminating() && !AbstractInactivityMonitor.ASYNC_TASKS.isTerminated()) {
                    AbstractInactivityMonitor.LOG.error("Async read check was rejected from the executor: ", ex);
                    throw ex;
                }
            }
        }
        else if (AbstractInactivityMonitor.LOG.isTraceEnabled()) {
            AbstractInactivityMonitor.LOG.trace("Message received since last read check, resetting flag: ");
        }
        this.commandReceived.set(false);
    }
    
    protected abstract void processInboundWireFormatInfo(final WireFormatInfo p0) throws IOException;
    
    protected abstract void processOutboundWireFormatInfo(final WireFormatInfo p0) throws IOException;
    
    @Override
    public void onCommand(final Object command) {
        this.commandReceived.set(true);
        this.inReceive.set(true);
        try {
            if (command.getClass() == KeepAliveInfo.class) {
                final KeepAliveInfo info = (KeepAliveInfo)command;
                if (info.isResponseRequired()) {
                    this.sendLock.readLock().lock();
                    try {
                        info.setResponseRequired(false);
                        this.oneway(info);
                    }
                    catch (IOException e) {
                        this.onException(e);
                    }
                    finally {
                        this.sendLock.readLock().unlock();
                    }
                }
            }
            else {
                if (command.getClass() == WireFormatInfo.class) {
                    synchronized (this) {
                        try {
                            this.processInboundWireFormatInfo((WireFormatInfo)command);
                        }
                        catch (IOException e) {
                            this.onException(e);
                        }
                    }
                }
                this.transportListener.onCommand(command);
            }
        }
        finally {
            this.inReceive.set(false);
        }
    }
    
    @Override
    public void oneway(final Object o) throws IOException {
        this.sendLock.readLock().lock();
        this.inSend.set(true);
        try {
            this.doOnewaySend(o);
        }
        finally {
            this.commandSent.set(true);
            this.inSend.set(false);
            this.sendLock.readLock().unlock();
        }
    }
    
    private void doOnewaySend(final Object command) throws IOException {
        if (this.failed.get()) {
            throw new InactivityIOException("Cannot send, channel has already failed: " + this.next.getRemoteAddress());
        }
        if (command.getClass() == WireFormatInfo.class) {
            synchronized (this) {
                this.processOutboundWireFormatInfo((WireFormatInfo)command);
            }
        }
        this.next.oneway(command);
    }
    
    @Override
    public void onException(final IOException error) {
        if (this.failed.compareAndSet(false, true)) {
            this.stopMonitorThreads();
            if (this.sendLock.writeLock().isHeldByCurrentThread()) {
                this.sendLock.writeLock().unlock();
            }
            this.transportListener.onException(error);
        }
    }
    
    public void setUseKeepAlive(final boolean val) {
        this.useKeepAlive = val;
    }
    
    public long getReadCheckTime() {
        return this.readCheckTime;
    }
    
    public void setReadCheckTime(final long readCheckTime) {
        this.readCheckTime = readCheckTime;
    }
    
    public long getWriteCheckTime() {
        return this.writeCheckTime;
    }
    
    public void setWriteCheckTime(final long writeCheckTime) {
        this.writeCheckTime = writeCheckTime;
    }
    
    public long getInitialDelayTime() {
        return this.initialDelayTime;
    }
    
    public void setInitialDelayTime(final long initialDelayTime) {
        this.initialDelayTime = initialDelayTime;
    }
    
    public boolean isKeepAliveResponseRequired() {
        return this.keepAliveResponseRequired;
    }
    
    public void setKeepAliveResponseRequired(final boolean value) {
        this.keepAliveResponseRequired = value;
    }
    
    public boolean isMonitorStarted() {
        return this.monitorStarted.get();
    }
    
    protected synchronized void startMonitorThreads() throws IOException {
        if (this.monitorStarted.get()) {
            return;
        }
        if (!this.configuredOk()) {
            return;
        }
        if (this.readCheckTime > 0L) {
            this.readCheckerTask = new SchedulerTimerTask(this.readChecker);
        }
        if (this.writeCheckTime > 0L) {
            this.writeCheckerTask = new SchedulerTimerTask(this.writeChecker);
        }
        if (this.writeCheckTime > 0L || this.readCheckTime > 0L) {
            this.monitorStarted.set(true);
            synchronized (AbstractInactivityMonitor.class) {
                if (AbstractInactivityMonitor.CHECKER_COUNTER == 0) {
                    AbstractInactivityMonitor.ASYNC_TASKS = this.createExecutor();
                    AbstractInactivityMonitor.READ_CHECK_TIMER = new Timer("ActiveMQ InactivityMonitor ReadCheckTimer", true);
                    AbstractInactivityMonitor.WRITE_CHECK_TIMER = new Timer("ActiveMQ InactivityMonitor WriteCheckTimer", true);
                }
                ++AbstractInactivityMonitor.CHECKER_COUNTER;
                if (this.readCheckTime > 0L) {
                    AbstractInactivityMonitor.READ_CHECK_TIMER.schedule(this.readCheckerTask, this.initialDelayTime, this.readCheckTime);
                }
                if (this.writeCheckTime > 0L) {
                    AbstractInactivityMonitor.WRITE_CHECK_TIMER.schedule(this.writeCheckerTask, this.initialDelayTime, this.writeCheckTime);
                }
            }
        }
    }
    
    protected abstract boolean configuredOk() throws IOException;
    
    protected synchronized void stopMonitorThreads() {
        if (this.monitorStarted.compareAndSet(true, false)) {
            if (this.readCheckerTask != null) {
                this.readCheckerTask.cancel();
            }
            if (this.writeCheckerTask != null) {
                this.writeCheckerTask.cancel();
            }
            synchronized (AbstractInactivityMonitor.class) {
                AbstractInactivityMonitor.WRITE_CHECK_TIMER.purge();
                AbstractInactivityMonitor.READ_CHECK_TIMER.purge();
                --AbstractInactivityMonitor.CHECKER_COUNTER;
                if (AbstractInactivityMonitor.CHECKER_COUNTER == 0) {
                    AbstractInactivityMonitor.WRITE_CHECK_TIMER.cancel();
                    AbstractInactivityMonitor.READ_CHECK_TIMER.cancel();
                    AbstractInactivityMonitor.WRITE_CHECK_TIMER = null;
                    AbstractInactivityMonitor.READ_CHECK_TIMER = null;
                    ThreadPoolUtils.shutdown(AbstractInactivityMonitor.ASYNC_TASKS);
                }
            }
        }
    }
    
    private ThreadPoolExecutor createExecutor() {
        final ThreadPoolExecutor exec = new ThreadPoolExecutor(0, Integer.MAX_VALUE, getDefaultKeepAliveTime(), TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), this.factory);
        exec.allowCoreThreadTimeOut(true);
        return exec;
    }
    
    private static int getDefaultKeepAliveTime() {
        return Integer.getInteger("org.apache.activemq.transport.AbstractInactivityMonitor.keepAliveTime", 30);
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbstractInactivityMonitor.class);
        AbstractInactivityMonitor.DEFAULT_CHECK_TIME_MILLS = 30000L;
    }
}
