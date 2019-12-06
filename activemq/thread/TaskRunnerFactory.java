// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.thread;

import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.util.ThreadPoolUtils;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import java.util.concurrent.Executor;

public class TaskRunnerFactory implements Executor
{
    private static final Logger LOG;
    private ExecutorService executor;
    private int maxIterationsPerRun;
    private String name;
    private int priority;
    private boolean daemon;
    private final AtomicLong id;
    private boolean dedicatedTaskRunner;
    private long shutdownAwaitTermination;
    private final AtomicBoolean initDone;
    private int maxThreadPoolSize;
    private RejectedExecutionHandler rejectedTaskHandler;
    
    public TaskRunnerFactory() {
        this("ActiveMQ Task");
    }
    
    public TaskRunnerFactory(final String name) {
        this(name, 5, true, 1000);
    }
    
    private TaskRunnerFactory(final String name, final int priority, final boolean daemon, final int maxIterationsPerRun) {
        this(name, priority, daemon, maxIterationsPerRun, false);
    }
    
    public TaskRunnerFactory(final String name, final int priority, final boolean daemon, final int maxIterationsPerRun, final boolean dedicatedTaskRunner) {
        this(name, priority, daemon, maxIterationsPerRun, dedicatedTaskRunner, Integer.MAX_VALUE);
    }
    
    public TaskRunnerFactory(final String name, final int priority, final boolean daemon, final int maxIterationsPerRun, final boolean dedicatedTaskRunner, final int maxThreadPoolSize) {
        this.id = new AtomicLong(0L);
        this.shutdownAwaitTermination = 30000L;
        this.initDone = new AtomicBoolean(false);
        this.maxThreadPoolSize = Integer.MAX_VALUE;
        this.rejectedTaskHandler = null;
        this.name = name;
        this.priority = priority;
        this.daemon = daemon;
        this.maxIterationsPerRun = maxIterationsPerRun;
        this.dedicatedTaskRunner = dedicatedTaskRunner;
        this.maxThreadPoolSize = maxThreadPoolSize;
    }
    
    public void init() {
        if (this.initDone.compareAndSet(false, true)) {
            if (this.dedicatedTaskRunner || "true".equalsIgnoreCase(System.getProperty("org.apache.activemq.UseDedicatedTaskRunner"))) {
                this.executor = null;
            }
            else if (this.executor == null) {
                this.executor = this.createDefaultExecutor();
            }
            TaskRunnerFactory.LOG.debug("Initialized TaskRunnerFactory[{}] using ExecutorService: {}", this.name, this.executor);
        }
    }
    
    public void shutdown() {
        if (this.executor != null) {
            ThreadPoolUtils.shutdown(this.executor);
            this.executor = null;
        }
        this.initDone.set(false);
    }
    
    public void shutdownNow() {
        if (this.executor != null) {
            ThreadPoolUtils.shutdownNow(this.executor);
            this.executor = null;
        }
        this.initDone.set(false);
    }
    
    public void shutdownGraceful() {
        if (this.executor != null) {
            ThreadPoolUtils.shutdownGraceful(this.executor, this.shutdownAwaitTermination);
            this.executor = null;
        }
        this.initDone.set(false);
    }
    
    public TaskRunner createTaskRunner(final Task task, final String name) {
        this.init();
        if (this.executor != null) {
            return new PooledTaskRunner(this.executor, task, this.maxIterationsPerRun);
        }
        return new DedicatedTaskRunner(task, name, this.priority, this.daemon);
    }
    
    @Override
    public void execute(final Runnable runnable) {
        this.execute(runnable, this.name);
    }
    
    public void execute(final Runnable runnable, final String name) {
        this.init();
        TaskRunnerFactory.LOG.trace("Execute[{}] runnable: {}", name, runnable);
        if (this.executor != null) {
            this.executor.execute(runnable);
        }
        else {
            this.doExecuteNewThread(runnable, name);
        }
    }
    
    private void doExecuteNewThread(final Runnable runnable, final String name) {
        final String threadName = name + "-" + this.id.incrementAndGet();
        final Thread thread = new Thread(runnable, threadName);
        thread.setDaemon(this.daemon);
        TaskRunnerFactory.LOG.trace("Created and running thread[{}]: {}", threadName, thread);
        thread.start();
    }
    
    protected ExecutorService createDefaultExecutor() {
        final ThreadPoolExecutor rc = new ThreadPoolExecutor(0, this.getMaxThreadPoolSize(), getDefaultKeepAliveTime(), TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                final String threadName = TaskRunnerFactory.this.name + "-" + TaskRunnerFactory.this.id.incrementAndGet();
                final Thread thread = new Thread(runnable, threadName);
                thread.setDaemon(TaskRunnerFactory.this.daemon);
                thread.setPriority(TaskRunnerFactory.this.priority);
                TaskRunnerFactory.LOG.trace("Created thread[{}]: {}", threadName, thread);
                return thread;
            }
        });
        if (this.rejectedTaskHandler != null) {
            rc.setRejectedExecutionHandler(this.rejectedTaskHandler);
        }
        return rc;
    }
    
    public ExecutorService getExecutor() {
        return this.executor;
    }
    
    public void setExecutor(final ExecutorService executor) {
        this.executor = executor;
    }
    
    public int getMaxIterationsPerRun() {
        return this.maxIterationsPerRun;
    }
    
    public void setMaxIterationsPerRun(final int maxIterationsPerRun) {
        this.maxIterationsPerRun = maxIterationsPerRun;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public void setPriority(final int priority) {
        this.priority = priority;
    }
    
    public boolean isDaemon() {
        return this.daemon;
    }
    
    public void setDaemon(final boolean daemon) {
        this.daemon = daemon;
    }
    
    public boolean isDedicatedTaskRunner() {
        return this.dedicatedTaskRunner;
    }
    
    public void setDedicatedTaskRunner(final boolean dedicatedTaskRunner) {
        this.dedicatedTaskRunner = dedicatedTaskRunner;
    }
    
    public int getMaxThreadPoolSize() {
        return this.maxThreadPoolSize;
    }
    
    public void setMaxThreadPoolSize(final int maxThreadPoolSize) {
        this.maxThreadPoolSize = maxThreadPoolSize;
    }
    
    public RejectedExecutionHandler getRejectedTaskHandler() {
        return this.rejectedTaskHandler;
    }
    
    public void setRejectedTaskHandler(final RejectedExecutionHandler rejectedTaskHandler) {
        this.rejectedTaskHandler = rejectedTaskHandler;
    }
    
    public long getShutdownAwaitTermination() {
        return this.shutdownAwaitTermination;
    }
    
    public void setShutdownAwaitTermination(final long shutdownAwaitTermination) {
        this.shutdownAwaitTermination = shutdownAwaitTermination;
    }
    
    private static int getDefaultKeepAliveTime() {
        return Integer.getInteger("org.apache.activemq.thread.TaskRunnerFactory.keepAliveTime", 30);
    }
    
    static {
        LOG = LoggerFactory.getLogger(TaskRunnerFactory.class);
    }
}
