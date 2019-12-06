// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import org.apache.activemq.util.ThreadPoolUtils;
import java.util.TimerTask;
import org.apache.activemq.transport.AbstractInactivityMonitor;
import java.io.IOException;
import org.apache.activemq.transport.InactivityIOException;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import java.util.concurrent.ThreadFactory;
import org.apache.activemq.thread.SchedulerTimerTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Timer;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.apache.activemq.transport.TransportFilter;

public class MQTTInactivityMonitor extends TransportFilter
{
    private static final Logger LOG;
    private static final long DEFAULT_CHECK_TIME_MILLS = 30000L;
    private static ThreadPoolExecutor ASYNC_TASKS;
    private static int CHECKER_COUNTER;
    private static Timer READ_CHECK_TIMER;
    private final AtomicBoolean monitorStarted;
    private final AtomicBoolean failed;
    private final AtomicBoolean inReceive;
    private final AtomicInteger lastReceiveCounter;
    private final ReentrantLock sendLock;
    private SchedulerTimerTask readCheckerTask;
    private long readGraceTime;
    private long readKeepAliveTime;
    private boolean keepAliveResponseRequired;
    private MQTTProtocolConverter protocolConverter;
    private final Runnable readChecker;
    private final ThreadFactory factory;
    
    private boolean allowReadCheck(final long elapsed) {
        return elapsed > this.readGraceTime * 9L / 10L;
    }
    
    public MQTTInactivityMonitor(final Transport next, final WireFormat wireFormat) {
        super(next);
        this.monitorStarted = new AtomicBoolean(false);
        this.failed = new AtomicBoolean(false);
        this.inReceive = new AtomicBoolean(false);
        this.lastReceiveCounter = new AtomicInteger(0);
        this.sendLock = new ReentrantLock();
        this.readGraceTime = 30000L;
        this.readKeepAliveTime = 30000L;
        this.readChecker = new Runnable() {
            long lastReceiveTime = System.currentTimeMillis();
            
            @Override
            public void run() {
                final long now = System.currentTimeMillis();
                final int currentCounter = MQTTInactivityMonitor.this.next.getReceiveCounter();
                final int previousCounter = MQTTInactivityMonitor.this.lastReceiveCounter.getAndSet(currentCounter);
                if (MQTTInactivityMonitor.this.inReceive.get() || currentCounter != previousCounter) {
                    if (MQTTInactivityMonitor.LOG.isTraceEnabled()) {
                        MQTTInactivityMonitor.LOG.trace("Command received since last read check.");
                    }
                    this.lastReceiveTime = now;
                    return;
                }
                if (now - this.lastReceiveTime >= MQTTInactivityMonitor.this.readKeepAliveTime + MQTTInactivityMonitor.this.readGraceTime && MQTTInactivityMonitor.this.monitorStarted.get() && !MQTTInactivityMonitor.ASYNC_TASKS.isTerminating()) {
                    if (MQTTInactivityMonitor.LOG.isDebugEnabled()) {
                        MQTTInactivityMonitor.LOG.debug("No message received since last read check for " + MQTTInactivityMonitor.this.toString() + "! Throwing InactivityIOException.");
                    }
                    MQTTInactivityMonitor.ASYNC_TASKS.execute(new Runnable() {
                        @Override
                        public void run() {
                            MQTTInactivityMonitor.this.onException(new InactivityIOException("Channel was inactive for too (>" + (MQTTInactivityMonitor.this.readKeepAliveTime + MQTTInactivityMonitor.this.readGraceTime) + ") long: " + MQTTInactivityMonitor.this.next.getRemoteAddress()));
                        }
                    });
                }
            }
        };
        this.factory = new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                final Thread thread = new Thread(runnable, "MQTTInactivityMonitor Async Task: " + runnable);
                thread.setDaemon(true);
                return thread;
            }
        };
    }
    
    @Override
    public void start() throws Exception {
        this.next.start();
        this.startMonitorThread();
    }
    
    @Override
    public void stop() throws Exception {
        this.stopMonitorThread();
        this.next.stop();
    }
    
    @Override
    public void onCommand(final Object command) {
        this.inReceive.set(true);
        try {
            this.transportListener.onCommand(command);
        }
        finally {
            this.inReceive.set(false);
        }
    }
    
    @Override
    public void oneway(final Object o) throws IOException {
        this.sendLock.lock();
        try {
            this.doOnewaySend(o);
        }
        finally {
            this.sendLock.unlock();
        }
    }
    
    private void doOnewaySend(final Object command) throws IOException {
        if (this.failed.get()) {
            throw new InactivityIOException("Cannot send, channel has already failed: " + this.next.getRemoteAddress());
        }
        this.next.oneway(command);
    }
    
    @Override
    public void onException(final IOException error) {
        if (this.failed.compareAndSet(false, true)) {
            this.stopMonitorThread();
            if (this.protocolConverter != null) {
                this.protocolConverter.onTransportError();
            }
            this.transportListener.onException(error);
        }
    }
    
    public long getReadGraceTime() {
        return this.readGraceTime;
    }
    
    public void setReadGraceTime(final long readGraceTime) {
        this.readGraceTime = readGraceTime;
    }
    
    public long getReadKeepAliveTime() {
        return this.readKeepAliveTime;
    }
    
    public void setReadKeepAliveTime(final long readKeepAliveTime) {
        this.readKeepAliveTime = readKeepAliveTime;
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
    
    public void setProtocolConverter(final MQTTProtocolConverter protocolConverter) {
        this.protocolConverter = protocolConverter;
    }
    
    public MQTTProtocolConverter getProtocolConverter() {
        return this.protocolConverter;
    }
    
    synchronized void startMonitorThread() {
        if (this.protocolConverter == null) {
            return;
        }
        if (this.monitorStarted.get()) {
            return;
        }
        if (this.readKeepAliveTime > 0L) {
            this.readCheckerTask = new SchedulerTimerTask(this.readChecker);
        }
        if (this.readKeepAliveTime > 0L) {
            this.monitorStarted.set(true);
            synchronized (AbstractInactivityMonitor.class) {
                if (MQTTInactivityMonitor.CHECKER_COUNTER == 0) {
                    MQTTInactivityMonitor.ASYNC_TASKS = this.createExecutor();
                    MQTTInactivityMonitor.READ_CHECK_TIMER = new Timer("InactivityMonitor ReadCheck", true);
                }
                ++MQTTInactivityMonitor.CHECKER_COUNTER;
                if (this.readKeepAliveTime > 0L) {
                    MQTTInactivityMonitor.READ_CHECK_TIMER.schedule(this.readCheckerTask, this.readKeepAliveTime, this.readGraceTime);
                }
            }
        }
    }
    
    synchronized void stopMonitorThread() {
        if (this.monitorStarted.compareAndSet(true, false)) {
            if (this.readCheckerTask != null) {
                this.readCheckerTask.cancel();
            }
            synchronized (AbstractInactivityMonitor.class) {
                MQTTInactivityMonitor.READ_CHECK_TIMER.purge();
                --MQTTInactivityMonitor.CHECKER_COUNTER;
                if (MQTTInactivityMonitor.CHECKER_COUNTER == 0) {
                    MQTTInactivityMonitor.READ_CHECK_TIMER.cancel();
                    MQTTInactivityMonitor.READ_CHECK_TIMER = null;
                    ThreadPoolUtils.shutdown(MQTTInactivityMonitor.ASYNC_TASKS);
                    MQTTInactivityMonitor.ASYNC_TASKS = null;
                }
            }
        }
    }
    
    private ThreadPoolExecutor createExecutor() {
        final ThreadPoolExecutor exec = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), this.factory);
        exec.allowCoreThreadTimeOut(true);
        return exec;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MQTTInactivityMonitor.class);
    }
}
